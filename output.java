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

 class bpmn_process_Process_1njo00q { ; public void flow_StartEvent_1() {//start event: null;
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
if (pippo().equals(1.0)){flow_Task_4();
} else {flow_Task_5();
};
}

public void task_generic_Task_4() {System.out.println("task_generic_Task 4");
}

public void task_generic_Task_5() {System.out.println("task_generic_Task 5");
}

public void task_generic_Task3() {System.out.println("task_generic_Task3");
}

public void task_generic_Task2() {System.out.println("task_generic_Task2");
}

public void task_generic_Task1() {System.out.println("task_generic_Task1");
}}
