package dellapenna.personal.bpmn.unused;

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
    public void GATEWAY_Decision(BPMNExecProcessUtils.ProcessStatus s) {//exclusive gateway
        BPMNExecProcessUtils.debugOutput("EXCLUSIVE GATEWAY Decision");
        if (BPMNExecTypeUtils.tonumber(a) >= BPMNExecTypeUtils.tonumber(10.0)) {
            EVENT_Successful(s);
        } else if (BPMNExecTypeUtils.tonumber(a) < BPMNExecTypeUtils.tonumber(10.0)) {
            TASK_Some_task(s);
        } else {
            BPMNExecProcessUtils.noDefaultCaseError(s);
        }
    }

    public void EVENT_Start(BPMNExecProcessUtils.ProcessStatus s) {//start event: Start
        BPMNExecProcessUtils.debugOutput("START EVENT: Start");
        a = input_a;
        BPMNExecProcessUtils.debugOutput("ASSIGNING a TO %s", input_a);
        TASK_Some_task(s);
    }

    public void EVENT_Successful(BPMNExecProcessUtils.ProcessStatus s) {//end event: Successful
        BPMNExecProcessUtils.success(s);
    }

    public void TASK_Some_task(BPMNExecProcessUtils.ProcessStatus s) {//script task: Some task
        BPMNExecProcessUtils.debugOutput("SCRIPT TASK Some task");
        a = (BPMNExecTypeUtils.tonumber(a) + BPMNExecTypeUtils.tonumber(1.0));
        GATEWAY_Decision(s);
    }

    public void init() {
        this.input_a = 0;	//TODO assign input variable
        if (this.input_a == null) {
            input_a = BPMNExecProcessUtils.inputs.getProperty("input_a", null);
        }
        BPMNExecProcessUtils.logInput("input_a", this.input_a);
    }

    public static void main(String[] args) {
        BPMNExecProcessUtils.enableTrueParallel();
        BPMNExecProcessUtils.ProcessStatus s = new BPMNExecProcessUtils.ProcessStatus();
        bpmn_process_loop process = new bpmn_process_loop();
        process.init();
        BPMNExecProcessUtils.startProcess(s,process::EVENT_Start);
        //process.EVENT_Start(s);
        BPMNExecProcessUtils.endProcess(s);
    }
}
