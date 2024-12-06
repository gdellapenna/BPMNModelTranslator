package dellapenna.personal.bpmn.bpmn;

import dellapenna.personal.bpmn.bpmn.BPMNDecodedProcess.VariableDirection;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VariableDefinition {
    
    
    public static record UsageData(String sourceId, String sourceExpression){};

    String name;
    String type = null;
    Map<VariableDirection, Set<UsageData>> usages = new HashMap<>();

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Collection<UsageData> getUsages(VariableDirection d) {
        return usages.get(d);
    }

    public boolean isRead() {
        return !usages.get(VariableDirection.READ).isEmpty();
    }

    public boolean isWritten() {
        return !usages.get(VariableDirection.WRITE).isEmpty();
    }

    public VariableDefinition(String name) {
        this.usages.put(VariableDirection.READ, new HashSet<>());
        this.usages.put(VariableDirection.WRITE, new HashSet<>());
        this.name = name;
    }

}
