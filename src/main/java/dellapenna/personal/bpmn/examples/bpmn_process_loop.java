package dellapenna.personal.bpmn.examples;
import dellapenna.personal.bpmn.exec.*;


/*
 * ****************************** BPMN Generated Code *************************
 */
class bpmn_process_loop {

//Input Variables
// READ: StartEvent_1
    Object input_a = null;

//Process Variables
// READ: Activity_1k1rd56, Gateway_0x6sgvb
// WRITTEN: StartEvent_1, Activity_1k1rd56
    Object a = null;

//Process Dynamics
    public void EVENT_Event_053oyzy_Successful(BPMNExecProcessUtils.ProcessStatus s) {//End Event Successful [Event_053oyzy]
        BPMNExecProcessUtils.debugOutput("End Event Successful [Event_053oyzy]");
        BPMNExecProcessUtils.logCurrentNode("Event_053oyzy", "Successful");
        BPMNExecProcessUtils.success(s);
    }

    public void EVENT_StartEvent_1_Start(BPMNExecProcessUtils.ProcessStatus s) {//Start Event Start [StartEvent_1]
        BPMNExecProcessUtils.debugOutput("Start Event Start [StartEvent_1]");
        BPMNExecProcessUtils.logCurrentNode("StartEvent_1", "Start");
        a = input_a;
        BPMNExecProcessUtils.debugOutput("	 ASSIGNING a TO %s", input_a);
//[outgoing edge] Activity_1k1rd56 - Some task
        BPMNExecProcessUtils.logTransition("StartEvent_1", "Activity_1k1rd56");
        TASK_Activity_1k1rd56_Some_task(s.withCurrent("StartEvent_1"));
    }

    public void GATEWAY_Gateway_0x6sgvb_Decision(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Decision [Gateway_0x6sgvb]
        BPMNExecProcessUtils.debugOutput("Exclusive Gateway Decision [Gateway_0x6sgvb]");
        BPMNExecProcessUtils.logCurrentNode("Gateway_0x6sgvb", "Decision");
        if (BPMNExecTypeUtils.tonumber(a) >= BPMNExecTypeUtils.tonumber(10.0)) {//[outgoing edge] Event_053oyzy - Successful
            BPMNExecProcessUtils.logTransition("Gateway_0x6sgvb", "Event_053oyzy");
            EVENT_Event_053oyzy_Successful(s.withCurrent("Gateway_0x6sgvb"));
        } else if (BPMNExecTypeUtils.tonumber(a) < BPMNExecTypeUtils.tonumber(10.0)) {//[outgoing edge] Activity_1k1rd56 - Some task
            BPMNExecProcessUtils.logTransition("Gateway_0x6sgvb", "Activity_1k1rd56");
            TASK_Activity_1k1rd56_Some_task(s.withCurrent("Gateway_0x6sgvb"));
        } else {
            BPMNExecProcessUtils.noDefaultCaseError(s);
        }
    }

    public void TASK_Activity_1k1rd56_Some_task(BPMNExecProcessUtils.ProcessStatus s) {//Script Task Some task [Activity_1k1rd56]
        BPMNExecProcessUtils.debugOutput("Script Task Some task [Activity_1k1rd56]");
        BPMNExecProcessUtils.logCurrentNode("Activity_1k1rd56", "Some task");
        a = (BPMNExecTypeUtils.tonumber(a) + BPMNExecTypeUtils.tonumber(1.0));
//[outgoing edge] Gateway_0x6sgvb - Decision
        BPMNExecProcessUtils.logTransition("Activity_1k1rd56", "Gateway_0x6sgvb");
        GATEWAY_Gateway_0x6sgvb_Decision(s.withCurrent("Activity_1k1rd56"));
    }

    public void init() {
        if (this.input_a == null) {
            input_a = BPMNExecProcessUtils.inputs.getProperty("input_a", null);
        }
        BPMNExecProcessUtils.logInput("input_a", this.input_a);
//parallel join initializers

    }

    public boolean globalAssert(BPMNExecProcessUtils.ProcessStatus s, String node_id) {
        boolean success = true;

        return success;

    }

    public void execute(Object _input_a) {
        this.input_a = _input_a;
        BPMNExecProcessUtils.executeProcess("loop", this::init, this::EVENT_StartEvent_1_Start);
    }

    public static void main(String[] args) {
        BPMNExecProcessUtils.setExternalTraceFile("loop");
        BPMNExecProcessUtils.enableTrueParallel();
        bpmn_process_loop process = new bpmn_process_loop();
        process.execute(null/*input_a*/);
    }
}
