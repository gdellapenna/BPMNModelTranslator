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
    private boolean generateReadNextGetters;

    private final List<List<String>> usedVariableNames;

    public FeelTranslationInfo() {
        this.usedVariableNames = new ArrayList<>();
        this.excludeGetters = new ArrayList<>();
        this.maxGettersLevel = 0;
        this.generateReadNextGetters = false;
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

    public void setGenerateGetters(boolean generateGetters) {
        if (!generateGetters) {
            this.maxGettersLevel = 0;
        } else if (this.maxGettersLevel <= 0) {
            this.maxGettersLevel = 1;
        }
    }

    public boolean isGenerateReadNextGetters() {
        return generateReadNextGetters;
    }

    public void setGenerateReadNextGetters(boolean generateReadNextGetters) {
        this.generateReadNextGetters = generateReadNextGetters;
    }

}
