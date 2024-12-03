
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
    public void EVENT_Start(BPMNExecProcessUtils.ProcessStatus s) {//Start Event Start [StartEvent_1]
        BPMNExecProcessUtils.debugOutput("Start Event Start [StartEvent_1]");
        a = input_a;
        BPMNExecProcessUtils.debugOutput("	 ASSIGNING a TO %s", input_a);
//[outgoing edge] Activity_1k1rd56 - Some task
        BPMNExecProcessUtils.logTransition("StartEvent_1", "Activity_1k1rd56");
        TASK_Some_task(s.withCurrent("StartEvent_1"));
    }

    public void EVENT_Successful(BPMNExecProcessUtils.ProcessStatus s) {//End Event Successful [Event_053oyzy]
        BPMNExecProcessUtils.debugOutput("End Event Successful [Event_053oyzy]");
        BPMNExecProcessUtils.success(s);
    }

    public void TASK_Some_task(BPMNExecProcessUtils.ProcessStatus s) {//Script Task Some task [Activity_1k1rd56]
        BPMNExecProcessUtils.debugOutput("Script Task Some task [Activity_1k1rd56]");
        a = (BPMNExecTypeUtils.tonumber(a) + BPMNExecTypeUtils.tonumber(1.0));
//[outgoing edge] Gateway_0x6sgvb - Decision
        BPMNExecProcessUtils.logTransition("Activity_1k1rd56", "Gateway_0x6sgvb");
        GATEWAY_Decision(s.withCurrent("Activity_1k1rd56"));
    }

    public void GATEWAY_Decision(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Decision [Gateway_0x6sgvb]
        BPMNExecProcessUtils.debugOutput("Exclusive Gateway Decision [Gateway_0x6sgvb]");
        if (BPMNExecTypeUtils.tonumber(a) >= BPMNExecTypeUtils.tonumber(10.0)) {//[outgoing edge] Event_053oyzy - Successful
            BPMNExecProcessUtils.logTransition("Gateway_0x6sgvb", "Event_053oyzy");
            EVENT_Successful(s.withCurrent("Gateway_0x6sgvb"));
        } else if (BPMNExecTypeUtils.tonumber(a) < BPMNExecTypeUtils.tonumber(10.0)) {//[outgoing edge] Activity_1k1rd56 - Some task
            BPMNExecProcessUtils.logTransition("Gateway_0x6sgvb", "Activity_1k1rd56");
            TASK_Some_task(s.withCurrent("Gateway_0x6sgvb"));
        } else {
            BPMNExecProcessUtils.noDefaultCaseError(s);
        }
    }

    public void init() {
        this.input_a = null;	//TODO assign input variable
        if (this.input_a == null) {
            input_a = BPMNExecProcessUtils.inputs.getProperty("input_a", null);
        }
        BPMNExecProcessUtils.logInput("input_a", this.input_a);
//parallel join initializers

    }

    public static void main(String[] args) {
        BPMNExecProcessUtils.enableTrueParallel();
        bpmn_process_loop process = new bpmn_process_loop();
        BPMNExecProcessUtils.executeProcess(process::init, process::EVENT_Start);
    }
}
