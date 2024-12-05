package dellapenna.personal.bpmn;

import dellapenna.personal.bpmn.bpmn.BPMNDecoded;
import dellapenna.personal.bpmn.bpmn.BpmnTranslatorException;
import dellapenna.personal.bpmn.bpmn.BPMNTranslationInfo;
import dellapenna.personal.bpmn.bpmn.ToJavaBPMNTranslator;
import dellapenna.personal.bpmn.dmn.DMNDecodedModel;
import dellapenna.personal.bpmn.dmn.DMNTranslator;
import dellapenna.personal.bpmn.dmn.DMNTranslationInfo;
import dellapenna.personal.bpmn.dmn.ToJavaDMNTranslator;
import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import dellapenna.personal.bpmn.feel.FeelTranslator;
import dellapenna.personal.bpmn.feel.ToJavaFeelTranslator;
import dellapenna.personal.bpmn.versim.ToJavaMainMaker;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.feel.FeelEngine;
import org.camunda.feel.api.EvaluationResult;
import org.camunda.feel.api.FeelEngineApi;
import org.camunda.feel.api.ParseResult;
import org.camunda.feel.impl.SpiServiceLoader;

/**
 *
 * @author giuse
 */
public class BPMNModelTest {

    public static void testFeel(String expression) throws FeelTranslatorException {
        FeelTranslator t = new ToJavaFeelTranslator();

        System.out.println(t.translate(expression, null));

        //////
        FeelEngine engine = new FeelEngine.Builder()
                .valueMapper(SpiServiceLoader.loadValueMapper())
                .functionProvider(SpiServiceLoader.loadFunctionProvider())
                .build();
        FeelEngineApi api = new FeelEngineApi(engine);

        final Map<String, Object> variables = Map.of("x", 21);

        ParseResult parsing = api.parseExpression(expression);
        if (parsing.isSuccess()) {
            EvaluationResult evaluating = api.evaluate(parsing.parsedExpression(), variables);
            if (evaluating.isSuccess()) {
                System.out.println("result is " + evaluating.result());
            } else {
                System.err.println("evaluation error: " + evaluating.failure().message());
            }
        } else {
            System.err.println("parsing error: " + parsing.failure().message());
        }
    }

    ////////////////////////////////////////
    public static final String PRE_CODE = "import dellapenna.personal.bpmn.exec.*;";
    public static final String POST_CODE = "";

    public static void compile(Path[] dmn_files, Path bpmn_file)
            throws IOException, FeelTranslatorException, BpmnTranslatorException {

        DMNTranslator<String> dt = new ToJavaDMNTranslator();
        ToJavaBPMNTranslator bt = new ToJavaBPMNTranslator();
        ToJavaMainMaker mm = new ToJavaMainMaker();

        BPMNTranslationInfo b_info = new BPMNTranslationInfo();
        b_info.setDebug(true);
        b_info.setTrueParallel(true);

        DMNTranslationInfo d_info = new DMNTranslationInfo();

        Path output_file = bpmn_file.toAbsolutePath().getParent().resolve(bpmn_file.getFileName() + ".java");

        try (BufferedWriter out = new BufferedWriter(new FileWriter(output_file.toFile()))) {
            out.write(PRE_CODE);
            out.newLine();
            out.newLine();
//
            DMNDecodedModel<String>[] dmns = new DMNDecodedModel[dmn_files.length];
            int i = 0;
            for (Path dmn_file : dmn_files) {
                DmnModelInstance dmnInstance = Dmn.readModelFromFile(dmn_file.toFile());
                dmns[i] = dt.decodeDecisionModel(dmnInstance, d_info);
                out.write(dt.generateDecisionModelSource(dmns[i++], d_info));
            }
//
            out.newLine();
//
            BpmnModelInstance bpmnInstance = Bpmn.readModelFromFile(bpmn_file.toFile());
            BPMNDecoded bpmn = bt.decodeBpmn(bpmnInstance, dmns, b_info);
            out.write(bt.generateBpmnSource(bpmn, b_info));
//
            out.newLine();
//
            out.write(mm.generateProcessLauncher(bpmn, dmns, b_info));
//
            out.newLine();
            out.newLine();
            out.write(POST_CODE);
            
            mm.findDMNConstraints(dmns, "GetLengthDT", "Type");
        }

        
    }

    public static void main(String[] args) throws FeelTranslatorException, IOException, BpmnTranslatorException {

        compile(new Path[]{Path.of("get_length.dmn"), Path.of("determine_mode.dmn"), Path.of("choose_consent.dmn")}, Path.of("diagram_shipment.bpmn"));

        compile(new Path[0], Path.of("diagram_loop.bpmn"));

        compile(new Path[0], Path.of("diagram_parallel.bpmn"));

        compile(new Path[0], Path.of("diagram_parallel_2.bpmn"));

        //FeelTranslator ft = new ToJavaFeelTranslator();
        //System.out.println(ft.generateBpmnSource("abs(x)>1 and (a=\"w\" or (b in [1..4]))"));
        //System.out.println(ft.generateBpmnSource("a.b=c"));        
    }
}
