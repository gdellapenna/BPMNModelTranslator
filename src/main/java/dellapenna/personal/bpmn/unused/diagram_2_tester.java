package dellapenna.personal.bpmn.unused;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

class ProcessStatus2 {

    String branchID;

    public ProcessStatus2(ProcessStatus2 parent) {
        this.branchID = parent.branchID;
    }

    public ProcessStatus2(String branchID) {
        this.branchID = branchID;
    }

    public ProcessStatus2() {
        this.branchID = "";
    }

};

class ProcessUtils2 {

    static java.io.PrintStream debugChannel = System.out;
    static java.io.PrintStream resultChannel = System.out;
    static java.util.Properties outputs = new java.util.Properties();
    static java.util.Properties inputs = new java.util.Properties();

    static Map<String, List<String>> parallels = new HashMap<>();
    // {parallel_gateway_x -> [thread_1,thread_2], ... }
    //hyp: parallels are joined in lifo order. a parallel-of-parallel cannot close the outermost one.
    static int thread_count = 0;

    static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void start() {
        java.io.File inputs_file = new java.io.File("inputs.properties");
        if (inputs_file.canRead()) {
            try {
                inputs.load(new java.io.FileReader(inputs_file));
            } catch (java.io.IOException ex) {

            }
        }
    }

    public static void end() {
        java.io.File outputs_file = new java.io.File("outputs.properties");
        try {
            outputs.store(new java.io.FileWriter(outputs_file), null);
        } catch (java.io.IOException ex) {
            //
        }
        System.exit(Integer.parseInt(outputs.getProperty("code", "0")));
    }

    public static void fork(ProcessStatus2 s, String parallel_gateway_id, Consumer<ProcessStatus2>... branches) {
        String parallel_id = s.branchID + "-" + parallel_gateway_id;
        if (!parallels.containsKey(parallel_id)) {
            parallels.put(parallel_id, new ArrayList<>());
        }

        String[] branch_ids = new String[branches.length];
        for (int i = 0; i < branches.length; ++i) {
            String branch_id = parallel_id + "-" + (++thread_count);
            branch_ids[i] = branch_id;
            parallels.get(parallel_id).add(branch_id);
        }
        for (int i = 0; i < branches.length; ++i) {
            ProcessUtils2.debugOutput("FORKING BRANCH: %s FROM PARALLEL %s", branch_ids[i], parallel_id);
            //shoud be run in parallel
            final Consumer<ProcessStatus2> branch = branches[i];
            final String branch_id = branch_ids[i];
            executor.submit(() -> branch.accept(new ProcessStatus2(branch_id)));
        }
    }

    public static void join(ProcessStatus2 s, Consumer<ProcessStatus2> branch) {
        int delimiter_pos = s.branchID.lastIndexOf("-");
        String parallel_id = s.branchID.substring(0, delimiter_pos);
        String branch_id = s.branchID;
        String parallel_gateway_id = parallel_id.substring(parallel_id.lastIndexOf("-") + 1);

        ProcessUtils2.debugOutput("JOINING BRANCH: %s OF PARALLEL %s STARTED FROM GATEWAY %s", branch_id, parallel_id, parallel_gateway_id);
        parallels.get(parallel_id).remove(branch_id);
        if (parallels.get(parallel_id).isEmpty()) {
            branch.accept(new ProcessStatus2(parallel_id.substring(0, parallel_id.lastIndexOf("-"))));
        }
    }

    public static void endCurrentThread() {
    }

    public static void error(String s, int c) {
        ProcessUtils2.debugOutput("ERROR: %s", s);
        ProcessUtils2.logResult(false, s, c);
        ProcessUtils2.end();
    }

    public static void noDefaultCaseError() {
        error("No default branch in gateway", 9999);
    }

    public static void success(String s, int c) {
        if (s != null) {
            ProcessUtils2.debugOutput("SUCCESS: %s", s);
        } else {
            ProcessUtils2.debugOutput("SUCCESS");
        }
        ProcessUtils2.logResult(true, s, c);
        ProcessUtils2.end();
    }

    public static void success() {
        success(null, 0);
    }

    public static void debugOutput(String s, Object... args) {
        String message = String.format(s, args);
        debugChannel.println(message);
    }

    public static void logInput(String name, Object value) {
        resultChannel.println(name + "=" + value);
        outputs.setProperty(name, (value != null ? value.toString() : "<NULL>"));
    }

    public static void logResult(boolean success, String message, int code) {
        resultChannel.println(success ? "SUCCESS" : "FAILURE" + "," + code + "," + message);
        outputs.setProperty("output_success", success ? "true" : "false");
        outputs.setProperty("output_message", message != null ? message : "");
        outputs.setProperty("output_code", String.valueOf(code));
    }
}

/*
 * ****************************** BPMN Generated Code *************************
 */
class diagram_2_tester {

//Input Variables
    ;



//Process Variables
;



//Process Dynamics
public void GATEWAY_Exclusive1(ProcessStatus2 s) {//exclusive gateway
        ProcessUtils2.debugOutput("EXCLUSIVE GATEWAY Exclusive1");
        if ("a".equals(1.0)) {
            TASK_Task2Branch1(s);
        } else {
            TASK_Task2DefaultBranch(s);
        }
    }

    public void GATEWAY_Parallel1Join(ProcessStatus2 s) {
        ProcessUtils2.join(s, this::TASK_Final_Task);
        ProcessUtils2.endCurrentThread();
    }

    public void GATEWAY_Parallel1Split(ProcessStatus2 s) {//parallel split
        ProcessUtils2.debugOutput("PARALLEL GATEWAY Parallel1Split");
        ProcessUtils2.fork(s, "Parallel1Split", this::TASK_Parallel_Task_1, this::TASK_Parallel_Task_2);
        ProcessUtils2.endCurrentThread();
    }

    public void EVENT_Start(ProcessStatus2 s) {//start event: Start
        ProcessUtils2.debugOutput("START EVENT: Start");
        GATEWAY_Parallel1Split(s);
    }

    public void EVENT_End(ProcessStatus2 s) {//end event: End
        ProcessUtils2.success();
    }

    public void TASK_Task2DefaultBranch(ProcessStatus2 s) {//generic task: Task2DefaultBranch
        ProcessUtils2.debugOutput("TASK Task2DefaultBranch");
        GATEWAY_Parallel1Join(s);
    }

    public void TASK_Parallel_Task_2(ProcessStatus2 s) {//generic task: Parallel Task 2
        ProcessUtils2.debugOutput("TASK Parallel Task 2");
        GATEWAY_Exclusive1(s);
    }

    public void TASK_Parallel_Task_1(ProcessStatus2 s) {//generic task: Parallel Task 1
        ProcessUtils2.debugOutput("TASK Parallel Task 1");
        GATEWAY_Parallel1Join(s);
    }

    public void TASK_Final_Task(ProcessStatus2 s) {//generic task: Final Task
        ProcessUtils2.debugOutput("TASK Final Task");
        EVENT_End(s);
    }

    public void TASK_Task2Branch1(ProcessStatus2 s) {//generic task: Task2Branch1
        ProcessUtils2.debugOutput("TASK Task2Branch1");
        GATEWAY_Parallel1Join(s);
    }

    public void init() {
    }

    public static void main(String[] args) {
        ProcessUtils2.start();
        diagram_2_tester process = new diagram_2_tester();
        process.init();
        ProcessStatus2 s = new ProcessStatus2();
        process.EVENT_Start(s);
    }
}
