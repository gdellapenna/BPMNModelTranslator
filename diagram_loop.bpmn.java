import dellapenna.personal.bpmn.exec.*;



/*
 * ****************************** BPMN Generated Code *************************
 */
 class bpmn_process_loop { 

//Input Variables
Object input_a;



//Process Variables
Object a;



//Process Dynamics
public void GATEWAY_Decision(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Decision [Gateway_0x6sgvb]
if (BPMNExecTypeUtils.tonumber(a) >= BPMNExecTypeUtils.tonumber(10.0)){//[outgoing edge] Event_053oyzy - Successful
EVENT_Successful(s);
} else if (BPMNExecTypeUtils.tonumber(a) < BPMNExecTypeUtils.tonumber(10.0)){//[outgoing edge] Activity_1k1rd56 - Some task
TASK_Some_task(s);
} else { BPMNExecProcessUtils.noDefaultCaseError(s); }
}

public void TASK_Some_task(BPMNExecProcessUtils.ProcessStatus s) {//Script Task Some task [Activity_1k1rd56]
a=(BPMNExecTypeUtils.tonumber(a) + BPMNExecTypeUtils.tonumber(1.0));
//[outgoing edge] Gateway_0x6sgvb - Decision
GATEWAY_Decision(s);
}

public void EVENT_Start(BPMNExecProcessUtils.ProcessStatus s) {//Start Event Start [StartEvent_1]
a=input_a;
//[outgoing edge] Activity_1k1rd56 - Some task
TASK_Some_task(s);
}

public void EVENT_Successful(BPMNExecProcessUtils.ProcessStatus s) {//End Event Successful [Event_053oyzy]
BPMNExecProcessUtils.success(s);
}

public void init() {
this.input_a = null;	//TODO assign input variable
if (this.input_a==null) input_a=BPMNExecProcessUtils.inputs.getProperty("input_a", null);
BPMNExecProcessUtils.logInput("input_a",this.input_a);
}public static void main(String[] args) {
BPMNExecProcessUtils.debugChannel=new java.io.PrintStream(java.io.OutputStream.nullOutputStream());bpmn_process_loop process = new bpmn_process_loop();
BPMNExecProcessUtils.executeProcess(process::init,process::EVENT_Start);}}
