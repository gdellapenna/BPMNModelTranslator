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
                    ProcessUtils.logResult(false,s,c);
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
                    ProcessUtils.logResult(true,s,c);
                    ProcessUtils.end();
                }

                public static void success() {
                    success(null, 0);
                }

                public static void debugOutput(String s, Object... args) {
                    String message = String.format(s,args);
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
 class bpmn_process_Process_1njo00q { 

//Input Variables
;



//Process Variables
;



//Process Dynamics
public void flow_StartEvent_1() {//start event:
ProcessUtils.debugOutput("START EVENT: StartEvent_1");
//parallel split
ProcessUtils.debugOutput("PARALLEL GATEWAY Gateway_1gapucr");
ProcessUtils.fork("flow_Task2");
ProcessUtils.fork("flow_Task1");
}

public void flow_Task1() {task_generic_Task1();
ProcessUtils.signal(ID DEL NODO ENTRANTE);
}

public void flow_Task_4() {task_generic_Task_4();
ProcessUtils.signal(ID DEL NODO ENTRANTE);
}

public void flow_Task_5() {task_generic_Task_5();
ProcessUtils.signal(ID DEL NODO ENTRANTE);
}

public void flow_Task3() {task_generic_Task3();
}

public void flow_Task2() {task_generic_Task2();
//exclusive gateway
ProcessUtils.debugOutput("EXCLUSIVE GATEWAY Gateway_1c7340a");
if (pippo().equals(1.0)){ProcessUtils.debugOutput("JOINING FLOW flow_Task_4");
flow_Task_4();
} else {ProcessUtils.debugOutput("JOINING FLOW flow_Task_5");
flow_Task_5();
}
}

public void task_generic_Task_4() {//generic task: Task 4
ProcessUtils.debugOutput("TASK Task 4");
}

public void task_generic_Task_5() {//generic task: Task 5
ProcessUtils.debugOutput("TASK Task 5");
}

public void task_generic_Task3() {//generic task: Task3
ProcessUtils.debugOutput("TASK Task3");
}

public void task_generic_Task2() {//generic task: Task2
ProcessUtils.debugOutput("TASK Task2");
}

public void task_generic_Task1() {//generic task: Task1
ProcessUtils.debugOutput("TASK Task1");
}

public void init() {
}public static void main(String[] args) {
ProcessUtils.start();bpmn_process_Process_1njo00q process = new bpmn_process_Process_1njo00q();
process.init();
process.flow_StartEvent_1();
}}
