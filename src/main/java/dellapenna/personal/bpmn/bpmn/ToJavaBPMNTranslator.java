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
import org.camunda.bpm.model.bpmn.instance.EventDefinition;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.ManualTask;
import org.camunda.bpm.model.bpmn.instance.ReceiveTask;
import org.camunda.bpm.model.bpmn.instance.ScriptTask;
import org.camunda.bpm.model.bpmn.instance.SendTask;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

//TODO le variabili di output devono essere globali?!? Ma non possiamo determinarne il tipo! Facciamo object?!? e poi con gli operatori?
public class ToJavaBPMNTranslator extends AbstractBPMNTranslator<String> {

    private final static String ZEEBENS = "http://camunda.org/schema/zeebe/1.0";

    private static final ToJavaFeelTranslator feel = new ToJavaFeelTranslator();
    private final Map<String, FunctionDefinition> functions = new HashMap<>();
    private final Map<String, GlobalVariableDefinition> globals = new HashMap<>();

    @Override
    protected void reset() {
        functions.clear();
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

    private FunctionDefinition registerProcedure(String name, String code) {
        if (code == null || code.isBlank()) {
            code = "\tSystem.out.println(\"" + name + "\");\n";
            /*+ "\treturn null;"*/
        }
        return registerFunction(name, code, Void.class);
    }

    private FunctionDefinition registerFunction(String name, String code, Class returnType) {
        FunctionDefinition f = new FunctionDefinition(sanitizeName(name), code, returnType, Collections.EMPTY_MAP);
        functions.put(name, f);
        return f;
    }

    private String sanitizeName(String n) {
        return n.replaceAll("[^A-Za-z0-9_]", "_");
    }

    private String outputFunctionCode(FunctionDefinition f) {
        return "public " + (f.returnType().equals(Void.class) ? "void" : f.returnType().getName()) + " " + f.name() + "("
                + f.parameters().entrySet().stream().map(e -> e.getValue().getName() + " " + e.getKey()).collect(Collectors.joining(", ")) //convert types?
                + ") {\n"
                + f.body()
                + "\n}";
    }

    @Override
    protected String finalizeProcessTranslation(String name) {
        return "\nclass bpmn_p_" + sanitizeName(name) + " {\n\n"
                + globals.values().stream()
                        .map(gd -> "Object " + gd.name())
                        .collect(Collectors.joining(";\n")) + ";\n\n"
                + functions.values().stream()
                        .map(fd -> outputFunctionCode(fd))
                        .collect(Collectors.joining("\n\n"))
                + "\n}\n\n";
    }

    @Override
    protected String finalizeModelTranslation(List<String> processes) {
        return processes.stream().collect(Collectors.joining("\n\n"));
    }

    private String translateOutputs(FlowNode t) {
        ModelElementInstance ioMapping = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "ioMapping");
        if (ioMapping != null) {

            ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "output").stream().forEach(e -> registerGlobalVariable(e.getAttribute("target")));

            return ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "output").stream()
                    .map(e -> /*"\tvar " + */ e.getAttribute("target") + "=" + feel.translateChecked(e.getAttribute("source").substring(1)))
                    .collect(Collectors.joining(";\n"));
        } else {
            return "";
        }
    }

    @Override
    protected String translateNamedFlow(String name, String code) {
        return outputFunctionCode(registerProcedure(name, code));
    }

    @Override
    protected String translateSequence(List<String> statements) {
        return statements.stream().collect(Collectors.joining(";\n"));
    }

    /////TASKS
    private String internal_TranslateAsFunction(String name, String code) {
        FunctionDefinition proc = registerProcedure(name, code);
        return proc.name() + "()";
    }

    @Override
    protected String translateGenericTask(Task t) {
        return internal_TranslateAsFunction("t_g_" + t.getName(), null);
    }

    @Override
    protected String translateManualTask(ManualTask t) {
        return internal_TranslateAsFunction("t_m_" + t.getName(), null);
    }

    @Override
    protected String translateScriptTask(ScriptTask t) {
        return internal_TranslateAsFunction("t_s_" + t.getName(), null);
    }

    @Override
    protected String translateUserTask(UserTask t) {
        String outs = translateOutputs(t);
        return internal_TranslateAsFunction("t_u_" + t.getName(), ((!outs.isBlank()) ? outs + ";" : null));
    }

    @Override
    protected String translateServiceTask(ServiceTask t) {
        return internal_TranslateAsFunction("t_a_" + t.getName(), null);
    }

    @Override
    protected String translateSendTask(SendTask t) {
        return internal_TranslateAsFunction("t_send_" + t.getName(), null);
    }

    @Override
    protected String translateReceiveTask(ReceiveTask t) {
        return internal_TranslateAsFunction("t_recv_" + t.getName(), null);
    }

    @Override
    protected String translateBusinessRuleTask(BusinessRuleTask t) {

        ModelElementInstance ioMapping = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "ioMapping");
        ModelElementInstance calledDecision = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "calledDecision");

        String procName = sanitizeName("dmn_" + calledDecision.getAttributeValue("decisionId"));
        String output_record_name = procName + "_result";

        return (t.getName() != null ? "//" + t.getName() : "")
                + "\n\t" + output_record_name + " " + calledDecision.getAttributeValue("resultVariable")
                + "=" + procName + ".execute"
                + "(" + ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "input").stream()
                        .map(e -> "/*" + e.getAttribute("target") + "*/" + feel.translateChecked(e.getAttribute("source").substring(1))).collect(Collectors.joining(", "))
                + ");\n"
                + translateOutputs(t);
//                + ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "output").stream()
//                        .map(e -> "\tvar " + e.getAttribute("target") + "=" + feel.translateChecked(e.getAttribute("source").substring(1)))
//                        .collect(Collectors.joining(";\n"));
    }

    @Override
    protected String translateEndEvent(EndEvent t) {
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
            return internal_TranslateAsFunction("e_e_" + t.getName(), "//end: " + t.getName() + "\n" + code);
        } else {
            return "//end: " + t.getName() + "\nSystem.exit(0);";
        }
    }

    @Override
    protected String translateStartEvent(StartEvent t) {
        String outs = translateOutputs(t);
        if (!outs.isBlank()) {
            return internal_TranslateAsFunction("e_s_" + t.getName(), "//start: " + t.getName() + "\n" + outs + ";");
        } else {
            return "//start: " + t.getName();
        }
        //inserire una condizione??
    }

    ////// GATEWAYS
    private String translateJoiningGateway(String name, String code) throws FeelTranslatorException {
        FunctionDefinition proc = registerProcedure(name, code);
        return /*"return " +*/ proc.name() + "()";
    }

    @Override
    protected String translateParallelJoiningGateway(BPMNDecodedNamedFlow<String> joinedflow) throws FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected String translateEventJoiningGateway(BPMNDecodedNamedFlow<String> joinedflow) throws FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected String translateInclusiveJoiningGateway(BPMNDecodedNamedFlow<String> joinedflow) throws FeelTranslatorException {
        return translateJoiningGateway(joinedflow.name(), joinedflow.code());
    }

    @Override
    protected String translateExclusiveJoiningGateway(BPMNDecodedNamedFlow<String> joinedflow) throws FeelTranslatorException {
        return translateJoiningGateway(joinedflow.name(), joinedflow.code());
    }

    @Override
    protected String translateParallelGateway(List<BPMNDecodedConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected String translateEventGateway(List<BPMNDecodedConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected String translateInclusiveGateway(List<BPMNDecodedConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
        String result = "";
        for (int o = 0; o < splitFlows.size(); ++o) {
            result += "if "
                    + "(" + feel.translate(splitFlows.get(o).condition().substring(1)) + ")" + "{\n" //TEMP, dobbiamo togliere l'uguale se c'è altrimenti non è un'espressione feel
                    + splitFlows.get(o).code()+";"
                    + "\n}";
        }
        return result;
    }

    @Override
    protected String translateExclusiveGateway(List<BPMNDecodedConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
        String result = "";
        BPMNDecodedConditionalFlow<String> default_branch = null;
        for (int o = 0; o < splitFlows.size(); ++o) {
            if (splitFlows.get(o).condition() != null) {
                if (!result.isBlank()) {
                    result += " else ";
                }
                result += "if " + "(" + feel.translate(splitFlows.get(o).condition().substring(1)) + ")";  //TEMP, dobbiamo togliere l'uguale se c'è altrimenti non è un'espressione feel
                result += "{\n" + splitFlows.get(o).code() + ";\n}";
            } else {
                default_branch = splitFlows.get(o);
            }
        }
        if (!result.isBlank()) {
            result += " else ";
        }
        if (default_branch != null) {
            result += "{\n" + default_branch.code() + "\n}";
        } else {
            result += """
                      {
                      //no default case
                      System.exit(9999);
                      }""";
            //////result += " { return null; }";
        }
        return result;
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