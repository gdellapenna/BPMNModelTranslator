package dellapenna.personal.bpmn;

import dellapenna.personal.util.OutputManager;
import dellapenna.personal.bpmn.bpmn.BPMNDecodedModel;
import dellapenna.personal.bpmn.bpmn.BPMNDecodedProcess;
import dellapenna.personal.bpmn.bpmn.BpmnTranslatorException;
import dellapenna.personal.bpmn.bpmn.BPMNTranslationInfo;
import dellapenna.personal.bpmn.bpmn.ToJavaBPMNTranslator;
import dellapenna.personal.bpmn.bpmn.VariableDefinition;
import dellapenna.personal.bpmn.dmn.DMNDecodedModel;
import dellapenna.personal.bpmn.dmn.DMNDecodedRule;
import dellapenna.personal.bpmn.dmn.DMNDecodedTable;
import dellapenna.personal.bpmn.dmn.DMNTranslator;
import dellapenna.personal.bpmn.dmn.DMNTranslationInfo;
import dellapenna.personal.bpmn.dmn.ToJavaDMNTranslator;
import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import dellapenna.personal.bpmn.versim.VariableUtils;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;

/**
 *
 * @author Giuseppe Della Penna
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
        //b_info.addGlobalAssertion("pWeight>0", "weight is positive"); //TEST ONLY

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
            Path output_rules_file = output_folder.resolve(dmn_files.get(i).getFileName() + ".rules");
            //output table rules
            try (BufferedWriter outputRules = new BufferedWriter(new FileWriter(output_rules_file.toFile()))) {
                outputRules.write(generateDMNModelDescription(dmns[i]));
            }
        }

        //decode BPMNs
        BPMNDecodedModel[] bpmns = new BPMNDecodedModel[bpmn_files.size()];
        for (int i = 0; i < bpmn_files.size(); ++i) {
            Path bpmn_file = bpmn_files.get(i);
            BPMNTranslationInfo b_info = new BPMNTranslationInfo(t_info);
            //b_info.setDebug(true); //TEST ONLY
            //b_info.setTrueParallel(true); //TEST ONLY
            //b_info.addGlobalAssertion("pWeight>0", "weight is positive"); //TEST ONLY
            //b_info.addForcedInputVariable("a"); //TEST ONLY
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
                    //outputJava.write(generateProcessCode(process, dmns, b_info, d_info));
                }

                //output process inputs
                try (BufferedWriter outputProperties = new BufferedWriter(new FileWriter(gen_info.output_inputs_file.toFile()))) {
                    outputProperties.write(/*mm.*/generateInputProperties(process, dmns, b_info));
                }

                //output process graph
                try (BufferedWriter outputGraph = new BufferedWriter(new FileWriter(gen_info.output_graph_file.toFile()))) {
                    outputGraph.write(/*mm.*/generateProcessGraph(process, dmns, b_info));
                }

                generated_code.add(gen_info);
            }
        }
        return generated_code;
    }

    //DA ATTIVARE PREVIA VERIFICA 
//    public String generateProcessCode(BPMNDecodedProcess process, DMNDecodedModel<String>[] dmns,
//            BPMNTranslationInfo b_info, DMNTranslationInfo d_info) {
//
//        String result = "";
//        result += PRE_CODE;
//        result += "\n\n";
//        for (int j = 0; j < dmns.length; ++j) {
//            result += dt.generateDecisionModelSource(dmns[j], d_info);
//        }
//        result += "\n";
//        result += bt.generateProcessSource(process, b_info);
//        result += "\n\n";
//        result += POST_CODE;
//        return result;
//    }
    public String generateInputProperties(BPMNDecodedProcess process, DMNDecodedModel<String>[] dmns, BPMNTranslationInfo info) {
        outlog.emit(1, "Translator", "generating input properties");
        String result = "";

        VariableUtils vu = new VariableUtils();
        for (VariableDefinition v : process.getFreeVariables(info)) {

            if (!v.getUsages(BPMNDecodedProcess.VariableDirection.READ).stream().anyMatch(u -> u.sourceExpression() == null)) {
                outlog.emit(OutputManager.MessageType.WARNING, 2, "Translator", "Variable " + v.getName() + " is auto-declared as input being read but never written");
            } else {
                v.getUsages(BPMNDecodedProcess.VariableDirection.READ).stream().filter(u -> u.sourceExpression() == null).forEach(u -> {
                    outlog.emit(OutputManager.MessageType.INFO, 2, "Translator", "Variable " + v.getName() + " is declared as data input in node " + u.sourceId());
                });
            }

            vu.analyzeInputConstraints(v, dmns, info);
            String value = "?";
            if (v.getBounds().getCases() != null && !v.getBounds().getCases().isEmpty()) {
                value = v.getBounds().getCases().toArray(new String[0])[0];
            } else if (v.getBounds().getMin() != null) {
                value = String.valueOf(v.getBounds().getMin() + (v.getBounds().isMinExclusive() ? 1 : 0));
            }
            result += v.getName() + "=" + value + "\n#" + v.getBounds() + "\n";
        }

        for (VariableDefinition v : process.getBoundVariables(info)) {
            if (v.getUsages(BPMNDecodedProcess.VariableDirection.READ).stream().anyMatch(u -> u.sourceExpression() == null)) {
                outlog.emit(OutputManager.MessageType.ERROR, 2, "Translator", "Variable " + v.getName() + " is declared as data input but also written");
            }
        }

        return result;
    }

    public String generateProcessGraph(BPMNDecodedProcess process, DMNDecodedModel<String>[] dmns, BPMNTranslationInfo info) {
        outlog.emit(1, "Translator", "generating process graph");
        String result = "# nodes\n";
        for (Map.Entry<FlowNode, BPMNDecodedProcess.FlowNodeInfo> e : process.getGraph().entrySet()) {
            FlowNode s = e.getKey();
            result += s.getId() + "[" + (s.getName() != null ? ("label=\"" + s.getName() + "\"") : "") + "]" + "\n";
        }

        result += "\n\n# edges\n";
        for (Map.Entry<FlowNode, BPMNDecodedProcess.FlowNodeInfo> e : process.getGraph().entrySet()) {
            FlowNode s = e.getKey();
            for (FlowNode t : e.getValue().getOutgoingEdges()) {
                result += "\"" + s.getId() + "\"" + " -> " + "\"" + t.getId() + "\"" + "\n";
            }
        }

        return result;
    }

    private String generateDMNModelDescription(DMNDecodedModel<String> dmn) {
        outlog.emit(1, "Translator", "generating DMN model description " + dmn.id());
        String result = "";
        for (DMNDecodedTable<String> t : dmn.tables().values()) {
            result += "# table " + t.id() + "\n";
            for (DMNDecodedRule<String> r : t.rules()) {
                result += t.id() + "(" + r.id() + ")" + " #" + r.index() + "\n";
            }
        }
        return result;
    }
}
