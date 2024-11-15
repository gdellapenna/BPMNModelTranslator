package dellapenna.personal.bpmn.unused;

/*
 * ****************************** BPMN Generated Code *************************
 */
class bpmn_process_loop {

//Input Variables
    Object input_a;

//Process Variables
    Object a;

//Process Dynamics
    public void task_script_Some_task() {//script task: Some task
        ProcessUtils.debugOutput("SCRIPT TASK Some task");
        a = (TypeUtils.tonumber(a) + TypeUtils.tonumber(1.0));
    }

    public void flow_Some_task_JOIN_GATEWAY() {//inclusive joining gateway
        ProcessUtils.debugOutput("JOINING FLOW flow_Some_task");
        flow_Some_task();
    }

    public void flow_Some_task() {
        task_script_Some_task();
//exclusive gateway
        ProcessUtils.debugOutput("EXCLUSIVE GATEWAY Decision");
        if (TypeUtils.tonumber(a) >= TypeUtils.tonumber(10.0)) {
            ProcessUtils.debugOutput("JOINING FLOW flow_Successful");
            flow_Successful();
        } else if (TypeUtils.tonumber(a) < TypeUtils.tonumber(10.0)) {
            ProcessUtils.debugOutput("JOINING FLOW flow_Some_task_JOIN_GATEWAY");
            flow_Some_task_JOIN_GATEWAY();
        } else {
            ProcessUtils.debugOutput("JOINING FLOW ProcessUtils.noDefaultCaseError");
            ProcessUtils.noDefaultCaseError();
        }
    }

    public void flow_Successful() {//end event: Successful
        ProcessUtils.success();
    }

    public void flow_Start() {//start event: Start
        ProcessUtils.debugOutput("START EVENT: Start");
        a = input_a;
        ProcessUtils.debugOutput("ASSIGNING a TO %s", input_a);
//inclusive joining gateway
        ProcessUtils.debugOutput("JOINING FLOW flow_Some_task");
        flow_Some_task();
    }

    public void init() {
        this.input_a = 0;	//TODO assign input variable
        if (this.input_a == null) {
            input_a = ProcessUtils.inputs.getProperty("input_a", null);
        }
        ProcessUtils.logInput("input_a", this.input_a);
    }

    public static void main(String[] args) {
        ProcessUtils.start();
        bpmn_process_loop process = new bpmn_process_loop();
        process.init();
        process.flow_Start();
    }
}
