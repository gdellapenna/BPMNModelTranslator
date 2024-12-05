package dellapenna.personal.bpmn.dmn;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author giuse
 */
public class DMNTranslationInfo {
    
    private final List<List<String>> readVariables = new ArrayList<>();
    private final List<List<String>> writtenVariables = new ArrayList<>();

    public DMNTranslationInfo() {
    }

    public DMNTranslationInfo(DMNTranslationInfo other) {
        this.readVariables.addAll(other.readVariables);
        this.writtenVariables.addAll(other.writtenVariables);
    }

    public List<List<String>> getReadVariables() {
        return readVariables;
    }

    public List<List<String>> getWrittenVariables() {
        return writtenVariables;
    }
    
}
