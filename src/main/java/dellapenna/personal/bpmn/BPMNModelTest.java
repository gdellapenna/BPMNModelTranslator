package dellapenna.personal.bpmn;

import dellapenna.personal.bpmn.bpmn.AbstractBPMNTranslator;
import dellapenna.personal.bpmn.bpmn.BpmnTranslatorException;
import dellapenna.personal.bpmn.bpmn.Options;
import dellapenna.personal.bpmn.bpmn.ToJavaBPMNTranslator;
import dellapenna.personal.bpmn.dmn.DMNTranslator;
import dellapenna.personal.bpmn.dmn.ToJavaDMNTranslator;
import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import dellapenna.personal.bpmn.feel.FeelTranslator;
import dellapenna.personal.bpmn.feel.ToJavaFeelTranslator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

        System.out.println(t.translate(expression));

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

    public static String pre_code() {
        return """
            class TypeUtils {

                public static Double tonumber(Object o) {
                    if (o instanceof Number n) {
                        return n.doubleValue();
                    } else {
                        try {
                            return Double.valueOf(o.toString());
                        } catch (NumberFormatException ex) {
                            return 0.0; //should raise an exception
                        }
                    }
                }

                public static String tostring(Object o) {
                    return o.toString();
                }

                public static Boolean toboolean(Object o) {
                    if (o instanceof Boolean b) {
                        return b;
                    } else if (o instanceof Number n) {
                        return n.doubleValue() != 0;
                    } else {
                        return Boolean.valueOf(o.toString());

                    }
                }
            }
               
               class ProcessUtils {
                         static java.io.PrintStream debugChannel = System.out;
                         static java.io.PrintStream resultChannel = System.out;
                         static java.util.Properties outputs = new java.util.Properties();
                         static java.util.Properties inputs = new java.util.Properties();
               
                         
                             public static void start() {        
                                 java.io.File inputs_file = new java.io.File("inputs.properties");
                                 if (inputs_file.canRead()) {
                                     try {
                                         inputs.load(new java.io.FileReader(inputs_file));
                                     } catch (java.io.IOException ex) {
                         
                                 }
                             }
                         }
                             
                             public static void end() {
                                 java.io.File outputs_file = new java.io.File("outputs.properties");
                                try {
                                    outputs.store(new java.io.FileWriter(outputs_file), null);
                                } catch (java.io.IOException ex) {
                                    //
                                }                                 
                                 System.exit(Integer.parseInt(outputs.getProperty("code", "0")));
                             }
               
                	 public static void signal(String s) {
                            }
                        
                            public static void wait(String... s) {
                            }
                        
                            public static void error(String s, int c) {
                                ProcessUtils.debugOutput("ERROR: %s", s);
                                ProcessUtils.logResult(false,s,c);
                                ProcessUtils.end();
                            }
                        
                            public static void noDefaultCaseError() {
                                error("No default branch in gateway", 9999);
                            }
                        
                            public static void success(String s, int c) {
                                if (s != null) {
                                    ProcessUtils.debugOutput("SUCCESS: %s", s);
                                } else {
                                    ProcessUtils.debugOutput("SUCCESS");               
                                }
                                ProcessUtils.logResult(true,s,c);
                                ProcessUtils.end();                                
                            }
                        
                            public static void success() {
                                success(null, 0);                                
                            }
                        
                            public static void debugOutput(String s, Object... args) {
                                String message = String.format(s,args);
                                debugChannel.println(message);
                            }         	
               
                            public static void logInput(String name, Object value) {
                                    resultChannel.println(name + "=" + value);
                                    outputs.setProperty(name, (value != null ? value.toString() : "<NULL>"));
                                }
                            
                            public static void logResult(boolean success, String message, int code) {
                                resultChannel.println(success ? "SUCCESS" : "FAILURE" + "," + code + "," + message);
                                outputs.setProperty("output_success", success ? "true" : "false");
                                outputs.setProperty("output_message", message != null ? message : "");
                                outputs.setProperty("output_code", String.valueOf(code));
                            }  
                }
            """;
    }

    public static String post_code() {
        return "";
    }

    public static void paperTranslation() throws IOException, FeelTranslatorException, BpmnTranslatorException {
        Options opt = new Options();
        opt.setDebug(true);
        try (BufferedWriter out = new BufferedWriter(new FileWriter("paper.java"))) {

            out.write(pre_code());
            out.newLine();

            DMNTranslator<String> dt = new ToJavaDMNTranslator();
            AbstractBPMNTranslator bt = new ToJavaBPMNTranslator();

            DmnModelInstance dmnInstance = Dmn.readModelFromFile(new File("get_length.dmn"));
            out.write(dt.translate(dmnInstance));
            dmnInstance = Dmn.readModelFromFile(new File("determine_mode.dmn"));
            out.write(dt.translate(dmnInstance));
            dmnInstance = Dmn.readModelFromFile(new File("choose_consent.dmn"));
            out.write(dt.translate(dmnInstance));

            out.newLine();
            out.newLine();

            BpmnModelInstance bpmnInstance = Bpmn.readModelFromFile(new File("diagram_paper.bpmn"));
            out.write(bt.generateBpmnSource(bt.decodeBpmn(bpmnInstance, opt), opt));

            out.newLine();
            out.write(post_code());
        }

    }

    public static void main(String[] args) throws FeelTranslatorException, IOException, BpmnTranslatorException {
        paperTranslation();
        //System.exit(0);

        Options opt = new Options();
        opt.setDebug(true);
        BufferedWriter out = new BufferedWriter(new FileWriter("output.java"));

        //FeelTranslator ft = new ToJavaFeelTranslator();
        //System.out.println(ft.generateBpmnSource("abs(x)>1 and (a=\"w\" or (b in [1..4]))"));
        //System.out.println(ft.generateBpmnSource("a.b=c"));        
//
        out.write(pre_code());
        out.newLine();

//        DmnModelInstance dmnInstance = Dmn.readModelFromFile(new File("diagram_1.dmn"));
//        DMNTranslator<String> dt = new ToJavaDMNTranslator();
//        //((AbstractDMNTranslator) dt).dump(dmnInstance);
//        out.write(dt.translate(dmnInstance));
//
//        out.newLine();
//        out.newLine();
//
        BpmnModelInstance bpmnInstance = Bpmn.readModelFromFile(new File("diagram_2.bpmn"));
        AbstractBPMNTranslator bt = new ToJavaBPMNTranslator();
        //((AbstractBPMNTranslator) bt).dump(bpmnInstance);

        out.write(bt.generateBpmnSource(bt.decodeBpmn(bpmnInstance,opt),opt));

        out.newLine();
        out.write(post_code());
//        
        out.close();

//Bpmn.writeModelToFile(new File("test.bpmn"), modelInstance);
    }
}
