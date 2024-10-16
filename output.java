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
        public static void NoDefaultError() { System.exit(9999); }
        public static void signal(String s) {  }
        public static void wait(String... s) {  }
    }

 class bpmn_process_Process_1njo00q { 

//Process Variables
;



//Process Dynamics
public void task_generic_Task_4() {//generic task: Task 4;
}

public void task_generic_Task_5() {//generic task: Task 5;
}

public void task_generic_Task3() {//generic task: Task3;
}

public void task_generic_Task2() {//generic task: Task2;
}

public void task_generic_Task1() {//generic task: Task1;
}

public void flow_StartEvent_1() {//start event: ;
//parallel split;
ProcessUtils.fork("ID_DELPARALLELO","ID_DEL_RAMO","funzione_da_chiamare");
ProcessUtils.fork("ID_DELPARALLELO","ID_DEL_RAMO","funzione_da_chiamare");
}

public void flow_Task1() {task_generic_Task1();
ProcessUtils.signal("ID_DELPARALLELO","ID_DEL_RAMO");
}

public void flow_Task_4() {task_generic_Task_4();
ProcessUtils.signal("ID_DELPARALLELO","ID_DEL_RAMO");
}

public void flow_Task_5() {task_generic_Task_5();
ProcessUtils.signal("ID_DELPARALLELO","ID_DEL_RAMO");
}

public void flow_Task3() {
    ProcessUtils.wait("ID_DELPARALLELO");
    
    task_generic_Task3();
}

public void flow_Task2() {task_generic_Task2();
if (pippo().equals(1.0)){flow_Task_4();
} else {flow_Task_5();
};
}

}
