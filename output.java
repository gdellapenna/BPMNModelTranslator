
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

    Object ID_usato_anche_nel_parallel_join_parallels;

    public void flow_StartEvent_1() {
//start: null;
        ID_usato_anche_nel_parallel_join_parallels = new FutureTask[2];
        FutureTask<Integer> t;
        t = new FutureTask<>(() -> {
            ID_usato_anche_nel_parallel_join_parallel_0();

            return 1;
        });
        t.run();
        ((FutureTask<Integer>[]) ID_usato_anche_nel_parallel_join_parallels)[0] = t;
        t = new FutureTask<>(() -> {
            ID_usato_anche_nel_parallel_join_parallel_1();

            return 1;
        });
        t.run();
        ((FutureTask<Integer>[]) ID_usato_anche_nel_parallel_join_parallels)[1] = t;

        chiamata_a_funzione_join();
    }

    public void flow_Activity_16zc6e7() {
        while (!Arrays.stream(((FutureTask<Integer>[]) flow_Activity_16zc6e7_parallels)).allMatch(t -> t.isDone())) {
            Thread.sleep(300);
        }

        task_generic_Task3();
    }

    public void ID_usato_anche_nel_parallel_join_parallel_1() {
        if (true) {
            task_generic_Task1();

        }
    }

    public void ID_usato_anche_nel_parallel_join_parallel_0() {
        if (true) {
            task_generic_Task2();

        }
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
