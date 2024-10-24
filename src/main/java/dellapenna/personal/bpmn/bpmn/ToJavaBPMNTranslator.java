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
    private static final Pattern input_pattern = Pattern.compile("^input_([a-z0-9_-]+)$", Pattern.CASE_INSENSITIVE);
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
        return "public " + (f.returnType().equals(Void.class) ? "void" : f.returnType().getName()) + " " + f.name() + "("
                + f.parameters().entrySet().stream().map(e -> e.getValue().getName() + " " + e.getKey()).collect(Collectors.joining(", ")) //convert types?
                + ") {" + generateCodeSource(f.body()) + "}";
    }

    //generates the text source for a code block    
    public String generateCodeSource(Code code) {
        //return code.getStatements().stream().collect(Collectors.joining(";\n", "", ";\n"));
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
        String source = "public void init() {\n";
        source += process.getInputs().values().stream().map(v -> "this." + v.name() + " = null;\t//TODO assign input variable\n").collect(Collectors.joining());
        source += process.getInputs().values().stream()
                .map(v -> "if (this." + v.name() + "==null) " + v.name() + "=ProcessUtils.inputs.getProperty(\"" + v.name() + "\", null);\n")
                .collect(Collectors.joining());

        source += process.getInputs().values().stream().map(v -> "ProcessUtils.logInput(\"" + v.name() + "\",this." + v.name() + ");\n").collect(Collectors.joining());
        source += "}";

        return source;
    }

    //generates the text source for the process main function
    public String generateProcessMainSource(BPMNDecodedProcess process, Options opt) {
        String source = "public static void main(String[] args) {\n";
        if (!opt.isDebug()) {
            source += "ProcessUtils.debugChannel=new java.io.PrintStream(java.io.OutputStream.nullOutputStream());";
        }
        source += "ProcessUtils.start();";
        source += "bpmn_process_" + sanitizeName(process.getName()) + " process = new " + "bpmn_process_" + sanitizeName(process.getName()) + "();\n";
        source += "process.init();\n";
        if (!process.getStartEventFlowNames().isEmpty()) {
            source += "process." + sanitizeName(process.getStartEventFlowNames().getFirst()) + "();\n";
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
        Code code = new Code("\t//generic task: " + (t.getName() != null ? t.getName() : ""));
        if (opt.isDebug()) {
            code.append("ProcessUtils.debugOutput(\"TASK " + (t.getName() != null ? t.getName() : t.getId()) + "\")");
        }
        FunctionDefinition f = p.registerProcedure(
                "task_generic_" + (t.getName() != null ? t.getName() : ""),
                code,
                Code.ProcType.TASK);
        Code result = generateProcedureCallCode(p, f);
        //result.prepend("//generic task: " + (t.getName() != null ? t.getName() : ""));
        return result;
    }

    @Override
    public Code generateManualTaskCode(BPMNDecodedProcess p, ManualTask t, Options opt) {
        Code code = new Code("\t//manual task: " + (t.getName() != null ? t.getName() : ""));
        if (opt.isDebug()) {
            code.append("ProcessUtils.debugOutput(\"MANUAL TASK " + (t.getName() != null ? t.getName() : t.getId()) + "\")");
        }
        FunctionDefinition f = p.registerProcedure(
                "task_manual_" + (t.getName() != null ? t.getName() : ""),
                code,
                Code.ProcType.TASK);
        Code result = generateProcedureCallCode(p, f);
        //result.prepend("//manual task: " + (t.getName() != null ? t.getName() : ""));
        return result;
    }

    @Override
    public Code generateScriptTaskCode(BPMNDecodedProcess p, ScriptTask t, Options opt) {
        Code code = new Code("\t//script task: " + (t.getName() != null ? t.getName() : ""));
        if (opt.isDebug()) {
            code.append("ProcessUtils.debugOutput(\"SCRIPT TASK " + (t.getName() != null ? t.getName() : t.getId()) + "\")");
        }
        FunctionDefinition f = p.registerProcedure("task_script_" + (t.getName() != null ? t.getName() : ""),
                code,
                Code.ProcType.TASK);
        Code result = generateProcedureCallCode(p, f);
        //result.prepend("//script task: " + (t.getName() != null ? t.getName() : ""));
        return result;
    }

    @Override
    public Code generateServiceTaskCode(BPMNDecodedProcess p, ServiceTask t, Options opt) {
        Code code = new Code("\t//service task: " + (t.getName() != null ? t.getName() : ""));
        if (opt.isDebug()) {
            code.append("ProcessUtils.debugOutput(\"SERVICE TASK " + (t.getName() != null ? t.getName() : t.getId()) + "\")");
        }
        FunctionDefinition f = p.registerProcedure("task_service_" + (t.getName() != null ? t.getName() : ""),
                code,
                Code.ProcType.TASK);
        Code result = generateProcedureCallCode(p, f);
        //result.prepend("//service task: " + (t.getName() != null ? t.getName() : ""));
        return result;
    }

    @Override
    public Code generateSendTaskCode(BPMNDecodedProcess p, SendTask t, Options opt) {
        Code code = new Code("\t//send task: " + (t.getName() != null ? t.getName() : ""));
        if (opt.isDebug()) {
            code.append("ProcessUtils.debugOutput(\"SEND TASK " + (t.getName() != null ? t.getName() : t.getId()) + "\")");
        }

        FunctionDefinition f = p.registerProcedure("task_send_" + (t.getName() != null ? t.getName() : ""),
                code,
                Code.ProcType.TASK);
        Code result = generateProcedureCallCode(p, f);
        //result.prepend("//send task: " + (t.getName() != null ? t.getName() : ""));
        return result;
    }

    @Override
    public Code generateReceiveTaskCode(BPMNDecodedProcess p, ReceiveTask t, Options opt) {
        Code code = new Code("\t//receive task: " + (t.getName() != null ? t.getName() : ""));
        if (opt.isDebug()) {
            code.append("ProcessUtils.debugOutput(\"RECEIVE TASK " + (t.getName() != null ? t.getName() : t.getId()) + "\")");
        }

        FunctionDefinition f = p.registerProcedure("task_receive_" + (t.getName() != null ? t.getName() : ""),
                code,
                Code.ProcType.TASK);
        Code result = generateProcedureCallCode(p, f);
        //result.prepend("//receive task: " + (t.getName() != null ? t.getName() : ""));
        return result;
    }

    @Override
    public Code generateUserTaskCode(BPMNDecodedProcess p, UserTask t, Options opt) {
        Code outs = generateOutputAssignmentsCode(p, t, opt);
        if (opt.isDebug()) {
            outs.prepend("ProcessUtils.debugOutput(\"USER TASK " + (t.getName() != null ? t.getName() : t.getId()) + "\")");
        }
        outs.prepend("//user task: " + (t.getName() != null ? t.getName() : ""));
        FunctionDefinition f = p.registerProcedure("task_user_" + (t.getName() != null ? t.getName() : ""), outs, Code.ProcType.TASK);
        Code result = generateProcedureCallCode(p, f);
        //result.prepend("//user task: " + (t.getName() != null ? t.getName() : ""));
        return result;
    }

    @Override
    public Code generateBusinessRuleTaskCode(BPMNDecodedProcess p, BusinessRuleTask t, Options opt) {
        ModelElementInstance ioMapping = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "ioMapping");
        ModelElementInstance calledDecision = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "calledDecision");

        String procName = sanitizeName("dmn_dtable_" + calledDecision.getAttributeValue("decisionId"));
        String output_record_name = procName + "_result";

        Code code = new Code(
                output_record_name + " " + calledDecision.getAttributeValue("resultVariable")
                + "=" + procName + ".execute"
                + "(" + ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "input").stream()
                        .map(e -> "/*" + e.getAttribute("target") + "*/" + feel.translateChecked(e.getAttribute("source").substring(1))).collect(Collectors.joining(", "))
                + ")");

        if (opt.isDebug()) {
            code.append("ProcessUtils.debugOutput(\"DECISION RESULT IS %s\"," + calledDecision.getAttributeValue("resultVariable") + ")");
        }

        code.append(generateOutputAssignmentsCode(p, t, opt));

        if (opt.isDebug()) {
            code.prepend("ProcessUtils.debugOutput(\"EXECUTING DECISION " + (t.getName() != null ? t.getName() : t.getId()) + "\")");
        }
        code.prepend("\t//business rule task: " + (t.getName() != null ? t.getName() : ""));
        return code;

    }

    //EVENTS
    @Override
    public Code generateEndEventCode(BPMNDecodedProcess p/*UNUSED*/, EndEvent t, Options opt) {
        Code code = new Code();
        Collection<EventDefinition> eventDefs = t.getEventDefinitions();
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
                code.append("ProcessUtils.error(\"" + error_message + "\", " + error_code + ")");
            }
        }

        if (code.isEmpty()) {
            code.append("ProcessUtils.success()");
        }
        code.prepend("\t//end event: " + (t.getName() != null ? t.getName() : ""));
        return code;
    }

    @Override
    public Code generateStartEventCode(BPMNDecodedProcess p, StartEvent t, Options opt) {
        Code code = generateOutputAssignmentsCode(p, t, opt);
        if (opt.isDebug()) {
            code.prepend("ProcessUtils.debugOutput(\"START EVENT: " + (t.getName() != null ? t.getName() : t.getId()) + "\")");
        }
        code.prepend("\t//start event: " + (t.getName() != null ? t.getName() : ""));
        p.registerStartEventFlowName(sanitizeName(getFlowName(t)));
        return code;
    }

////// GATEWAYS
    private Code generateJoiningGatewayCode(BPMNDecodedProcess p, FlowNode joinedflow, Options opt) throws FeelTranslatorException {
        return generateFlowJointCode(p, joinedflow, opt);
    }

    ////***********BISOGNA GENERARE UNA FUNZIONE AGGANCIATA AL GATEWAY AVENTE COME TRIGGER I NODI ENTRANTI CHE CHIAMA IL JOINEDFLOW*************
    @Override
    public Code generateParallelJoiningGatewayCode(BPMNDecodedProcess p, ParallelGateway n, FlowNode joinedflow, Options opt) throws BpmnTranslatorException, FeelTranslatorException {
//        String join_code = "while (!Arrays.stream(((FutureTask<Integer>[])" + sanitizeName(joinedflow.name()) + "_parallels)" + ").allMatch(t -> t.isDone())) { Thread.sleep(300); } ";
//        FunctionDefinition proc = registerProcedure(
//                joinedflow.name(), join_code + "\n\n" + joinedflow.code() + ";" + "//start:" + joinedflow.firstStep().getId()
//                + "//end:" + joinedflow.lastStep().getId(), ProcType.FLOW);
//TUTTI I RAMI PARALLELI DEVONO ARRIVARE AL GW PRIMA CHE CONTINUI L'ELABORAZIONE        
        return new Code("ProcessUtils.signal(ID DEL NODO ENTRANTE)");
    }

    @Override
    public Code generateInclusiveJoiningGatewayCode(BPMNDecodedProcess p, InclusiveGateway n, FlowNode joinedflow, Options opt) throws BpmnTranslatorException, FeelTranslatorException {
        Code code = generateJoiningGatewayCode(p, joinedflow, opt);
        code.prepend("\t//inclusive joining gateway");
        return code;
    }

    @Override
    public Code generateExclusiveJoiningGatewayCode(BPMNDecodedProcess p, ExclusiveGateway n, FlowNode joinedflow, Options opt) throws BpmnTranslatorException, FeelTranslatorException {
        Code code = generateJoiningGatewayCode(p, joinedflow, opt);
        code.prepend("\t//exclusive joining gateway");
        return code;
    }

    @Override
    public Code generateEventJoiningGatewayCode(BPMNDecodedProcess p, EventBasedGateway n, FlowNode joinedflow, Options opt) throws BpmnTranslatorException, FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Code generateParallelGatewayCode(BPMNDecodedProcess p, ParallelGateway n, List<BPMNDecodedConditionalFlow> splitFlows, Options opt) throws FeelTranslatorException {
        Code result = new Code();
        for (int o = 0; o < splitFlows.size(); ++o) {
            result.append("ProcessUtils.fork(\"" + sanitizeName(getFlowName(splitFlows.get(o).firstStep())) + "\")");
        }
        //code += "\nchiamata_a_funzione_join();"; //oppure continuiamo inline??? si potrebbe inserire già qui il codice della join gw, ma come?        
        if (opt.isDebug()) {
            result.prepend("ProcessUtils.debugOutput(\"PARALLEL GATEWAY " + (n.getName() != null ? n.getName() : n.getId()) + "\")");
        }
        result.prepend("\t//parallel split");
        return result;
    }

    @Override
    public Code generateInclusiveGatewayCode(BPMNDecodedProcess p, InclusiveGateway n, List<BPMNDecodedConditionalFlow> splitFlows, Options opt) throws FeelTranslatorException {
        String source = "";
        for (int o = 0; o < splitFlows.size(); ++o) {
            Code splitCode = ToJavaBPMNTranslator.this.generateFlowJointCode(p, splitFlows.get(o).firstStep(), opt);
            source += "if "
                    + "(" + feel.translate(splitFlows.get(o).condition().substring(1)) + ")" //TEMP, dobbiamo togliere l'uguale se c'è altrimenti non è un'espressione feel
                    + "{" + generateCodeSource(splitCode)
                    + "} ";
        }

        Code code = new Code(source);
        if (opt.isDebug()) {
            code.prepend("ProcessUtils.debugOutput(\"INCLUSIVE GATEWAY " + (n.getName() != null ? n.getName() : n.getId()) + "\")");
        }
        code.prepend("\t//inclusive gateway");
        return code;
    }

    @Override
    public Code generateExclusiveGatewayCode(BPMNDecodedProcess p, ExclusiveGateway n, List<BPMNDecodedConditionalFlow> splitFlows, Options opt) throws FeelTranslatorException {
        String source = "";
        BPMNDecodedConditionalFlow default_branch = null;
        for (int o = 0; o < splitFlows.size(); ++o) {
            Code splitCode = ToJavaBPMNTranslator.this.generateFlowJointCode(p, splitFlows.get(o).firstStep(), opt);
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
            Code splitCode = ToJavaBPMNTranslator.this.generateFlowJointCode(p, default_branch.firstStep(), opt);
            source += "{" + generateCodeSource(splitCode) + "}";
        } else {
            source += "{" + generateCodeSource(generateFlowJointCode(p, "ProcessUtils.noDefaultCaseError", opt)) + "}";

        }

        Code code = new Code(source);
        if (opt.isDebug()) {
            code.prepend("ProcessUtils.debugOutput(\"EXCLUSIVE GATEWAY " + (n.getName() != null ? n.getName() : n.getId()) + "\")");
        }
        code.prepend("\t//exclusive gateway");
        return code;
    }

    @Override
    public Code generateEventGatewayCode(BPMNDecodedProcess p, EventBasedGateway n, List<BPMNDecodedConditionalFlow> splitFlows, Options opt) throws FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Code generateFlowJointCode(BPMNDecodedProcess p, FlowNode flowStart, Options opt) {
        return generateFlowJointCode(p, sanitizeName(getFlowName(flowStart)), opt);
    }

    //////////////
    // Code generation utilities
    //////////////
    protected Code generateFlowJointCode(BPMNDecodedProcess p, String flowName, Options opt) {
        //return new Code(sanitizeName(flowName) + "()");
        Code code = new Code(flowName + "()");
        if (opt.isDebug()) {
            code.prepend("ProcessUtils.debugOutput(\"JOINING FLOW " + flowName + "\")");
        }
        return code;
    }

    //generates the code to call a function, registering it if needed
    private Code generateProcedureCallCode(BPMNDecodedProcess p, FunctionDefinition proc) {
        return new Code(sanitizeName(proc.name()) + "()");

    }

    //generates the code to capture the output of a node, as a set of variable assignments
    private Code generateOutputAssignmentsCode(BPMNDecodedProcess p, FlowNode t, Options opt) {
        Code result = new Code();
        ModelElementInstance ioMapping = t.getExtensionElements() != null ? t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "ioMapping") : null;
        if (ioMapping != null) {
            ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "output").stream().forEach(e -> p.registerProcessVariable(e.getAttribute("target")));

            ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "output").stream().forEach(e -> {
                String expression = feel.translateChecked(e.getAttribute("source").substring(1));
                Matcher matcher = input_pattern.matcher(expression);
                if (matcher.matches()) {
                    p.registerInput(expression);
                }
            });
            result.append(ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "output").stream()
                    .map(e -> e.getAttribute("target") + "=" + feel.translateChecked(e.getAttribute("source").substring(1)))
                    .collect(Collectors.toList()));

            if (opt.isDebug()) {
                result.append(ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "output").stream()
                        .map(e -> "ProcessUtils.debugOutput(\"ASSIGNING " + e.getAttribute("target") + " TO %s\"," + feel.translateChecked(e.getAttribute("source").substring(1)) + ")")
                        .collect(Collectors.toList()));
            }
        }
        return result;
    }

//    //generate the code for a decoded flow
//    protected Code registerFlow(BPMNDecodedFlow<String> flow) {
//        Code result = new Code();
//        new Code(generateFunctionSource(registerProcedure(flow.name(), flow.code(), Code.ProcType.FLOW)));
//        return result;
//    }
}

/*
ExecutorService pgw_123_executor = Executors.newFixedThreadPool(10);
Future<Integer> future = new SquareCalculator().calculate(10);

while(!future.isDone()) {
    System.out.println("Calculating...");
    Thread.sleep(300);
}

Integer result = future.get();

---------

ForkJoinPool commonPool = ForkJoinPool.commonPool();
forkJoinPool.execute(customRecursiveTask);
int result = customRecursiveTask.join();
ForkJoinTask.invokeAll(createSubtasks());


public void test() {

        //parallel gateway
        ForkJoinTask<Integer>[] tasks = new ForkJoinTask[]{
            ForkJoinTask.adapt(() -> {
                System.out.println("ciao");
                return 1;
            }),
            ForkJoinTask.adapt(() -> {
                System.out.println("ciao");
                return 1;
            })
        };
        //serve un modo per dare un nome al pool in modo da poter dire di quali elementi fare il JOIN DOPO averli chiamati, e non allo stesso tempo
        //parallel join gateway 
        Collection<ForkJoinTask<Integer>> results = ForkJoinTask.invokeAll(Arrays.asList(tasks));
    }
 */

 /*
SOLUZIONE MIGLIORE


//parallel gateway
        FutureTask<Integer>[] pgw_123_tasks = new FutureTask[]{
            new FutureTask<>(() -> {
                System.out.println("ciao");
                return 1;
            }),
            new FutureTask<>(() -> {
                System.out.println("ciao");
                return 1;
            })
        };
        for (FutureTask<Integer> t : pgw_123_tasks) {
            t.run();
        }
        ----
        //i vari sotto-flussi devono uscire ritornando qualcosa invece di chiamatre la funzione-flusso di uscita dal join parallel gw...
        ----
        //join parallel gateway 
        while (!Arrays.stream(pgw_123_tasks).allMatch(t -> t.isDone())) {
            Thread.sleep(300);
        }
        //funzione-flusso di uscita dal join parallel gw...
 */
