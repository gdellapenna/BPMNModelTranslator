
import dellapenna.personal.bpmn.exec.*;


/*
 * ****************************** Process Code *************************
 */
class SimplePAR {

//Input Variables
    ;



//Process Variables
;



//Process Dynamics
public void EVENT_E_E(BPMNExecProcessUtils.ProcessStatus s) {//End Event E [E]
        BPMNExecProcessUtils.debugOutput(s, "End Event E [E]");
        BPMNExecProcessUtils.logCurrentNode("E", "E");
        BPMNExecProcessUtils.success(s);
    }

    public void EVENT_S_S(BPMNExecProcessUtils.ProcessStatus s) {//Start Event S [S]
        BPMNExecProcessUtils.debugOutput(s, "Start Event S [S]");
        BPMNExecProcessUtils.logCurrentNode("S", "S");
//[outgoing edge] A - A
        BPMNExecProcessUtils.logTransition("S", "A");
        TASK_A_A(s.withCurrent("S"));
    }

    public void GATEWAY_G1_G1(BPMNExecProcessUtils.ProcessStatus s) {//Parallel Gateway G1 [G1]
        BPMNExecProcessUtils.debugOutput(s, "Parallel Gateway G1 [G1]");
        BPMNExecProcessUtils.logCurrentNode("G1", "G1");
//[outgoing edge] B - B
        BPMNExecProcessUtils.logTransition("G1", "B");
//[outgoing edge] C - C
        BPMNExecProcessUtils.logTransition("G1", "C");
//FORKS: B,C
        BPMNExecProcessUtils.fork(s, "G1", this::TASK_B_B, this::TASK_C_C);
        BPMNExecProcessUtils.stopThread();
    }

    public void GATEWAY_G2_G2(BPMNExecProcessUtils.ProcessStatus s) {//Parallel Joining Gateway G2 [G2]
        BPMNExecProcessUtils.debugOutput(s, "Parallel Joining Gateway G2 [G2]");
        BPMNExecProcessUtils.logCurrentNode("G2", "G2");
//[outgoing edge] D - D
        BPMNExecProcessUtils.logTransition("G2", "D");
//JOINS: B,C
        BPMNExecProcessUtils.join(s, "G2", this::TASK_D_D);
    }

    public void TASK_A_A(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task A [A]
        BPMNExecProcessUtils.debugOutput(s, "Generic Task A [A]");
        BPMNExecProcessUtils.logCurrentNode("A", "A");
//[outgoing edge] G1 - G1
        BPMNExecProcessUtils.logTransition("A", "G1");
        GATEWAY_G1_G1(s.withCurrent("A"));
    }

    public void TASK_B_B(BPMNExecProcessUtils.ProcessStatus s) {//Send Task B [B]
        BPMNExecProcessUtils.debugOutput(s, "Send Task B [B]");
        BPMNExecProcessUtils.logCurrentNode("B", "B");
        BPMNExecProcessUtils.Message m = new BPMNExecProcessUtils.Message();
        m.setPart("v1", "pippo");
        BPMNExecProcessUtils.debugOutput(s, "	 SENDING message on channel pipe");
        BPMNExecProcessUtils.sendMessage(s, "pipe", m);
//[outgoing edge] G2 - G2
        BPMNExecProcessUtils.logTransition("B", "G2");
        GATEWAY_G2_G2(s.withCurrent("B"));
    }

    public void TASK_C_C(BPMNExecProcessUtils.ProcessStatus s) {//Receive Task C [C]
        BPMNExecProcessUtils.debugOutput(s, "Receive Task C [C]");
        BPMNExecProcessUtils.logCurrentNode("C", "C");
        BPMNExecProcessUtils.debugOutput(s, "	 RECEIVING message on channel pipe");
        BPMNExecProcessUtils.Message message1 = BPMNExecProcessUtils.receiveMessage(s, "pipe");
//[outgoing edge] G2 - G2
        BPMNExecProcessUtils.logTransition("C", "G2");
        GATEWAY_G2_G2(s.withCurrent("C"));
    }

    public void TASK_D_D(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task D [D]
        BPMNExecProcessUtils.debugOutput(s, "Generic Task D [D]");
        BPMNExecProcessUtils.logCurrentNode("D", "D");
//[outgoing edge] E - E
        BPMNExecProcessUtils.logTransition("D", "E");
        EVENT_E_E(s.withCurrent("D"));
    }

    public void init() {

    }

    public boolean globalAssert(BPMNExecProcessUtils.ProcessStatus s, String node_id) {
        boolean success = true;

        return success;

    }

    public void execute() {
        BPMNExecProcessUtils.executeProcess("SimplePAR", this::init, this::EVENT_S_S);
    }

    public static void main(String[] args) {
        BPMNExecProcessUtils.setExternalTraceFile("SimplePAR");
        BPMNExecProcessUtils.enableTrueParallel();
        SimplePAR process = new SimplePAR();
        process.execute();
    }
}
