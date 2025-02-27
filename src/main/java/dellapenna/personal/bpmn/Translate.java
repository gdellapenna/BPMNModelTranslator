package dellapenna.personal.bpmn;

import dellapenna.personal.bpmn.bpmn.BpmnTranslatorException;
import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import java.io.IOException;
import java.nio.file.Path;

/**
 *
 * @author giuse
 */
public class Translate {

    public static void main(String[] args) throws FeelTranslatorException, IOException, BpmnTranslatorException, Exception {

        BPDMNTranslator t = new BPDMNTranslator();
        Path[] inputs = new Path[args.length];
        for (int i = 0; i < args.length; ++i) {
            inputs[i] = Path.of(args[i]);
        }
        t.compile_inputs(inputs);

    }
}
