package dellapenna.personal.bpmn.exec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class BPMNExecProcessUtils {

    public static class ProcessStatus {

        String branchID;

        public ProcessStatus(ProcessStatus parent) {
            this.branchID = parent.branchID;
        }

        public ProcessStatus(String branchID) {
            this.branchID = branchID;
        }

        public ProcessStatus() {
            this.branchID = "";
        }

    };

    static java.io.PrintStream debugChannel = System.out;
    static java.io.PrintStream resultChannel = System.out;
    public static java.util.Properties outputs = new java.util.Properties();
    public static java.util.Properties inputs = new java.util.Properties();

    static Map<String, List<String>> parallels = new HashMap<>();
    static int parallel_branch_count = 0;
    static ExecutorService executor = null;

    public static void enableTrueParallel() {
        executor = Executors.newFixedThreadPool(10);
    }

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

    public static void fork(ProcessStatus s, String parallel_gateway_id, Consumer<ProcessStatus>... branches) {
        String parallel_id = s.branchID + "-" + parallel_gateway_id;
        if (!parallels.containsKey(parallel_id)) {
            parallels.put(parallel_id, new ArrayList<>());
        }

        String[] branch_ids = new String[branches.length];
        for (int i = 0; i < branches.length; ++i) {
            String branch_id;
            synchronized (BPMNExecProcessUtils.class) {
                branch_id = parallel_id + "-" + (++parallel_branch_count);
                branch_ids[i] = branch_id;
                parallels.get(parallel_id).add(branch_id);
            }
        }
        for (int i = 0; i < branches.length; ++i) {
            debugOutput("FORKING BRANCH: %s FROM PARALLEL %s", branch_ids[i], parallel_id);
            final Consumer<ProcessStatus> branch = branches[i];
            final String branch_id = branch_ids[i];
            if (executor != null) {
                executor.submit(() -> branch.accept(new ProcessStatus(branch_id)));
            } else {
                branches[i].accept(new ProcessStatus(branch_ids[i]));
            }

        }
    }

    public static void join(ProcessStatus s, Consumer<ProcessStatus> branch) {
        int delimiter_pos = s.branchID.lastIndexOf("-");
        String parallel_id = s.branchID.substring(0, delimiter_pos);
        String branch_id = s.branchID;
        String parallel_gateway_id = parallel_id.substring(parallel_id.lastIndexOf("-") + 1);

        debugOutput("JOINING BRANCH: %s OF PARALLEL %s STARTED FROM GATEWAY %s", branch_id, parallel_id, parallel_gateway_id);
        synchronized (BPMNExecProcessUtils.class) {
            parallels.get(parallel_id).remove(branch_id);
            if (parallels.get(parallel_id).isEmpty()) {
                branch.accept(new ProcessStatus(parallel_id.substring(0, parallel_id.lastIndexOf("-"))));
            }
        }
    }

    public static void endCurrentThread() {
    }

    public static void error(String s, int c) {
        debugOutput("ERROR: %s", s);
        logResult(false, s, c);
        end();
    }

    public static void noDefaultCaseError() {
        error("No default branch in gateway", 9999);
    }

    public static void success(String s, int c) {
        if (s != null) {
            debugOutput("SUCCESS: %s", s);
        } else {
            debugOutput("SUCCESS");
        }
        logResult(true, s, c);
        end();
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
