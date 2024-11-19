package dellapenna.personal.bpmn.unused;

import dellapenna.personal.bpmn.exec.*;

/*
 * ****************************** BPMN Generated Code *************************
 */
class bpmn_process_parallel {

//Input Variables
    ;



//Process Variables
;



//Process Dynamics
public void GATEWAY_Exclusive1(BPMNExecProcessUtils.ProcessStatus s) {//exclusive gateway
        BPMNExecProcessUtils.debugOutput("EXCLUSIVE GATEWAY Exclusive1");
        if ("a".equals(1.0)) {
            TASK_Task2Branch1(s);
        } else {
            TASK_Task2DefaultBranch(s);
        }
    }

    public void GATEWAY_Parallel1Join(BPMNExecProcessUtils.ProcessStatus s) {//parallel joining gateway
        BPMNExecProcessUtils.debugOutput("PARALLEL JOINING GATEWAY Parallel1Join");
        BPMNExecProcessUtils.join(s, this::TASK_Final_Task);
    }

    public void GATEWAY_Parallel1Split(BPMNExecProcessUtils.ProcessStatus s) {//parallel gateway
        BPMNExecProcessUtils.debugOutput("PARALLEL GATEWAY Parallel1Split");
        BPMNExecProcessUtils.fork(s, "Parallel1Split", this::TASK_Parallel_Task_2, this::TASK_Parallel_Task_1);
    }

    public void GATEWAY_Parallel2Split(BPMNExecProcessUtils.ProcessStatus s) {//parallel gateway
        BPMNExecProcessUtils.debugOutput("PARALLEL GATEWAY Parallel2Split");
        BPMNExecProcessUtils.fork(s, "Parallel2Split", this::TASK_InnerParallelTask1, this::TASK_InnerParallelTask2);
        BPMNExecProcessUtils.stopThread();
    }

    public void GATEWAY_Parallel2Join(BPMNExecProcessUtils.ProcessStatus s) {//parallel joining gateway
        BPMNExecProcessUtils.debugOutput("PARALLEL JOINING GATEWAY Parallel2Join");
        BPMNExecProcessUtils.join(s, this::GATEWAY_Parallel1Join);
        BPMNExecProcessUtils.stopThread();
    }

    public void EVENT_Start(BPMNExecProcessUtils.ProcessStatus s) {//start event: Start
        BPMNExecProcessUtils.debugOutput("START EVENT: Start");
        GATEWAY_Parallel1Split(s);
    }

    public void EVENT_End(BPMNExecProcessUtils.ProcessStatus s) {//end event: End
        BPMNExecProcessUtils.debugOutput("END EVENT End");
        BPMNExecProcessUtils.success(s);
    }

    public void TASK_InnerParallelTask1(BPMNExecProcessUtils.ProcessStatus s) {//generic task: InnerParallelTask1
        BPMNExecProcessUtils.debugOutput("TASK InnerParallelTask1");
        GATEWAY_Parallel2Join(s);
    }

    public void TASK_Task2DefaultBranch(BPMNExecProcessUtils.ProcessStatus s) {//generic task: Task2DefaultBranch
        BPMNExecProcessUtils.debugOutput("TASK Task2DefaultBranch");
        GATEWAY_Parallel2Split(s);
    }

    public void TASK_InnerParallelTask2(BPMNExecProcessUtils.ProcessStatus s) {//generic task: InnerParallelTask2
        BPMNExecProcessUtils.debugOutput("TASK InnerParallelTask2");
        GATEWAY_Parallel2Join(s);
    }

    public void TASK_Parallel_Task_2(BPMNExecProcessUtils.ProcessStatus s) {//generic task: Parallel Task 2
        BPMNExecProcessUtils.debugOutput("TASK Parallel Task 2");
        GATEWAY_Exclusive1(s);
    }

    public void TASK_Parallel_Task_1(BPMNExecProcessUtils.ProcessStatus s) {//generic task: Parallel Task 1
        BPMNExecProcessUtils.debugOutput("TASK Parallel Task 1");
        GATEWAY_Parallel1Join(s);
    }

    public void TASK_Final_Task(BPMNExecProcessUtils.ProcessStatus s) {//generic task: Final Task
        BPMNExecProcessUtils.debugOutput("TASK Final Task");
        EVENT_End(s);
    }

    public void TASK_Task2Branch1(BPMNExecProcessUtils.ProcessStatus s) {//generic task: Task2Branch1
        BPMNExecProcessUtils.debugOutput("TASK Task2Branch1");
        GATEWAY_Parallel1Join(s);
    }

    public void init() {
    }

    public static void main(String[] args) {
        BPMNExecProcessUtils.enableTrueParallel();
        BPMNExecProcessUtils.ProcessStatus s = new BPMNExecProcessUtils.ProcessStatus();
        bpmn_process_parallel process = new bpmn_process_parallel();
        BPMNExecProcessUtils.initProcess(s, process::init);
        BPMNExecProcessUtils.startProcess(s, process::EVENT_Start);
        BPMNExecProcessUtils.endProcess(s);
    }
}
