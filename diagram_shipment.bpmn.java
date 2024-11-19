
import dellapenna.personal.bpmn.exec.*;

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

        String Type = BPMNExecTypeUtils.tostring(_Type);

        if (BPMNExecTypeUtils.tostring(Type).equals("std")) {
            return new dmn_dtable_GetLengthDT_result(/*Length*/0.5);
        } else if (BPMNExecTypeUtils.tostring(Type).equals("large")) {
            return new dmn_dtable_GetLengthDT_result(/*Length*/1.0);
        } else if (BPMNExecTypeUtils.tostring(Type).equals("xl")) {
            return new dmn_dtable_GetLengthDT_result(/*Length*/2.0);
        } else {
            return new dmn_dtable_GetLengthDT_result(/*Length*/-(BPMNExecTypeUtils.tonumber(1.0)));
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

        Double Length = BPMNExecTypeUtils.tonumber(_Length);
        Double Weight = BPMNExecTypeUtils.tonumber(_Weight);

        if ((BPMNExecTypeUtils.tonumber(Length) > 0.0 && BPMNExecTypeUtils.tonumber(Length) <= 1.0) && (BPMNExecTypeUtils.tonumber(Weight) > 0.0 && BPMNExecTypeUtils.tonumber(Weight) <= 5.0)) {
            return new dmn_dtable_DetermineModeDT_result(/*Mode*/"car");
        } else if ((BPMNExecTypeUtils.tonumber(Length) > 1.0 && BPMNExecTypeUtils.tonumber(Length) <= 2.0) && (BPMNExecTypeUtils.tonumber(Weight) > 0.0 && BPMNExecTypeUtils.tonumber(Weight) <= 5.0)) {
            return new dmn_dtable_DetermineModeDT_result(/*Mode*/"truck");
        } else if ((BPMNExecTypeUtils.tonumber(Weight) > 5.0 && BPMNExecTypeUtils.tonumber(Weight) <= 10.0)) {
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

        String Mode = BPMNExecTypeUtils.tostring(_Mode);
        Double Weight = BPMNExecTypeUtils.tonumber(_Weight);

        if (BPMNExecTypeUtils.tostring(Mode).equals("car") && BPMNExecTypeUtils.tonumber(Weight) > BPMNExecTypeUtils.tonumber(6.0)) {
            return new dmn_dtable_ChooseConsentDT_result(/*Consent*/"owner");
        } else if (BPMNExecTypeUtils.tostring(Mode).equals("truck") && BPMNExecTypeUtils.tonumber(Weight) > BPMNExecTypeUtils.tonumber(8.0)) {
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
    public void GATEWAY_Gateway_1tgxmu2(BPMNExecProcessUtils.ProcessStatus s) {//exclusive gateway
        BPMNExecProcessUtils.debugOutput("EXCLUSIVE GATEWAY Gateway_1tgxmu2");
        if (BPMNExecTypeUtils.tonumber(pWeight) > BPMNExecTypeUtils.tonumber(10.0)) {
            EVENT_unsuppoted_weight(s);
        } else {
            TASK_determine_mode(s);
        }
    }

    public void GATEWAY_Gateway_1ocbjca(BPMNExecProcessUtils.ProcessStatus s) {//exclusive gateway
        BPMNExecProcessUtils.debugOutput("EXCLUSIVE GATEWAY Gateway_1ocbjca");
        if (sMode.equals("undef")) {
            EVENT_no_shipment(s);
        } else {
            TASK_choose_consent(s);
        }
    }

    public void GATEWAY_Gateway_07f90ke(BPMNExecProcessUtils.ProcessStatus s) {//exclusive joining gateway
        BPMNExecProcessUtils.debugOutput("EXCLUSIVE JOINING GATEWAY Gateway_07f90ke");
        EVENT_ready_for_shipment(s);
    }

    public void GATEWAY_Gateway_0i2yujj(BPMNExecProcessUtils.ProcessStatus s) {//exclusive gateway
        BPMNExecProcessUtils.debugOutput("EXCLUSIVE GATEWAY Gateway_0i2yujj");
        if (pLength.equals(-(BPMNExecTypeUtils.tonumber(1.0)))) {
            EVENT_undefined_length(s);
        } else {
            TASK_measure_weight(s);
        }
    }

    public void GATEWAY_Gateway_0u50uj6(BPMNExecProcessUtils.ProcessStatus s) {//exclusive gateway
        BPMNExecProcessUtils.debugOutput("EXCLUSIVE GATEWAY Gateway_0u50uj6");
        if (consent.equals("com")) {
            TASK_sign_declaration(s);
        } else if (consent.equals("owner")) {
            TASK_fetch_declaration(s);
        } else if (consent.equals("none")) {
            GATEWAY_Gateway_07f90ke(s);
        } else {
            BPMNExecProcessUtils.noDefaultCaseError(s);
        }
    }

    public void EVENT_package_received(BPMNExecProcessUtils.ProcessStatus s) {//start event: package received
        BPMNExecProcessUtils.debugOutput("START EVENT: package received");
        pType = input_PackageType;
        BPMNExecProcessUtils.debugOutput("ASSIGNING pType TO %s", input_PackageType);
        TASK_get_length(s);
    }

    public void EVENT_unsuppoted_weight(BPMNExecProcessUtils.ProcessStatus s) {//end event: unsuppoted weight
        BPMNExecProcessUtils.debugOutput("END EVENT unsuppoted weight");
        BPMNExecProcessUtils.error(s, "Unsupported Weight", 2);
    }

    public void EVENT_ready_for_shipment(BPMNExecProcessUtils.ProcessStatus s) {//end event: ready for shipment
        BPMNExecProcessUtils.debugOutput("END EVENT ready for shipment");
        BPMNExecProcessUtils.success(s);
    }

    public void EVENT_undefined_length(BPMNExecProcessUtils.ProcessStatus s) {//end event: undefined length
        BPMNExecProcessUtils.debugOutput("END EVENT undefined length");
        BPMNExecProcessUtils.error(s, "Undefined Length", 1);
    }

    public void EVENT_no_shipment(BPMNExecProcessUtils.ProcessStatus s) {//end event: no shipment
        BPMNExecProcessUtils.debugOutput("END EVENT no shipment");
        BPMNExecProcessUtils.error(s, "No Shipment", 3);
    }

    public void TASK_determine_mode(BPMNExecProcessUtils.ProcessStatus s) {//business rule task: determine mode
        BPMNExecProcessUtils.debugOutput("EXECUTING DECISION determine mode");
        dmn_dtable_DetermineModeDT_result determineModeResult = dmn_dtable_DetermineModeDT.execute(/*Length*/pLength, /*Weight*/ pWeight);
        BPMNExecProcessUtils.debugOutput("DECISION RESULT IS %s", determineModeResult);
        sMode = determineModeResult.Mode;
        BPMNExecProcessUtils.debugOutput("ASSIGNING sMode TO %s", determineModeResult.Mode);
        GATEWAY_Gateway_1ocbjca(s);
    }

    public void TASK_get_length(BPMNExecProcessUtils.ProcessStatus s) {//business rule task: get length
        BPMNExecProcessUtils.debugOutput("EXECUTING DECISION get length");
        dmn_dtable_GetLengthDT_result getLengthResult = dmn_dtable_GetLengthDT.execute(/*Type*/pType);
        BPMNExecProcessUtils.debugOutput("DECISION RESULT IS %s", getLengthResult);
        pLength = getLengthResult.Length;
        BPMNExecProcessUtils.debugOutput("ASSIGNING pLength TO %s", getLengthResult.Length);
        GATEWAY_Gateway_0i2yujj(s);
    }

    public void TASK_measure_weight(BPMNExecProcessUtils.ProcessStatus s) {//user task: measure weight
        BPMNExecProcessUtils.debugOutput("USER TASK measure weight");
        pWeight = input_PackageWeight;
        BPMNExecProcessUtils.debugOutput("ASSIGNING pWeight TO %s", input_PackageWeight);
        GATEWAY_Gateway_1tgxmu2(s);
    }

    public void TASK_sign_declaration(BPMNExecProcessUtils.ProcessStatus s) {//generic task: sign declaration
        BPMNExecProcessUtils.debugOutput("TASK sign declaration");
        GATEWAY_Gateway_07f90ke(s);
    }

    public void TASK_fetch_declaration(BPMNExecProcessUtils.ProcessStatus s) {//generic task: fetch declaration
        BPMNExecProcessUtils.debugOutput("TASK fetch declaration");
        GATEWAY_Gateway_07f90ke(s);
    }

    public void TASK_choose_consent(BPMNExecProcessUtils.ProcessStatus s) {//business rule task: choose consent
        BPMNExecProcessUtils.debugOutput("EXECUTING DECISION choose consent");
        dmn_dtable_ChooseConsentDT_result chooseConsentResult = dmn_dtable_ChooseConsentDT.execute(/*Mode*/sMode, /*Weight*/ pWeight);
        BPMNExecProcessUtils.debugOutput("DECISION RESULT IS %s", chooseConsentResult);
        consent = chooseConsentResult.Consent;
        BPMNExecProcessUtils.debugOutput("ASSIGNING consent TO %s", chooseConsentResult.Consent);
        GATEWAY_Gateway_0u50uj6(s);
    }

    public void init() {
        this.input_PackageWeight = null;	//TODO assign input variable
        this.input_PackageType = null;	//TODO assign input variable
        if (this.input_PackageWeight == null) {
            input_PackageWeight = BPMNExecProcessUtils.inputs.getProperty("input_PackageWeight", null);
        }
        if (this.input_PackageType == null) {
            input_PackageType = BPMNExecProcessUtils.inputs.getProperty("input_PackageType", null);
        }
        BPMNExecProcessUtils.logInput("input_PackageWeight", this.input_PackageWeight);
        BPMNExecProcessUtils.logInput("input_PackageType", this.input_PackageType);
    }

    public static void main(String[] args) {
        BPMNExecProcessUtils.enableTrueParallel();
        bpmn_process_Shipment process = new bpmn_process_Shipment();
        BPMNExecProcessUtils.ProcessStatus s = new BPMNExecProcessUtils.ProcessStatus();
        BPMNExecProcessUtils.initProcess(s, process::init);
        BPMNExecProcessUtils.startProcess(s, process::EVENT_package_received);
        BPMNExecProcessUtils.endProcess(s);
    }
}
