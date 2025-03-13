
import dellapenna.personal.bpmn.exec.*;


/*
 * ****************************** Process Code *************************
 */
class Parallel1Err {

//Input Variables
// READ: Gateway_1c7340a
    Object a = null;

//Process Variables
    ;



//Process Dynamics
public void EVENT_Event_0zv9hfo_End(BPMNExecProcessUtils.ProcessStatus s) {//End Event End [Event_0zv9hfo]
        BPMNExecProcessUtils.debugOutput(s, "End Event End [Event_0zv9hfo]");
        BPMNExecProcessUtils.logCurrentNode("Event_0zv9hfo", "End");
        BPMNExecProcessUtils.success(s);
    }

    public void EVENT_Event_1w9jxe1_ErrorOnParallel1(BPMNExecProcessUtils.ProcessStatus s) {//End Event ErrorOnParallel1 [Event_1w9jxe1]
        BPMNExecProcessUtils.debugOutput(s, "End Event ErrorOnParallel1 [Event_1w9jxe1]");
        BPMNExecProcessUtils.logCurrentNode("Event_1w9jxe1", "ErrorOnParallel1");
        BPMNExecProcessUtils.error(s, "BigError", 500);
    }

    public void EVENT_StartEvent_1_Start(BPMNExecProcessUtils.ProcessStatus s) {//Start Event Start [StartEvent_1]
        BPMNExecProcessUtils.debugOutput(s, "Start Event Start [StartEvent_1]");
        BPMNExecProcessUtils.logCurrentNode("StartEvent_1", "Start");
//[outgoing edge] Gateway_1gapucr - Parallel1Split
        BPMNExecProcessUtils.logTransition("StartEvent_1", "Gateway_1gapucr");
        GATEWAY_Gateway_1gapucr_Parallel1Split(s.withCurrent("StartEvent_1"));
    }

    public void GATEWAY_Gateway_02100g2_Parallel1Join(BPMNExecProcessUtils.ProcessStatus s) {//Parallel Joining Gateway Parallel1Join [Gateway_02100g2]
        BPMNExecProcessUtils.debugOutput(s, "Parallel Joining Gateway Parallel1Join [Gateway_02100g2]");
        BPMNExecProcessUtils.logCurrentNode("Gateway_02100g2", "Parallel1Join");
//[outgoing edge] Activity_16zc6e7 - Final Task
        BPMNExecProcessUtils.logTransition("Gateway_02100g2", "Activity_16zc6e7");
//JOINS: Activity_0pfa4n5,Activity_09oefmk,Gateway_1iv1tvb
        BPMNExecProcessUtils.join(s, "Gateway_02100g2", this::TASK_Activity_16zc6e7_Final_Task);
    }

    public void GATEWAY_Gateway_0fm3ji9_Parallel2Split(BPMNExecProcessUtils.ProcessStatus s) {//Parallel Gateway Parallel2Split [Gateway_0fm3ji9]
        BPMNExecProcessUtils.debugOutput(s, "Parallel Gateway Parallel2Split [Gateway_0fm3ji9]");
        BPMNExecProcessUtils.logCurrentNode("Gateway_0fm3ji9", "Parallel2Split");
//[outgoing edge] Activity_0svmpy2 - InnerParallelTask1
        BPMNExecProcessUtils.logTransition("Gateway_0fm3ji9", "Activity_0svmpy2");
//[outgoing edge] Activity_1otvmx2 - InnerParallelTask2
        BPMNExecProcessUtils.logTransition("Gateway_0fm3ji9", "Activity_1otvmx2");
//FORKS: Activity_0svmpy2,Activity_1otvmx2
        BPMNExecProcessUtils.fork(s, "Gateway_0fm3ji9", this::TASK_Activity_0svmpy2_InnerParallelTask1, this::TASK_Activity_1otvmx2_InnerParallelTask2);
        BPMNExecProcessUtils.stopThread();
    }

    public void GATEWAY_Gateway_1c7340a_Exclusive1(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Exclusive1 [Gateway_1c7340a]
        BPMNExecProcessUtils.debugOutput(s, "Exclusive Gateway Exclusive1 [Gateway_1c7340a]");
        BPMNExecProcessUtils.logCurrentNode("Gateway_1c7340a", "Exclusive1");
        if (BPMNExecTypeUtils.equals(a, 3.0)) {//[outgoing edge] Activity_09oefmk - Task2Branch1
            BPMNExecProcessUtils.logTransition("Gateway_1c7340a", "Activity_09oefmk");
            TASK_Activity_09oefmk_Task2Branch1(s.withCurrent("Gateway_1c7340a"));
        } else if (BPMNExecTypeUtils.equals(a, 2.0)) {//[outgoing edge] Activity_0sbgnmb - Task2DefaultBranch
            BPMNExecProcessUtils.logTransition("Gateway_1c7340a", "Activity_0sbgnmb");
            TASK_Activity_0sbgnmb_Task2DefaultBranch(s.withCurrent("Gateway_1c7340a"));
        } else if (BPMNExecTypeUtils.equals(a, 1.0)) {//[outgoing edge] Event_1w9jxe1 - ErrorOnParallel1
            BPMNExecProcessUtils.logTransition("Gateway_1c7340a", "Event_1w9jxe1");
            EVENT_Event_1w9jxe1_ErrorOnParallel1(s.withCurrent("Gateway_1c7340a"));
        } else {
            BPMNExecProcessUtils.noDefaultCaseError(s);
        }
    }

    public void GATEWAY_Gateway_1gapucr_Parallel1Split(BPMNExecProcessUtils.ProcessStatus s) {//Parallel Gateway Parallel1Split [Gateway_1gapucr]
        BPMNExecProcessUtils.debugOutput(s, "Parallel Gateway Parallel1Split [Gateway_1gapucr]");
        BPMNExecProcessUtils.logCurrentNode("Gateway_1gapucr", "Parallel1Split");
//[outgoing edge] Activity_0veq5io - Parallel Task 2
        BPMNExecProcessUtils.logTransition("Gateway_1gapucr", "Activity_0veq5io");
//[outgoing edge] Activity_0pfa4n5 - Parallel Task 1
        BPMNExecProcessUtils.logTransition("Gateway_1gapucr", "Activity_0pfa4n5");
//FORKS: Activity_0veq5io,Activity_0pfa4n5
        BPMNExecProcessUtils.fork(s, "Gateway_1gapucr", this::TASK_Activity_0veq5io_Parallel_Task_2, this::TASK_Activity_0pfa4n5_Parallel_Task_1);
        BPMNExecProcessUtils.stopThread();
    }

    public void GATEWAY_Gateway_1iv1tvb_Parallel2Join(BPMNExecProcessUtils.ProcessStatus s) {//Parallel Joining Gateway Parallel2Join [Gateway_1iv1tvb]
        BPMNExecProcessUtils.debugOutput(s, "Parallel Joining Gateway Parallel2Join [Gateway_1iv1tvb]");
        BPMNExecProcessUtils.logCurrentNode("Gateway_1iv1tvb", "Parallel2Join");
//[outgoing edge] Gateway_02100g2 - Parallel1Join
        BPMNExecProcessUtils.logTransition("Gateway_1iv1tvb", "Gateway_02100g2");
//JOINS: Activity_0svmpy2,Activity_1otvmx2
        BPMNExecProcessUtils.join(s, "Gateway_1iv1tvb", this::GATEWAY_Gateway_02100g2_Parallel1Join);
    }

    public void TASK_Activity_09oefmk_Task2Branch1(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Task2Branch1 [Activity_09oefmk]
        BPMNExecProcessUtils.debugOutput(s, "Generic Task Task2Branch1 [Activity_09oefmk]");
        BPMNExecProcessUtils.logCurrentNode("Activity_09oefmk", "Task2Branch1");
//[outgoing edge] Gateway_02100g2 - Parallel1Join
        BPMNExecProcessUtils.logTransition("Activity_09oefmk", "Gateway_02100g2");
        GATEWAY_Gateway_02100g2_Parallel1Join(s.withCurrent("Activity_09oefmk"));
    }

    public void TASK_Activity_0pfa4n5_Parallel_Task_1(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Parallel Task 1 [Activity_0pfa4n5]
        BPMNExecProcessUtils.debugOutput(s, "Generic Task Parallel Task 1 [Activity_0pfa4n5]");
        BPMNExecProcessUtils.logCurrentNode("Activity_0pfa4n5", "Parallel Task 1");
//[outgoing edge] Gateway_02100g2 - Parallel1Join
        BPMNExecProcessUtils.logTransition("Activity_0pfa4n5", "Gateway_02100g2");
        GATEWAY_Gateway_02100g2_Parallel1Join(s.withCurrent("Activity_0pfa4n5"));
    }

    public void TASK_Activity_0sbgnmb_Task2DefaultBranch(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Task2DefaultBranch [Activity_0sbgnmb]
        BPMNExecProcessUtils.debugOutput(s, "Generic Task Task2DefaultBranch [Activity_0sbgnmb]");
        BPMNExecProcessUtils.logCurrentNode("Activity_0sbgnmb", "Task2DefaultBranch");
//[outgoing edge] Gateway_0fm3ji9 - Parallel2Split
        BPMNExecProcessUtils.logTransition("Activity_0sbgnmb", "Gateway_0fm3ji9");
        GATEWAY_Gateway_0fm3ji9_Parallel2Split(s.withCurrent("Activity_0sbgnmb"));
    }

    public void TASK_Activity_0svmpy2_InnerParallelTask1(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task InnerParallelTask1 [Activity_0svmpy2]
        BPMNExecProcessUtils.debugOutput(s, "Generic Task InnerParallelTask1 [Activity_0svmpy2]");
        BPMNExecProcessUtils.logCurrentNode("Activity_0svmpy2", "InnerParallelTask1");
//[outgoing edge] Gateway_1iv1tvb - Parallel2Join
        BPMNExecProcessUtils.logTransition("Activity_0svmpy2", "Gateway_1iv1tvb");
        GATEWAY_Gateway_1iv1tvb_Parallel2Join(s.withCurrent("Activity_0svmpy2"));
    }

    public void TASK_Activity_0veq5io_Parallel_Task_2(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Parallel Task 2 [Activity_0veq5io]
        BPMNExecProcessUtils.debugOutput(s, "Generic Task Parallel Task 2 [Activity_0veq5io]");
        BPMNExecProcessUtils.logCurrentNode("Activity_0veq5io", "Parallel Task 2");
//[outgoing edge] Gateway_1c7340a - Exclusive1
        BPMNExecProcessUtils.logTransition("Activity_0veq5io", "Gateway_1c7340a");
        GATEWAY_Gateway_1c7340a_Exclusive1(s.withCurrent("Activity_0veq5io"));
    }

    public void TASK_Activity_16zc6e7_Final_Task(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Final Task [Activity_16zc6e7]
        BPMNExecProcessUtils.debugOutput(s, "Generic Task Final Task [Activity_16zc6e7]");
        BPMNExecProcessUtils.logCurrentNode("Activity_16zc6e7", "Final Task");
//[outgoing edge] Event_0zv9hfo - End
        BPMNExecProcessUtils.logTransition("Activity_16zc6e7", "Event_0zv9hfo");
        EVENT_Event_0zv9hfo_End(s.withCurrent("Activity_16zc6e7"));
    }

    public void TASK_Activity_1otvmx2_InnerParallelTask2(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task InnerParallelTask2 [Activity_1otvmx2]
        BPMNExecProcessUtils.debugOutput(s, "Generic Task InnerParallelTask2 [Activity_1otvmx2]");
        BPMNExecProcessUtils.logCurrentNode("Activity_1otvmx2", "InnerParallelTask2");
//[outgoing edge] Gateway_1iv1tvb - Parallel2Join
        BPMNExecProcessUtils.logTransition("Activity_1otvmx2", "Gateway_1iv1tvb");
        GATEWAY_Gateway_1iv1tvb_Parallel2Join(s.withCurrent("Activity_1otvmx2"));
    }

    public void init() {
        if (this.a == null) {
            a = BPMNExecProcessUtils.inputs.getProperty("a", null);
        }
        BPMNExecProcessUtils.logInput("a", this.a);

    }

    public boolean globalAssert(BPMNExecProcessUtils.ProcessStatus s, String node_id) {
        boolean success = true;

        return success;

    }

    public void execute(Object _a) {
        this.a = _a;
        BPMNExecProcessUtils.executeProcess("Parallel1Err", this::init, this::EVENT_StartEvent_1_Start);
    }

    public static void main(String[] args) {
        BPMNExecProcessUtils.setExternalTraceFile("Parallel1Err");
        BPMNExecProcessUtils.enableTrueParallel();
        Parallel1Err process = new Parallel1Err();
        process.execute(null/*a*/);
    }
}
