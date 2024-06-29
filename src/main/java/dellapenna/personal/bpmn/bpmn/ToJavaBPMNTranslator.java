package dellapenna.personal.bpmn.bpmn;

import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import dellapenna.personal.bpmn.feel.ToJavaFeelTranslator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

public class ToJavaBPMNTranslator extends AbstractBPMNTranslator<String> {

    private final static String ZEEBENS = "http://camunda.org/schema/zeebe/1.0";

    private static final ToJavaFeelTranslator feel = new ToJavaFeelTranslator();

    private enum ProcType {
        EVENT, TASK, FLOW
    };

    private final Map<ProcType, Map<String, FunctionDefinition>> functions = new HashMap<>();
    private final Map<String, GlobalVariableDefinition> globals = new HashMap<>();

    public ToJavaBPMNTranslator() {
        reset();
    }

    @Override
    protected void reset() {
        super.reset();
        globals.clear();
        functions.clear();
        for (ProcType t : ProcType.values()) {
            functions.put(t, new HashMap<>());
        }
    }

    private String sanitizeName(String n) {
        return n.replaceAll("[^A-Za-z0-9_]", "_");
    }

    private GlobalVariableDefinition registerGlobalVariable(String name) {
        GlobalVariableDefinition g;
        if (!globals.containsKey(name)) {
            g = new GlobalVariableDefinition(name);
            globals.put(name, g);
        } else {
            g = globals.get(name);
        }
        return g;
    }

    private FunctionDefinition registerFunction(String name, List<String> code, Class returnType, ProcType type) {
        return registerFunction(name, code, new ArrayList<>(List.of()), returnType, type);
    }

    private FunctionDefinition registerFunction(String name, List<String> code, List<String> triggers, Class returnType, ProcType type) {
        FunctionDefinition f;
        if (!functions.get(type).containsKey(name)) {
            f = new FunctionDefinition(sanitizeName(name), code, triggers, returnType, Collections.EMPTY_MAP);
            functions.get(type).put(name, f);

        } else {
            f = functions.get(type).get(name);
            System.err.println("warning: discarding function re-definition: " + name);
        }
        return f;
    }

    private FunctionDefinition registerProcedure(String name, List<String> code, ProcType type) {
        if (code == null || code.isEmpty()) {
            code = new ArrayList<>(List.of("System.out.println(\"" + name + "\")"));
        }
        return registerFunction(name, code, Void.class, type);
    }

    /////
    private String translateFunction(FunctionDefinition f) {
        return "public " + (f.returnType().equals(Void.class) ? "void" : f.returnType().getName()) + " " + f.name() + "("
                + f.parameters().entrySet().stream().map(e -> e.getValue().getName() + " " + e.getKey()).collect(Collectors.joining(", ")) //convert types?
                + ") {" + translateStatementSequence(f.body()) + "}";
    }

    private List<String> translateFunctionCall(String name, List<String> code, ProcType type) {
        FunctionDefinition proc = registerProcedure(name, code, type);
        return new ArrayList<>(List.of(proc.name() + "()"));
    }

    private List<String> translateOutputs(FlowNode t) {
        ModelElementInstance ioMapping = t.getExtensionElements() != null ? t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "ioMapping") : null;
        if (ioMapping != null) {
            ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "output").stream().forEach(e -> registerGlobalVariable(e.getAttribute("target")));
            return ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "output").stream()
                    .map(e -> e.getAttribute("target") + "=" + feel.translateChecked(e.getAttribute("source").substring(1)))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>(List.of());
        }
    }

    //////
    @Override
    protected List<String> translateFlow(BPMNDecodedFlow<String> flow) {
        return new ArrayList<>(List.of(translateFunction(registerProcedure(flow.name(),
                flow.code(),
                ProcType.FLOW))));
    }

    @Override
    public String translateStatementSequence(List<String> statements) {
        return statements.stream().collect(Collectors.joining(";\n", "", ";\n"));
    }

    /////
    @Override
    protected String translateBpmn(List<String> processes_code) {
        return processes_code.stream().collect(Collectors.joining("\n\n"));
    }

    @Override
    protected String translateProcess(String name, List<String> flows_code) {
        return " class bpmn_process_" + sanitizeName(name) + " { "
                + globals.values().stream()
                        .map(gd -> "Object " + gd.name())
                        .collect(Collectors.joining(";\n")) + "; "
                + functions.values().stream()
                        .flatMap(f -> f.values().stream())
                        .map(fd -> translateFunction(fd))
                        .collect(Collectors.joining("\n\n"))
                + "}";
    }

    ////TASKS
    @Override
    public List<String> translateGenericTask(Task t) {
        return translateFunctionCall("task_generic_" + t.getName(), null, ProcType.TASK);
    }

    @Override
    public List<String> translateManualTask(ManualTask t) {
        return translateFunctionCall("task_manual_" + t.getName(), null, ProcType.TASK);
    }

    @Override
    public List<String> translateScriptTask(ScriptTask t) {
        return translateFunctionCall("task_script_" + t.getName(), null, ProcType.TASK);
    }

    @Override
    public List<String> translateUserTask(UserTask t) {
        List<String> outs = translateOutputs(t);
        return translateFunctionCall("task_user_" + t.getName(), ((!outs.isEmpty()) ? outs : null), ProcType.TASK);
    }

    @Override
    public List<String> translateServiceTask(ServiceTask t) {
        return translateFunctionCall("task_service_" + t.getName(), null, ProcType.TASK);
    }

    @Override
    public List<String> translateSendTask(SendTask t) {
        return translateFunctionCall("task_send_" + t.getName(), null, ProcType.TASK);
    }

    @Override
    public List<String> translateReceiveTask(ReceiveTask t) {
        return translateFunctionCall("task_receive_" + t.getName(), null, ProcType.TASK);
    }

    @Override
    public List<String> translateBusinessRuleTask(BusinessRuleTask t) {

        ModelElementInstance ioMapping = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "ioMapping");
        ModelElementInstance calledDecision = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "calledDecision");

        String procName = sanitizeName("dmn_dtable_" + calledDecision.getAttributeValue("decisionId"));
        String output_record_name = procName + "_result";

        List<String> statements = new ArrayList<>(List.of(
                t.getName() != null ? "//" + t.getName() : "",
                output_record_name + " " + calledDecision.getAttributeValue("resultVariable")
                + "=" + procName + ".execute"
                + "(" + ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "input").stream()
                        .map(e -> "/*" + e.getAttribute("target") + "*/" + feel.translateChecked(e.getAttribute("source").substring(1))).collect(Collectors.joining(", "))
                + ")"));
        statements.addAll(translateOutputs(t));
        return statements;

    }

    ////EVENTS
    @Override
    public List<String> translateEndEvent(EndEvent t) {
        List<String> code = new ArrayList<>();
        Collection<EventDefinition> eventDefs = t.getEventDefinitions();
        for (EventDefinition eventDef : eventDefs) {
            if (eventDef instanceof ErrorEventDefinition eed) {
                code.add("System.err.println(\"" + eed.getError().getName() + "\")"); //TODO: handle other event definitions here?
                try {
                    int error_code = Integer.valueOf(eed.getError().getErrorCode());
                    code.add("System.exit(" + error_code + ")");
                } catch (NumberFormatException ex) {
                    //code is not a number
                    code.add("System.exit(1)");
                }
            }
        }

        if (code.isEmpty()) {
            code.add("System.exit(0)");
        }
//        if (!code.isEmpty()) {
//            code.add(0, "//end: " + t.getName());
//            return translateFunctionCall("event_end_" + t.getName(), code, ProcType.EVENT);
//        } else {
        code.add(0, "//end event: " + t.getName());
        return code;
//        return new ArrayList<>(List.of("//end: " + t.getName(), "System.exit(0)"));

//        }
    }

    @Override
    public List<String> translateStartEvent(StartEvent t) {
        List<String> code = translateOutputs(t);
//        if (!code.isEmpty()) {
//            code.add(0, "//start: " + t.getName());
//            return translateFunctionCall("event_start_" + t.getName(), code, ProcType.EVENT);
//        } else {
        code.add(0, "//start event: " + t.getName());
        return code;
        //return new ArrayList<>(List.of("//start: " + t.getName()));
//    }
        //inserire una condizione??
    }

////// GATEWAYS
    private List<String> translateJoiningGateway(FlowNode joinedflow) throws FeelTranslatorException {
        return translateNodeJoint(joinedflow);
//        return translateFunctionCall(
//                joinedflow.name(),
//                joinedflow.code()
//                + "//start:" + joinedflow.firstStep().getId()
//                + "//end:" + joinedflow.lastStep().getId(),
//                ProcType.FLOW);
//        FunctionDefinition proc = registerProcedure(name, code, ProcType.FLOW);
//        return proc.name() + "()";
    }

    
    ////***********BISOGNA GENERARE UNA FUNZIONE AGGANCIATA AL GATEWAY AVENTE COME TRIGGER I NODI ENTRANTI CHE CHIAMA IL JOINEDFLOW*************
    
    @Override
    public List<String> translateParallelJoiningGateway(ParallelGateway n, FlowNode joinedflow) throws BpmnTranslatorException, FeelTranslatorException {
//        String join_code = "while (!Arrays.stream(((FutureTask<Integer>[])" + sanitizeName(joinedflow.name()) + "_parallels)" + ").allMatch(t -> t.isDone())) { Thread.sleep(300); } ";
//        FunctionDefinition proc = registerProcedure(
//                joinedflow.name(), join_code + "\n\n" + joinedflow.code() + ";" + "//start:" + joinedflow.firstStep().getId()
//                + "//end:" + joinedflow.lastStep().getId(), ProcType.FLOW);
        return new ArrayList<>(List.of("ProcessUtils.signal(ID DEL NODO ENTRANTE)")); 
    }

    @Override
    public List<String> translateInclusiveJoiningGateway(InclusiveGateway n, FlowNode joinedflow) throws BpmnTranslatorException, FeelTranslatorException {
        return translateJoiningGateway(joinedflow);
    }

    @Override
    public List<String> translateExclusiveJoiningGateway(ExclusiveGateway n, FlowNode joinedflow) throws BpmnTranslatorException, FeelTranslatorException {
        return translateJoiningGateway(joinedflow);
    }

    @Override
    public List<String> translateEventJoiningGateway(EventBasedGateway n, FlowNode joinedflow) throws BpmnTranslatorException, FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

//    @Override
//    public String translateParallelGateway(ParallelGateway n, List<BPMNDecodedConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
//        String common_name = "ID_usato_anche_nel_parallel_join";
//        registerGlobalVariable(common_name + "_parallels");
//        String code = /*"FutureTask<Integer>[] " +*/ common_name + "_parallels = new FutureTask[" + splitFlows.size() + "];"
//                + "\nFutureTask<Integer> t;";
//        for (int o = 0; o < splitFlows.size(); ++o) {
//
//            String subflow_code = "";
//            if (splitFlows.get(o).condition() != null) {
//                subflow_code += "if " + "(" + feel.translate(splitFlows.get(o).condition().substring(1)) + ")";  //TEMP, dobbiamo togliere l'uguale se c'è altrimenti non è un'espressione feel
//                subflow_code += "{\n" + splitFlows.get(o).code() + "\n}";
//            } else {
//                subflow_code += splitFlows.get(o).code();
//            }
//            code += "t = new FutureTask<>(() -> {" + translateFunctionCall(common_name + "_parallel_" + o, subflow_code, ProcType.FLOW) + ";\n\n return 1;});"
//                    + "\nt.run();"
//                    + "\n((FutureTask<Integer>[])" + common_name + "_parallels)[" + o + "]=t;"
//                    + "\n";
//        }
//        code += "\nchiamata_a_funzione_join();"; //oppure continuiamo inline??? si potrebbe inserire già qui il codice della join gw, ma come?
//        return code;
//    }
    @Override
    public List<String> translateParallelGateway(ParallelGateway n, List<BPMNDecodedConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
        List<String> code = new ArrayList<>();
        for (int o = 0; o < splitFlows.size(); ++o) {
//            String subflow_code = "";
//            if (splitFlows.get(o).condition() != null) { //no conditions in parallel gateways
//                subflow_code += "if " + "(" + feel.translate(splitFlows.get(o).condition().substring(1)) + ")";  //TEMP, dobbiamo togliere l'uguale se c'è altrimenti non è un'espressione feel
//                subflow_code += "{\n" + splitFlows.get(o).code() + "\n}";
//            } else {
//                subflow_code += splitFlows.get(o).code();
//            }

//registerGlobalVariable(splitFlows.get(o).name() + "_thread");
//            code += splitFlows.get(o).name() + "_thread = new FutureTask<>(() -> {" + translateFunctionCall(splitFlows.get(o).name() + "_parallel", splitFlows.get(o).code(), ProcType.FLOW) + ";\n\n return 1;});"
//                    + "\n" + splitFlows.get(o).name() + "_thread.run();"
//                    + "\n";
            code.add(translateStatementSequence(List.of("ProcessUtils.fork(" + translateNodeJoint(splitFlows.get(o).firstStep())))); //start parallel tasks
        }
        //code += "\nchiamata_a_funzione_join();"; //oppure continuiamo inline??? si potrebbe inserire già qui il codice della join gw, ma come?

        return new ArrayList<>(List.of());
    }

    @Override
    public List<String> translateInclusiveGateway(InclusiveGateway n, List<BPMNDecodedConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
        String code = "";
        for (int o = 0; o < splitFlows.size(); ++o) {
            code += "if "
                    + "(" + feel.translate(splitFlows.get(o).condition().substring(1)) + ")" //TEMP, dobbiamo togliere l'uguale se c'è altrimenti non è un'espressione feel
                    + "{" + translateStatementSequence(translateNodeJoint(splitFlows.get(o).firstStep())) + "} ";
        }
        return new ArrayList<>(List.of(code));
    }

    @Override
    public List<String> translateExclusiveGateway(ExclusiveGateway n, List<BPMNDecodedConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
        String code = "";
        BPMNDecodedConditionalFlow<String> default_branch = null;
        for (int o = 0; o < splitFlows.size(); ++o) {
            if (splitFlows.get(o).condition() != null) {
                if (!code.isBlank()) {
                    code += " else ";
                }
                code += "if " + "(" + feel.translate(splitFlows.get(o).condition().substring(1)) + ")";  //TEMP, dobbiamo togliere l'uguale se c'è altrimenti non è un'espressione feel
                code += "{" + translateStatementSequence(translateNodeJoint(splitFlows.get(o).firstStep())) + "}";
            } else {
                default_branch = splitFlows.get(o);
            }
        }
        if (!code.isBlank()) {
            code += " else ";
        }
        if (default_branch != null) {
            code += "{" + translateStatementSequence(translateNodeJoint(default_branch.firstStep())) + "}";
        } else {
            code += "{" + translateStatementSequence(translateNodeJoint("ProcessUtils.NoDefaultError")) + "}";
//            code += """
//                      {
//                      //no default case
//                      System.exit(9999);
//                      }""";
            //////result += " { return null; }";
        }
        return new ArrayList<>(List.of(code));
    }

    @Override
    public List<String> translateEventGateway(EventBasedGateway n, List<BPMNDecodedConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected List<String> translateNodeJoint(FlowNode flowStart) {
        return translateNodeJoint(getFlowName(flowStart));
    }

    protected List<String> translateNodeJoint(String flowName) {
        return new ArrayList<>(List.of(sanitizeName(flowName) + "()"));
    }

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
