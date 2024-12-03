package dellapenna.personal.bpmn.bpmn;

import dellapenna.personal.bpmn.feel.FeelTranslator;
import dellapenna.personal.bpmn.feel.FeelTranslator.FeelTranslationInfo;
import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import dellapenna.personal.bpmn.feel.ToJavaFeelTranslator;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.camunda.bpm.model.bpmn.instance.BusinessRuleTask;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.ErrorEventDefinition;
import org.camunda.bpm.model.bpmn.instance.EventBasedGateway;
import org.camunda.bpm.model.bpmn.instance.EventDefinition;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.InclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.ManualTask;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.ReceiveTask;
import org.camunda.bpm.model.bpmn.instance.ScriptTask;
import org.camunda.bpm.model.bpmn.instance.SendTask;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

public class ToJavaBPMNTranslator extends AbstractBPMNTranslator<String> {

    private final static String ZEEBENS = "http://camunda.org/schema/zeebe/1.0";
    private static final Pattern INPUT_PATTERN = Pattern.compile("^input_([a-z0-9_-]+)$", Pattern.CASE_INSENSITIVE);
    private static final ToJavaFeelTranslator feel = new ToJavaFeelTranslator();

    public ToJavaBPMNTranslator() {
        reset();
    }

    @Override
    protected void reset() {
        super.reset();
    }

    public static boolean isVariableIncluded(String v, List<String> vl) {
        String wv = "";
        String[] wvss = v.split("\\.");
        for (String wvs : wvss) {
            wv += wvs;
            if (vl.contains(wv)) {
                return true;
            }
        }
        return false;
    }

    public static String sanitizeName(String n) {
        return n.replaceAll("[^A-Za-z0-9_]", "_");
    }

    public static String generateDebugOutputStament(String s, Object... args) {
        String message = String.format(s, args);
        return "BPMNExecProcessUtils.debugOutput(\"" + message + "\")";
    }

    public static Code generateTransitionDescriptionStaments(FlowNode source, FlowNode target, BPMNTranslationInfo info) {
        Code code = new Code<String>();
        code.append("//[outgoing edge] " + target.getId() + ((target.getName() != null && !target.getName().isBlank()) ? (" - " + target.getName()) : ""));
        if (info != null && info.isDebug()) {
            code.append("BPMNExecProcessUtils.logTransition(\"" + source.getId() + "\",\"" + target.getId() + "\")");
        }
        return code;
    }

    public static Code generateNodeDescriptionStaments(FlowNode n, BPMNTranslationInfo info) {
        Code code = new Code<String>();
        String description = getNodeDescription(n);
        code.append("//" + description);
        if (info != null && info.isDebug()) {
            code.append("BPMNExecProcessUtils.debugOutput(\"" + description + "\")");
        }
        return code;
    }

    public static String getNodeDescription(FlowNode n) {
        String nodeTypeString = switch (n) {
            case ManualTask t ->
                "Manual Task";
            case UserTask t ->
                "User Task";
            case ScriptTask t ->
                "Script Task";
            case ServiceTask t ->
                "Service Task";
            case SendTask t ->
                "Send Task";
            case ReceiveTask t ->
                "Receive Task";
            case BusinessRuleTask t ->
                "Business Rule Task";
            case Task t ->
                "Generic Task";
            case EndEvent t ->
                "End Event";
            case StartEvent t ->
                "Start Event";
            case InclusiveGateway t ->
                "Inclusive" + (t.getOutgoing().size() == 1 ? " Joining" : "") + " Gateway";
            case ExclusiveGateway t ->
                "Exclusive" + (t.getOutgoing().size() == 1 ? " Joining" : "") + " Gateway";
            case ParallelGateway t ->
                "Parallel" + (t.getOutgoing().size() == 1 ? " Joining" : "") + " Gateway";
            case EventBasedGateway t ->
                "Event-Based" + (t.getOutgoing().size() == 1 ? " Joining" : "") + " Gateway";
            default ->
                "Unknown node";
        };
        String nodeId = (n.getName() != null ? (n.getName() + " [" + n.getId() + "]") : n.getId());
        String message = String.format("%s %s", nodeTypeString, nodeId);
        return message;
    }

    /////////////////////////////////////////////////////////////////////
    //generates the source code for a complete BPMN given the code of its single processes
    @Override
    public String generateBpmnSource(BPMNDecoded bpmn, BPMNTranslationInfo info) {
        return """
               /*
                * ****************************** BPMN Generated Code *************************
                */
               """
                + bpmn.processes().stream()
                        .map(p -> generateProcessSource(p, info))
                        .collect(Collectors.joining("\n\n"));
    }

    //generates the source code for a complete BPMN process
    protected String generateProcessSource(BPMNDecodedProcess process, BPMNTranslationInfo info) {
        //input variables should be deduced by differencing declared globals from
        //variables referenced by feel expressions

        String global_variables = process.getWrittenVariables().values().stream()
                .map(gd -> "Object " + gd.name())
                .collect(Collectors.joining(";\n", "\n\n//Process Variables\n", ";\n\n"));
        String input_variables = process.getFreeVariables().values().stream()
                .map(id -> "Object " + id.name())
                .collect(Collectors.joining(";\n", "\n\n//Input Variables\n", ";\n\n"));
        String functions = process.getFunctions().values().stream()
                .flatMap(fc -> fc.values().stream())
                .map(fd -> generateFunctionSource(fd))
                .collect(Collectors.joining("\n\n", "\n\n//Process Dynamics\n", "\n\n"));
        return " class bpmn_process_" + sanitizeName(process.getName()) + " { "
                + input_variables
                + global_variables
                + functions
                + generateProcessInitSource(process, info)
                + generateProcessMainSource(process, info)
                + "}";
    }

    //generates the source code for a given function definition
    private String generateFunctionSource(FunctionDefinition<String> f) {
        return "public " + f.returnType() + " " + f.name() + "("
                + f.parameters().entrySet().stream().map(e -> e.getValue() + " " + e.getKey()).collect(Collectors.joining(", "))
                + ") {" + generateCodeSource(f.body()) + "}";
    }

    //generates the text source for a code block    
    public String generateCodeSource(Code code) {
        List<String> statements = code.getStatements();
        String source = "";
        for (int l = 0; l < statements.size(); ++l) {
            String statement = statements.get(l).trim();
            if (!statement.startsWith("//") && !statement.endsWith("}")) {
                statement += ";";
            }
            source += (statement + "\n");
        }
        return source;
    }

    //generates the input variables initialization code
    public String generateProcessInitSource(BPMNDecodedProcess process, BPMNTranslationInfo info) {
        String source = "";
        source += process.getFreeVariables().values().stream().map(v -> "this." + v.name() + " = null;\t//TODO assign input variable\n").collect(Collectors.joining());
        source += process.getFreeVariables().values().stream()
                .map(v -> "if (this." + v.name() + "==null) " + v.name() + "=BPMNExecProcessUtils.inputs.getProperty(\"" + v.name() + "\", null);\n")
                .collect(Collectors.joining());

        source += process.getFreeVariables().values().stream().map(v -> "BPMNExecProcessUtils.logInput(\"" + v.name() + "\",this." + v.name() + ");\n").collect(Collectors.joining());

        source += "//parallel join initializers\n";
        for (ParallelGateway g : parallel_joining) {
            source += "BPMNExecProcessUtils.initJoin(\"" + g.getId() + "\","
                    + g.getIncoming().stream().map(s -> "\"" + s.getSource().getId() + "\"").collect(Collectors.joining(",")) + ");";
        }

        return "public void init() {\n" + source + "\n}";
    }

    //generates the text source for the process main function
    public String generateProcessMainSource(BPMNDecodedProcess process, BPMNTranslationInfo info) {
        String source = "public static void main(String[] args) {\n";
        if (info == null || !info.isDebug()) {
            source += "BPMNExecProcessUtils.debugChannel=new java.io.PrintStream(java.io.OutputStream.nullOutputStream());";
        }
        if (info == null || info.isTrueParallel()) {
            source += "BPMNExecProcessUtils.enableTrueParallel();";
        }
        source += "bpmn_process_" + sanitizeName(process.getName()) + " process = new " + "bpmn_process_" + sanitizeName(process.getName()) + "();\n";
        if (!process.getStartEventFlowNames().isEmpty()) {
            source += "BPMNExecProcessUtils.executeProcess(process::init,process::" + sanitizeName(process.getStartEventFlowNames().getFirst()) + ");\n";
        }
        source += "}";
        return source;
    }

    /* *********************************************************************************** */
    //////////////////
    // Generate code for specific BPMN nodes
    //////////////////
    //TASKS
    @Override
    public Code generateGenericTaskCode(BPMNDecodedProcess p, Task t, BPMNTranslationInfo info) {
        Code code = new Code<String>(generateNodeDescriptionStaments(t, info));
        return code;
    }

    @Override
    public Code generateManualTaskCode(BPMNDecodedProcess p, ManualTask t, BPMNTranslationInfo info) {
        Code code = new Code<String>(generateNodeDescriptionStaments(t, info));
        return code;
    }

    @Override
    public Code generateScriptTaskCode(BPMNDecodedProcess p, ScriptTask t, BPMNTranslationInfo info) throws FeelTranslatorException {
        Code code = new Code<String>(generateNodeDescriptionStaments(t, info));
        Code outs = generateOutputAssignmentsCode(p, t, Collections.EMPTY_LIST, info);
        code.append(outs);

        ModelElementInstance script = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "script");
        if (script != null) {
            String resultVariable = script.getAttributeValue("resultVariable");
            String expression = script.getAttributeValue("expression");
            FeelTranslationInfo f_info = new FeelTranslator.FeelTranslationInfo();
            code.append(resultVariable + "=" + feel.translate(expression.substring(1), f_info));
            p.registerProcessVariable(resultVariable, BPMNDecodedProcess.VariableDirection.WRITE);
            //in questo modo, però, una variabile di input, se viene riassegnata nel codice, non sarà più considerata tale, non potendo capire staticamente
            p.registerProcessVariables(f_info.getUsedVariableNames(), BPMNDecodedProcess.VariableDirection.READ);
        }
        return code;
    }

    @Override
    public Code generateServiceTaskCode(BPMNDecodedProcess p, ServiceTask t, BPMNTranslationInfo info) {
        Code code = new Code<String>(generateNodeDescriptionStaments(t, info));
        return code;
    }

    @Override
    public Code generateSendTaskCode(BPMNDecodedProcess p, SendTask t, BPMNTranslationInfo info) {
        Code code = new Code<String>(generateNodeDescriptionStaments(t, info));
        return code;
    }

    @Override
    public Code generateReceiveTaskCode(BPMNDecodedProcess p, ReceiveTask t, BPMNTranslationInfo info) {
        Code code = new Code<String>(generateNodeDescriptionStaments(t, info));
        return code;
    }

    @Override
    public Code generateUserTaskCode(BPMNDecodedProcess p, UserTask t, BPMNTranslationInfo info) {
        Code code = new Code<String>(generateNodeDescriptionStaments(t, info));
        code.append(generateOutputAssignmentsCode(p, t, Collections.EMPTY_LIST, info));
        return code;
    }

    @Override
    public Code generateBusinessRuleTaskCode(BPMNDecodedProcess p, BusinessRuleTask t, BPMNTranslationInfo info) {
        Code code = new Code<String>(generateNodeDescriptionStaments(t, info));
        if (info != null && info.isDebug()) {
            code.append("BPMNExecProcessUtils.debugOutput(\"\t EXECUTING DECISION " + (t.getName() != null ? t.getName() : t.getId()) + "\")");
        }

        ModelElementInstance ioMapping = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "ioMapping");
        ModelElementInstance calledDecision = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "calledDecision");

        String procName = sanitizeName("dmn_dtable_" + calledDecision.getAttributeValue("decisionId"));
        String output_record_name = procName + "_result";

        FeelTranslationInfo f_info = new FeelTranslationInfo();
        code.append(
                output_record_name + " " + calledDecision.getAttributeValue("resultVariable")
                + "=" + procName + ".execute"
                + "(" + ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "input").stream()
                        .map(e -> "/*" + e.getAttribute("target") + "*/" + feel.translateChecked(e.getAttribute("source").substring(1), f_info)).collect(Collectors.joining(", "))
                + ")");

        //p.registerProcessVariable(calledDecision.getAttributeValue("resultVariable"), BPMNDecodedProcess.VariableDirection.WRITE); //locale
        //p.registerProcessVariables(f_info.getUsedVariableNames(), BPMNDecodedProcess.VariableDirection.READ); //non dovrebbe servire, le tabelle non dovrebbero far riferimento alle veriabili di processo
        if (info != null && info.isDebug()) {
            code.append("BPMNExecProcessUtils.debugOutput(\"\t DECISION RESULT IS %s\"," + calledDecision.getAttributeValue("resultVariable") + ")");
        }

        code.append(generateOutputAssignmentsCode(p, t, List.of(calledDecision.getAttributeValue("resultVariable")), info));

        return code;
    }

    //EVENTS
    @Override
    public Code generateEndEventCode(BPMNDecodedProcess p/*UNUSED*/, EndEvent t, BPMNTranslationInfo info) {
        Code code = new Code<String>(generateNodeDescriptionStaments(t, info));
        Collection<EventDefinition> eventDefs = t.getEventDefinitions();
        boolean isSuccess = true;
        for (EventDefinition eventDef : eventDefs) {
            if (eventDef instanceof ErrorEventDefinition eed) {
                String error_message = eed.getError().getName();  //TODO: handle other event definitions here?                
                int error_code;
                try {
                    error_code = Integer.valueOf(eed.getError().getErrorCode());
                } catch (NumberFormatException ex) {
                    //code is not a number
                    error_code = 1;
                }
                isSuccess = false;
                code.append("BPMNExecProcessUtils.error(s,\"" + error_message + "\", " + error_code + ")");
            }
        }
        if (isSuccess) {
            code.append("BPMNExecProcessUtils.success(s)");
        }
        return code;
    }

    @Override
    public Code generateStartEventCode(BPMNDecodedProcess p, StartEvent t, BPMNTranslationInfo info) {
        Code code = new Code<String>(generateNodeDescriptionStaments(t, info));
        code.append(generateOutputAssignmentsCode(p, t, Collections.EMPTY_LIST, info));
        p.registerStartEventFlowName(sanitizeName(p.getFlowName(t)));
        return code;
    }

////// GATEWAYS
    private Code generateJoiningGatewayCode(BPMNDecodedProcess p, Gateway g, FlowNode j, BPMNTranslationInfo info) throws FeelTranslatorException {
        Code code = new Code<String>(generateNodeDescriptionStaments(g, info));
        code.append(generateFlowJointCode(p, g, j, info));
        return code;
    }

    @Override
    public Code generateParallelJoiningGatewayCode(BPMNDecodedProcess p, ParallelGateway n, FlowNode joinedflow, BPMNTranslationInfo info) throws BpmnTranslatorException, FeelTranslatorException {
        Code code = new Code<String>(generateNodeDescriptionStaments(n, info));
        code.append(generateTransitionDescriptionStaments(n, joinedflow, info));
        code.append("//JOINS: " + n.getIncoming().stream().map(s -> s.getSource().getId()).collect(Collectors.joining(",")));
        code.append("BPMNExecProcessUtils.join(s,\"" + n.getId() + "\", " + ("this::" + sanitizeName(p.getFlowName(joinedflow))) + ")");
        return code;
    }

    @Override
    public Code generateInclusiveJoiningGatewayCode(BPMNDecodedProcess p, InclusiveGateway n, FlowNode joinedflow, BPMNTranslationInfo info) throws BpmnTranslatorException, FeelTranslatorException {
        Code code = generateJoiningGatewayCode(p, n, joinedflow, info);
        return code;
    }

    @Override
    public Code generateExclusiveJoiningGatewayCode(BPMNDecodedProcess p, ExclusiveGateway n, FlowNode joinedflow, BPMNTranslationInfo info) throws BpmnTranslatorException, FeelTranslatorException {
        Code code = generateJoiningGatewayCode(p, n, joinedflow, info);
        return code;
    }

    @Override
    public Code generateEventJoiningGatewayCode(BPMNDecodedProcess p, EventBasedGateway n, FlowNode joinedflow, BPMNTranslationInfo info) throws BpmnTranslatorException, FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Code generateParallelGatewayCode(BPMNDecodedProcess p, ParallelGateway n, List<BPMNDecodedConditionalFlow> splitFlows, BPMNTranslationInfo info) throws FeelTranslatorException {
        Code code = new Code<String>(generateNodeDescriptionStaments(n, info));
        String[] branch_functions = new String[splitFlows.size()];
        for (int o = 0; o < splitFlows.size(); ++o) {
            code.append(generateTransitionDescriptionStaments(n, splitFlows.get(o).firstStep(), info));
            branch_functions[o] = "this::" + sanitizeName(p.getFlowName(splitFlows.get(o).firstStep()));
        }
        code.append("//FORKS: " + splitFlows.stream().map(s -> s.firstStep().getId()).collect(Collectors.joining(",")));
        code.append("BPMNExecProcessUtils.fork(s,\"" + (n.getName() != null ? n.getName() : n.getId()) + "\"," + String.join(",", branch_functions) + ")");
        code.append("BPMNExecProcessUtils.stopThread()");

        return code;
    }

    @Override
    public Code generateInclusiveGatewayCode(BPMNDecodedProcess p, InclusiveGateway n, List<BPMNDecodedConditionalFlow> splitFlows, BPMNTranslationInfo info) throws FeelTranslatorException {
        Code code = new Code<String>(generateNodeDescriptionStaments(n, info));
        String source = "";
        FeelTranslationInfo f_info = new FeelTranslationInfo();
        for (int o = 0; o < splitFlows.size(); ++o) {
            Code splitCode = ToJavaBPMNTranslator.this.generateFlowJointCode(p, n, splitFlows.get(o).firstStep(), info);
            source += "if "
                    + "(" + feel.translate(splitFlows.get(o).condition().substring(1), f_info) + ")" //TEMP, dobbiamo togliere l'uguale se c'è altrimenti non è un'espressione feel
                    + "{" + generateCodeSource(splitCode)
                    + "} ";
        }
        code.append(source);
        p.registerProcessVariables(f_info.getUsedVariableNames(), BPMNDecodedProcess.VariableDirection.READ);
        return code;
    }

    @Override
    public Code generateExclusiveGatewayCode(BPMNDecodedProcess p, ExclusiveGateway n, List<BPMNDecodedConditionalFlow> splitFlows, BPMNTranslationInfo info) throws FeelTranslatorException {
        Code code = new Code<String>(generateNodeDescriptionStaments(n, info));
        String source = "";
        BPMNDecodedConditionalFlow default_branch = null;
        FeelTranslationInfo f_info = new FeelTranslationInfo();
        for (int o = 0; o < splitFlows.size(); ++o) {
            Code splitCode = ToJavaBPMNTranslator.this.generateFlowJointCode(p, n, splitFlows.get(o).firstStep(), info);
            if (splitFlows.get(o).condition() != null) {
                if (!source.isBlank()) {
                    source += " else ";
                }
                source += "if " + "(" + feel.translate(splitFlows.get(o).condition().substring(1), f_info) + ")";  //TEMP, dobbiamo togliere l'uguale se c'è altrimenti non è un'espressione feel
                source += "{" + generateCodeSource(splitCode) + "}";
            } else {
                default_branch = splitFlows.get(o);
            }
        }
        if (!source.isBlank()) {
            source += " else ";
        }
        if (default_branch != null) {
            Code splitCode = ToJavaBPMNTranslator.this.generateFlowJointCode(p, n, default_branch.firstStep(), info);
            source += "{" + generateCodeSource(splitCode) + "}";
        } else {
            source += "{ BPMNExecProcessUtils.noDefaultCaseError(s); }";

        }

        code.append(source);
        p.registerProcessVariables(f_info.getUsedVariableNames(), BPMNDecodedProcess.VariableDirection.READ);

        return code;
    }

    @Override
    public Code generateEventGatewayCode(BPMNDecodedProcess p, EventBasedGateway n, List<BPMNDecodedConditionalFlow> splitFlows, BPMNTranslationInfo info) throws FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //////////////
    // Code generation utilities
    //////////////
    @Override
    protected Code generateFlowJointCode(BPMNDecodedProcess p, FlowNode current, FlowNode next, BPMNTranslationInfo info) {
        Code code = new Code<String>();
        code.append(generateTransitionDescriptionStaments(current, next, info));
        code.append(sanitizeName(p.getFlowName(next)) + "(s.withCurrent(\"" + current.getId() + "\"))");
        return code;
    }

    //generates the code to capture the output of a node, as a set of variable assignments
    private Code generateOutputAssignmentsCode(BPMNDecodedProcess p, FlowNode t, List<String> localVariables, BPMNTranslationInfo info) {
        Code result = new Code<String>();
        FeelTranslationInfo f_info = new FeelTranslationInfo();

        ModelElementInstance ioMapping = t.getExtensionElements() != null ? t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "ioMapping") : null;
        if (ioMapping != null) {
            //declare output (written) variables, if not local
            ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "output").stream()
                    .map(e -> e.getAttribute("target"))
                    .filter(v -> !isVariableIncluded(v, localVariables))
                    .forEach(v -> {
                        p.registerProcessVariable(v, BPMNDecodedProcess.VariableDirection.WRITE);
                    });
            /*
            ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "output").stream()
                    .forEach(e -> {
                        String expression = feel.translateChecked(e.getAttribute("source").substring(1), f_info);
                        Matcher matcher = INPUT_PATTERN.matcher(expression);
                        if (matcher.matches()) {
                            p.registerInputVariable(expression);
                        }
                    });
             */
            result.append(ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "output").stream()
                    .map(e -> e.getAttribute("target") + "=" + feel.translateChecked(e.getAttribute("source").substring(1), f_info))
                    .collect(Collectors.toList()));

            //declare source (read) variables, if not local
            p.registerProcessVariables(
                    f_info.getUsedVariableNames().stream()
                            .map(l -> String.join(".", l))
                            .filter(v -> !isVariableIncluded(v, localVariables))
                            .toList(),
                     BPMNDecodedProcess.VariableDirection.READ);

            if (info != null && info.isDebug()) {
                result.append(ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "output").stream()
                        .map(e -> "BPMNExecProcessUtils.debugOutput(\"\t ASSIGNING " + e.getAttribute("target") + " TO %s\"," + feel.translateChecked(e.getAttribute("source").substring(1), null) + ")")
                        .collect(Collectors.toList()));
            }
        }
        return result;
    }

    //    protected Code generateFlowJointCode(BPMNDecodedProcess p, String flowName, BPMNTranslationInfo info) {
//        Code code = new Code<String>(flowName + "(s)");
//        return code;
//    }
//    //generates the code to call a function, registering it if needed
//    private Code generateProcedureCallCode(BPMNDecodedProcess p, FunctionDefinition proc) {
//        return new Code<String>(sanitizeName(proc.name()) + "()");
//
//    }
}
