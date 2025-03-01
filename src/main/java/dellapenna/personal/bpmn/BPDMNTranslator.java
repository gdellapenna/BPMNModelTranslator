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
import dellapenna.personal.bpmn.versim.ToJavaMainMaker;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        } catch (IOException ex) {
            Logger.getLogger(BPDMNTranslator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLStreamException ex) {
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
        System.out.println("decoding DMN model " + dmn_file);
        DMNDecodedModel<String> dmn = dt.decodeDecisionModel(dmnInstance, d_info);
//        for (DMNDecodedTable<String> table : dmn.tables().values()) {
//            String tableClassName = sanitizeName("dmn_dtable_" + table.id());
//            Path output_java_file = dmn_file.toAbsolutePath().getParent().resolve(tableClassName + ".java");
//            outputJava.write(PRE_CODE);
//            outputJava.newLine();
//            outputJava.newLine();
//            outputJava.write(dt.generateDecisionTableSource(table, d_info));
//            outputJava.write(POST_CODE);
//        }
        return dmn;
    }

    public BPMNDecoded compile_bpmn(Path bpmn_file, DMNDecodedModel<String>[] dmns, BPMNTranslationInfo b_info)
            throws IOException, FeelTranslatorException, BpmnTranslatorException {

        if (b_info == null) {
            b_info = new BPMNTranslationInfo();
        }
        b_info.setDebug(true);
        b_info.setTrueParallel(true);
        //b_info.addGlobalAssertion("pWeight>0", "weight is positive");

        BpmnModelInstance bpmnInstance = Bpmn.readModelFromFile(bpmn_file.toFile());
        System.out.println("decoding BPMN model " + bpmn_file);
        return bt.decodeBpmn(bpmnInstance, dmns, b_info);
    }

    public List<Path> compile_inputs(Path output_folder, Path... files) throws IOException, FeelTranslatorException, BpmnTranslatorException, Exception {
        List<Path> dmn_files = new ArrayList<>();
        List<Path> bpmn_files = new ArrayList<>();
        List<Path> java_bpmn_files = new ArrayList<>();

        for (Path file : files) {
            switch (detectFileType(file)) {
                case DMN ->
                    dmn_files.add(file);
                case BPMN ->
                    bpmn_files.add(file);
                default ->
                    throw new Exception("Unknown file type: " + file.toString());
            }
        }

        DMNTranslationInfo d_info = new DMNTranslationInfo();
        DMNDecodedModel<String>[] dmns = new DMNDecodedModel[dmn_files.size()];
        BPMNDecoded[] bpmns = new BPMNDecoded[bpmn_files.size()];
        for (int i = 0; i < dmn_files.size(); ++i) {
            dmns[i] = compile_dmn(dmn_files.get(i), d_info);
        }

        for (int i = 0; i < bpmn_files.size(); ++i) {
            Path bpmn_file = bpmn_files.get(i);

            BPMNTranslationInfo b_info = new BPMNTranslationInfo();
            b_info.setDebug(true);
            b_info.setTrueParallel(true);
            //b_info.addGlobalAssertion("pWeight>0", "weight is positive");

            bpmns[i] = compile_bpmn(bpmn_file, dmns, b_info);

            //Path base_folder = Paths.get(".").toAbsolutePath().normalize();

            for (BPMNDecoded bpmn : bpmns) {

//            Path output_java_file = bpmn_file.toAbsolutePath().getParent().resolve(bpmn_file.getFileName() + ".java");
//            Path output_inputs_file = bpmn_file.toAbsolutePath().getParent().resolve(bpmn.processes().get(0).getName() + "_inputs.properties");
//            Path output_graph_file = bpmn_file.toAbsolutePath().getParent().resolve(bpmn.processes().get(0).getName() + ".graph");
//            Path base_folder = bpmn_file.toAbsolutePath().getParent();
                Path output_java_file = output_folder.resolve(sanitizeName(bpmn.processes().get(0).getName()) + ".java");
                Path output_inputs_file = output_folder.resolve(sanitizeName(bpmn.processes().get(0).getName()) + "_inputs.properties");
                Path output_graph_file = output_folder.resolve(sanitizeName(bpmn.processes().get(0).getName()) + ".graph");

                try (BufferedWriter outputJava = new BufferedWriter(new FileWriter(output_java_file.toFile()))) {
                    outputJava.write(PRE_CODE);
                    outputJava.newLine();
                    outputJava.newLine();
                    for (int j = 0; j < dmns.length; ++j) {
                        outputJava.write(dt.generateDecisionModelSource(dmns[j], d_info));
                    }
                    outputJava.newLine();
                    outputJava.write(bt.generateBpmnSource(bpmn, b_info));
                    outputJava.newLine();
                    outputJava.newLine();
                    outputJava.write(POST_CODE);
                }

                try (BufferedWriter outputProperties = new BufferedWriter(new FileWriter(output_inputs_file.toFile()))) {
                    outputProperties.write(mm.generateInputProperties(bpmn.processes().get(0), dmns, b_info));
                }

                try (BufferedWriter outputGraph = new BufferedWriter(new FileWriter(output_graph_file.toFile()))) {
                    outputGraph.write(mm.generateTraceProperties(bpmn.processes().get(0), dmns, b_info));
                }

                java_bpmn_files.add(output_java_file);
            }
        }
        return java_bpmn_files;
        //save_compiled(dmns, bpmns);
    }

    //il problema è che, ad esempio, per un modello dmn possono dover essere generate più classi, e non viene fatto qui..
//    public void save_compiled(String output_path, DMNDecodedModel<String>[] dmns, BPMNDecoded[] bpmns)
//            throws IOException, FeelTranslatorException, BpmnTranslatorException, Exception {
//
//        for (int i = 0; i < dmns.length; ++i) {
//            Path output_java_file = Path.of(output_path).resolve(dmns[i].id() + ".java");
//        }
//
//        for (int i = 0; i < bpmns.length; ++i) {
//
//            Path output_java_file = bpmn_file.toAbsolutePath().getParent().resolve(bpmn_file.getFileName() + ".java");
//            Path output_inputs_file = bpmn_file.toAbsolutePath().getParent().resolve(bpmn.processes().get(0).getName() + "_inputs.properties");
//
//            try (BufferedWriter outputJava = new BufferedWriter(new FileWriter(output_java_file.toFile()))) {
//                outputJava.write(PRE_CODE);
//                outputJava.newLine();
//                outputJava.newLine();
//                for (int j = 0; j < dmns.length; ++j) {
//                    outputJava.write(dt.generateDecisionModelSource(dmns[j], d_info));
//                }
//                outputJava.newLine();
//                outputJava.write(bt.generateBpmnSource(bpmn, b_info));
//                outputJava.newLine();
//                outputJava.newLine();
//                outputJava.write(POST_CODE);
//            }
//
//            try (BufferedWriter outputProperties = new BufferedWriter(new FileWriter(output_inputs_file.toFile()))) {
//                outputProperties.write(mm.generateInputProperties(bpmn.processes().get(0), dmns, b_info));
//            }
//        }
//    }
//    public void compile(Path[] dmn_files, Path bpmn_file)
//            throws IOException, FeelTranslatorException, BpmnTranslatorException {
//
//        DMNTranslator<String> dt = new ToJavaDMNTranslator();
//        ToJavaBPMNTranslator bt = new ToJavaBPMNTranslator();
//        ToJavaMainMaker mm = new ToJavaMainMaker();
//
//        BPMNTranslationInfo b_info = new BPMNTranslationInfo();
//        b_info.setDebug(true);
//        b_info.setTrueParallel(true);
//        //b_info.addGlobalAssertion("pWeight>0", "weight is positive");
//
//        DMNTranslationInfo d_info = new DMNTranslationInfo();
//
//        DMNDecodedModel<String>[] dmns = new DMNDecodedModel[dmn_files.length];
//
//        for (int i = 0; i < dmn_files.length; ++i) {
//            DmnModelInstance dmnInstance = Dmn.readModelFromFile(dmn_files[i].toFile());
//            System.out.println("decoding DMN model " + dmn_files[i]);
//            dmns[i] = dt.decodeDecisionModel(dmnInstance, d_info);
//        }
//        BpmnModelInstance bpmnInstance = Bpmn.readModelFromFile(bpmn_file.toFile());
//        System.out.println("decoding BPMN model " + bpmn_file);
//        BPMNDecoded bpmn = bt.decodeBpmn(bpmnInstance, dmns, b_info);
//
//        Path output_java_file = bpmn_file.toAbsolutePath().getParent().resolve(bpmn_file.getFileName() + ".java");
//        Path output_inputs_file = bpmn_file.toAbsolutePath().getParent().resolve(bpmn.processes().get(0).getName() + "_inputs.properties");
//        Path trace_inputs_file = bpmn_file.toAbsolutePath().getParent().resolve(bpmn.processes().get(0).getName() + "_nodes.list");
//
//        try (BufferedWriter outputJava = new BufferedWriter(new FileWriter(output_java_file.toFile()))) {
//            outputJava.write(PRE_CODE);
//            outputJava.newLine();
//            outputJava.newLine();
//            for (int i = 0; i < dmns.length; ++i) {
//                outputJava.write(dt.generateDecisionModelSource(dmns[i], d_info));
//            }
//            outputJava.newLine();
//            outputJava.write(bt.generateBpmnSource(bpmn, b_info));
//            outputJava.newLine();
//            outputJava.write(mm.generateProcessLauncher(bpmn, dmns, b_info));
////
//            //deve essere per-processo?!?
//            //outputJava.write(mm.generateInputCostraintNotes(bpmn.processes().get(0), dmns, b_info));
////
//            outputJava.newLine();
//            outputJava.newLine();
//            outputJava.write(POST_CODE);
//        }
//
//        try (BufferedWriter outputProperties = new BufferedWriter(new FileWriter(output_inputs_file.toFile()))) {
//            outputProperties.write(mm.generateInputProperties(bpmn.processes().get(0), dmns, b_info));
//        }
//
//        try (BufferedWriter outputGraph = new BufferedWriter(new FileWriter(trace_inputs_file.toFile()))) {
//            outputGraph.write(mm.generateTraceProperties(bpmn.processes().get(0), dmns, b_info));
//        }
//
//    }
}
