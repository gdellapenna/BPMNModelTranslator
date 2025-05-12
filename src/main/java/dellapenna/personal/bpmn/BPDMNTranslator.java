package dellapenna.personal.bpmn;

import dellapenna.personal.bpmn.bpmn.BPMNDecodedModel;
import dellapenna.personal.bpmn.bpmn.BPMNDecodedProcess;
import dellapenna.personal.bpmn.bpmn.BpmnTranslatorException;
import dellapenna.personal.bpmn.bpmn.BPMNTranslationInfo;
import dellapenna.personal.bpmn.bpmn.ToJavaBPMNTranslator;
import dellapenna.personal.bpmn.dmn.DMNDecodedModel;
import dellapenna.personal.bpmn.dmn.DMNTranslator;
import dellapenna.personal.bpmn.dmn.DMNTranslationInfo;
import dellapenna.personal.bpmn.dmn.ToJavaDMNTranslator;
import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import dellapenna.personal.bpmn.versim.ToJavaMainMaker;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;

/**
 *
 * @author giuse
 */
public class BPDMNTranslator {

    private static final OutputManager outlog = new OutputManager();

    public static class GenerationInfo {

        public BPMNDecodedModel bpmn;
        public BPMNDecodedProcess process;
        public DMNDecodedModel[] dmns;

        public Path output_java_file;
        public Path output_inputs_file;
        public Path output_graph_file;
    }

    public static final String PRE_CODE = "import dellapenna.personal.bpmn.exec.*;";
    public static final String POST_CODE = "";

    public static final String NS_DMN_2019_A = "https://www.omg.org/spec/DMN/20191111/MODEL/";
    public static final String NS_DMN_2019_B = "https://www.omg.org/spec/DMN/20191111/MODEL";
    public static final String NS_BPMN_2010_A = "http://www.omg.org/spec/BPMN/20100524/MODEL/";
    public static final String NS_BPMN_2010_B = "http://www.omg.org/spec/BPMN/20100524/MODEL";

    public static enum FileType {
        BPMN,
        DMN,
        OTHER
    };

    DMNTranslator<String> dt = new ToJavaDMNTranslator();
    ToJavaBPMNTranslator bt = new ToJavaBPMNTranslator();
    ToJavaMainMaker mm = new ToJavaMainMaker();

    protected String sanitizeName(String n) {
        return n.replaceAll("[^A-Za-z0-9_]", "_");
    }

    public FileType detectFileType(Path file) {
        String ns = "";
        try (Reader r = Files.newBufferedReader(file)) {
            XMLInputFactory xif = javax.xml.stream.XMLInputFactory.newInstance();
            XMLStreamReader xreader = xif.createXMLStreamReader(r);
            while (xreader.hasNext()) {
                int et = xreader.next();
                if (et == XMLStreamReader.START_ELEMENT) { //read only root element
                    ns = xreader.getNamespaceURI();
                    break;
                }
            }
            switch (ns) {
                case NS_BPMN_2010_A, NS_BPMN_2010_B -> {
                    return FileType.BPMN;
                }
                case NS_DMN_2019_A, NS_DMN_2019_B -> {
                    return FileType.DMN;
                }
            }
        } catch (IOException | XMLStreamException ex) {
            Logger.getLogger(BPDMNTranslator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return FileType.OTHER;
    }

    public DMNDecodedModel<String> compile_dmn(Path dmn_file, DMNTranslationInfo d_info)
            throws IOException, FeelTranslatorException, BpmnTranslatorException {

        if (d_info == null) {
            d_info = new DMNTranslationInfo();
        }
        DmnModelInstance dmnInstance = Dmn.readModelFromFile(dmn_file.toFile());
        outlog.emit(0, "Translator", "decoding DMN model " + dmn_file);
        DMNDecodedModel<String> dmn = dt.decodeDecisionModel(dmnInstance, d_info);
        return dmn;
    }

    public BPMNDecodedModel compile_bpmn(Path bpmn_file, DMNDecodedModel<String>[] dmns, BPMNTranslationInfo b_info)
            throws IOException, FeelTranslatorException, BpmnTranslatorException {

        if (b_info == null) {
            b_info = new BPMNTranslationInfo();
        }
        b_info.setDebug(true);
        b_info.setTrueParallel(true);
        //b_info.addGlobalAssertion("pWeight>0", "weight is positive");

        BpmnModelInstance bpmnInstance = Bpmn.readModelFromFile(bpmn_file.toFile());
        outlog.emit(0, "Translator", "decoding BPMN model " + bpmn_file);
        return bt.decodeBpmn(bpmnInstance, dmns, b_info);
    }

    public List<GenerationInfo> compile_inputs(Path output_folder, BPMNTranslationInfo t_info, Path... files) throws IOException, BDTransException {
        List<Path> dmn_files = new ArrayList<>();
        List<Path> bpmn_files = new ArrayList<>();
        List<GenerationInfo> generated_code = new ArrayList<>();

        for (Path file : files) {
            switch (detectFileType(file)) {
                case DMN ->
                    dmn_files.add(file);
                case BPMN ->
                    bpmn_files.add(file);
                default ->
                    throw new BDTransException("Unknown file type: " + file.toString());
            }
        }

        //decode DMNs
        DMNTranslationInfo d_info = new DMNTranslationInfo();
        DMNDecodedModel<String>[] dmns = new DMNDecodedModel[dmn_files.size()];
        for (int i = 0; i < dmn_files.size(); ++i) {
            dmns[i] = compile_dmn(dmn_files.get(i), d_info);
        }

        //decode BPMNs
        BPMNDecodedModel[] bpmns = new BPMNDecodedModel[bpmn_files.size()];
        for (int i = 0; i < bpmn_files.size(); ++i) {
            Path bpmn_file = bpmn_files.get(i);
            BPMNTranslationInfo b_info = new BPMNTranslationInfo(t_info);
            //b_info.setDebug(true);
            //b_info.setTrueParallel(true);
            //b_info.addGlobalAssertion("pWeight>0", "weight is positive");
            //b_info.addForcedInputVariable("a");
            bpmns[i] = compile_bpmn(bpmn_file, dmns, b_info);
            for (BPMNDecodedProcess process : bpmns[i].processes()) {
                GenerationInfo gen_info = new GenerationInfo();
                gen_info.dmns = dmns;
                gen_info.bpmn = bpmns[i];
                gen_info.process = process;
                gen_info.output_java_file = output_folder.resolve(sanitizeName(process.getName()) + ".java");
                gen_info.output_inputs_file = output_folder.resolve(sanitizeName(process.getName()) + "_inputs.properties");
                gen_info.output_graph_file = output_folder.resolve(sanitizeName(process.getName()) + ".graph");

                //output process class
                try (BufferedWriter outputJava = new BufferedWriter(new FileWriter(gen_info.output_java_file.toFile()))) {
                    outputJava.write(PRE_CODE);
                    outputJava.newLine();
                    outputJava.newLine();
                    for (int j = 0; j < dmns.length; ++j) {
                        outputJava.write(dt.generateDecisionModelSource(dmns[j], d_info));
                    }
                    outputJava.newLine();
                    outputJava.write(bt.generateProcessSource(process, b_info));
                    outputJava.newLine();
                    outputJava.newLine();
                    outputJava.write(POST_CODE);
                }

                //output process inputs
                try (BufferedWriter outputProperties = new BufferedWriter(new FileWriter(gen_info.output_inputs_file.toFile()))) {
                    outputProperties.write(mm.generateInputProperties(process, dmns, b_info));
                }

                //output process graph
                try (BufferedWriter outputGraph = new BufferedWriter(new FileWriter(gen_info.output_graph_file.toFile()))) {
                    outputGraph.write(mm.generateTraceProperties(process, dmns, b_info));
                }

                generated_code.add(gen_info);
            }
        }
        return generated_code;
    }
}
