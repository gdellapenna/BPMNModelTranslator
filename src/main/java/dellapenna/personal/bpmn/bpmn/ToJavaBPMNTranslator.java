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
    public String generateBpmnSource(BPMNDecoded<String> bpmn) {
        return bpmn.processes().stream()
                .map(p -> generateProcessSource(p))
                .collect(Collectors.joining("\n\n"));
    }

    //generates the source code for a complete BPMN process
    protected String generateProcessSource(BPMNDecodedProcess<String> process) {
        String globals = process.body().stream()
                .flatMap(flow -> flow.code().getGlobals().values().stream()
                .map(gd -> "Object " + gd.name()))
                .collect(Collectors.joining(";\n")) + "; ";
        String functions = process.body().stream()
                .flatMap(flow -> flow.code().getFunctions().values().stream()
                .flatMap(fc -> fc.values().stream())
                .map(fd -> generateFunctionSource(fd)))
                .collect(Collectors.joining("\n\n")) + "; ";

        return " class bpmn_process_" + sanitizeName(process.name()) + " { "
                + globals + functions + "}";
    }

    //generates the source code for a given function definition
    private String generateFunctionSource(FunctionDefinition<String> f) {
        return "public " + (f.returnType().equals(Void.class) ? "void" : f.returnType().getName()) + " " + f.name() + "("
                + f.parameters().entrySet().stream().map(e -> e.getValue().getName() + " " + e.getKey()).collect(Collectors.joining(", ")) //convert types?
                + ") {" + generateCodeSource(f.body()) + "}";
    }

    //generates the text source for a code block    
    public String generateCodeSource(Code<String> code) {
        return code.getStatements().stream().collect(Collectors.joining(";\n", "", ";\n"));
    }

    //////////////////
    // Generate code for specific BPMN nodes
    //////////////////
    //TASKS
    @Override
    public Code<String> generateGenericTaskCode(Task t) {
        return generateProcedureCallCode("task_generic_" + t.getName(), null, Code.ProcType.TASK);
    }

    @Override
    public Code<String> generateManualTaskCode(ManualTask t) {
        return generateProcedureCallCode("task_manual_" + t.getName(), null, Code.ProcType.TASK);
    }

    @Override
    public Code<String> generateScriptTaskCode(ScriptTask t) {
        return generateProcedureCallCode("task_script_" + t.getName(), null, Code.ProcType.TASK);
    }

    @Override
    public Code<String> generateServiceTaskCode(ServiceTask t) {
        return generateProcedureCallCode("task_service_" + t.getName(), null, Code.ProcType.TASK);
    }

    @Override
    public Code<String> generateSendTaskCode(SendTask t) {
        return generateProcedureCallCode("task_send_" + t.getName(), null, Code.ProcType.TASK);
    }

    @Override
    public Code<String> generateReceiveTaskCode(ReceiveTask t) {
        return generateProcedureCallCode("task_receive_" + t.getName(), null, Code.ProcType.TASK);
    }

    @Override
    public Code<String> generateUserTaskCode(UserTask t) {
        Code<String> outs = generateOutputAssignemntsCode(t);
        return generateProcedureCallCode("task_user_" + t.getName(), ((!outs.isEmpty()) ? outs : null), Code.ProcType.TASK);
    }

    @Override
    public Code<String> generateBusinessRuleTaskCode(BusinessRuleTask t) {
        ModelElementInstance ioMapping = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "ioMapping");
        ModelElementInstance calledDecision = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "calledDecision");

        String procName = sanitizeName("dmn_dtable_" + calledDecision.getAttributeValue("decisionId"));
        String output_record_name = procName + "_result";

        Code<String> code = new Code<>(
                t.getName() != null ? "//" + t.getName() : "",
                output_record_name + " " + calledDecision.getAttributeValue("resultVariable")
                + "=" + procName + ".execute"
                + "(" + ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "input").stream()
                        .map(e -> "/*" + e.getAttribute("target") + "*/" + feel.translateChecked(e.getAttribute("source").substring(1))).collect(Collectors.joining(", "))
                + ")");

        code.append(generateOutputAssignemntsCode(t));
        return code;

    }

    //EVENTS
    @Override
    public Code<String> generateEndEventCode(EndEvent t) {
        Code<String> code = new Code<>();
        Collection<EventDefinition> eventDefs = t.getEventDefinitions();
        for (EventDefinition eventDef : eventDefs) {
            if (eventDef instanceof ErrorEventDefinition eed) {
                code.append("System.err.println(\"" + eed.getError().getName() + "\")"); //TODO: handle other event definitions here?
                try {
                    int error_code = Integer.valueOf(eed.getError().getErrorCode());
                    code.append("System.exit(" + error_code + ")");
                } catch (NumberFormatException ex) {
                    //code is not a number
                    code.append("System.exit(1)");
                }
            }
        }

        if (code.isEmpty()) {
            code.append("System.exit(0)");
        }
        code.prepend("//end event: " + t.getName());
        return code;
    }

    @Override
    public Code<String> generateStartEventCode(StartEvent t) {
        Code<String> code = generateOutputAssignemntsCode(t);
        code.prepend("//start event: " + t.getName());
        return code;
    }

////// GATEWAYS
    private Code<String> generateJoiningGatewayCode(FlowNode joinedflow) throws FeelTranslatorException {
        return generateNodeJointCode(joinedflow);
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
    public Code<String> generateParallelJoiningGatewayCode(ParallelGateway n, FlowNode joinedflow) throws BpmnTranslatorException, FeelTranslatorException {
//        String join_code = "while (!Arrays.stream(((FutureTask<Integer>[])" + sanitizeName(joinedflow.name()) + "_parallels)" + ").allMatch(t -> t.isDone())) { Thread.sleep(300); } ";
//        FunctionDefinition proc = registerProcedure(
//                joinedflow.name(), join_code + "\n\n" + joinedflow.code() + ";" + "//start:" + joinedflow.firstStep().getId()
//                + "//end:" + joinedflow.lastStep().getId(), ProcType.FLOW);
        return new Code<>("ProcessUtils.signal(ID DEL NODO ENTRANTE)");
    }

    @Override
    public Code<String> generateInclusiveJoiningGatewayCode(InclusiveGateway n, FlowNode joinedflow) throws BpmnTranslatorException, FeelTranslatorException {
        return generateJoiningGatewayCode(joinedflow);
    }

    @Override
    public Code<String> generateExclusiveJoiningGatewayCode(ExclusiveGateway n, FlowNode joinedflow) throws BpmnTranslatorException, FeelTranslatorException {
        return generateJoiningGatewayCode(joinedflow);
    }

    @Override
    public Code<String> generateEventJoiningGatewayCode(EventBasedGateway n, FlowNode joinedflow) throws BpmnTranslatorException, FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

//    @Override
//    public String generateParallelGatewayCode(ParallelGateway n, List<BPMNDecodedConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
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
    public Code<String> generateParallelGatewayCode(ParallelGateway n, List<BPMNDecodedConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
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
            code.add(generateCodeSource(new Code<>("ProcessUtils.fork(" + generateNodeJointCode(splitFlows.get(o).firstStep())))); //start parallel tasks
        }
        //code += "\nchiamata_a_funzione_join();"; //oppure continuiamo inline??? si potrebbe inserire già qui il codice della join gw, ma come?

        return new Code();
    }

    @Override
    public Code<String> generateInclusiveGatewayCode(InclusiveGateway n, List<BPMNDecodedConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
        Code<String> result = new Code<>();
        String code = "";
        for (int o = 0; o < splitFlows.size(); ++o) {
            Code<String> splitCode = generateNodeJointCode(splitFlows.get(o).firstStep());
            result.append(splitCode);
            code += "if "
                    + "(" + feel.translate(splitFlows.get(o).condition().substring(1)) + ")" //TEMP, dobbiamo togliere l'uguale se c'è altrimenti non è un'espressione feel
                    + "{" + generateCodeSource(splitCode)
                    + "} ";
        }
        result.append(code);
        return result;
    }

    @Override
    public Code<String> generateExclusiveGatewayCode(ExclusiveGateway n, List<BPMNDecodedConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
        Code<String> result = new Code<>();
        String code = "";
        BPMNDecodedConditionalFlow<String> default_branch = null;
        for (int o = 0; o < splitFlows.size(); ++o) {
            Code<String> splitCode = generateNodeJointCode(splitFlows.get(o).firstStep());
            result.append(splitCode);            
            if (splitFlows.get(o).condition() != null) {
                if (!code.isBlank()) {
                    code += " else ";
                }
                code += "if " + "(" + feel.translate(splitFlows.get(o).condition().substring(1)) + ")";  //TEMP, dobbiamo togliere l'uguale se c'è altrimenti non è un'espressione feel
                code += "{" + generateCodeSource(splitCode) + "}";
            } else {
                default_branch = splitFlows.get(o);
            }
        }
        if (!code.isBlank()) {
            code += " else ";
        }
        if (default_branch != null) {
            Code<String> splitCode = generateNodeJointCode(default_branch.firstStep());
            result.append(splitCode);            
            code += "{" + generateCodeSource(splitCode) + "}";
        } else {
            code += "{" + generateCodeSource(generateNodeJointCode("ProcessUtils.NoDefaultError")) + "}";
//            code += """
//                      {
//                      //no default case
//                      System.exit(9999);
//                      }""";
            //////result += " { return null; }";
        }
        result.append(code);
        return result;
    }

    @Override
    public Code<String> generateEventGatewayCode(EventBasedGateway n, List<BPMNDecodedConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected Code<String> generateNodeJointCode(FlowNode flowStart) {
        return generateNodeJointCode(getFlowName(flowStart));
    }

    protected Code<String> generateNodeJointCode(String flowName) {
        return new Code<>(sanitizeName(flowName) + "()");
    }

    //////////////
    // Code generation utilities
    //////////////
    //generates the code to call a function, registering it if needed
    private Code<String> generateProcedureCallCode(String name, Code<String> code, Code.ProcType type) {
        Code<String> result = new Code<>();
        FunctionDefinition proc = result.registerProcedure(name, code, type);
        result.append(proc.name() + "()");
        return result;

    }

    //generates the code to capture the output of a node, as a set of variable assignments
    private Code<String> generateOutputAssignemntsCode(FlowNode t) {
        Code<String> result = new Code<>();
        ModelElementInstance ioMapping = t.getExtensionElements() != null ? t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "ioMapping") : null;
        if (ioMapping != null) {
            ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "output").stream().forEach(e -> result.registerGlobalVariable(e.getAttribute("target")));
            result.append(ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "output").stream()
                    .map(e -> e.getAttribute("target") + "=" + feel.translateChecked(e.getAttribute("source").substring(1)))
                    .collect(Collectors.toList()));
        }
        return result;
    }

//    //generate the code for a decoded flow
//    protected Code<String> generateFlowCode(BPMNDecodedFlow<String> flow) {
//        Code<String> result = new Code<>();
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
