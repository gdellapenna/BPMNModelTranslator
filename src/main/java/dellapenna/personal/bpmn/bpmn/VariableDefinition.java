package dellapenna.personal.bpmn.bpmn;

import dellapenna.personal.bpmn.bpmn.BPMNDecodedProcess.VariableDirection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VariableDefinition {

    public static record VariableUsageData(String sourceId, String sourceExpression) {

    }

    ;
    public static class VariableBounds {

        private List<String> cases = null;
        private List<String> expressions = null;
        private Double min = null;
        private Double max = null;

        public List<String> getCases() {
            return cases;
        }

        public void addCase(String c) {
            if (this.cases == null) {
                this.cases = new ArrayList<>();
            }
            this.cases.add(c);
        }

        public List<String> getExpressions() {
            return expressions;
        }

        public void addExpression(String e) {
            if (this.expressions == null) {
                this.expressions = new ArrayList<>();
            }
            this.expressions.add(e);
        }

        public Double getMin() {
            return min;
        }

        public void updateMin(Double min, boolean exclusive) {
            if (this.min == null) {
                this.min = min;
            } else {
                this.min = Math.min(this.min, min);
            }
        }

        public Double getMax() {
            return max;
        }

        public void updateMax(Double max, boolean exclusive) {
            if (this.max == null) {
                this.max = max;
            } else {
                this.max = Math.max(this.max, max);
            }
        }

        public void updateRange(double member) {
            updateMax(member, false);
            updateMin(member, false);
        }

        @Override
        public String toString() {
            return "VariableBounds{" + "cases=" + cases + ", expressions=" + expressions + ", min=" + min + ", max=" + max + '}';
        }

    };

    String name;
    String type = null;
    Map<VariableDirection, Set<VariableUsageData>> usages = new HashMap<>();
    VariableBounds bounds = new VariableBounds();

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Collection<VariableUsageData> getUsages(VariableDirection d) {
        return usages.get(d);
    }

    public boolean isRead() {
        return !usages.get(VariableDirection.READ).isEmpty();
    }

    public boolean isWritten() {
        return !usages.get(VariableDirection.WRITE).isEmpty();
    }

    public VariableBounds getBounds() {
        return bounds;
    }

    public VariableDefinition(String name) {
        this.usages.put(VariableDirection.READ, new HashSet<>());
        this.usages.put(VariableDirection.WRITE, new HashSet<>());
        this.name = name;
    }

}
