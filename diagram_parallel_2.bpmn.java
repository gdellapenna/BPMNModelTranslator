import dellapenna.personal.bpmn.exec.*;



/*
 * ****************************** BPMN Generated Code *************************
 */
 class bpmn_process_Process_1ai2j0m { 

//Input Variables
;



//Process Variables
;



//Process Dynamics
public void GATEWAY_UnclosedParallel(BPMNExecProcessUtils.ProcessStatus s) {//parallel gateway
BPMNExecProcessUtils.debugOutput("PARALLEL GATEWAY UnclosedParallel");
BPMNExecProcessUtils.fork(s,"UnclosedParallel",this::TASK_Task1,this::TASK_Task2);
BPMNExecProcessUtils.endThread();
}

public void EVENT_Start(BPMNExecProcessUtils.ProcessStatus s) {//start event: Start
BPMNExecProcessUtils.debugOutput("START EVENT: Start");
GATEWAY_UnclosedParallel(s);
}

public void EVENT_Ok2(BPMNExecProcessUtils.ProcessStatus s) {//end event: Ok2
BPMNExecProcessUtils.debugOutput("END EVENT Ok2");
BPMNExecProcessUtils.success(s);
}

public void EVENT_Ok1(BPMNExecProcessUtils.ProcessStatus s) {//end event: Ok1
BPMNExecProcessUtils.debugOutput("END EVENT Ok1");
BPMNExecProcessUtils.success(s);
}

public void TASK_Task1(BPMNExecProcessUtils.ProcessStatus s) {//generic task: Task1
BPMNExecProcessUtils.debugOutput("TASK Task1");
EVENT_Ok1(s);
}

public void TASK_Task2(BPMNExecProcessUtils.ProcessStatus s) {//generic task: Task2
BPMNExecProcessUtils.debugOutput("TASK Task2");
EVENT_Ok2(s);
}

public void init() {
}public static void main(String[] args) {
BPMNExecProcessUtils.enableTrueParallel();bpmn_process_Process_1ai2j0m process = new bpmn_process_Process_1ai2j0m();
BPMNExecProcessUtils.ProcessStatus s = new BPMNExecProcessUtils.ProcessStatus();
BPMNExecProcessUtils.initProcess(s,process::init);BPMNExecProcessUtils.startProcess(s,process::EVENT_Start);BPMNExecProcessUtils.endProcess(s);}}
