package dellapenna.personal.bpmn.bpmn;

import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import dellapenna.personal.bpmn.feel.ToJavaFeelTranslator;
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

    private FunctionDefinition registerFunction(String name, String code, Class returnType, ProcType type) {
        FunctionDefinition f;
        if (!functions.get(type).containsKey(name)) {
            f = new FunctionDefinition(sanitizeName(name), code, returnType, Collections.EMPTY_MAP);
            functions.get(type).put(name, f);

        } else {
            f = functions.get(type).get(name);
            System.err.println("warning: discarding function re-definition: " + name);
        }
        return f;
    }

    private FunctionDefinition registerProcedure(String name, String code, ProcType type) {
        if (code == null || code.isBlank()) {
            code = "\tSystem.out.println(\"" + name + "\");\n";
        }
        return registerFunction(name, code, Void.class, type);
    }

    /////
    private String translateFunction(FunctionDefinition f) {
        return "public " + (f.returnType().equals(Void.class) ? "void" : f.returnType().getName()) + " " + f.name() + "("
                + f.parameters().entrySet().stream().map(e -> e.getValue().getName() + " " + e.getKey()).collect(Collectors.joining(", ")) //convert types?
                + ") {\n"
                + f.body()
                + "\n}";
    }

    private String translateFunctionCall(String name, String code, ProcType type) {
        FunctionDefinition proc = registerProcedure(name, code, type);
        return proc.name() + "()";
    }

    private String translateOutputs(FlowNode t) {
        ModelElementInstance ioMapping = t.getExtensionElements() != null ? t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "ioMapping") : null;
        if (ioMapping != null) {
            ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "output").stream().forEach(e -> registerGlobalVariable(e.getAttribute("target")));
            return ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "output").stream()
                    .map(e -> e.getAttribute("target") + "=" + feel.translateChecked(e.getAttribute("source").substring(1)))
                    .collect(Collectors.joining(";\n"));
        } else {
            return "";
        }
    }

    //////
    @Override
    protected String translateFlow(BPMNDecodedFlow<String> flow) {
        return translateFunction(registerProcedure(flow.name(),
                flow.code()
                + "//start:" + flow.firstStep().getId()
                + "//end:" + flow.lastStep().getId(),
                ProcType.FLOW));
    }

    @Override
    protected String translateCodeSequence(List<String> statements) {
        return statements.stream().collect(Collectors.joining(";\n"));
    }

    /////
    @Override
    protected String translateBpmn(List<String> processes_code) {
        return processes_code.stream().collect(Collectors.joining("\n\n"));
    }

    @Override
    protected String translateProcess(String name, List<String> flows_code) {
        return "\nclass bpmn_process_" + sanitizeName(name) + " {\n\n"
                + globals.values().stream()
                        .map(gd -> "Object " + gd.name())
                        .collect(Collectors.joining(";\n")) + ";\n\n"
                + functions.values().stream()
                        .flatMap(f -> f.values().stream())
                        .map(fd -> translateFunction(fd))
                        .collect(Collectors.joining("\n\n"))
                + "\n}\n\n";
    }

    ////TASKS
    @Override
    public String translateGenericTask(Task t) {
        return translateFunctionCall("task_generic_" + t.getName(), null, ProcType.TASK);
    }

    @Override
    public String translateManualTask(ManualTask t) {
        return translateFunctionCall("task_manual_" + t.getName(), null, ProcType.TASK);
    }

    @Override
    public String translateScriptTask(ScriptTask t) {
        return translateFunctionCall("task_script_" + t.getName(), null, ProcType.TASK);
    }

    @Override
    public String translateUserTask(UserTask t) {
        String outs = translateOutputs(t);
        return translateFunctionCall("task_user_" + t.getName(), ((!outs.isBlank()) ? outs + ";" : null), ProcType.TASK);
    }

    @Override
    public String translateServiceTask(ServiceTask t) {
        return translateFunctionCall("task_service_" + t.getName(), null, ProcType.TASK);
    }

    @Override
    public String translateSendTask(SendTask t) {
        return translateFunctionCall("task_send_" + t.getName(), null, ProcType.TASK);
    }

    @Override
    public String translateReceiveTask(ReceiveTask t) {
        return translateFunctionCall("task_receive_" + t.getName(), null, ProcType.TASK);
    }

    @Override
    public String translateBusinessRuleTask(BusinessRuleTask t) {

        ModelElementInstance ioMapping = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "ioMapping");
        ModelElementInstance calledDecision = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "calledDecision");

        String procName = sanitizeName("dmn_dtable_" + calledDecision.getAttributeValue("decisionId"));
        String output_record_name = procName + "_result";

        return (t.getName() != null ? "//" + t.getName() : "")
                + "\n\t" + output_record_name + " " + calledDecision.getAttributeValue("resultVariable")
                + "=" + procName + ".execute"
                + "(" + ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "input").stream()
                        .map(e -> "/*" + e.getAttribute("target") + "*/" + feel.translateChecked(e.getAttribute("source").substring(1))).collect(Collectors.joining(", "))
                + ");\n"
                + translateOutputs(t);
    }

    ////EVENTS
    @Override
    public String translateEndEvent(EndEvent t) {
        String code = "";
        Collection<EventDefinition> eventDefs = t.getEventDefinitions();
        for (EventDefinition eventDef : eventDefs) {
            if (eventDef instanceof ErrorEventDefinition eed) {
                code += "\nSystem.err.println(\"" + eed.getError().getName() + "\");"; //TODO: handle other event definitions here?
                try {
                    int error_code = Integer.valueOf(eed.getError().getErrorCode());
                    code += "\nSystem.exit(" + error_code + ");";
                } catch (NumberFormatException ex) {
                    //code is not a number
                    code += "\nSystem.exit(1);";
                }
            }
        }
        if (!code.isBlank()) {
            return translateFunctionCall("event_end_" + t.getName(), "//end: " + t.getName() + "\n" + code, ProcType.EVENT);
        } else {
            return "//end: " + t.getName() + "\nSystem.exit(0);";
        }
    }

    @Override
    public String translateStartEvent(StartEvent t) {
        String outs = translateOutputs(t);
        if (!outs.isBlank()) {
            return translateFunctionCall("event_start_" + t.getName(), "//start: " + t.getName() + "\n" + outs + ";", ProcType.EVENT);
        } else {
            return "//start: " + t.getName();
        }
        //inserire una condizione??
    }

    ////// GATEWAYS
    private String translateJoiningGateway(BPMNDecodedFlow<String> joinedflow) throws FeelTranslatorException {
        return translateFunctionCall(
                joinedflow.name(),
                joinedflow.code()
                + "//start:" + joinedflow.firstStep().getId()
                + "//end:" + joinedflow.lastStep().getId(),
                ProcType.FLOW);
//        FunctionDefinition proc = registerProcedure(name, code, ProcType.FLOW);
//        return proc.name() + "()";
    }

    @Override
    public String translateParallelJoiningGateway(ParallelGateway n, BPMNDecodedFlow<String> joinedflow) throws BpmnTranslatorException, FeelTranslatorException {
        String join_code = "while (!Arrays.stream(((FutureTask<Integer>[])" + sanitizeName(joinedflow.name()) + "_parallels)" + ").allMatch(t -> t.isDone())) { Thread.sleep(300); } ";
        FunctionDefinition proc = registerProcedure(
                joinedflow.name(), join_code + "\n\n" + joinedflow.code() + ";"+ "//start:" + joinedflow.firstStep().getId()
                + "//end:" + joinedflow.lastStep().getId(), ProcType.FLOW);
        return ""; //no join code at the end of the parallel flows
    }

    @Override
    public String translateInclusiveJoiningGateway(InclusiveGateway n, BPMNDecodedFlow<String> joinedflow) throws BpmnTranslatorException, FeelTranslatorException {
        return translateJoiningGateway(joinedflow);
    }

    @Override
    public String translateExclusiveJoiningGateway(ExclusiveGateway n, BPMNDecodedFlow<String> joinedflow) throws BpmnTranslatorException, FeelTranslatorException {
        return translateJoiningGateway(joinedflow);
    }

    @Override
    public String translateEventJoiningGateway(EventBasedGateway n, BPMNDecodedFlow<String> joinedflow) throws BpmnTranslatorException, FeelTranslatorException {
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
    public String translateParallelGateway(ParallelGateway n, List<BPMNDecodedConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
        String code = "";
        for (int o = 0; o < splitFlows.size(); ++o) {
//            String subflow_code = "";
//            if (splitFlows.get(o).condition() != null) { //no conditions in parallel gateways
//                subflow_code += "if " + "(" + feel.translate(splitFlows.get(o).condition().substring(1)) + ")";  //TEMP, dobbiamo togliere l'uguale se c'è altrimenti non è un'espressione feel
//                subflow_code += "{\n" + splitFlows.get(o).code() + "\n}";
//            } else {
//                subflow_code += splitFlows.get(o).code();
//            }
            registerGlobalVariable(splitFlows.get(o).name() + "_thread");
            code += splitFlows.get(o).name() + "_thread = new FutureTask<>(() -> {" + translateFunctionCall(splitFlows.get(o).name() + "_parallel", splitFlows.get(o).code(), ProcType.FLOW) + ";\n\n return 1;});"
                    + "\n" + splitFlows.get(o).name() + "_thread.run();"
                    + "\n";
        }
        code += "\nchiamata_a_funzione_join();"; //oppure continuiamo inline??? si potrebbe inserire già qui il codice della join gw, ma come?
        return code;
    }

    @Override
    public String translateInclusiveGateway(InclusiveGateway n, List<BPMNDecodedConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
        String code = "";
        for (int o = 0; o < splitFlows.size(); ++o) {
            code += "if "
                    + "(" + feel.translate(splitFlows.get(o).condition().substring(1)) + ")" + "{\n" //TEMP, dobbiamo togliere l'uguale se c'è altrimenti non è un'espressione feel
                    + splitFlows.get(o).code() + ";"
                    + "\n}";
        }
        return code;
    }

    @Override
    public String translateExclusiveGateway(ExclusiveGateway n, List<BPMNDecodedConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
        String code = "";
        BPMNDecodedConditionalFlow<String> default_branch = null;
        for (int o = 0; o < splitFlows.size(); ++o) {
            if (splitFlows.get(o).condition() != null) {
                if (!code.isBlank()) {
                    code += " else ";
                }
                code += "if " + "(" + feel.translate(splitFlows.get(o).condition().substring(1)) + ")";  //TEMP, dobbiamo togliere l'uguale se c'è altrimenti non è un'espressione feel
                code += "{\n" + splitFlows.get(o).code() + ";\n}";
            } else {
                default_branch = splitFlows.get(o);
            }
        }
        if (!code.isBlank()) {
            code += " else ";
        }
        if (default_branch != null) {
            code += "{\n" + default_branch.code() + "\n}";
        } else {
            code += """
                      {
                      //no default case
                      System.exit(9999);
                      }""";
            //////result += " { return null; }";
        }
        return code;
    }

    @Override
    public String translateEventGateway(EventBasedGateway n, List<BPMNDecodedConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
