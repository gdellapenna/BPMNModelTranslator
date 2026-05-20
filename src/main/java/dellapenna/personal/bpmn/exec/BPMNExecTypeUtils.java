package dellapenna.personal.bpmn.exec;

import java.util.Collections;
import java.util.Map;

public class BPMNExecTypeUtils {

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
        if (o == null) {
            return false;
        } else if (o instanceof Boolean b) {
            return b;
        } else if (o instanceof Number n) {
            return n.doubleValue() != 0;
        } else {
            return Boolean.valueOf(o.toString());

        }
    }

    public static Map<String, Object> tocontext(Object o) {
        if (o != null) {
            if (o instanceof Map m
                    && m.keySet().stream().allMatch(k -> k instanceof String)) {
                return m;
            }
        }
        return Collections.EMPTY_MAP; //o un errore?
    }

    public static boolean equals(Object o1, Object o2) {
        if (o1.getClass().equals(o2.getClass())) {
            return o1.equals(o2);
        } else if (o1 instanceof Number n1 && o2 instanceof Number n2) {
            return n1.doubleValue() == n2.doubleValue();
        } else if (o1 instanceof String || o2 instanceof String) {
            return o1.toString().equals(o2.toString());
        } else {
            return o1.equals(o2);

        }
    }
}
