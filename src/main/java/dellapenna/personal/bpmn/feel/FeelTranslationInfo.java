package dellapenna.personal.bpmn.feel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author giuse
 */
public class FeelTranslationInfo {

    private List<String> excludeGetters;
    private int maxGettersLevel;

    private final List<List<String>> usedVariableNames;

    public FeelTranslationInfo(boolean generateGetters) {
        this.usedVariableNames = new ArrayList<>();
        this.excludeGetters = new ArrayList<>();
        this.maxGettersLevel = generateGetters ? 1 : 0;
    }

    public List<List<String>> getUsedVariableNames() {
        return usedVariableNames;
    }

    public List<String> getExcludeGetters() {
        return excludeGetters;
    }

    public void setExcludeGetters(List<String> excludeGetters) {
        this.excludeGetters = excludeGetters;
    }

    public int getMaxGettersLevel() {
        return maxGettersLevel;
    }

    public void setMaxGettersLevel(int maxGettersLevel) {
        this.maxGettersLevel = maxGettersLevel;
    }

}
