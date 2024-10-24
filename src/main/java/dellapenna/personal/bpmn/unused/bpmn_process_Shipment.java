package dellapenna.personal.bpmn.unused;

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
        ProcessUtils.logResult(false, s, c);
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
        ProcessUtils.logResult(true, s, c);
        ProcessUtils.end();
    }

    public static void success() {
        success(null, 0);
    }

    public static void debugOutput(String s, Object... args) {
        String message = String.format(s, args);
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

/*
 * ****************************** DMN Generated Code *************************
 */
// wrapper class for the output of DMN table GetLengthDT
class dmn_dtable_GetLengthDT_result {

    Double Length;

    public dmn_dtable_GetLengthDT_result(Double Length) {
        this.Length = Length;
    }

    public String toString() {
        String result = "{";
        result += "Length=" + this.Length;
        return result + "}";
    }
}

// decision code for DMN table GetLengthDT
class dmn_dtable_GetLengthDT {

    public static dmn_dtable_GetLengthDT_result execute(Object _Type) {

        String Type = TypeUtils.tostring(_Type);

        if (TypeUtils.tostring(Type).equals("std")) {
            return new dmn_dtable_GetLengthDT_result(/*Length*/0.5);
        } else if (TypeUtils.tostring(Type).equals("large")) {
            return new dmn_dtable_GetLengthDT_result(/*Length*/1.0);
        } else if (TypeUtils.tostring(Type).equals("xl")) {
            return new dmn_dtable_GetLengthDT_result(/*Length*/2.0);
        } else {
            return new dmn_dtable_GetLengthDT_result(/*Length*/-(TypeUtils.tonumber(1.0)));
        }
    }
}/*
 * ****************************** DMN Generated Code *************************
 */
// wrapper class for the output of DMN table DetermineModeDT
class dmn_dtable_DetermineModeDT_result {

    String Mode;

    public dmn_dtable_DetermineModeDT_result(String Mode) {
        this.Mode = Mode;
    }

    public String toString() {
        String result = "{";
        result += "Mode=" + this.Mode;
        return result + "}";
    }
}

// decision code for DMN table DetermineModeDT
class dmn_dtable_DetermineModeDT {

    public static dmn_dtable_DetermineModeDT_result execute(Object _Length, Object _Weight) {

        Double Length = TypeUtils.tonumber(_Length);
        Double Weight = TypeUtils.tonumber(_Weight);

        if ((TypeUtils.tonumber(Length) > 0.0 && TypeUtils.tonumber(Length) <= 1.0) && (TypeUtils.tonumber(Weight) > 0.0 && TypeUtils.tonumber(Weight) <= 5.0)) {
            return new dmn_dtable_DetermineModeDT_result(/*Mode*/"car");
        } else if ((TypeUtils.tonumber(Length) > 1.0 && TypeUtils.tonumber(Length) <= 2.0) && (TypeUtils.tonumber(Weight) > 0.0 && TypeUtils.tonumber(Weight) <= 5.0)) {
            return new dmn_dtable_DetermineModeDT_result(/*Mode*/"truck");
        } else if ((TypeUtils.tonumber(Weight) > 5.0 && TypeUtils.tonumber(Weight) <= 10.0)) {
            return new dmn_dtable_DetermineModeDT_result(/*Mode*/"truck");
        } else {
            return new dmn_dtable_DetermineModeDT_result(/*Mode*/"undef");
        }
    }
}/*
 * ****************************** DMN Generated Code *************************
 */
// wrapper class for the output of DMN table ChooseConsentDT
class dmn_dtable_ChooseConsentDT_result {

    String Consent;

    public dmn_dtable_ChooseConsentDT_result(String Consent) {
        this.Consent = Consent;
    }

    public String toString() {
        String result = "{";
        result += "Consent=" + this.Consent;
        return result + "}";
    }
}

// decision code for DMN table ChooseConsentDT
class dmn_dtable_ChooseConsentDT {

    public static dmn_dtable_ChooseConsentDT_result execute(Object _Mode, Object _Weight) {

        String Mode = TypeUtils.tostring(_Mode);
        Double Weight = TypeUtils.tonumber(_Weight);

        if (TypeUtils.tostring(Mode).equals("car") && TypeUtils.tonumber(Weight) > TypeUtils.tonumber(6.0)) {
            return new dmn_dtable_ChooseConsentDT_result(/*Consent*/"owner");
        } else if (TypeUtils.tostring(Mode).equals("truck") && TypeUtils.tonumber(Weight) > TypeUtils.tonumber(8.0)) {
            return new dmn_dtable_ChooseConsentDT_result(/*Consent*/"com");
        } else {
            return new dmn_dtable_ChooseConsentDT_result(/*Consent*/"none");
        }
    }
}

/*
 * ****************************** BPMN Generated Code *************************
 */
class bpmn_process_Shipment {

//Input Variables
    Object input_PackageWeight;
    Object input_PackageType;

//Process Variables
    Object pType;
    Object pWeight;
    Object pLength;
    Object consent;
    Object sMode;

//Process Dynamics
    public void flow_undefined_length() {//end event: undefined length
        ProcessUtils.error("Undefined Length", 1);
    }

    public void flow_unsuppoted_weight() {//end event: unsuppoted weight
        ProcessUtils.error("Unsupported Weight", 2);
    }

    public void flow_ready_for_shipment() {//end event: ready for shipment
        ProcessUtils.success();
    }

    public void flow_Gateway_07f90ke() {//exclusive joining gateway
        ProcessUtils.debugOutput("JOINING FLOW flow_ready_for_shipment");
        flow_ready_for_shipment();
    }

    public void flow_sign_declaration() {
        task_generic_sign_declaration();
        ProcessUtils.debugOutput("JOINING FLOW flow_Gateway_07f90ke");
        flow_Gateway_07f90ke();
    }

    public void flow_fetch_declaration() {
        task_generic_fetch_declaration();
        ProcessUtils.debugOutput("JOINING FLOW flow_Gateway_07f90ke");
        flow_Gateway_07f90ke();
    }

    public void flow_package_received() {//start event: package received
        ProcessUtils.debugOutput("START EVENT: package received");
        pType = input_PackageType;
        ProcessUtils.debugOutput("ASSIGNING pType TO %s", input_PackageType);
//business rule task: get length
        ProcessUtils.debugOutput("EXECUTING DECISION get length");
        dmn_dtable_GetLengthDT_result getLengthResult = dmn_dtable_GetLengthDT.execute(/*Type*/pType);
        ProcessUtils.debugOutput("DECISION RESULT IS %s", getLengthResult);
        pLength = getLengthResult.Length;
        ProcessUtils.debugOutput("ASSIGNING pLength TO %s", getLengthResult.Length);
//exclusive gateway
        ProcessUtils.debugOutput("EXCLUSIVE GATEWAY Gateway_0i2yujj");
        if (pLength.equals(-(TypeUtils.tonumber(1.0)))) {
            ProcessUtils.debugOutput("JOINING FLOW flow_undefined_length");
            flow_undefined_length();
        } else {
            ProcessUtils.debugOutput("JOINING FLOW flow_measure_weight");
            flow_measure_weight();
        }
    }

    public void flow_no_shipment() {//end event: no shipment
        ProcessUtils.error("No Shipment", 3);
    }

    public void flow_choose_consent() {//business rule task: choose consent
        ProcessUtils.debugOutput("EXECUTING DECISION choose consent");
        dmn_dtable_ChooseConsentDT_result chooseConsentResult = dmn_dtable_ChooseConsentDT.execute(/*Mode*/sMode, /*Weight*/ pWeight);
        ProcessUtils.debugOutput("DECISION RESULT IS %s", chooseConsentResult);
        consent = chooseConsentResult.Consent;
        ProcessUtils.debugOutput("ASSIGNING consent TO %s", chooseConsentResult.Consent);
//exclusive gateway
        ProcessUtils.debugOutput("EXCLUSIVE GATEWAY Gateway_0u50uj6");
        if (consent.equals("com")) {
            ProcessUtils.debugOutput("JOINING FLOW flow_sign_declaration");
            flow_sign_declaration();
        } else if (consent.equals("owner")) {
            ProcessUtils.debugOutput("JOINING FLOW flow_fetch_declaration");
            flow_fetch_declaration();
        } else if (consent.equals("none")) {
            ProcessUtils.debugOutput("JOINING FLOW flow_Gateway_07f90ke");
            flow_Gateway_07f90ke();
        } else {
            ProcessUtils.debugOutput("JOINING FLOW ProcessUtils.noDefaultCaseError");
            ProcessUtils.noDefaultCaseError();
        }
    }

    public void flow_measure_weight() {
        task_user_measure_weight();
//exclusive gateway
        ProcessUtils.debugOutput("EXCLUSIVE GATEWAY Gateway_1tgxmu2");
        if (TypeUtils.tonumber(pWeight) > TypeUtils.tonumber(10.0)) {
            ProcessUtils.debugOutput("JOINING FLOW flow_unsuppoted_weight");
            flow_unsuppoted_weight();
        } else {
            ProcessUtils.debugOutput("JOINING FLOW flow_determine_mode");
            flow_determine_mode();
        }
    }

    public void flow_determine_mode() {//business rule task: determine mode
        ProcessUtils.debugOutput("EXECUTING DECISION determine mode");
        dmn_dtable_DetermineModeDT_result determineModeResult = dmn_dtable_DetermineModeDT.execute(/*Length*/pLength, /*Weight*/ pWeight);
        ProcessUtils.debugOutput("DECISION RESULT IS %s", determineModeResult);
        sMode = determineModeResult.Mode;
        ProcessUtils.debugOutput("ASSIGNING sMode TO %s", determineModeResult.Mode);
//exclusive gateway
        ProcessUtils.debugOutput("EXCLUSIVE GATEWAY Gateway_1ocbjca");
        if (sMode.equals("undef")) {
            ProcessUtils.debugOutput("JOINING FLOW flow_no_shipment");
            flow_no_shipment();
        } else {
            ProcessUtils.debugOutput("JOINING FLOW flow_choose_consent");
            flow_choose_consent();
        }
    }

    public void task_user_measure_weight() {//user task: measure weight
        ProcessUtils.debugOutput("USER TASK measure weight");
        pWeight = input_PackageWeight;
        ProcessUtils.debugOutput("ASSIGNING pWeight TO %s", input_PackageWeight);
    }

    public void task_generic_fetch_declaration() {//generic task: fetch declaration
        ProcessUtils.debugOutput("TASK fetch declaration");
    }

    public void task_generic_sign_declaration() {//generic task: sign declaration
        ProcessUtils.debugOutput("TASK sign declaration");
    }

    public void init() {
        this.input_PackageWeight = null;	//TODO assign input variable
        this.input_PackageType = null;	//TODO assign input variable
        if (this.input_PackageWeight == null) {
            input_PackageWeight = ProcessUtils.inputs.getProperty("input_PackageWeight", null);
        }
        if (this.input_PackageType == null) {
            input_PackageType = ProcessUtils.inputs.getProperty("input_PackageType", null);
        }
        ProcessUtils.logInput("input_PackageWeight", this.input_PackageWeight);
        ProcessUtils.logInput("input_PackageType", this.input_PackageType);
    }

    public static void main(String[] args) {
        ProcessUtils.start();
        bpmn_process_Shipment process = new bpmn_process_Shipment();
        process.init();
        process.flow_package_received();
    }
}

