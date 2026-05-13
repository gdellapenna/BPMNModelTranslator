package dellapenna.personal.bpmn.bpmn;

import dellapenna.personal.bpmn.versim.Assertion;
import dellapenna.personal.bpmn.feel.FeelTranslationInfo;
import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import dellapenna.personal.bpmn.feel.ToJavaFeelTranslator;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.camunda.bpm.model.bpmn.instance.BoundaryEvent;
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
import org.camunda.bpm.model.bpmn.instance.MessageEventDefinition;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.ReceiveTask;
import org.camunda.bpm.model.bpmn.instance.ScriptTask;
import org.camunda.bpm.model.bpmn.instance.SendTask;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.Process;
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
        } else if (t instanceof Task g) { //data outputs of generic tasks also addressed?
            p.registerProcessVariables(g.getDataOutputAssociations().stream()
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
        }

        return result;
    }

    private Code generateCommonNodeExitStatements(FlowNode n, BPMNTranslationInfo info) {
        Code code = new Code<String>();
        return code;
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
    public Code generateBoundaryDispatcherCode(BPMNDecodedProcess p, FlowNode ownerNode, BPMNTranslationInfo info) {
        Code code = new Code();

        //find parent process: boundary elements are not children of the task
        ModelElementInstance parent = ownerNode.getParentElement();
        while (parent != null && !(parent instanceof Process)) {
            parent = parent.getParentElement();
        }
        Process process = (Process) parent; //si assume sempre non nullo

        List<BoundaryEvent> boundaryEvents = process.getChildElementsByType(BoundaryEvent.class).stream()
                .map(e -> (BoundaryEvent) e)
                .filter(be -> ownerNode.getId().equals(be.getAttributeValue("attachedToRef")))
                .collect(Collectors.toList());

        //"this::" + sanitizeName(p.getFlowName(splitFlows.get(o).firstStep()));
        code.append(EXECUTILEXPRESSION + ".forkBoundaryWatch(s, \"" + ownerNode.getId() + "\",this::" + p.getFlowName(ownerNode,p.BOUNDED_NODE_NORMAL_NAME_VARIANT)+ ",this::" + p.getFlowName(ownerNode, p.BOUNDED_NODE_BOUNDARY_NAME_VARIANT) + ")");
        code.append(EXECUTILEXPRESSION + ".stopThread()");
        return code;
    }

    @Override
    public Code generateBoundaryEventsCode(BPMNDecodedProcess p, FlowNode ownerNode, List<BPMNDecodedBoundaryFlow> boundaryFlows, BPMNTranslationInfo info) {
        Code code = new Code();
        String boundaryFlowsCode = boundaryFlows.stream()
                .map(bf -> {
                    EventDefinition e = bf.event().getEventDefinitions().iterator().next(); //should be only one!
                    String event_source = "";
                    if (e instanceof MessageEventDefinition me) {
                        String channel_name = me.getMessage().getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "subscription").getAttributeValue("correlationKey").substring(1);
                        String message_name = me.getMessage().getName();
                        MessageDefinition message = p.registerProcessMessage(message_name);
                        event_source += generateDebugOutputStament("\t CHECKING message on channel " + channel_name) + ";";
                        if (!message_name.equalsIgnoreCase("passthrough")) {
                            event_source += "Message_" + message_name + " receivedMessage = (" + "Message_" + message_name + ")" + EXECUTILEXPRESSION + ".receiveMessage(s,\"" + channel_name + "\",50,false);"
                                    + "if (receivedMessage != null)";
                        } else {
                            event_source += generateDebugOutputStament("\t ASSUMING RECEPTION of message on channel " + channel_name) + ";";
                        }
                        //code.append(generateOutputAssignmentsStatements(p, t, List.of("receivedMessage"), false, info)); //no read next
                    }
                    event_source += "{" + EXECUTILEXPRESSION + ".resolveBoundaryWatch(s, true);\n"
                            + generateCodeSource(generateTransitionCode(p, ownerNode, bf.firstStep(), info))
                            + "break; }";
                    return event_source;
                }).collect(Collectors.joining("\n"));

        code.append("while(true) {" + boundaryFlowsCode + "}");
        return code;
    }

    @Override
    public Code generateGenericTaskCode(BPMNDecodedProcess p, Task t, BPMNTranslationInfo info) {
        Code code = new Code<String>(generateCommonNodeEntryStaments(t, info));
        code.append(generateOutputAssignmentsStatements(p, t, Collections.EMPTY_LIST, false, info)); //no read next
        code.append(generateCommonNodeExitStatements(t, info));
        return code;
    }

    @Override
    public Code generateManualTaskCode(BPMNDecodedProcess p, ManualTask t, BPMNTranslationInfo info) {
        Code code = new Code<String>(generateCommonNodeEntryStaments(t, info));
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
            code.append("Message_" + message_name + " receivedMessage = (" + "Message_" + message_name + ")" + EXECUTILEXPRESSION + ".receiveMessage(s,\"" + channel_name + "\")");
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
        code.append(generateCommonNodeExitStatements(t, info));

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

    // GATEWAYS
    @Override
    public Code generateParallelJoiningGatewayCode(BPMNDecodedProcess p, ParallelGateway g, FlowNode joinedflow, BPMNTranslationInfo info) throws BpmnTranslatorException, FeelTranslatorException {
        Code code = new Code<String>(generateCommonNodeEntryStaments(g, info));
        code.append(generateTransitionDescriptionStaments(g, joinedflow, info));
        p.registerDecodedEdge(g, joinedflow);
        code.append("//JOINS: " + g.getIncoming().stream().map(s -> s.getSource().getId()).collect(Collectors.joining(",")));
        code.append(EXECUTILEXPRESSION + ".join(s,\"" + g.getId() + "\", " + ("this::" + sanitizeName(p.getFlowName(joinedflow))) + ")");

        code.append(generateCommonNodeExitStatements(g, info));
        return code;
    }

    //same as generateParallelJoiningGatewayCode, code must be merged with appropriate generic parameter types
    @Override
    public Code generateInclusiveJoiningGatewayCode(BPMNDecodedProcess p, InclusiveGateway g, FlowNode joinedflow, BPMNTranslationInfo info) throws BpmnTranslatorException, FeelTranslatorException {
        Code code = new Code<String>(generateCommonNodeEntryStaments(g, info));
        code.append(generateTransitionDescriptionStaments(g, joinedflow, info));
        p.registerDecodedEdge(g, joinedflow);
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
            code.append(generateTransitionDescriptionStaments(g, splitFlows.get(o).firstStep(), info));
            p.registerDecodedEdge(g, splitFlows.get(o).firstStep());
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
                        : "{ " + EXECUTILEXPRESSION + ".noDefaultCaseError(s); }")
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
            source += "{ " + EXECUTILEXPRESSION + ".noDefaultCaseError(s); }";

        }

        code.append(source);
        //p.registerProcessVariables(f_info.getUsedVariableNames(), BPMNDecodedProcess.VariableDirection.READ, n.getId());

        code.append(generateCommonNodeExitStatements(g, info));
        return code;
    }

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
        code.append(generateTransitionDescriptionStaments(current, next, info));
        p.registerDecodedEdge(current, next);
        code.append(sanitizeName(p.getFlowName(next)) + "(s.withCurrent(\"" + current.getId() + "\"))");
        return code;
    }

}
