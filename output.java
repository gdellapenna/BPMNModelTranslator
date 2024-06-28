
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

class bpmn_process_Process_1njo00q {

    Object flow_Activity_0pfa4n5_thread;
    Object flow_Activity_0veq5io_thread;

    public void flow_StartEvent_1() {
//start: null;
        flow_Activity_0veq5io_thread = new FutureTask<>(() -> {
            flow_Activity_0veq5io_parallel();

            return 1;
        });
        flow_Activity_0veq5io_thread.run();
        flow_Activity_0pfa4n5_thread = new FutureTask<>(() -> {
            flow_Activity_0pfa4n5_parallel();

            return 1;
        });
        flow_Activity_0pfa4n5_thread.run();

        chiamata_a_funzione_join();//start:StartEvent_1//end:Gateway_1gapucr
    }

    public void flow_Activity_16zc6e7() {
        while (!Arrays.stream(((FutureTask<Integer>[]) flow_Activity_16zc6e7_parallels)).allMatch(t -> t.isDone())) {
            Thread.sleep(300);
        }

        task_generic_Task3();//start:Activity_16zc6e7//end:Activity_16zc6e7
    }

    public void flow_Activity_0pfa4n5_parallel() {
        task_generic_Task1();

    }

    public void flow_Activity_0veq5io_parallel() {
        task_generic_Task2();
        if (pippo().equals(1.0)) {
            task_generic_Task_4();
            ;
        } else {
            task_generic_Task_5();

        }
    }

    public void task_generic_Task_4() {
        System.out.println("task_generic_Task 4");

    }

    public void task_generic_Task_5() {
        System.out.println("task_generic_Task 5");

    }

    public void task_generic_Task3() {
        System.out.println("task_generic_Task3");

    }

    public void task_generic_Task2() {
        System.out.println("task_generic_Task2");

    }

    public void task_generic_Task1() {
        System.out.println("task_generic_Task1");

    }
}
