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

        DMNDecodedModel<String>[] dmns = new DMNDecodedModel[dmn_files.length];

        for (int i = 0; i < dmn_files.length; ++i) {
            DmnModelInstance dmnInstance = Dmn.readModelFromFile(dmn_files[i].toFile());
            dmns[i] = dt.decodeDecisionModel(dmnInstance, d_info);
        }
        BpmnModelInstance bpmnInstance = Bpmn.readModelFromFile(bpmn_file.toFile());
        BPMNDecoded bpmn = bt.decodeBpmn(bpmnInstance, dmns, b_info);

        Path output_java_file = bpmn_file.toAbsolutePath().getParent().resolve(bpmn_file.getFileName() + ".java");
        Path output_inputs_file = bpmn_file.toAbsolutePath().getParent().resolve(bpmn.processes().get(0).getName() + "_inputs.properties");

        try (BufferedWriter outputJava = new BufferedWriter(new FileWriter(output_java_file.toFile()))) {
            outputJava.write(PRE_CODE);
            outputJava.newLine();
            outputJava.newLine();
//
            for (int i = 0; i < dmns.length; ++i) {
                outputJava.write(dt.generateDecisionModelSource(dmns[i], d_info));
            }
//
            outputJava.newLine();
//
            outputJava.write(bt.generateBpmnSource(bpmn, b_info));
//
            outputJava.newLine();
//
            outputJava.write(mm.generateProcessLauncher(bpmn, dmns, b_info));
//
            //deve essere per-processo?!?
            //outputJava.write(mm.generateInputCostraintNotes(bpmn.processes().get(0), dmns, b_info));
//
            outputJava.newLine();
            outputJava.newLine();
            outputJava.write(POST_CODE);
        }
        
        try (BufferedWriter outputProperties = new BufferedWriter(new FileWriter(output_inputs_file.toFile()))) {
            outputProperties.write(mm.generateInputProperties(bpmn.processes().get(0), dmns, b_info));
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
