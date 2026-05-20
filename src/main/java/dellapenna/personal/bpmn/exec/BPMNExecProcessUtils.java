package dellapenna.personal.bpmn.exec;

import java.io.FileNotFoundException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class BPMNExecProcessUtils {

    public static interface Message {

    };

    public static class ProcessStatus {

        public enum BoundaryRole {
            NORMAL("Standard"), DISPATCHER(""), BOUNDARYWATCHER("Boundary");

            private final String flowVariantlabel;

            BoundaryRole(String flowVariantlabel
            ) {
                this.flowVariantlabel = flowVariantlabel;
            }

            @Override

            public String toString() {
                return flowVariantlabel;
            }

        };

        String branchID;
        //
        String previousStep; //NO MORE USED?
        //
        public String boundaryID;
        public BoundaryRole boundaryRole;
        //
        boolean statusSuccess;
        String statusMessage;
        int statusCode;

        public ProcessStatus withCurrent(String current) {
            ProcessStatus n = new ProcessStatus(this);
            n.previousStep = current;
            return n;
        }

        public ProcessStatus withBranchAndcurrent(String branch_id, String current) {
            ProcessStatus n = new ProcessStatus(this);
            n.branchID = branch_id;
            n.previousStep = current;
            return n;
        }

        public ProcessStatus withBoundaryAndcurrent(String boundaryID, BoundaryRole boundaryRole, String current) {
            ProcessStatus n = new ProcessStatus(this);
            n.boundaryID = boundaryID;
            n.boundaryRole = boundaryRole;
            n.previousStep = current;
            return n;
        }

        public ProcessStatus withResetBoundary() {
            ProcessStatus n = new ProcessStatus(this);
            n.boundaryID = null;
            n.boundaryRole = null;
            return n;
        }

        public ProcessStatus(String branchID) {
            this.branchID = branchID;
            this.boundaryID = null;
            this.boundaryRole = null;
            this.previousStep = "";
            this.statusSuccess = true;
            this.statusMessage = "OK";
            this.statusCode = 0;
        }

        public ProcessStatus(ProcessStatus parent) {
            this();
            this.branchID = parent.branchID;
            this.boundaryID = parent.boundaryID;
            this.boundaryRole = parent.boundaryRole;
            this.previousStep = parent.previousStep;
            this.statusSuccess = parent.statusSuccess;
            this.statusMessage = parent.statusMessage;
            this.statusCode = parent.statusCode;
        }

        public ProcessStatus(String branchID, String previousStep) {
            this(branchID);
            this.previousStep = previousStep;
        }

        public ProcessStatus() {
            this("Main");
        }

    };

    static String process_name = "bpmn_process";

    static boolean globalSuccess = true;

    public static java.io.PrintStream debugChannel = System.out;
    public static java.io.PrintStream resultChannel = System.out;
    public static java.io.PrintStream traceChannel = new java.io.PrintStream(java.io.OutputStream.nullOutputStream());
    public static java.util.Properties outputs = new java.util.Properties();
    public static java.util.Properties inputs = new java.util.Properties();

    //parallel fork/join and send/receive support data
    static Map<String, List<String>> parallels = new HashMap<>();
    static Map<String, LinkedBlockingQueue<Message>> massage_channels = new HashMap<>();
    //public static Map<String, List<String>> joins = new HashMap<>();
    //boundary events support data
    static Map<String, Future> futureRegistry = new HashMap<>();
    //to avoid copncurrency issues, we register the calcelled threads and do not start them later, if reqiested
    //static Map<String, Future> cancelledThreadRegistry = new HashMap<>();
    private static volatile SecureRandom numberGenerator = new SecureRandom();
    private static final long MSB = 0x8000000000000000L;
    //static int parallel_branch_count = 0;
    static Integer active_threads_count = 0;
    public static int max_wait_sync_time = 10000;
    static ExecutorService executor = null;
    //

    public static String getRandomID() {
        byte[] bytes = new byte[16];
        numberGenerator.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        //return Long.toHexString(MSB | numberGenerator.nextLong()) + Long.toHexString(MSB | numberGenerator.nextLong());
    }

    public static void enableTrueParallel() {
        executor = Executors.newFixedThreadPool(10);
    }

    public static void closeChannels() {
        if (traceChannel != null) {
            traceChannel.flush();
            traceChannel.close();
        }
        if (debugChannel != null) {
            debugChannel.flush();
            debugChannel.close();
        }
        if (resultChannel != null) {
            resultChannel.flush();
            resultChannel.close();
        }
    }

    public static void setExternalTraceFile(String name) {
        try {
            traceChannel = new java.io.PrintStream(name + ".trace");
        } catch (FileNotFoundException ex) {

        
    

    ///
        }
    }

    public static void setExternalResultFile(String name) {
        try {
            resultChannel = new java.io.PrintStream(name + ".result");
        } catch (FileNotFoundException ex) {

        
    

    ///
        }
    }

    public static void setExternalDebugFile(String name) {
        try {
            debugChannel = new java.io.PrintStream(name + ".debug");
        } catch (FileNotFoundException ex) {

        
    

    ///
        }
    }

    public static void setDebugChannel(java.io.PrintStream c) {
        debugChannel = c;
    }

    public static void setTraceChannel(java.io.PrintStream c) {
        traceChannel = c;
    }

    public static void setResultChannel(java.io.PrintStream c) {
        resultChannel = c;
    }

    public static String getProcessName() {
        return process_name;
    }

    public static void loadExternalInputs() {
        java.io.File inputs_file = new java.io.File(process_name + "_inputs.properties");
        if (inputs_file.canRead()) {
            try {
                inputs.load(new java.io.FileReader(inputs_file));
            } catch (java.io.IOException ex) {

            }
        }
    }

    public static void saveExternalOutputs() {
        java.io.File outputs_file = new java.io.File(process_name + "_outputs.properties");
        try {
            outputs.store(new java.io.FileWriter(outputs_file), null);
        } catch (java.io.IOException ex) {
            //
        }
    }

    public static void sendMessage(ProcessStatus s, String channel, Message message) {
        if (!massage_channels.containsKey(channel)) {
            massage_channels.put(channel, new LinkedBlockingQueue<>());
        }
        try {
            massage_channels.get(channel).put(message);
        } catch (InterruptedException ex) {
            timeoutError(s);
        }
    }

    public static Message receiveMessage(ProcessStatus s, String channel) throws InterruptedException {
        return receiveMessage(s, channel, max_wait_sync_time, true);
    }

    public static Message receiveMessage(ProcessStatus s, String channel, int timeout, boolean triggerTimeout) throws InterruptedException {
        if (!massage_channels.containsKey(channel)) {
            massage_channels.put(channel, new LinkedBlockingQueue<>());
        }
        //try {
        Message message = massage_channels.get(channel).poll(timeout, TimeUnit.MILLISECONDS);
        if (message != null) {
            return message;
        } else {
            if (triggerTimeout) {
                timeoutError(s);
            }
            return null;
        }
        /*} catch (InterruptedException ex) {
            if (triggerTimeout) {
                timeoutError(s);
            } else {
                throw ex; //rethrow
            }
            return null;
        }*/
    }

    public static void executeProcess(String name, Runnable init, Consumer<ProcessStatus> start) {
        process_name = name;
        ProcessStatus s = new ProcessStatus();
        if (init != null) {
            debugOutput(s, "INITIALIZING PROCESS " + process_name);
            loadExternalInputs();
            init.run();
        }
        if (start != null) {
            debugOutput(s, "STARTING PROCESS " + process_name);
            startThread(start, s);
        }
        //wait for process threads to finish
        while (active_threads_count > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                //
            }
        }

        logResult(s, globalSuccess, null, 0);

        saveExternalOutputs();

        if (executor != null) {
            executor.shutdown();
        }

        debugOutput(s, "ENDING PROCESS " + process_name);

//        Set<Thread> threads = Thread.getAllStackTraces().keySet();
//        threads.stream()
//                .filter(t -> t.isAlive() && !t.isDaemon())
//                .forEach(t -> {
//                    System.out.println("Live thread: " + t.getName()
//                            + "  state=" + t.getState()
//                            + "  daemon=" + t.isDaemon());
//                    for (StackTraceElement e : t.getStackTrace()) {
//                        System.out.println("    at " + e);
//                    }
//                });
        closeChannels();
    }

    public static void startThread(Consumer<ProcessStatus> branch, ProcessStatus s) {
        startThread(branch, s, s.branchID);
    }

    public static void startThread(Consumer<ProcessStatus> branch, ProcessStatus s, String threadID) {
        synchronized (BPMNExecProcessUtils.class) {
//            if (futureRegistry.containsKey(threadID)) {
//                System.out.print("thread " + threadID + " not starting since is has been already started or cancelled");
//                return;
//            }
            active_threads_count++;
            //active_threads_count.notifyAll();
            if (executor != null) {
                Future thread = executor.submit(() -> branch.accept(s));
                //debugOutput(s, "\t STARTING THREAD " + threadID);
                //System.out.println("\t STARTING THREAD " + threadID);
                synchronized (BPMNExecProcessUtils.class) {
                    futureRegistry.put(threadID, thread);
                }
            } else {
                branch.accept(s);
            }
        }
    }

//    public static void startThread(Runnable branch, ProcessStatus s) {
//        startThread(branch, s, s.branchID);
//    }
//    public static void startThread(Runnable branch, ProcessStatus s, String threadID) {
//        synchronized (BPMNExecProcessUtils.class) {
//            active_threads_count++;
//            //active_threads_count.notifyAll();
//            if (executor != null) {
//                Future thread = executor.submit(branch);
//                synchronized (BPMNExecProcessUtils.class) {
//                    futureRegistry.put(threadID, thread);
//                }
//            } else {
//                branch.run();
//            }
//        }
//    }
    public static void stopThread() {
        synchronized (BPMNExecProcessUtils.class) {
            active_threads_count--;
            //active_threads_count.notifyAll();
            if (executor != null) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void stopThread(Future thread) {
        synchronized (BPMNExecProcessUtils.class) {
            if (thread != null) {
                active_threads_count--;
                //active_threads_count.notifyAll();
                thread.cancel(true);
                //
                String threadEntry = null;
                for (Map.Entry<String, Future> entry : futureRegistry.entrySet()) {
                    if (entry.getValue() == thread) {
                        threadEntry = entry.getKey();
                    }
                }
                if (threadEntry != null) {
                    futureRegistry.put(threadEntry, null);
                }
            }
        }
    }

    public static void stopThread(String threadID) {
        synchronized (BPMNExecProcessUtils.class) {
            Future thread;
            thread = futureRegistry.get(threadID);
            //threadRegistry.remove(threadID);
            futureRegistry.put(threadID, null);
            if (thread != null) {
                //debugOutput(s,"\t CANCELLING THREAD " + threadID);            
                //System.out.println("\t CANCELLING THREAD " + threadID);
                stopThread(thread);
            }
        }
    }

    public static void goTo(ProcessStatus s, String currentNodeId, String nextNodeId, Consumer<ProcessStatus> nextNodeProc, boolean debug) {
        if (!Thread.currentThread().isInterrupted()) {
//            if (debug) {
//                BPMNExecProcessUtils.logTransition(currentNodeId, nextNodeId);
//            }
            nextNodeProc.accept(s.withCurrent(currentNodeId));
        }
    }

    public static void fork(ProcessStatus s, String gatewayId, Consumer<ProcessStatus>... branches) {
        String source_parallel_gateway_id = gatewayId;
        String parallel_id = s.branchID + "|" + source_parallel_gateway_id + "|" + getRandomID();
        if (!parallels.containsKey(parallel_id)) {
            parallels.put(parallel_id, new ArrayList<>());
        }

        String[] branch_ids = new String[branches.length];
        for (int i = 0; i < branches.length; ++i) {
            String branch_id;
            synchronized (BPMNExecProcessUtils.class) {
                String parallel_branch_uuid = getRandomID();
                branch_id = parallel_id + "|" + parallel_branch_uuid;
                branch_ids[i] = branch_id;
                parallels.get(parallel_id).add(branch_id);
            }
        }
        for (int i = 0; i < branches.length; ++i) {
            debugOutput(s, "\t FORKING BRANCH %s FROM ACTIVITY %s", branch_ids[i], parallel_id);
            final Consumer<ProcessStatus> branch = branches[i];
            final String branch_id = branch_ids[i];
            startThread(branch, s.withBranchAndcurrent(branch_id, source_parallel_gateway_id));
        }
    }

    public static void join(ProcessStatus s, String gatewayId, Consumer<ProcessStatus> join_branch) {
        String branch_id = s.branchID;
        int delimiter_pos = branch_id.lastIndexOf("|");
        String parallel_id = branch_id.substring(0, delimiter_pos);
        String parallel_branch_uuid = branch_id.substring(delimiter_pos + 1);
        String temp = parallel_id.substring(0, parallel_id.lastIndexOf("|"));
        String source_parallel_gateway_id = temp.substring(temp.lastIndexOf("|") + 1);
        String parallel_parent_branch_id = temp.substring(0, temp.lastIndexOf("|"));

        debugOutput(s, "\t JOINING BRANCH %s OF PARALLEL %s STARTED FROM GATEWAY %s", branch_id, parallel_id, source_parallel_gateway_id);
        synchronized (BPMNExecProcessUtils.class) {
            parallels.get(parallel_id).remove(branch_id);
            //joins.get(gatewayId).remove(s.previousStep);

//            if (joins.get(gatewayId).isEmpty()) {
//                joins.remove(gatewayId); //andrebbe invece ricaricato in caso si percorra il jgw più volte?
//                String parent_branch_id = parallel_id.substring(0, parallel_id.lastIndexOf("|"));
//                startThread(join_branch, new ProcessStatus(parent_branch_id, gatewayId));
//            }
            if (parallels.get(parallel_id).isEmpty()) {
                parallels.remove(parallel_id);
                debugOutput(s, "\t PARALLEL %s STARTED FROM GATEWAY %s COMPLETE, RETURNING ON BRANCH %s", parallel_id, source_parallel_gateway_id, parallel_parent_branch_id);
                startThread(join_branch, new ProcessStatus(parallel_parent_branch_id, gatewayId));
            }

            stopThread();
        }
    }

    public static void forkBoundary(BPMNExecProcessUtils.ProcessStatus s, String taskid, Consumer<BPMNExecProcessUtils.ProcessStatus> normalflow, Consumer<BPMNExecProcessUtils.ProcessStatus> boundaryflow) {
        synchronized (BPMNExecProcessUtils.class) {
            debugOutput(s, "\t STARTING " + taskid + " BOUNDARY EVENT HANDLER");
            String boundaryWatchID = taskid + "_B_" + getRandomID();
            String watcher_thread_id = boundaryWatchID + "_" + ProcessStatus.BoundaryRole.BOUNDARYWATCHER.toString();
            String normal_thread_id = boundaryWatchID + "_" + ProcessStatus.BoundaryRole.NORMAL.toString();
            debugOutput(s, "\t FORKING NORMAL EXECUTION BRANCH %s for activity %s", normal_thread_id, taskid);
            startThread(normalflow, s.withBoundaryAndcurrent(boundaryWatchID, ProcessStatus.BoundaryRole.NORMAL, "Activity_05h597z"), normal_thread_id);
            debugOutput(s, "\t FORKING BOUNDARY WATCH BRANCH %s for activity %s", watcher_thread_id, taskid);
            startThread(boundaryflow, s.withBoundaryAndcurrent(boundaryWatchID, ProcessStatus.BoundaryRole.BOUNDARYWATCHER, "Activity_05h597z"), watcher_thread_id);
        }
    }

    public static void killBoundary(BPMNExecProcessUtils.ProcessStatus s) {
        synchronized (BPMNExecProcessUtils.class) {
            if (!Thread.currentThread().isInterrupted() && s.boundaryID != null) {
                String boundaryThreadName = s.boundaryID + "_" + ((s.boundaryRole == ProcessStatus.BoundaryRole.BOUNDARYWATCHER) ? ProcessStatus.BoundaryRole.NORMAL.toString() : ProcessStatus.BoundaryRole.BOUNDARYWATCHER.toString());
                stopThread(boundaryThreadName);
                s.boundaryID = null;
                s.boundaryRole = null;
            }
        }
    }

    public static void error(ProcessStatus s, String m, int c) {
        debugOutput(s, "\t ERROR %s ON BRANCH %s", m, s.branchID);
        globalSuccess &= false;
        s.statusCode = c;
        s.statusMessage = m;
        s.statusSuccess = false;
        logResult(s, false, m, c);
        //endCurrentBranch();
        stopThread();
    }

    public static void noDefaultCaseError(ProcessStatus s) {
        error(s, "No default branch in gateway", 9999);
    }

    public static void timeoutError(ProcessStatus s) {
        error(s, "Timeout in wait operation", 9999);
    }

    public static void success(ProcessStatus s, String m, int c) {
        if (m != null) {
            debugOutput(s, "\t SUCCESS %s ON BRANCH %s", m, s.branchID);
        } else {
            debugOutput(s, "\t SUCCESS ON BRANCH %s", s.branchID);
        }
        globalSuccess &= true;
        logResult(s, true, m, c);
        //endCurrentBranch();
        stopThread();
    }

    public static void success(ProcessStatus s) {
        success(s, null, 0);
    }

    public static void debugOutput(ProcessStatus s, String message, Object... args) {
        String formattedmessage = /*"T-"+System.currentTimeMillis()+": "+*/ "[" + s.branchID + (s.boundaryID != null && s.boundaryRole == ProcessStatus.BoundaryRole.BOUNDARYWATCHER ? " (BOUNDARY)" : "") + "] " + String.format(message, args);
        debugChannel.println(formattedmessage);
    }

    public static void logInput(String name, Object value) {
        resultChannel.println("\t " + name + "=" + value);
        outputs.setProperty(name, (value != null ? value.toString() : "<NULL>"));
    }

    public static void logTransition(String source, String target) {
        traceChannel.println("\"" + source + "\" -> \"" + target + "\"");
    }

    public static void logDMNRuleHit(String table_id, String rule_id, int index) {
        traceChannel.println("#DMN hit " + table_id + "(" + rule_id + ")" + " [" + index + "]");
    }

    public static void logCurrentNode(String id, String description) {
        traceChannel.println(id + "[" + (description != null && !description.isBlank() ? ("label=\"" + description + "\"") : "") + "]");
    }

    public static void logResult(ProcessStatus s, boolean success, String message, int code) {
        resultChannel.println(s.branchID + ":" + (success ? "SUCCESS" : "FAILURE" + "," + code + "," + message));
        outputs.setProperty(s.branchID + ":output_success", success ? "true" : "false");
        outputs.setProperty(s.branchID + ":output_message", message != null ? message : "");
        outputs.setProperty(s.branchID + ":output_code", String.valueOf(code));
    }

    public static boolean assertion(ProcessStatus s, String node_id, String condition_description, boolean condition) {
        if (!condition) {
            debugOutput(s, "\t ASSERTION %s FAILED ON NODE %s IN BRANCH %s", condition_description, node_id, s.branchID);
            //stopThread();
            System.exit(1000);
            return false;
        } else {
            return true;
        }
    }
}
