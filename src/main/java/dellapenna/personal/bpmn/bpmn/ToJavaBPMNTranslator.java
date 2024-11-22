package dellapenna.personal.bpmn.bpmn;

import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import dellapenna.personal.bpmn.feel.ToJavaFeelTranslator;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
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

public class ToJavaBPMNTranslator extends AbstractBPMNTranslator {

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

    public static String sanitizeName(String n) {
        return n.replaceAll("[^A-Za-z0-9_]", "_");
    }

    public static String generateDebugOutputStament(String s, Object... args) {
        String message = String.format(s, args);
        return "BPMNExecProcessUtils.debugOutput(\"" + message + "\")";
    }

    public static Code generateTransitionDescriptionStaments(FlowNode source, FlowNode target, Options opt) {
        Code code = new Code();
        code.append("//[outgoing edge] " + target.getId() + ((target.getName() != null && !target.getName().isBlank()) ? (" - " + target.getName()) : ""));
        if (opt.isDebug()) {
            code.append("BPMNExecProcessUtils.logTransition(\"" + source.getId() + "\",\"" + target.getId() + "\")");
        }
        return code;
    }

    public static Code generateNodeDescriptionStaments(FlowNode n, Options opt) {
        Code code = new Code();
        String description = getNodeDescription(n);
        code.append("//" + description);
        if (opt.isDebug()) {
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
    public String generateBpmnSource(BPMNDecoded bpmn, Options opt) {
        return """
               /*
                * ****************************** BPMN Generated Code *************************
                */
               """
                + bpmn.processes().stream()
                        .map(p -> generateProcessSource(p, opt))
                        .collect(Collectors.joining("\n\n"));
    }

    //generates the source code for a complete BPMN process
    protected String generateProcessSource(BPMNDecodedProcess process, Options opt) {
        String global_variables = process.getVariables().values().stream()
                .map(gd -> "Object " + gd.name())
                .collect(Collectors.joining(";\n", "\n\n//Process Variables\n", ";\n\n"));
        String input_variables = process.getInputs().values().stream()
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
                + generateProcessInitSource(process, opt)
                + generateProcessMainSource(process, opt)
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
    public String generateProcessInitSource(BPMNDecodedProcess process, Options opt) {
        String source = "";
        source += process.getInputs().values().stream().map(v -> "this." + v.name() + " = null;\t//TODO assign input variable\n").collect(Collectors.joining());
        source += process.getInputs().values().stream()
                .map(v -> "if (this." + v.name() + "==null) " + v.name() + "=BPMNExecProcessUtils.inputs.getProperty(\"" + v.name() + "\", null);\n")
                .collect(Collectors.joining());

        source += process.getInputs().values().stream().map(v -> "BPMNExecProcessUtils.logInput(\"" + v.name() + "\",this." + v.name() + ");\n").collect(Collectors.joining());

        source += "//parallel join initializers\n";
        for (ParallelGateway g : parallel_joining) {
            source += "BPMNExecProcessUtils.initJoin(\"" + g.getId() + "\","
                    + g.getIncoming().stream().map(s -> "\"" + s.getSource().getId() + "\"").collect(Collectors.joining(",")) + ");";
        }

        return "public void init() {\n" + source + "\n}";
    }

    //generates the text source for the process main function
    public String generateProcessMainSource(BPMNDecodedProcess process, Options opt) {
        String source = "public static void main(String[] args) {\n";
        if (!opt.isDebug()) {
            source += "BPMNExecProcessUtils.debugChannel=new java.io.PrintStream(java.io.OutputStream.nullOutputStream());";
        }
        if (opt.isTrueParallel()) {
            source += "BPMNExecProcessUtils.enableTrueParallel();";
        }
        source += "bpmn_process_" + sanitizeName(process.getName()) + " process = new " + "bpmn_process_" + sanitizeName(process.getName()) + "();\n";
        if (!process.getStartEventFlowNames().isEmpty()) {
            source += "BPMNExecProcessUtils.executeProcess(process::init,process::" + sanitizeName(process.getStartEventFlowNames().getFirst()) + ");";
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
    public Code generateGenericTaskCode(BPMNDecodedProcess p, Task t, Options opt) {
        Code code = new Code(generateNodeDescriptionStaments(t, opt));
        return code;
    }

    @Override
    public Code generateManualTaskCode(BPMNDecodedProcess p, ManualTask t, Options opt) {
        Code code = new Code(generateNodeDescriptionStaments(t, opt));
        return code;
    }

    @Override
    public Code generateScriptTaskCode(BPMNDecodedProcess p, ScriptTask t, Options opt) {
        Code code = new Code(generateNodeDescriptionStaments(t, opt));
        Code outs = generateOutputAssignmentsCode(p, t, opt);
        code.append(outs);

        ModelElementInstance script = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "script");
        if (script != null) {
            String resultVariable = script.getAttributeValue("resultVariable");
            String expression = script.getAttributeValue("expression");
            code.append(resultVariable + "=" + feel.translateChecked(expression.substring(1)));
        }
        return code;
    }

    @Override
    public Code generateServiceTaskCode(BPMNDecodedProcess p, ServiceTask t, Options opt) {
        Code code = new Code(generateNodeDescriptionStaments(t, opt));
        return code;
    }

    @Override
    public Code generateSendTaskCode(BPMNDecodedProcess p, SendTask t, Options opt) {
        Code code = new Code(generateNodeDescriptionStaments(t, opt));
        return code;
    }

    @Override
    public Code generateReceiveTaskCode(BPMNDecodedProcess p, ReceiveTask t, Options opt) {
        Code code = new Code(generateNodeDescriptionStaments(t, opt));
        return code;
    }

    @Override
    public Code generateUserTaskCode(BPMNDecodedProcess p, UserTask t, Options opt) {
        Code code = new Code(generateNodeDescriptionStaments(t, opt));
        code.append(generateOutputAssignmentsCode(p, t, opt));
        return code;
    }

    @Override
    public Code generateBusinessRuleTaskCode(BPMNDecodedProcess p, BusinessRuleTask t, Options opt) {
        Code code = new Code(generateNodeDescriptionStaments(t, opt));
        if (opt.isDebug()) {
            code.append("BPMNExecProcessUtils.debugOutput(\"\t EXECUTING DECISION " + (t.getName() != null ? t.getName() : t.getId()) + "\")");
        }

        ModelElementInstance ioMapping = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "ioMapping");
        ModelElementInstance calledDecision = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "calledDecision");

        String procName = sanitizeName("dmn_dtable_" + calledDecision.getAttributeValue("decisionId"));
        String output_record_name = procName + "_result";

        code.append(
                output_record_name + " " + calledDecision.getAttributeValue("resultVariable")
                + "=" + procName + ".execute"
                + "(" + ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "input").stream()
                        .map(e -> "/*" + e.getAttribute("target") + "*/" + feel.translateChecked(e.getAttribute("source").substring(1))).collect(Collectors.joining(", "))
                + ")");

        if (opt.isDebug()) {
            code.append("BPMNExecProcessUtils.debugOutput(\"\t DECISION RESULT IS %s\"," + calledDecision.getAttributeValue("resultVariable") + ")");
        }

        code.append(generateOutputAssignmentsCode(p, t, opt));

        return code;
    }

    //EVENTS
    @Override
    public Code generateEndEventCode(BPMNDecodedProcess p/*UNUSED*/, EndEvent t, Options opt) {
        Code code = new Code(generateNodeDescriptionStaments(t, opt));
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
    public Code generateStartEventCode(BPMNDecodedProcess p, StartEvent t, Options opt) {
        Code code = new Code(generateNodeDescriptionStaments(t, opt));
        code.append(generateOutputAssignmentsCode(p, t, opt));
        p.registerStartEventFlowName(sanitizeName(p.getFlowName(t)));
        return code;
    }

////// GATEWAYS
    private Code generateJoiningGatewayCode(BPMNDecodedProcess p, Gateway g, FlowNode j, Options opt) throws FeelTranslatorException {
        Code code = new Code(generateNodeDescriptionStaments(g, opt));
        code.append(generateFlowJointCode(p, g, j, opt));
        return code;
    }

    @Override
    public Code generateParallelJoiningGatewayCode(BPMNDecodedProcess p, ParallelGateway n, FlowNode joinedflow, Options opt) throws BpmnTranslatorException, FeelTranslatorException {
        Code code = new Code(generateNodeDescriptionStaments(n, opt));
        code.append(generateTransitionDescriptionStaments(n, joinedflow, opt));
        code.append("//JOINS: " + n.getIncoming().stream().map(s -> s.getSource().getId()).collect(Collectors.joining(",")));
        code.append("BPMNExecProcessUtils.join(s,\"" + n.getId() + "\", " + ("this::" + sanitizeName(p.getFlowName(joinedflow))) + ")");
        return code;
    }

    @Override
    public Code generateInclusiveJoiningGatewayCode(BPMNDecodedProcess p, InclusiveGateway n, FlowNode joinedflow, Options opt) throws BpmnTranslatorException, FeelTranslatorException {
        Code code = generateJoiningGatewayCode(p, n, joinedflow, opt);
        return code;
    }

    @Override
    public Code generateExclusiveJoiningGatewayCode(BPMNDecodedProcess p, ExclusiveGateway n, FlowNode joinedflow, Options opt) throws BpmnTranslatorException, FeelTranslatorException {
        Code code = generateJoiningGatewayCode(p, n, joinedflow, opt);
        return code;
    }

    @Override
    public Code generateEventJoiningGatewayCode(BPMNDecodedProcess p, EventBasedGateway n, FlowNode joinedflow, Options opt) throws BpmnTranslatorException, FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Code generateParallelGatewayCode(BPMNDecodedProcess p, ParallelGateway n, List<BPMNDecodedConditionalFlow> splitFlows, Options opt) throws FeelTranslatorException {
        Code code = new Code(generateNodeDescriptionStaments(n, opt));
        String[] branch_functions = new String[splitFlows.size()];
        for (int o = 0; o < splitFlows.size(); ++o) {
            code.append(generateTransitionDescriptionStaments(n, splitFlows.get(o).firstStep(), opt));
            branch_functions[o] = "this::" + sanitizeName(p.getFlowName(splitFlows.get(o).firstStep()));
        }
        code.append("//FORKS: " + splitFlows.stream().map(s -> s.firstStep().getId()).collect(Collectors.joining(",")));
        code.append("BPMNExecProcessUtils.fork(s,\"" + (n.getName() != null ? n.getName() : n.getId()) + "\"," + String.join(",", branch_functions) + ")");
        code.append("BPMNExecProcessUtils.stopThread()");

        return code;
    }

    @Override
    public Code generateInclusiveGatewayCode(BPMNDecodedProcess p, InclusiveGateway n, List<BPMNDecodedConditionalFlow> splitFlows, Options opt) throws FeelTranslatorException {
        Code code = new Code(generateNodeDescriptionStaments(n, opt));
        String source = "";
        for (int o = 0; o < splitFlows.size(); ++o) {
            Code splitCode = ToJavaBPMNTranslator.this.generateFlowJointCode(p, n, splitFlows.get(o).firstStep(), opt);
            source += "if "
                    + "(" + feel.translate(splitFlows.get(o).condition().substring(1)) + ")" //TEMP, dobbiamo togliere l'uguale se c'è altrimenti non è un'espressione feel
                    + "{" + generateCodeSource(splitCode)
                    + "} ";
        }
        code.append(source);
        return code;
    }

    @Override
    public Code generateExclusiveGatewayCode(BPMNDecodedProcess p, ExclusiveGateway n, List<BPMNDecodedConditionalFlow> splitFlows, Options opt) throws FeelTranslatorException {
        Code code = new Code(generateNodeDescriptionStaments(n, opt));
        String source = "";
        BPMNDecodedConditionalFlow default_branch = null;
        for (int o = 0; o < splitFlows.size(); ++o) {
            Code splitCode = ToJavaBPMNTranslator.this.generateFlowJointCode(p, n, splitFlows.get(o).firstStep(), opt);
            if (splitFlows.get(o).condition() != null) {
                if (!source.isBlank()) {
                    source += " else ";
                }
                source += "if " + "(" + feel.translate(splitFlows.get(o).condition().substring(1)) + ")";  //TEMP, dobbiamo togliere l'uguale se c'è altrimenti non è un'espressione feel
                source += "{" + generateCodeSource(splitCode) + "}";
            } else {
                default_branch = splitFlows.get(o);
            }
        }
        if (!source.isBlank()) {
            source += " else ";
        }
        if (default_branch != null) {
            Code splitCode = ToJavaBPMNTranslator.this.generateFlowJointCode(p, n, default_branch.firstStep(), opt);
            source += "{" + generateCodeSource(splitCode) + "}";
        } else {
            source += "{ BPMNExecProcessUtils.noDefaultCaseError(s); }";

        }

        code.append(source);
        return code;
    }

    @Override
    public Code generateEventGatewayCode(BPMNDecodedProcess p, EventBasedGateway n, List<BPMNDecodedConditionalFlow> splitFlows, Options opt) throws FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //////////////
    // Code generation utilities
    //////////////
    @Override
    protected Code generateFlowJointCode(BPMNDecodedProcess p, FlowNode current, FlowNode next, Options opt) {
        Code code = new Code();
        code.append(generateTransitionDescriptionStaments(current, next, opt));
        code.append(sanitizeName(p.getFlowName(next)) + "(s.withCurrent(\"" + current.getId() + "\"))");
        return code;
    }

    //generates the code to capture the output of a node, as a set of variable assignments
    private Code generateOutputAssignmentsCode(BPMNDecodedProcess p, FlowNode t, Options opt) {
        Code result = new Code();
        ModelElementInstance ioMapping = t.getExtensionElements() != null ? t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "ioMapping") : null;
        if (ioMapping != null) {
            ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "output").stream().forEach(e -> p.registerProcessVariable(e.getAttribute("target")));

            ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "output").stream().forEach(e -> {
                String expression = feel.translateChecked(e.getAttribute("source").substring(1));
                Matcher matcher = INPUT_PATTERN.matcher(expression);
                if (matcher.matches()) {
                    p.registerInput(expression);
                }
            });
            result.append(ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "output").stream()
                    .map(e -> e.getAttribute("target") + "=" + feel.translateChecked(e.getAttribute("source").substring(1)))
                    .collect(Collectors.toList()));

            if (opt.isDebug()) {
                result.append(ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "output").stream()
                        .map(e -> "BPMNExecProcessUtils.debugOutput(\"\t ASSIGNING " + e.getAttribute("target") + " TO %s\"," + feel.translateChecked(e.getAttribute("source").substring(1)) + ")")
                        .collect(Collectors.toList()));
            }
        }
        return result;
    }

    //    protected Code generateFlowJointCode(BPMNDecodedProcess p, String flowName, Options opt) {
//        Code code = new Code(flowName + "(s)");
//        return code;
//    }
//    //generates the code to call a function, registering it if needed
//    private Code generateProcedureCallCode(BPMNDecodedProcess p, FunctionDefinition proc) {
//        return new Code(sanitizeName(proc.name()) + "()");
//
//    }
}
