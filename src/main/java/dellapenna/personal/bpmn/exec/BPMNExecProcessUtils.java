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
            this.branchID = "Main";
        }

    };

    static boolean globalSuccess = true;

    public static java.io.PrintStream debugChannel = System.out;
    static java.io.PrintStream resultChannel = System.out;
    public static java.util.Properties outputs = new java.util.Properties();
    public static java.util.Properties inputs = new java.util.Properties();

    static Map<String, List<String>> parallels = new HashMap<>();
    static int parallel_branch_count = 0;
    static Integer active_threads_count = 0;
    static ExecutorService executor = null;

    public static void enableTrueParallel() {
        executor = Executors.newFixedThreadPool(10);
    }

    public static void loadExternalInputs() {
        java.io.File inputs_file = new java.io.File("inputs.properties");
        if (inputs_file.canRead()) {
            try {
                inputs.load(new java.io.FileReader(inputs_file));
            } catch (java.io.IOException ex) {

            }
        }
    }

    public static void saveExternalOutputs() {
        java.io.File outputs_file = new java.io.File("outputs.properties");
        try {
            outputs.store(new java.io.FileWriter(outputs_file), null);
        } catch (java.io.IOException ex) {
            //
        }
    }

    public static void executeProcess(Runnable init, Consumer<ProcessStatus> start) {
        ProcessStatus s = new ProcessStatus();
        if (init != null) {
            debugOutput("INITIALIZING PROCESS");
            loadExternalInputs();
            init.run();
        }
        if (start != null) {
            debugOutput("STARTING PROCESS");
            startThread(start, s.branchID);
        }
        while (active_threads_count > 0) {
            try {
                Thread.sleep(10);
//            try {
//                active_threads_count.wait();
//            } catch (InterruptedException ex) {
//                debugOutput("INTERNAL ERROR: THREAD INTERRUPTED");
//            }
            } catch (InterruptedException ex) {
                //
            }
        }
        logResult(s, globalSuccess, null, 0);
        saveExternalOutputs();
        if (executor != null) {
            executor.shutdown(); //forse viene invocato troppo presto? bisogna esser certi che i branch thread siano terminati...
        }
        debugOutput("ENDING PROCESS");
    }

//    public static void initProcess(ProcessStatus s, Runnable main) {
//        debugOutput("INITIALIZING PROCESS");
//        loadExternalInputs();
//        main.run();
//    }
//
//    public static void startProcess(ProcessStatus s, Consumer<ProcessStatus> main) {
//        debugOutput("STARTING PROCESS");
//        //main.accept(s); //andrebbe lanciato in un thread... indagare perchè si blocca sul sync...
//        startThread(main, s.branchID);
//    }
//
//    public static void endProcess(ProcessStatus s) {
//        //non può essere synchronized altrimenti bloccherebbe tutto... meglio usare un semaforo?        
//        while (active_threads_count > 0) {
//            try {
//                Thread.sleep(10);
////            try {
////                active_threads_count.wait();
////            } catch (InterruptedException ex) {
////                debugOutput("INTERNAL ERROR: THREAD INTERRUPTED");
////            }
//            } catch (InterruptedException ex) {
//                //
//            }
//        }
//
//        logResult(s, globalSuccess, null, 0);
//        saveExternalOutputs();
//        if (executor != null) {
//            executor.shutdown(); //forse viene invocato troppo presto? bisogna esser certi che i branch thread siano terminati...
//        }
//        debugOutput("ENDING PROCESS");
//        //System.exit(Integer.parseInt(outputs.getProperty("code", "0")));
//    }
    public static void startThread(Consumer<ProcessStatus> branch, String branch_id) {
        synchronized (BPMNExecProcessUtils.class) {
            active_threads_count++;
            //active_threads_count.notifyAll();
        }
        if (executor != null) {
            executor.submit(() -> branch.accept(new ProcessStatus(branch_id)));
        } else {
            branch.accept(new ProcessStatus(branch_id));
        }
    }

    public static void startThread(Runnable branch, String branch_id) {
        synchronized (BPMNExecProcessUtils.class) {
            active_threads_count++;
            //active_threads_count.notifyAll();
        }
        if (executor != null) {
            executor.submit(branch);
        } else {
            branch.run();
        }
    }

    public static void stopThread() {
        synchronized (BPMNExecProcessUtils.class) {
            active_threads_count--;
            //active_threads_count.notifyAll();
        }
        if (executor != null) {
            Thread.currentThread().interrupt();
        }
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
            debugOutput("\t FORKING BRANCH: %s FROM PARALLEL %s", branch_ids[i], parallel_id);
            final Consumer<ProcessStatus> branch = branches[i];
            final String branch_id = branch_ids[i];
            startThread(branch, branch_id);
        }
    }

    public static void join(ProcessStatus s, Consumer<ProcessStatus> join_branch) {
        int delimiter_pos = s.branchID.lastIndexOf("-");
        String parallel_id = s.branchID.substring(0, delimiter_pos);
        String branch_id = s.branchID;
        String parallel_gateway_id = parallel_id.substring(parallel_id.lastIndexOf("-") + 1);

        debugOutput("\t JOINING BRANCH: %s OF PARALLEL %s STARTED FROM GATEWAY %s", branch_id, parallel_id, parallel_gateway_id);
        synchronized (BPMNExecProcessUtils.class) {
            parallels.get(parallel_id).remove(branch_id);
            if (parallels.get(parallel_id).isEmpty()) {
                parallels.remove(parallel_id);
                String parent_branch_id = parallel_id.substring(0, parallel_id.lastIndexOf("-"));
                startThread(join_branch, parent_branch_id);
            }
            //endCurrentBranch();
            stopThread();
        }
    }

//    public static void endCurrentBranch() {
//        stopThread();
//    }
    public static void error(ProcessStatus s, String m, int c) {
        debugOutput("\t ERROR %s ON BRANCH %s", m, s.branchID);
        globalSuccess &= false;
        logResult(s, false, m, c);
        //endCurrentBranch();
        stopThread();
    }

    public static void noDefaultCaseError(ProcessStatus s) {
        error(s, "No default branch in gateway", 9999);
    }

    public static void success(ProcessStatus s, String m, int c) {
        if (m != null) {
            debugOutput("\t SUCCESS %s ON BRANCH %s", m, s.branchID);
        } else {
            debugOutput("\t SUCCESS ON BRANCH %s", s.branchID);
        }
        globalSuccess &= true;
        logResult(s, true, m, c);
        //endCurrentBranch();
        stopThread();
    }

    public static void success(ProcessStatus s) {
        success(s, null, 0);
    }

    public static void debugOutput(String s, Object... args) {
        String message = String.format(s, args);
        debugChannel.println(message);
    }

    public static void logInput(String name, Object value) {
        resultChannel.println("\t " + name + "=" + value);
        outputs.setProperty(name, (value != null ? value.toString() : "<NULL>"));
    }

    public static void logResult(ProcessStatus s, boolean success, String message, int code) {
        resultChannel.println(s.branchID + ":" + (success ? "SUCCESS" : "FAILURE" + "," + code + "," + message));
        outputs.setProperty(s.branchID + ":output_success", success ? "true" : "false");
        outputs.setProperty(s.branchID + ":output_message", message != null ? message : "");
        outputs.setProperty(s.branchID + ":output_code", String.valueOf(code));
    }
}
