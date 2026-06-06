package dellapenna.personal.bpmn.bpmn;

import static dellapenna.personal.bpmn.exec.BPMNExecProcessUtils.ProcessStatus.BoundaryRole.BOUNDARYWATCHER;
import static dellapenna.personal.bpmn.exec.BPMNExecProcessUtils.ProcessStatus.BoundaryRole.NORMAL;
import dellapenna.personal.bpmn.versim.Assertion;
import dellapenna.personal.bpmn.feel.FeelTranslationInfo;
import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import dellapenna.personal.bpmn.feel.ToJavaFeelTranslator;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
import org.camunda.bpm.model.bpmn.instance.IntermediateCatchEvent;
import org.camunda.bpm.model.bpmn.instance.ManualTask;
import org.camunda.bpm.model.bpmn.instance.MessageEventDefinition;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.ReceiveTask;
import org.camunda.bpm.model.bpmn.instance.ScriptTask;
import org.camunda.bpm.model.bpmn.instance.SendTask;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.SignalEventDefinition;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.bpmn.instance.TimerEventDefinition;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

/**
 *
 * @author Giuseppe Della Penna
 */
public class ToJavaBPMNTranslator extends AbstractBPMNTranslator<String> {

    private final static String ZEEBENS = "http://camunda.org/schema/zeebe/1.0";
    private static final ToJavaFeelTranslator feel = new ToJavaFeelTranslator();
    public static final String EXECUTILEXPRESSION = "BPMNExecProcessUtils";

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

    private String generateDebugOutputStament(String s, Object... args) {
        String message = String.format(s, args);
        return EXECUTILEXPRESSION + ".debugOutput(s,\"" + message + "\")";
    }

    private String generateDummyWorkloadStatement(BPMNTranslationInfo info) {
        if (info.isDummyWorkload()) {
            return "try {Thread.sleep(10);} catch (InterruptedException ex)  {Thread.currentThread().interrupt();}";
        } else {
            return "//do something";
        }
    }

    private Code generateTransitionDescriptionStaments(FlowNode source, FlowNode target, BPMNTranslationInfo info) {
        Code code = new Code<String>();
        code.append("//[outgoing edge] " + target.getId() + ((target.getName() != null && !target.getName().isBlank()) ? (" - " + target.getName()) : ""));
        if (info != null && info.isDebug()) {
            code.append(EXECUTILEXPRESSION + ".logTransition(\"" + source.getId() + "\",\"" + target.getId() + "\")");
        }
        return code;
    }

    private Code generateCommonNodeEntryStaments(FlowNode n, BPMNTranslationInfo info) {
        Code code = new Code<String>();
        String description = getNodeDescription(n);
        code.append("//" + description);
        if (info != null && info.isDebug()) {
            code.append(generateDebugOutputStament(description));
            code.append(EXECUTILEXPRESSION + ".logCurrentNode(\"" + n.getId() + "\"," + (n.getName() != null ? ("\"" + n.getName() + "\"") : "null") + ")");
        }

        if (info != null && !info.getGlobalAssertions().isEmpty()) {
            code.append("globalAssert(s,\"" + n.getId() + "\")");
        }
        return code;
    }

    private Code generateCommonNodeExitStatements(FlowNode n, BPMNTranslationInfo info) {
        Code code = new Code<String>();
        return code;
    }

    //generates the code to capture the output of a node, as a set of variable assignments
    private Code generateOutputAssignmentsStatements(BPMNDecodedProcess p, FlowNode t, List<String> localVariables, boolean readInput, BPMNTranslationInfo info) {
        Code result = new Code<String>();

        ModelElementInstance ioMapping = t.getExtensionElements() != null ? t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "ioMapping") : null;
        if (ioMapping != null) {
            ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "output").stream().forEach(e -> {
                FeelTranslationInfo v_f_info = new FeelTranslationInfo();
                v_f_info.setGenerateGetters(true); //generate getters
                v_f_info.setGenerateReadNextGetters(readInput); //maybe reads next
                v_f_info.setExcludeGetters(localVariables); //exclude local variables from getters and process variable gathering
                String assigned_variable = e.getAttribute("target");
                //declare output (written) variables, if not local
                if (!isVariableIncluded(assigned_variable, localVariables)) {
                    p.registerProcessVariable(assigned_variable, BPMNDecodedProcess.VariableDirection.WRITE, t.getId(), null);
                }
                String assigned_expression = e.getAttribute("source").substring(1);
                result.append("set" + assigned_variable.substring(0, 1).toUpperCase() + assigned_variable.substring(1) + "(" + feel.translateChecked(assigned_expression, v_f_info) + ")");
                if (info != null && info.isDebug()) {
                    result.append(EXECUTILEXPRESSION + ".debugOutput(s,\"\t ASSIGNED " + assigned_variable + " TO %s\"," + assigned_variable + ")");
                }
                //declare source (read) variables, if not local
                p.registerProcessVariables(v_f_info.getUsedVariableNames().stream()
                        .map(l -> String.join(".", l))
                        .filter(v -> !isVariableIncluded(v, localVariables))
                        .toList(),
                        BPMNDecodedProcess.VariableDirection.READ, t.getId(), assigned_expression);
            });
        }

        /* trick: emulate a read on datainputs to declare them as input parameters */
        if (t instanceof UserTask a) {
            p.registerProcessVariables(a.getDataOutputAssociations().stream()
                    .map(oa -> oa.getTarget().getAttributeValue("name"))
                    .filter(v -> !isVariableIncluded(v, localVariables))
                    .toList(),
                    BPMNDecodedProcess.VariableDirection.READ, t.getId(), null);
        } else if (t instanceof StartEvent a) {
            p.registerProcessVariables(a.getDataOutputAssociations().stream()
                    .map(oa -> oa.getTarget().getAttributeValue("name"))
                    .filter(v -> !isVariableIncluded(v, localVariables))
                    .toList(),
                    BPMNDecodedProcess.VariableDirection.READ, t.getId(), null);
        } else if (t instanceof Task g && !(t instanceof BusinessRuleTask)) { //data outputs of generic tasks also addressed?
            p.registerProcessVariables(g.getDataOutputAssociations().stream()
                    .map(oa -> oa.getTarget().getAttributeValue("name"))
                    .filter(v -> !isVariableIncluded(v, localVariables))
                    .toList(),
                    BPMNDecodedProcess.VariableDirection.READ, t.getId(), null);
        }

        return result;
    }

    private String getNodeDescription(FlowNode n) {
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
            case IntermediateCatchEvent t ->
                "Intermediate catch event";
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

    ///////////////////////////////////////////////////////////////////
    
    //generates the source code for a given function definition
    private String generateFunctionSource(FunctionDefinition<String> f) {
        return "public " + f.returnType() + " " + f.name() + "("
                + f.parameters().entrySet().stream().map(e -> e.getValue() + " " + e.getKey()).collect(Collectors.joining(", "))
                + ") {" + generateCodeSource(f.body()) + "}";
    }

    //generates the source code for a code block    
    private String generateCodeSource(Code code) {
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

    //generates the source code to declare the process variables and their support methods
    private String generateProcessVariableDefinitionSource(BPMNDecodedProcess process, BPMNTranslationInfo info) {
        String global_variables = process.getBoundVariables(info).stream()
                .sorted((v1, v2) -> v1.getName().compareTo(v2.getName()))
                .map(v
                        -> "// READ: " + v.getUsages(BPMNDecodedProcess.VariableDirection.READ).stream().map(u -> u.sourceId()).distinct().collect(Collectors.joining(", "))
                + "\n// WRITTEN: " + v.getUsages(BPMNDecodedProcess.VariableDirection.WRITE).stream().map(u -> u.sourceId()).distinct().collect(Collectors.joining(", "))
                + "\nprivate Object " + v.getName() + "=null;"
                + "\npublic Object get" + v.getName().substring(0, 1).toUpperCase() + v.getName().substring(1) + "(" + EXECUTILEXPRESSION + ".ProcessStatus s, boolean readNext) {return this." + v.getName() + "; }"
                + "\npublic void set" + v.getName().substring(0, 1).toUpperCase() + v.getName().substring(1) + "(Object _value) {this." + v.getName() + "=_value; }"
                )
                .collect(Collectors.joining("\n", "\n\n//Process Variables\n", "\n"));

        //process.getFreeVariables().stream().forEach(v->vu.analyzeInputConstraints(v, dmns, info));
        String input_variables = process.getFreeVariables(info).stream()
                .sorted((v1, v2) -> v1.getName().compareTo(v2.getName()))
                .map(v
                        -> "// READ: " + v.getUsages(BPMNDecodedProcess.VariableDirection.READ).stream().map(u -> u.sourceId()).distinct().collect(Collectors.joining(", "))
                + "\n// WRITTEN: " + v.getUsages(BPMNDecodedProcess.VariableDirection.WRITE).stream().map(u -> u.sourceId()).distinct().collect(Collectors.joining(", "))
                //+ "\n// CONSTRAINTS: " + v.getBounds()
                + "\nprivate Object " + v.getName() + "=null;"
                + "\nprivate final java.util.ArrayDeque<Object> " + v.getName() + "_stream=new java.util.ArrayDeque<>();"
                + "\npublic Object get" + v.getName().substring(0, 1).toUpperCase() + v.getName().substring(1) + "(" + EXECUTILEXPRESSION + ".ProcessStatus s, boolean readNext) {"
                + "Object current = this." + v.getName() + ";"
                + "if (readNext && !this." + v.getName() + "_stream.isEmpty()) {"
                + "this." + v.getName() + " = this." + v.getName() + "_stream.pop();"
                + generateDebugOutputStament("\t READING next input value for " + v.getName()) + ";"
                + "}"
                + "return current;"
                + "}"
                )
                .collect(Collectors.joining("\n", "\n\n//Input Variables\n", "\n"));

        return input_variables + global_variables;
    }

    //generates the source code to declare the process message classes
    private String generateProcessMessageDefinitionsSource(BPMNDecodedProcess process, BPMNTranslationInfo info) {
        return process.getMessages().stream()
                .sorted((m1, m2) -> m1.getName().compareTo(m2.getName()))
                .map(m -> "private static class " + "Message_" + m.getName() + " implements " + EXECUTILEXPRESSION + ".Message {"
                + m.getParts().stream()
                        .sorted((p1, p2) -> p1.compareTo(p2))
                        .map(p -> "\nObject " + p + "=null;"
                        ).collect(Collectors.joining(";\n"))
                + "}").collect(Collectors.joining(";\n", "\n\n//Messages\n", ";\n\n"));
    }

    //generates the source code to declare the process class methods
    private String generateProcessFunctionSource(BPMNDecodedProcess process, BPMNTranslationInfo info) {
        return process.getFunctions().values().stream()
                .flatMap(fc -> fc.values().stream())
                .sorted((fd1, fd2) -> fd1.name().compareTo(fd2.name()))
                .map(fd -> generateFunctionSource(fd))
                .collect(Collectors.joining("\n\n", "\n\n//Process Dynamics\n", "\n\n"));
    }

    //generates the code for a global assertion (experimental)
    protected Code generateGlobalAssertionCode(Assertion a, BPMNTranslationInfo info) {
        Code code = new Code<String>();
        code.append(EXECUTILEXPRESSION + ".assertion(s,node_id,\"" + a.description() + "\",(" + feel.translateChecked(a.expression(), null) + "))");
        return code;
    }

    //generates the code for a local assertion (experimental)
    protected Code generateLocalAssertionCode(FlowNode current, Assertion a, BPMNTranslationInfo info) {
        Code code = new Code<String>();
        code.append(EXECUTILEXPRESSION + ".assertion(s,\"" + current.getId() + "\",\"" + a.description() + "\",(" + feel.translateChecked(a.expression(), null) + "))");
        return code;
    }

    //generates the source code for the global assertion checking method (experimental)
    private String generateProcessGlobalAssertionsSource(BPMNDecodedProcess process, BPMNTranslationInfo info) {
        String source = "boolean success=true;\n\n";
        if (info != null) {
            for (Assertion a : info.getGlobalAssertions()) {
                source += "success |= " + generateCodeSource(generateGlobalAssertionCode(a, info));
            }
        }
        return "public boolean globalAssert(" + EXECUTILEXPRESSION + ".ProcessStatus s, String node_id) {\n"
                + source
                + "return success;\n"
                + "\n}";
    }

    //generates the source code for the variables initialization method 
    private String generateProcessInitSource(BPMNDecodedProcess process, BPMNTranslationInfo info) {
        String source = "";
        source += process.getFreeVariables(info).stream()
                .map(v -> "if (this." + v.getName() + "_stream.isEmpty()) "
                + "java.util.Arrays.stream(" + EXECUTILEXPRESSION + ".inputs.getProperty(\"" + v.getName() + "\", \"\").split(\",\")).forEach(i->this." + v.getName() + "_stream.addLast(i));\n"
                + EXECUTILEXPRESSION + ".logInput(\"" + v.getName() + "\",this." + v.getName() + "_stream);\n"
                + "this." + v.getName() + " = this." + v.getName() + "_stream.pop();\n")
                //+ EXECUTILEXPRESSION + ".debugOutput(\"Input stream for variable" + v.getName() + " is %s\"," + v.getName() + "_stream);\n"

                .collect(Collectors.joining());
        return "public void init() {\n" + source + "\n}";
    }

    //generates the  source code  for the process execution method
    private String generateProcessEntryMethod(BPMNDecodedProcess process, BPMNTranslationInfo info) {
        String source = "public void execute(";
        source += process.getFreeVariables(info).stream().map(v -> "Object[] _" + v.getName() + "_stream").collect(Collectors.joining(","));
        source += ") {";
        source += process.getFreeVariables(info).stream()
                .map(v
                        -> "if (_" + v.getName() + "_stream != null)"
                + "java.util.Arrays.stream(_" + v.getName() + "_stream).forEach(i->this." + v.getName() + "_stream.addLast(i));\n"
                )
                .collect(Collectors.joining());
        if (!process.getStartEventFlowNames().isEmpty()) {
            source += EXECUTILEXPRESSION + ".executeProcess(\"" + sanitizeName(process.getName()) + "\",this::init,this::" + sanitizeName(process.getStartEventFlowNames().getFirst()) + ");\n";
        }
        source += "}";
        return source;
    }

    //generates the source code for the process main method
    private String generateProcessMainSource(BPMNDecodedProcess process, BPMNTranslationInfo info) {
        String source = "public static void main(String[] args) {\n";
        if (info != null && info.isTrace()) {
            source += EXECUTILEXPRESSION + ".setExternalTraceFile(\"" + sanitizeName(process.getName()) + "\");";
        }
        if (info == null || info.isTrueParallel()) {
            source += EXECUTILEXPRESSION + ".enableTrueParallel();";
        }
        source += /*"bpmn_process_" +*/ sanitizeName(process.getName()) + " process = new " /*+ "bpmn_process_"*/ + sanitizeName(process.getName()) + "();\n";
        source += "process.execute("
                + process.getFreeVariables(info).stream().map(v -> "null" + "/*" + v.getName() + "*/").collect(Collectors.joining(","))
                + ");";
        source += "}";
        return source;
    }

    //generate the common code for all the joining gateways
    private Code generateJoiningGatewayCode(BPMNDecodedProcess p, Gateway g, FlowNode j, BPMNTranslationInfo info) throws FeelTranslatorException {
        Code code = new Code<String>(generateCommonNodeEntryStaments(g, info));
        code.append(generateTransitionCode(p, g, j, info));
        code.append(generateCommonNodeExitStatements(g, info));
        return code;
    }

    /////////////////////////////////////////////////////////////////////
    //generates the source code for a complete BPMN given the code of its single processes (currently unused)    
    @Override
    public String generateBpmnSource(BPMNDecodedModel bpmn, BPMNTranslationInfo info) {
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
    @Override
    public String generateProcessSource(BPMNDecodedProcess process, BPMNTranslationInfo info) {
        return """
               /*
                * ****************************** Process Code *************************
                */
               """
                + " class " + sanitizeName(process.getName()) + " { "
                + generateProcessVariableDefinitionSource(process, info)
                + generateProcessMessageDefinitionsSource(process, info)
                + generateProcessFunctionSource(process, info)
                + generateProcessInitSource(process, info)
                + generateProcessGlobalAssertionsSource(process, info)
                + generateProcessEntryMethod(process, info)
                + generateProcessMainSource(process, info)
                + "}";
    }

    /////////////////////////////////////////////////////////////////////
    // Generation code for specific BPMN nodes    
    //TASKS
    
    
    
    @Override
    public Code generateGenericTaskCode(BPMNDecodedProcess p, Task t, BPMNTranslationInfo info) {
        Code code = new Code(generateCommonNodeEntryStaments(t, info));

        code.append(generateDummyWorkloadStatement(info));

        code.append(generateOutputAssignmentsStatements(p, t, Collections.EMPTY_LIST, false, info)); //no read next
        code.append(generateCommonNodeExitStatements(t, info));
        return code;
    }

    @Override
    public Code generateManualTaskCode(BPMNDecodedProcess p, ManualTask t, BPMNTranslationInfo info) {
        Code code = new Code<String>(generateCommonNodeEntryStaments(t, info));

        code.append(generateDummyWorkloadStatement(info));

        code.append(generateCommonNodeExitStatements(t, info));
        return code;
    }

    @Override
    public Code generateScriptTaskCode(BPMNDecodedProcess p, ScriptTask t, BPMNTranslationInfo info) throws FeelTranslatorException {
        Code code = new Code<String>(generateCommonNodeEntryStaments(t, info));
        Code outs = generateOutputAssignmentsStatements(p, t, Collections.EMPTY_LIST, false, info); //no read next
        code.append(outs);

        ModelElementInstance script = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "script");
        if (script != null) {
            String resultVariable = script.getAttributeValue("resultVariable");
            String expression = script.getAttributeValue("expression");
            FeelTranslationInfo f_info = new FeelTranslationInfo();
            f_info.setGenerateGetters(true); //generate getters, no read next
            String assigned_expression = expression.substring(1);
            code.append("set" + resultVariable.substring(0, 1).toUpperCase() + resultVariable.substring(1) + "(" + feel.translate(assigned_expression, f_info) + ")");
            if (info != null && info.isDebug()) {
                code.append(EXECUTILEXPRESSION + ".debugOutput(s,\"\t ASSIGNED " + resultVariable + " TO %s\"," + resultVariable + ")");
            }
            p.registerProcessVariable(resultVariable, BPMNDecodedProcess.VariableDirection.WRITE, t.getId(), null);
            p.registerProcessVariables(f_info.getUsedVariableNames(), BPMNDecodedProcess.VariableDirection.READ, t.getId(), assigned_expression);
        }
        code.append(generateCommonNodeExitStatements(t, info));
        return code;
    }

    @Override
    public Code generateServiceTaskCode(BPMNDecodedProcess p, ServiceTask t, BPMNTranslationInfo info) {
        Code code = new Code<String>(generateCommonNodeEntryStaments(t, info));

        code.append(generateDummyWorkloadStatement(info));

        code.append(generateCommonNodeExitStatements(t, info));
        return code;
    }

    @Override
    public Code generateSendTaskCode(BPMNDecodedProcess p, SendTask t, BPMNTranslationInfo info) {
        Code code = new Code<String>(generateCommonNodeEntryStaments(t, info));
        String channel_name = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "taskHeaders")
                .getDomElement().getChildElementsByNameNs(ZEEBENS, "header").stream()
                .filter(e -> "channel".equals(e.getAttribute("key")))
                .map(e -> e.getAttribute("value"))
                .findAny().orElse(null);
        String message_name = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "taskHeaders")
                .getDomElement().getChildElementsByNameNs(ZEEBENS, "header").stream()
                .filter(e -> "message".equals(e.getAttribute("key")))
                .map(e -> e.getAttribute("value"))
                .findAny().orElse(null);

        MessageDefinition message = p.registerProcessMessage(message_name);
        code.append("Message_" + message.getName() + " m = new " + "Message_" + message.getName() + "()");

        ModelElementInstance ioMapping = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "ioMapping");
        if (ioMapping != null) {
            ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "input").stream().forEach(e -> {
                String part_name = e.getAttribute("target");
                message.getParts().add(part_name);
                FeelTranslationInfo v_f_info = new FeelTranslationInfo();
                v_f_info.setGenerateGetters(true); //generate getters, no read next
                String assigned_expression = e.getAttribute("source").substring(1);
                code.append("m." + part_name + " = " + feel.translateChecked(assigned_expression, v_f_info));
                p.registerProcessVariables(v_f_info.getUsedVariableNames(), BPMNDecodedProcess.VariableDirection.READ, t.getId(), assigned_expression);
            });
        }
        code.append(generateDebugOutputStament("\t SENDING message on channel " + channel_name));
        code.append(EXECUTILEXPRESSION + ".sendMessage(s,\"" + channel_name + "\",m)");

        code.append(generateCommonNodeExitStatements(t, info));
        return code;
    }

    @Override
    public Code generateReceiveTaskCode(BPMNDecodedProcess p, ReceiveTask t, BPMNTranslationInfo info) {
        Code code = new Code<String>(generateCommonNodeEntryStaments(t, info));
        String channel_name = t.getMessage().getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "subscription")
                .getAttributeValue("correlationKey").substring(1);
        String message_name = t.getMessage().getName();

        if (!message_name.equalsIgnoreCase("passthrough")) {
            //MessageDefinition message = p.registerProcessMessage(message_name);
            code.append(generateDebugOutputStament("\t RECEIVING message on channel " + channel_name));
            code.append("Message_" + message_name + " receivedMessage;");
            code.append("try { "
                    + "receivedMessage = (" + "Message_" + message_name + ")" + EXECUTILEXPRESSION + ".receiveMessage(s,\"" + channel_name + "\");"
                    + "} catch (InterruptedException ex) {" + EXECUTILEXPRESSION + ".timeoutError(s); receivedMessage = null; }");
        } else {
            code.append(generateDebugOutputStament("\t ASSUMING RECEPTION of message on channel " + channel_name));
        }

        code.append(generateOutputAssignmentsStatements(p, t, List.of("receivedMessage"), false, info)); //no read next

        code.append(generateCommonNodeExitStatements(t, info));
        return code;
    }

    @Override
    public Code generateUserTaskCode(BPMNDecodedProcess p, UserTask t, BPMNTranslationInfo info) {
        Code code = new Code<String>(generateCommonNodeEntryStaments(t, info));

        code.append(generateDummyWorkloadStatement(info));

        code.append(generateOutputAssignmentsStatements(p, t, Collections.EMPTY_LIST, true, info)); //read next
        code.append(generateCommonNodeExitStatements(t, info));
        return code;
    }

    @Override
    public Code generateBusinessRuleTaskCode(BPMNDecodedProcess p, BusinessRuleTask t, BPMNTranslationInfo info) {
        Code code = new Code<String>(generateCommonNodeEntryStaments(t, info));
        if (info != null && info.isDebug()) {
            code.append(generateDebugOutputStament("\t EXECUTING DECISION " + (t.getName() != null ? t.getName() : t.getId())));
        }

        ModelElementInstance ioMapping = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "ioMapping");
        ModelElementInstance calledDecision = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "calledDecision");

        String tableId = calledDecision.getAttributeValue("decisionId");
        String tableClassName = sanitizeName("dmn_dtable_" + tableId);
        String resultClassName = tableClassName + "_result";
        String argumentsClassName = tableClassName + "_arguments";

        code.append(argumentsClassName + " args = new " + argumentsClassName + "()");
        //FeelTranslationInfo f_info = new FeelTranslationInfo();
        ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "input").stream().forEach(e -> {
            String input_name = e.getAttribute("target");
            FeelTranslationInfo v_f_info = new FeelTranslationInfo();
            v_f_info.setGenerateGetters(true); //generate getters, no read next
            String assigned_expression = e.getAttribute("source").substring(1);
            code.append("args." + input_name + " = " + feel.translateChecked(assigned_expression, v_f_info));
            p.registerProcessVariables(v_f_info.getUsedVariableNames(), BPMNDecodedProcess.VariableDirection.READ, t.getId(), assigned_expression);
            //registrazione ad-hoc per le tabelle DMN
            p.registerProcessVariables(v_f_info.getUsedVariableNames(), BPMNDecodedProcess.VariableDirection.READ, "$DMN$" + tableId + "$" + input_name, assigned_expression);
            //f_info.getUsedVariableNames().addAll(v_f_info.getUsedVariableNames());
        });

        code.append(resultClassName + " " + calledDecision.getAttributeValue("resultVariable") + "=" + tableClassName + ".execute(args" + ")");

        //p.registerProcessVariable(calledDecision.getAttributeValue("resultVariable"), BPMNDecodedProcess.VariableDirection.WRITE); //locale
        //p.registerProcessVariables(f_info.getUsedVariableNames(), BPMNDecodedProcess.VariableDirection.READ, t.getId());
        if (info != null && info.isDebug()) {
            code.append(EXECUTILEXPRESSION + ".debugOutput(s,\"\t DECISION RESULT IS %s\"," + calledDecision.getAttributeValue("resultVariable") + ")");
        }

        code.append(generateOutputAssignmentsStatements(p, t, List.of(calledDecision.getAttributeValue("resultVariable")), false, info)); //no read next

        code.append(generateCommonNodeExitStatements(t, info));
        return code;
    }

    //EVENTS
    @Override
    public Code generateEndEventCode(BPMNDecodedProcess p/*UNUSED*/, EndEvent t, BPMNTranslationInfo info) {
        Code code = new Code<String>(generateCommonNodeEntryStaments(t, info));
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
                code.append(EXECUTILEXPRESSION + ".error(s,\"" + error_message + "\", " + error_code + ")");
            }
        }
        if (isSuccess) {
            code.append(EXECUTILEXPRESSION + ".success(s)");
        }

        code.append(generateTransitionCode(p, t, null, info));
        code.append(generateCommonNodeExitStatements(t, info));
        return code;
    }

    @Override
    public Code generateStartEventCode(BPMNDecodedProcess p, StartEvent t, BPMNTranslationInfo info) {
        Code code = new Code<String>(generateCommonNodeEntryStaments(t, info));
        code.append(generateOutputAssignmentsStatements(p, t, Collections.EMPTY_LIST, true, info)); //read next
        p.registerStartEventFlowName(sanitizeName(p.getFlowName(t)));
        code.append(generateCommonNodeExitStatements(t, info));
        return code;
    }

    //OTHER EVENTS HELPERS
    private Code generateMessageEventCatchCode(BPMNDecodedProcess p, MessageEventDefinition me, BPMNTranslationInfo info, boolean live, String action) throws BpmnTranslatorException {
        Code code = new Code();
        String channel_name = me.getMessage().getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "subscription").getAttributeValue("correlationKey").substring(1);
        String message_name = me.getMessage().getName();

        //live wait mode (if using this event in parallel with others) MUST BE COMPLETED!
        code.append("Message_" + message_name + " receivedMessage;");
        if (!message_name.equalsIgnoreCase("passthrough")) {
            MessageDefinition message = p.registerProcessMessage(message_name);
            code.append(generateDebugOutputStament("\t RECEIVING message on channel " + channel_name));
            String check_source = "receivedMessage = (" + "Message_" + message_name + ")" + EXECUTILEXPRESSION + ".receiveMessage(s,\"" + channel_name + "\""
                    + ((!live) ? ");" : ",50,false);");
            code.append("try { " + check_source + "} catch (InterruptedException ex)  {receivedMessage=null; Thread.currentThread().interrupt();}");
            if (action != null) {
                code.append("if (receivedMessage != null)" + action); //DOES NOT WORK FOR BOUNDARY (live) MODE... MUST EXIT ALSO IF isInterrupted
            }
        } else {
            code.append(generateDebugOutputStament("\t ASSUMING RECEPTION of message on channel " + channel_name));
        }

        return code;
    }

    private Code generateTimerEventCatchCode(BPMNDecodedProcess p, TimerEventDefinition te, BPMNTranslationInfo info, boolean live, String action) throws BpmnTranslatorException {
        Code code = new Code();
        if (te.getTimeDuration() != null) {
            long waitTime = Duration.parse(te.getTimeDuration().getTextContent()).toMillis();
            if (!live) {
                code.append("try { Thread.sleep(" + waitTime + "); } catch (InterruptedException ex)  {Thread.currentThread().interrupt();}");
            } else {
                //live wait mode (if using this event in parallel with others)... 
                //TO BE REWRITTEN (while loop should not be here and timer init sould be at the beginning of the multiple check block)
                code.append("long enter_time = System.currentTimeMillis()");
                code.append("while (!Thread.currentThread().isInterrupted()) {if (System.currentTimeMillis() - enter_time >= " + waitTime + ") break; }");
            }
        } else {
            throw new BpmnTranslatorException("Cannot translate a timer event of this type");
        }
        code.append(generateDebugOutputStament("\t TIMER HIT"));
        return code;
    }

// TO BE COMPLETED    
//    private Code generateSignalEventCatchCode(BPMNDecodedProcess p, SignalEventDefinition ts, BPMNTranslationInfo info, boolean live) throws BpmnTranslatorException {
//        Code code = new Code();
//        String signal_name = ts.getSignal().getName();
//        if (!signal_name.equalsIgnoreCase("passthrough")) {
//            SignalDefinition signal = p.registerProcessSignal(signal_name);
//            code.append(generateDebugOutputStament("\t CHECKING for signal " + signal_name));
//            if (!live) {
//                code.append("try {" + EXECUTILEXPRESSION + ".checkSignal(s,\"" + signal_name + "\"); } catch (InterruptedException ex)  {Thread.currentThread().interrupt();}");
//            } else {
//                code.append("try {" + EXECUTILEXPRESSION + ".checkSignal(s,\"" + signal_name + "\",50,false); } catch (InterruptedException ex)  {Thread.currentThread().interrupt();}");
//            }
//        } else {
//            code.append(generateDebugOutputStament("\t ASSUMING signal " + signal_name));
//        }
//        return code;
//    }
    //INTERMEDIATE CATCH EVENTS
    @Override
    protected Code generateIntermediateCatchEventCode(BPMNDecodedProcess p, IntermediateCatchEvent t, BPMNTranslationInfo info) throws BpmnTranslatorException {
        Code code = new Code<String>(generateCommonNodeEntryStaments(t, info));

        EventDefinition e = t.getEventDefinitions().iterator().next(); //should be only one!
        if (e instanceof MessageEventDefinition me) {
            code.append(generateMessageEventCatchCode(p, me, info, false, null));
        } else if (e instanceof TimerEventDefinition te) {
            code.append(generateTimerEventCatchCode(p, te, info, false, null));
        } else if (e != null) {
            throw new BpmnTranslatorException("Cannot translate event of type " + e.getClass().getName());
        } else {
            throw new BpmnTranslatorException("Cannot translate catch event without events");
        }
        code.append(generateCommonNodeExitStatements(t, info));
        return code;
    }

    //BOUNDARY EVENTS    
    @Override
    public Code generateBoundaryDispatcherCode(BPMNDecodedProcess p, FlowNode ownerNode, BPMNTranslationInfo info) {
        Code code = new Code();
        code.append(EXECUTILEXPRESSION + ".forkBoundary(s, \"" + ownerNode.getId() + "\",this::" + sanitizeName(p.getFlowName(ownerNode, NORMAL.toString())) + ",this::" + sanitizeName(p.getFlowName(ownerNode, BOUNDARYWATCHER.toString())) + ")");
        code.append(EXECUTILEXPRESSION + ".stopThread()");
        return code;
    }

    @Override
    public Code generateBoundaryEventsCode(BPMNDecodedProcess p, FlowNode ownerNode, List<BPMNDecodedBoundaryFlow> boundaryFlows, BPMNTranslationInfo info) {
        Code code = new Code();
        String boundary_flows_code = boundaryFlows.stream()
                .map(bf -> {
                    EventDefinition e = bf.event().getEventDefinitions().iterator().next(); //should be only one!
                    String event_source = "";
                    if (e instanceof MessageEventDefinition me) {
                        String channel_name = me.getMessage().getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "subscription").getAttributeValue("correlationKey").substring(1);
                        String message_name = me.getMessage().getName();
                        MessageDefinition message = p.registerProcessMessage(message_name);
                        //event_source += generateDebugOutputStament("\t CHECKING message on channel " + channel_name) + ";";
                        if (!message_name.equalsIgnoreCase("passthrough")) {
                            event_source += "Message_" + message_name + " receivedMessage = (" + "Message_" + message_name + ")" + EXECUTILEXPRESSION + ".receiveMessage(s,\"" + channel_name + "\",50,false);"
                                    + "if (receivedMessage != null)";
                        } else {
                            event_source += generateDebugOutputStament("\t ASSUMING RECEPTION of message on channel " + channel_name) + ";";
                        }
                        //code.append(generateOutputAssignmentsStatements(p, t, List.of("receivedMessage"), false, info)); //no read next
                    }
                    event_source += "{"
                            + "if (!Thread.currentThread().isInterrupted()) { "
                            + generateDebugOutputStament("HIT BOUNDARY EVENT " + bf.event().getName() + " ON " + getNodeDescription(ownerNode)) + ";";
                    if (bf.event().cancelActivity()) { //interrupting
                        event_source += EXECUTILEXPRESSION + ".killBoundary(s);\n"
                                + generateCodeSource(generateTransitionCode(p, ownerNode, bf.firstStep(), info))
                                + "}\n break;";
                    } else { //not interrupting
                        p.registerDecodedEdge(ownerNode, bf.firstStep());
                        event_source += generateCodeSource(generateTransitionDescriptionStaments(ownerNode, bf.firstStep(), info))
                                + EXECUTILEXPRESSION + ".fork(s.withResetBoundary()," + "s.boundaryID," + "this::" + sanitizeName(p.getFlowName(bf.firstStep())) + ");\n"
                                + "}\n";
                    }
                    event_source += "}";
                    return event_source;
                }).collect(Collectors.joining("\n"));

        code.set("try{ while(!Thread.currentThread().isInterrupted()) {" + boundary_flows_code + "} } catch (Exception e) {"
                + "if (e instanceof InterruptedException)  Thread.currentThread().interrupt();"
                + "else throw (e instanceof RuntimeException re ? re : new RuntimeException(e));"
                + "}");
        return code;
    }

    @Override
    public Code generateBoundaryNormalCompletionCode(BPMNDecodedProcess p, FlowNode ownerNode, Code originalCode, List<BPMNDecodedBoundaryFlow> boundaryFlows, BPMNTranslationInfo info) {
        Code code = new Code("if (!Thread.currentThread().isInterrupted()) " + EXECUTILEXPRESSION + ".killBoundary(s)");
        return code;
    }

    @Override
    public void finalizeBoundaryNormalCode(BPMNDecodedProcess p, FlowNode ownerNode, Code code, BPMNTranslationInfo info) {
        String guarded_source = "try{\n " + generateCodeSource(code) + "} catch (Exception e) {"
                + "if (e instanceof InterruptedException)  Thread.currentThread().interrupt();"
                + "else throw (e instanceof RuntimeException re ? re : new RuntimeException(e));"
                + "}";
        code.set(guarded_source);
    }

    // GATEWAYS
    @Override
    public Code generateParallelJoiningGatewayCode(BPMNDecodedProcess p, ParallelGateway g, FlowNode joinedflow, BPMNTranslationInfo info) throws BpmnTranslatorException, FeelTranslatorException {
        Code code = new Code<String>(generateCommonNodeEntryStaments(g, info));
        p.registerDecodedEdge(g, joinedflow);

        code.append(generateTransitionDescriptionStaments(g, joinedflow, info));

        code.append("//JOINS: " + g.getIncoming().stream().map(s -> s.getSource().getId()).collect(Collectors.joining(",")));
        code.append(EXECUTILEXPRESSION + ".join(s,\"" + g.getId() + "\", " + ("this::" + sanitizeName(p.getFlowName(joinedflow))) + ")");

        code.append(generateCommonNodeExitStatements(g, info));
        return code;
    }

    //same as generateParallelJoiningGatewayCode, code must be merged with appropriate generic parameter types
    @Override
    public Code generateInclusiveJoiningGatewayCode(BPMNDecodedProcess p, InclusiveGateway g, FlowNode joinedflow, BPMNTranslationInfo info) throws BpmnTranslatorException, FeelTranslatorException {
        Code code = new Code<String>(generateCommonNodeEntryStaments(g, info));
        p.registerDecodedEdge(g, joinedflow);
        code.append(generateTransitionDescriptionStaments(g, joinedflow, info));
        code.append("//JOINS: " + g.getIncoming().stream().map(s -> s.getSource().getId()).collect(Collectors.joining(",")));
        code.append(EXECUTILEXPRESSION + ".join(s,\"" + g.getId() + "\", " + ("this::" + sanitizeName(p.getFlowName(joinedflow))) + ")");

        code.append(generateCommonNodeExitStatements(g, info));
        return code;
    }

    @Override
    public Code generateExclusiveJoiningGatewayCode(BPMNDecodedProcess p, ExclusiveGateway g, FlowNode joinedflow, BPMNTranslationInfo info) throws BpmnTranslatorException, FeelTranslatorException {
        Code code = generateJoiningGatewayCode(p, g, joinedflow, info);
        return code;
    }

    @Override
    public Code generateEventJoiningGatewayCode(BPMNDecodedProcess p, EventBasedGateway g, FlowNode joinedflow, BPMNTranslationInfo info) throws BpmnTranslatorException, FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Code generateParallelGatewayCode(BPMNDecodedProcess p, ParallelGateway g, List<BPMNDecodedConditionalFlow> splitFlows, BPMNTranslationInfo info) throws FeelTranslatorException {
        Code code = new Code<String>(generateCommonNodeEntryStaments(g, info));
        String[] branch_functions = new String[splitFlows.size()];
        for (int o = 0; o < splitFlows.size(); ++o) {
            p.registerDecodedEdge(g, splitFlows.get(o).firstStep());

            code.append(generateTransitionDescriptionStaments(g, splitFlows.get(o).firstStep(), info));
            branch_functions[o] = "this::" + sanitizeName(p.getFlowName(splitFlows.get(o).firstStep()));
        }
        code.append("//FORKS: " + splitFlows.stream().map(s -> s.firstStep().getId()).collect(Collectors.joining(",")));
        code.append(EXECUTILEXPRESSION + ".fork(s,\"" + g.getId() + "\"," + String.join(",", branch_functions) + ")");
        code.append(EXECUTILEXPRESSION + ".stopThread()");

        code.append(generateCommonNodeExitStatements(g, info));
        return code;
    }

    @Override
    public Code generateInclusiveGatewayCode(BPMNDecodedProcess p, InclusiveGateway g, List<BPMNDecodedConditionalFlow> splitFlows, BPMNTranslationInfo info) throws FeelTranslatorException {
        Code code = new Code<String>(generateCommonNodeEntryStaments(g, info));
        code.append("java.util.List<java.util.function.Consumer<" + EXECUTILEXPRESSION + ".ProcessStatus>> enabledBranches = new java.util.ArrayList<>()");
        code.append("//CONDITIONALLY FORKS: " + splitFlows.stream().map(s -> s.firstStep().getId()).collect(Collectors.joining(",")));

        BPMNDecodedConditionalFlow default_branch = null;

        for (int o = 0; o < splitFlows.size(); ++o) {
            FeelTranslationInfo v_f_info = new FeelTranslationInfo();
            v_f_info.setGenerateGetters(true); //generate getters, no read next
            if (splitFlows.get(o).condition() != null) {
                String condition_expression = splitFlows.get(o).condition().substring(1);
                code.append("if "
                        + "(" + feel.translate(condition_expression, v_f_info) + ")" //TEMP, dobbiamo togliere l'uguale se c'è altrimenti non è un'espressione feel
                        + "{"
                        + generateCodeSource(generateTransitionDescriptionStaments(g, splitFlows.get(o).firstStep(), info))
                        + "enabledBranches.add(" + "this::" + sanitizeName(p.getFlowName(splitFlows.get(o).firstStep())) + ");"
                        + "} ");
                p.registerProcessVariables(v_f_info.getUsedVariableNames(), BPMNDecodedProcess.VariableDirection.READ, g.getId(), condition_expression);
                p.registerDecodedEdge(g, splitFlows.get(o).firstStep());

            } else {
                default_branch = splitFlows.get(o);
            }
        }
        code.append("if (enabledBranches.isEmpty()) "
                + ((default_branch != null)
                        ? "{ enabledBranches.add(" + "this::" + sanitizeName(p.getFlowName(default_branch.firstStep())) + "); }"
                        : "{ " + EXECUTILEXPRESSION + ".noDefaultCaseError(s); " + generateTransitionCode(p, g, null, info) + " }")
        );
        code.append(EXECUTILEXPRESSION + ".fork(s,\"" + g.getId() + "\",enabledBranches.toArray(java.util.function.Consumer[]::new))");
        code.append(EXECUTILEXPRESSION + ".stopThread()");
        code.append(generateCommonNodeExitStatements(g, info));

        return code;
    }

    @Override
    public Code generateExclusiveGatewayCode(BPMNDecodedProcess p, ExclusiveGateway g, List<BPMNDecodedConditionalFlow> splitFlows, BPMNTranslationInfo info) throws FeelTranslatorException {
        Code code = new Code<String>(generateCommonNodeEntryStaments(g, info));
        String source = "";
        BPMNDecodedConditionalFlow default_branch = null;

        for (int o = 0; o < splitFlows.size(); ++o) {
            FeelTranslationInfo v_f_info = new FeelTranslationInfo();
            v_f_info.setGenerateGetters(true); //generate getters, no read next
            if (splitFlows.get(o).condition() != null) {
                Code splitCode = generateTransitionCode(p, g, splitFlows.get(o).firstStep(), info);
                String condition_expression = splitFlows.get(o).condition().substring(1);
                if (!source.isBlank()) {
                    source += " else ";
                }
                source += "if " + "(" + feel.translate(condition_expression, v_f_info) + ")";  //TEMP, dobbiamo togliere l'uguale se c'è altrimenti non è un'espressione feel
                source += "{" + generateCodeSource(splitCode) + "}";
                p.registerProcessVariables(v_f_info.getUsedVariableNames(), BPMNDecodedProcess.VariableDirection.READ, g.getId(), condition_expression);
            } else {
                default_branch = splitFlows.get(o);
            }
        }
        if (!source.isBlank()) {
            source += " else ";
        }
        if (default_branch != null) {
            Code splitCode = generateTransitionCode(p, g, default_branch.firstStep(), info);
            source += "{" + generateCodeSource(splitCode) + "}";
        } else {
            source += "{ " + EXECUTILEXPRESSION + ".noDefaultCaseError(s); return null; }";

        }

        code.append(source);
        //p.registerProcessVariables(f_info.getUsedVariableNames(), BPMNDecodedProcess.VariableDirection.READ, n.getId());

        code.append(generateCommonNodeExitStatements(g, info));
        return code;
    }

//    private Code generateEventWatchLoopCode(BPMNDecodedProcess p, BPMNTranslationInfo info) {
//        Code code = new Code<String>();
//        
//        //        code.append("long gw_enter_time = System.currentTimeMillis()");
    ////        code.append("while (true) {");
////
////        /////LOOP SUI splitFlows, verificando che inizino con un intermediateCatchEvent da cui preleviamo i dati per generare una della seguenti varianti di codice...
////        //per messaggi
////        code.append(generateDebugOutputStament("\t CHECKING for message on channel " + channel_name));
////        if (!message_name.equalsIgnoreCase("passthrough")) {
////            code.append("Message_" + message_name + " receivedMessage = (" + "Message_" + message_name + ")" + EXECUTILEXPRESSION + ".receiveMessage(s,\"" + channel_name + "\",50,false)");
////            code.append("if (receivedMessage != null)");
////        } else {
////            code.append(generateDebugOutputStament("\t ASSUMING RECEPTION of message on channel " + channel_name));
////        }
////        code.append("{ chiamata_next(); break; }");
////
////        //per i signal
////        code.append(generateDebugOutputStament("\t CHECKING for signal " + signal_name));
////        code.append("if (" + EXECUTILEXPRESSION + ".checkSignal(s,\"" + signal_name + "\",50,false) { chiamata_next(); break; }");
////
////        //per i timer
////        code.append(generateDebugOutputStament("\t CHECKING for timeout " + timeout_name));
////        code.append("if (System.currentTimeMillis() - gw_enter_time >= timeout_milliseconds) { chiamata_next(); break; }");
////
////        code.append("}");
//        
//        return code;
//    }
    
    @Override
    public Code generateEventGatewayCode(BPMNDecodedProcess p, EventBasedGateway g, List<BPMNDecodedConditionalFlow> splitFlows, BPMNTranslationInfo info) throws FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet.");

//        Code code = new Code<String>(generateCommonNodeEntryStaments(g, info));
//        code.append("long gw_enter_time = System.currentTimeMillis()");
//        code.append("while (true) {");
//
//        /////LOOP SUI splitFlows, verificando che inizino con un intermediateCatchEvent da cui preleviamo i dati per generare una della seguenti varianti di codice...
//        //per messaggi
//        code.append(generateDebugOutputStament("\t CHECKING for message on channel " + channel_name));
//        if (!message_name.equalsIgnoreCase("passthrough")) {
//            code.append("Message_" + message_name + " receivedMessage = (" + "Message_" + message_name + ")" + EXECUTILEXPRESSION + ".receiveMessage(s,\"" + channel_name + "\",50,false)");
//            code.append("if (receivedMessage != null)");
//        } else {
//            code.append(generateDebugOutputStament("\t ASSUMING RECEPTION of message on channel " + channel_name));
//        }
//        code.append("{ chiamata_next(); break; }");
//
//        //per i signal
//        code.append(generateDebugOutputStament("\t CHECKING for signal " + signal_name));
//        code.append("if (" + EXECUTILEXPRESSION + ".checkSignal(s,\"" + signal_name + "\",50,false) { chiamata_next(); break; }");
//
//        //per i timer
//        code.append(generateDebugOutputStament("\t CHECKING for timeout " + timeout_name));
//        code.append("if (System.currentTimeMillis() - gw_enter_time >= timeout_milliseconds) { chiamata_next(); break; }");
//
//        code.append("}");
//        code.append(generateCommonNodeExitStatements(g, info));
//        return code;
    }

    //generates the code for a transition
    @Override
    public Code generateTransitionCode(BPMNDecodedProcess p, FlowNode current, FlowNode next, BPMNTranslationInfo info) {
        Code code = new Code<String>();
        /*
        p.registerDecodedEdge(current, next);
        String transition_code
                = generateCodeSource(generateTransitionDescriptionStaments(current, next, info))
                + sanitizeName(p.getFlowName(next)) + "(s.withCurrent(\"" + current.getId() + "\"));";

        code.append("if (!Thread.currentThread().isInterrupted()) {" + transition_code + "}");
         */

        if (next != null) {
            p.registerDecodedEdge(current, next);
            code.set(generateCodeSource(generateTransitionDescriptionStaments(current, next, info))
                    + "return " + EXECUTILEXPRESSION + ".buildActivityResult(s,"
                    + "\"" + current.getId() + "\","
                    + "\"" + next.getId() + "\","
                    + "this::" + sanitizeName(p.getFlowName(next))
                    + ");"
            );
        } else {
            code.set("return " + EXECUTILEXPRESSION + ".buildActivityResult(s,"
                    + "\"" + current.getId() + "\","
                    + "null,null"
                    + ");"
            );
        }
        return code;
    }

}
