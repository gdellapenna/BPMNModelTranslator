
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
public void EVENT_Event_0aq6tzc_Ok1(BPMNExecProcessUtils.ProcessStatus s) {//End Event Ok1 [Event_0aq6tzc]
        BPMNExecProcessUtils.debugOutput("End Event Ok1 [Event_0aq6tzc]");
        BPMNExecProcessUtils.logCurrentNode("Event_0aq6tzc", "Ok1");
        BPMNExecProcessUtils.success(s);
    }

    public void EVENT_Event_0ftzlyc_Ok2(BPMNExecProcessUtils.ProcessStatus s) {//End Event Ok2 [Event_0ftzlyc]
        BPMNExecProcessUtils.debugOutput("End Event Ok2 [Event_0ftzlyc]");
        BPMNExecProcessUtils.logCurrentNode("Event_0ftzlyc", "Ok2");
        BPMNExecProcessUtils.success(s);
    }

    public void EVENT_StartEvent_1_Start(BPMNExecProcessUtils.ProcessStatus s) {//Start Event Start [StartEvent_1]
        BPMNExecProcessUtils.debugOutput("Start Event Start [StartEvent_1]");
        BPMNExecProcessUtils.logCurrentNode("StartEvent_1", "Start");
//[outgoing edge] Gateway_02uz50m - UnclosedParallel
        BPMNExecProcessUtils.logTransition("StartEvent_1", "Gateway_02uz50m");
        GATEWAY_Gateway_02uz50m_UnclosedParallel(s.withCurrent("StartEvent_1"));
    }

    public void GATEWAY_Gateway_02uz50m_UnclosedParallel(BPMNExecProcessUtils.ProcessStatus s) {//Parallel Gateway UnclosedParallel [Gateway_02uz50m]
        BPMNExecProcessUtils.debugOutput("Parallel Gateway UnclosedParallel [Gateway_02uz50m]");
        BPMNExecProcessUtils.logCurrentNode("Gateway_02uz50m", "UnclosedParallel");
//[outgoing edge] Activity_0qvix8b - Task1
        BPMNExecProcessUtils.logTransition("Gateway_02uz50m", "Activity_0qvix8b");
//[outgoing edge] Activity_1uu1n8a - Task2
        BPMNExecProcessUtils.logTransition("Gateway_02uz50m", "Activity_1uu1n8a");
//FORKS: Activity_0qvix8b,Activity_1uu1n8a
        BPMNExecProcessUtils.fork(s, "UnclosedParallel", this::TASK_Activity_0qvix8b_Task1, this::TASK_Activity_1uu1n8a_Task2);
        BPMNExecProcessUtils.stopThread();
    }

    public void TASK_Activity_0qvix8b_Task1(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Task1 [Activity_0qvix8b]
        BPMNExecProcessUtils.debugOutput("Generic Task Task1 [Activity_0qvix8b]");
        BPMNExecProcessUtils.logCurrentNode("Activity_0qvix8b", "Task1");
//[outgoing edge] Event_0aq6tzc - Ok1
        BPMNExecProcessUtils.logTransition("Activity_0qvix8b", "Event_0aq6tzc");
        EVENT_Event_0aq6tzc_Ok1(s.withCurrent("Activity_0qvix8b"));
    }

    public void TASK_Activity_1uu1n8a_Task2(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Task2 [Activity_1uu1n8a]
        BPMNExecProcessUtils.debugOutput("Generic Task Task2 [Activity_1uu1n8a]");
        BPMNExecProcessUtils.logCurrentNode("Activity_1uu1n8a", "Task2");
//[outgoing edge] Event_0ftzlyc - Ok2
        BPMNExecProcessUtils.logTransition("Activity_1uu1n8a", "Event_0ftzlyc");
        EVENT_Event_0ftzlyc_Ok2(s.withCurrent("Activity_1uu1n8a"));
    }

    public void init() {
//parallel join initializers

    }

    public boolean globalAssert(BPMNExecProcessUtils.ProcessStatus s, String node_id) {
        boolean success = true;

        return success;

    }

    public void execute() {
        BPMNExecProcessUtils.executeProcess("Process_1ai2j0m", this::init, this::EVENT_StartEvent_1_Start);
    }

    public static void main(String[] args) {
        BPMNExecProcessUtils.enableTrueParallel();
        bpmn_process_Process_1ai2j0m process = new bpmn_process_Process_1ai2j0m();
        process.execute();
    }
}

class Executor {

    public static void main(String[] args) {
        BPMNExecProcessUtils.enableTrueParallel();
        bpmn_process_Process_1ai2j0m process = new bpmn_process_Process_1ai2j0m();
        process.execute();
    }
}
