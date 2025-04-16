package dellapenna.personal.bpmn.bpmn;

import java.util.HashSet;
import java.util.Set;

public class MessageDefinition {

    private String name;
    private Set<String> parts = new HashSet<>();

    public MessageDefinition(String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the parts
     */
    public Set<String> getParts() {
        return parts;
    }

    /**
     * @param parts the parts to set
     */
    public void setParts(Set<String> parts) {
        this.parts = parts;
    }

}
