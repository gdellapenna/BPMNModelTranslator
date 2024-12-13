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
        boolean minExclusive = false;
        private Double max = null;
        boolean maxExclusive = false;

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
            if (this.min == null || this.min >= min) {
                this.min = min;
                this.minExclusive = exclusive;
            }
        }

        public Double getMax() {
            return max;
        }

        public void updateMax(Double max, boolean exclusive) {
            if (this.max == null || this.max <= max) {
                this.max = max;
                this.maxExclusive = exclusive;
            }
        }

        public void updateRange(double member) {
            updateMax(member, false);
            updateMin(member, false);
        }

        public boolean isMinExclusive() {
            return minExclusive;
        }

        public void setMinExclusive(boolean minExclusive) {
            this.minExclusive = minExclusive;
        }

        public boolean isMaxExclusive() {
            return maxExclusive;
        }

        public void setMaxExclusive(boolean maxExclusive) {
            this.maxExclusive = maxExclusive;
        }

        @Override
        public String toString() {
            String result = "";
            if (cases != null && !cases.isEmpty()) {
                result += (!result.isBlank() ? " / " : "") + "ENUM: " + String.join(",", cases);
            }
            if (min != null) {
                result += (!result.isBlank() ? " / " : "") + "MIN: " + min + " " + (minExclusive ? "EXCLUSIVE" : "INCLUSIVE");
            }
            if (max != null) {
                result += (!result.isBlank() ? " / " : "") + "MAX: " + max + " " + (maxExclusive ? "EXCLUSIVE" : "INCLUSIVE");
            }
            if (expressions != null && !expressions.isEmpty()) {
                result += (!result.isBlank() ? " / " : "") + "UNHANDLED: " + String.join(",", expressions);
            }
            return result;
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
