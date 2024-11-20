package dellapenna.personal.bpmn.unused;

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
class bpmn_process_shipment {

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
    public void EVENT_package_received(BPMNExecProcessUtils.ProcessStatus s) {//Start Event package received [StartEvent_1]
        BPMNExecProcessUtils.debugOutput("Start Event package received [StartEvent_1]");
        pType = input_PackageType;
        BPMNExecProcessUtils.debugOutput("\t ASSIGNING pType TO %s", input_PackageType);
//[outgoing edge] Activity_0h04jo2 - get length
        TASK_get_length(s);
    }

    public void EVENT_unsuppoted_weight(BPMNExecProcessUtils.ProcessStatus s) {//End Event unsuppoted weight [Event_0wjo1ye]
        BPMNExecProcessUtils.debugOutput("End Event unsuppoted weight [Event_0wjo1ye]");
        BPMNExecProcessUtils.error(s, "Unsupported Weight", 2);
    }

    public void EVENT_ready_for_shipment(BPMNExecProcessUtils.ProcessStatus s) {//End Event ready for shipment [Event_1pjc4df]
        BPMNExecProcessUtils.debugOutput("End Event ready for shipment [Event_1pjc4df]");
        BPMNExecProcessUtils.success(s);
    }

    public void EVENT_undefined_length(BPMNExecProcessUtils.ProcessStatus s) {//End Event undefined length [Event_06urgzi]
        BPMNExecProcessUtils.debugOutput("End Event undefined length [Event_06urgzi]");
        BPMNExecProcessUtils.error(s, "Undefined Length", 1);
    }

    public void EVENT_no_shipment(BPMNExecProcessUtils.ProcessStatus s) {//End Event no shipment [Event_19ylwnc]
        BPMNExecProcessUtils.debugOutput("End Event no shipment [Event_19ylwnc]");
        BPMNExecProcessUtils.error(s, "No Shipment", 3);
    }

    public void GATEWAY_Gateway_1tgxmu2(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Gateway_1tgxmu2
        BPMNExecProcessUtils.debugOutput("Exclusive Gateway Gateway_1tgxmu2");
        if (BPMNExecTypeUtils.tonumber(pWeight) > BPMNExecTypeUtils.tonumber(10.0)) {//[outgoing edge] Event_0wjo1ye - unsuppoted weight
            EVENT_unsuppoted_weight(s);
        } else {//[outgoing edge] Activity_1ol43bw - determine mode
            TASK_determine_mode(s);
        }
    }

    public void GATEWAY_Gateway_1ocbjca(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Gateway_1ocbjca
        BPMNExecProcessUtils.debugOutput("Exclusive Gateway Gateway_1ocbjca");
        if (sMode.equals("undef")) {//[outgoing edge] Event_19ylwnc - no shipment
            EVENT_no_shipment(s);
        } else {//[outgoing edge] Activity_1cbdv9z - choose consent
            TASK_choose_consent(s);
        }
    }

    public void GATEWAY_Gateway_07f90ke(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Joining Gateway Gateway_07f90ke
        BPMNExecProcessUtils.debugOutput("Exclusive Joining Gateway Gateway_07f90ke");
//[outgoing edge] Event_1pjc4df - ready for shipment
        EVENT_ready_for_shipment(s);
    }

    public void GATEWAY_Gateway_0i2yujj(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Gateway_0i2yujj
        BPMNExecProcessUtils.debugOutput("Exclusive Gateway Gateway_0i2yujj");
        if (pLength.equals(-(BPMNExecTypeUtils.tonumber(1.0)))) {//[outgoing edge] Event_06urgzi - undefined length
            EVENT_undefined_length(s);
        } else {//[outgoing edge] Activity_0iafefy - measure weight
            TASK_measure_weight(s);
        }
    }

    public void GATEWAY_Gateway_0u50uj6(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Gateway_0u50uj6
        BPMNExecProcessUtils.debugOutput("Exclusive Gateway Gateway_0u50uj6");
        if (consent.equals("com")) {//[outgoing edge] Activity_1njskid - sign declaration
            TASK_sign_declaration(s);
        } else if (consent.equals("owner")) {//[outgoing edge] Activity_1nfni4r - fetch declaration
            TASK_fetch_declaration(s);
        } else if (consent.equals("none")) {//[outgoing edge] Gateway_07f90ke
            GATEWAY_Gateway_07f90ke(s);
        } else {
            BPMNExecProcessUtils.noDefaultCaseError(s);
        }
    }

    public void TASK_determine_mode(BPMNExecProcessUtils.ProcessStatus s) {//Business Rule Task determine mode [Activity_1ol43bw]
        BPMNExecProcessUtils.debugOutput("Business Rule Task determine mode [Activity_1ol43bw]");
        BPMNExecProcessUtils.debugOutput("\t EXECUTING DECISION determine mode");
        dmn_dtable_DetermineModeDT_result determineModeResult = dmn_dtable_DetermineModeDT.execute(/*Length*/pLength, /*Weight*/ pWeight);
        BPMNExecProcessUtils.debugOutput("\t DECISION RESULT IS %s", determineModeResult);
        sMode = determineModeResult.Mode;
        BPMNExecProcessUtils.debugOutput("\t ASSIGNING sMode TO %s", determineModeResult.Mode);
//[outgoing edge] Gateway_1ocbjca
        GATEWAY_Gateway_1ocbjca(s);
    }

    public void TASK_get_length(BPMNExecProcessUtils.ProcessStatus s) {//Business Rule Task get length [Activity_0h04jo2]
        BPMNExecProcessUtils.debugOutput("Business Rule Task get length [Activity_0h04jo2]");
        BPMNExecProcessUtils.debugOutput("\t EXECUTING DECISION get length");
        dmn_dtable_GetLengthDT_result getLengthResult = dmn_dtable_GetLengthDT.execute(/*Type*/pType);
        BPMNExecProcessUtils.debugOutput("\t DECISION RESULT IS %s", getLengthResult);
        pLength = getLengthResult.Length;
        BPMNExecProcessUtils.debugOutput("\t ASSIGNING pLength TO %s", getLengthResult.Length);
//[outgoing edge] Gateway_0i2yujj
        GATEWAY_Gateway_0i2yujj(s);
    }

    public void TASK_measure_weight(BPMNExecProcessUtils.ProcessStatus s) {//User Task measure weight [Activity_0iafefy]
        BPMNExecProcessUtils.debugOutput("User Task measure weight [Activity_0iafefy]");
        pWeight = input_PackageWeight;
        BPMNExecProcessUtils.debugOutput("\t ASSIGNING pWeight TO %s", input_PackageWeight);
//[outgoing edge] Gateway_1tgxmu2
        GATEWAY_Gateway_1tgxmu2(s);
    }

    public void TASK_sign_declaration(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task sign declaration [Activity_1njskid]
        BPMNExecProcessUtils.debugOutput("Generic Task sign declaration [Activity_1njskid]");
//[outgoing edge] Gateway_07f90ke
        GATEWAY_Gateway_07f90ke(s);
    }

    public void TASK_fetch_declaration(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task fetch declaration [Activity_1nfni4r]
        BPMNExecProcessUtils.debugOutput("Generic Task fetch declaration [Activity_1nfni4r]");
//[outgoing edge] Gateway_07f90ke
        GATEWAY_Gateway_07f90ke(s);
    }

    public void TASK_choose_consent(BPMNExecProcessUtils.ProcessStatus s) {//Business Rule Task choose consent [Activity_1cbdv9z]
        BPMNExecProcessUtils.debugOutput("Business Rule Task choose consent [Activity_1cbdv9z]");
        BPMNExecProcessUtils.debugOutput("\t EXECUTING DECISION choose consent");
        dmn_dtable_ChooseConsentDT_result chooseConsentResult = dmn_dtable_ChooseConsentDT.execute(/*Mode*/sMode, /*Weight*/ pWeight);
        BPMNExecProcessUtils.debugOutput("\t DECISION RESULT IS %s", chooseConsentResult);
        consent = chooseConsentResult.Consent;
        BPMNExecProcessUtils.debugOutput("\t ASSIGNING consent TO %s", chooseConsentResult.Consent);
//[outgoing edge] Gateway_0u50uj6
        GATEWAY_Gateway_0u50uj6(s);
    }

    public void init() {
        this.input_PackageWeight = 100;	//TODO assign input variable
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
        bpmn_process_shipment process = new bpmn_process_shipment();
        BPMNExecProcessUtils.executeProcess(process::init, process::EVENT_package_received);
    }
}
