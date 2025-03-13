import dellapenna.personal.bpmn.exec.*;


/*
 * ****************************** Process Code *************************
 */
 class Events { 

//Input Variables
;



//Process Variables
;



//Process Dynamics
public void EVENT_Event_02n50iy_FinishProcess(BPMNExecProcessUtils.ProcessStatus s) {//End Event FinishProcess [Event_02n50iy]
BPMNExecProcessUtils.debugOutput(s,"End Event FinishProcess [Event_02n50iy]");
BPMNExecProcessUtils.logCurrentNode("Event_02n50iy","FinishProcess");
BPMNExecProcessUtils.success(s);
}

public void EVENT_Event_0otlgeu_StartProcess(BPMNExecProcessUtils.ProcessStatus s) {//Start Event StartProcess [Event_0otlgeu]
BPMNExecProcessUtils.debugOutput(s,"Start Event StartProcess [Event_0otlgeu]");
BPMNExecProcessUtils.logCurrentNode("Event_0otlgeu","StartProcess");
//[outgoing edge] Gateway_0vvmbrx - StartParallel
BPMNExecProcessUtils.logTransition("Event_0otlgeu","Gateway_0vvmbrx");
GATEWAY_Gateway_0vvmbrx_StartParallel(s.withCurrent("Event_0otlgeu"));
}

public void GATEWAY_Gateway_0vvmbrx_StartParallel(BPMNExecProcessUtils.ProcessStatus s) {//Parallel Gateway StartParallel [Gateway_0vvmbrx]
BPMNExecProcessUtils.debugOutput(s,"Parallel Gateway StartParallel [Gateway_0vvmbrx]");
BPMNExecProcessUtils.logCurrentNode("Gateway_0vvmbrx","StartParallel");
//[outgoing edge] Activity_05wh2j7 - DoSomething
BPMNExecProcessUtils.logTransition("Gateway_0vvmbrx","Activity_05wh2j7");
//[outgoing edge] Activity_1bbiwff - ReceiveMessage1
BPMNExecProcessUtils.logTransition("Gateway_0vvmbrx","Activity_1bbiwff");
//FORKS: Activity_05wh2j7,Activity_1bbiwff
BPMNExecProcessUtils.fork(s,"Gateway_0vvmbrx",this::TASK_Activity_05wh2j7_DoSomething,this::TASK_Activity_1bbiwff_ReceiveMessage1);
BPMNExecProcessUtils.stopThread();
}

public void GATEWAY_Gateway_1nlfvxl_EndParallel(BPMNExecProcessUtils.ProcessStatus s) {//Parallel Joining Gateway EndParallel [Gateway_1nlfvxl]
BPMNExecProcessUtils.debugOutput(s,"Parallel Joining Gateway EndParallel [Gateway_1nlfvxl]");
BPMNExecProcessUtils.logCurrentNode("Gateway_1nlfvxl","EndParallel");
//[outgoing edge] Event_02n50iy - FinishProcess
BPMNExecProcessUtils.logTransition("Gateway_1nlfvxl","Event_02n50iy");
//JOINS: Activity_1bbiwff,Activity_0ewds2n
BPMNExecProcessUtils.join(s,"Gateway_1nlfvxl", this::EVENT_Event_02n50iy_FinishProcess);
}

public void TASK_Activity_05wh2j7_DoSomething(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task DoSomething [Activity_05wh2j7]
BPMNExecProcessUtils.debugOutput(s,"Generic Task DoSomething [Activity_05wh2j7]");
BPMNExecProcessUtils.logCurrentNode("Activity_05wh2j7","DoSomething");
//[outgoing edge] Activity_0ewds2n - SendMessage1
BPMNExecProcessUtils.logTransition("Activity_05wh2j7","Activity_0ewds2n");
TASK_Activity_0ewds2n_SendMessage1(s.withCurrent("Activity_05wh2j7"));
}

public void TASK_Activity_0ewds2n_SendMessage1(BPMNExecProcessUtils.ProcessStatus s) {//Send Task SendMessage1 [Activity_0ewds2n]
BPMNExecProcessUtils.debugOutput(s,"Send Task SendMessage1 [Activity_0ewds2n]");
BPMNExecProcessUtils.logCurrentNode("Activity_0ewds2n","SendMessage1");
BPMNExecProcessUtils.debugOutput(s,"	 SENDING message on channel ch1");
BPMNExecProcessUtils.sendMessage(s,"ch1",true);
//[outgoing edge] Gateway_1nlfvxl - EndParallel
BPMNExecProcessUtils.logTransition("Activity_0ewds2n","Gateway_1nlfvxl");
GATEWAY_Gateway_1nlfvxl_EndParallel(s.withCurrent("Activity_0ewds2n"));
}

public void TASK_Activity_1bbiwff_ReceiveMessage1(BPMNExecProcessUtils.ProcessStatus s) {//Receive Task ReceiveMessage1 [Activity_1bbiwff]
BPMNExecProcessUtils.debugOutput(s,"Receive Task ReceiveMessage1 [Activity_1bbiwff]");
BPMNExecProcessUtils.logCurrentNode("Activity_1bbiwff","ReceiveMessage1");
BPMNExecProcessUtils.debugOutput(s,"	 RECEIVING message on channel ch1");
BPMNExecProcessUtils.receiveMessage(s,"ch1");
//[outgoing edge] Gateway_1nlfvxl - EndParallel
BPMNExecProcessUtils.logTransition("Activity_1bbiwff","Gateway_1nlfvxl");
GATEWAY_Gateway_1nlfvxl_EndParallel(s.withCurrent("Activity_1bbiwff"));
}

public void init() {

}public boolean globalAssert(BPMNExecProcessUtils.ProcessStatus s, String node_id) {
boolean success=true;

return success;

}public void execute() {BPMNExecProcessUtils.executeProcess("Events",this::init,this::EVENT_Event_0otlgeu_StartProcess);
}public static void main(String[] args) {
BPMNExecProcessUtils.setExternalTraceFile("Events");BPMNExecProcessUtils.enableTrueParallel();Events process = new Events();
process.execute();}}

