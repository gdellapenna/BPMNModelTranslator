
class TypeUtils {

    public static Double tonumber(Object o) {
        if (o instanceof Number n) {
            return n.doubleValue();
        } else {
            try {
                return Double.valueOf(o.toString());
            } catch (NumberFormatException ex) {
                return 0.0; //should raise an exception
            }
        }
    }

    public static String tostring(Object o) {
        return o.toString();
    }

    public static Boolean toboolean(Object o) {
        if (o instanceof Boolean b) {
            return b;
        } else if (o instanceof Number n) {
            return n.doubleValue() != 0;
        } else {
            return Boolean.valueOf(o.toString());

        }
    }
}

class ProcessUtils {

    static java.io.PrintStream debugChannel = System.out;
    static java.io.PrintStream resultChannel = System.out;
    static java.util.Properties outputs = new java.util.Properties();
    static java.util.Properties inputs = new java.util.Properties();

    public static void start() {
        java.io.File inputs_file = new java.io.File("inputs.properties");
        if (inputs_file.canRead()) {
            try {
                inputs.load(new java.io.FileReader(inputs_file));
            } catch (java.io.IOException ex) {

            }
        }
    }

    public static void end() {
        java.io.File outputs_file = new java.io.File("outputs.properties");
        try {
            outputs.store(new java.io.FileWriter(outputs_file), null);
        } catch (java.io.IOException ex) {
            //
        }
        System.exit(Integer.parseInt(outputs.getProperty("code", "0")));
    }

    public static void signal(String s) {
    }

    public static void wait(String... s) {
    }

    public static void error(String s, int c) {
        ProcessUtils.debugOutput("ERROR: %s", s);
        ProcessUtils.logResult(false, s, c);
        ProcessUtils.end();
    }

    public static void noDefaultCaseError() {
        error("No default branch in gateway", 9999);
    }

    public static void success(String s, int c) {
        if (s != null) {
            ProcessUtils.debugOutput("SUCCESS: %s", s);
        } else {
            ProcessUtils.debugOutput("SUCCESS");
        }
        ProcessUtils.logResult(true, s, c);
        ProcessUtils.end();
    }

    public static void success() {
        success(null, 0);
    }

    public static void debugOutput(String s, Object... args) {
        String message = String.format(s, args);
        debugChannel.println(message);
    }

    public static void logInput(String name, Object value) {
        resultChannel.println(name + "=" + value);
        outputs.setProperty(name, (value != null ? value.toString() : "<NULL>"));
    }

    public static void logResult(boolean success, String message, int code) {
        resultChannel.println(success ? "SUCCESS" : "FAILURE" + "," + code + "," + message);
        outputs.setProperty("output_success", success ? "true" : "false");
        outputs.setProperty("output_message", message != null ? message : "");
        outputs.setProperty("output_code", String.valueOf(code));
    }
}

/*
 * ****************************** BPMN Generated Code *************************
 */
class bpmn_process_Process_0ffex4k {

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
        this.input_a = null;	//TODO assign input variable
        if (this.input_a == null) {
            input_a = ProcessUtils.inputs.getProperty("input_a", null);
        }
        ProcessUtils.logInput("input_a", this.input_a);
    }

    public static void main(String[] args) {
        ProcessUtils.start();
        bpmn_process_Process_0ffex4k process = new bpmn_process_Process_0ffex4k();
        process.init();
        process.flow_Start();
    }
}
