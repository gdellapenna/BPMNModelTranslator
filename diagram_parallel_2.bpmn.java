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
public void GATEWAY_UnclosedParallel(BPMNExecProcessUtils.ProcessStatus s) {//Parallel Gateway UnclosedParallel [Gateway_02uz50m]
BPMNExecProcessUtils.fork(s,"UnclosedParallel",this::TASK_Task1,this::TASK_Task2);
BPMNExecProcessUtils.stopThread();
}

public void TASK_Task1(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Task1 [Activity_0qvix8b]
//[outgoing edge] Event_0aq6tzc - Ok1
EVENT_Ok1(s);
}

public void TASK_Task2(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Task2 [Activity_1uu1n8a]
//[outgoing edge] Event_0ftzlyc - Ok2
EVENT_Ok2(s);
}

public void EVENT_Start(BPMNExecProcessUtils.ProcessStatus s) {//Start Event Start [StartEvent_1]
//[outgoing edge] Gateway_02uz50m - UnclosedParallel
GATEWAY_UnclosedParallel(s);
}

public void EVENT_Ok2(BPMNExecProcessUtils.ProcessStatus s) {//End Event Ok2 [Event_0ftzlyc]
BPMNExecProcessUtils.success(s);
}

public void EVENT_Ok1(BPMNExecProcessUtils.ProcessStatus s) {//End Event Ok1 [Event_0aq6tzc]
BPMNExecProcessUtils.success(s);
}

public void init() {
}public static void main(String[] args) {
BPMNExecProcessUtils.debugChannel=new java.io.PrintStream(java.io.OutputStream.nullOutputStream());bpmn_process_Process_1ai2j0m process = new bpmn_process_Process_1ai2j0m();
BPMNExecProcessUtils.executeProcess(process::init,process::EVENT_Start);}}
