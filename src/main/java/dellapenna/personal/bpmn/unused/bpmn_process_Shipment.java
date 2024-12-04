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

    public static dmn_dtable_GetLengthDT_result execute(Object Type) {

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

    public static dmn_dtable_DetermineModeDT_result execute(Object Length, Object Weight) {

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

    public static dmn_dtable_ChooseConsentDT_result execute(Object Mode, Object Weight) {

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
    Object input_PackageType;
    Object input_PackageWeight;

//Process Variables
    Object pType;
    Object pWeight;
    Object pLength;
    Object consent;
    Object sMode;

//Process Dynamics
    public void TASK_determine_mode(BPMNExecProcessUtils.ProcessStatus s) {//Business Rule Task determine mode [Activity_1ol43bw]
        BPMNExecProcessUtils.debugOutput("Business Rule Task determine mode [Activity_1ol43bw]");
        BPMNExecProcessUtils.debugOutput("	 EXECUTING DECISION determine mode");
        dmn_dtable_DetermineModeDT_result determineModeResult = dmn_dtable_DetermineModeDT.execute(/*Length*/pLength, /*Weight*/ pWeight);
        BPMNExecProcessUtils.debugOutput("	 DECISION RESULT IS %s", determineModeResult);
        sMode = determineModeResult.Mode;
        BPMNExecProcessUtils.debugOutput("	 ASSIGNING sMode TO %s", determineModeResult.Mode);
//[outgoing edge] Gateway_1ocbjca
        BPMNExecProcessUtils.logTransition("Activity_1ol43bw", "Gateway_1ocbjca");
        GATEWAY_Gateway_1ocbjca(s.withCurrent("Activity_1ol43bw"));
    }

    public void TASK_get_length(BPMNExecProcessUtils.ProcessStatus s) {//Business Rule Task get length [Activity_0h04jo2]
        BPMNExecProcessUtils.debugOutput("Business Rule Task get length [Activity_0h04jo2]");
        BPMNExecProcessUtils.debugOutput("	 EXECUTING DECISION get length");
        dmn_dtable_GetLengthDT_result getLengthResult = dmn_dtable_GetLengthDT.execute(/*Type*/pType);
        BPMNExecProcessUtils.debugOutput("	 DECISION RESULT IS %s", getLengthResult);
        pLength = getLengthResult.Length;
        BPMNExecProcessUtils.debugOutput("	 ASSIGNING pLength TO %s", getLengthResult.Length);
//[outgoing edge] Gateway_0i2yujj
        BPMNExecProcessUtils.logTransition("Activity_0h04jo2", "Gateway_0i2yujj");
        GATEWAY_Gateway_0i2yujj(s.withCurrent("Activity_0h04jo2"));
    }

    public void TASK_measure_weight(BPMNExecProcessUtils.ProcessStatus s) {//User Task measure weight [Activity_0iafefy]
        BPMNExecProcessUtils.debugOutput("User Task measure weight [Activity_0iafefy]");
        pWeight = input_PackageWeight;
        BPMNExecProcessUtils.debugOutput("	 ASSIGNING pWeight TO %s", input_PackageWeight);
//[outgoing edge] Gateway_1tgxmu2
        BPMNExecProcessUtils.logTransition("Activity_0iafefy", "Gateway_1tgxmu2");
        GATEWAY_Gateway_1tgxmu2(s.withCurrent("Activity_0iafefy"));
    }

    public void TASK_sign_declaration(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task sign declaration [Activity_1njskid]
        BPMNExecProcessUtils.debugOutput("Generic Task sign declaration [Activity_1njskid]");
//[outgoing edge] Gateway_07f90ke
        BPMNExecProcessUtils.logTransition("Activity_1njskid", "Gateway_07f90ke");
        GATEWAY_Gateway_07f90ke(s.withCurrent("Activity_1njskid"));
    }

    public void TASK_fetch_declaration(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task fetch declaration [Activity_1nfni4r]
        BPMNExecProcessUtils.debugOutput("Generic Task fetch declaration [Activity_1nfni4r]");
//[outgoing edge] Gateway_07f90ke
        BPMNExecProcessUtils.logTransition("Activity_1nfni4r", "Gateway_07f90ke");
        GATEWAY_Gateway_07f90ke(s.withCurrent("Activity_1nfni4r"));
    }

    public void TASK_choose_consent(BPMNExecProcessUtils.ProcessStatus s) {//Business Rule Task choose consent [Activity_1cbdv9z]
        BPMNExecProcessUtils.debugOutput("Business Rule Task choose consent [Activity_1cbdv9z]");
        BPMNExecProcessUtils.debugOutput("	 EXECUTING DECISION choose consent");
        dmn_dtable_ChooseConsentDT_result chooseConsentResult = dmn_dtable_ChooseConsentDT.execute(/*Mode*/sMode, /*Weight*/ pWeight);
        BPMNExecProcessUtils.debugOutput("	 DECISION RESULT IS %s", chooseConsentResult);
        consent = chooseConsentResult.Consent;
        BPMNExecProcessUtils.debugOutput("	 ASSIGNING consent TO %s", chooseConsentResult.Consent);
//[outgoing edge] Gateway_0u50uj6
        BPMNExecProcessUtils.logTransition("Activity_1cbdv9z", "Gateway_0u50uj6");
        GATEWAY_Gateway_0u50uj6(s.withCurrent("Activity_1cbdv9z"));
    }

    public void EVENT_package_received(BPMNExecProcessUtils.ProcessStatus s) {//Start Event package received [StartEvent_1]
        BPMNExecProcessUtils.debugOutput("Start Event package received [StartEvent_1]");
        pType = input_PackageType;
        BPMNExecProcessUtils.debugOutput("	 ASSIGNING pType TO %s", input_PackageType);
//[outgoing edge] Activity_0h04jo2 - get length
        BPMNExecProcessUtils.logTransition("StartEvent_1", "Activity_0h04jo2");
        TASK_get_length(s.withCurrent("StartEvent_1"));
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
            BPMNExecProcessUtils.logTransition("Gateway_1tgxmu2", "Event_0wjo1ye");
            EVENT_unsuppoted_weight(s.withCurrent("Gateway_1tgxmu2"));
        } else {//[outgoing edge] Activity_1ol43bw - determine mode
            BPMNExecProcessUtils.logTransition("Gateway_1tgxmu2", "Activity_1ol43bw");
            TASK_determine_mode(s.withCurrent("Gateway_1tgxmu2"));
        }
    }

    public void GATEWAY_Gateway_1ocbjca(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Gateway_1ocbjca
        BPMNExecProcessUtils.debugOutput("Exclusive Gateway Gateway_1ocbjca");
        if (sMode.equals("undef")) {//[outgoing edge] Event_19ylwnc - no shipment
            BPMNExecProcessUtils.logTransition("Gateway_1ocbjca", "Event_19ylwnc");
            EVENT_no_shipment(s.withCurrent("Gateway_1ocbjca"));
        } else {//[outgoing edge] Activity_1cbdv9z - choose consent
            BPMNExecProcessUtils.logTransition("Gateway_1ocbjca", "Activity_1cbdv9z");
            TASK_choose_consent(s.withCurrent("Gateway_1ocbjca"));
        }
    }

    public void GATEWAY_Gateway_07f90ke(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Joining Gateway Gateway_07f90ke
        BPMNExecProcessUtils.debugOutput("Exclusive Joining Gateway Gateway_07f90ke");
//[outgoing edge] Event_1pjc4df - ready for shipment
        BPMNExecProcessUtils.logTransition("Gateway_07f90ke", "Event_1pjc4df");
        EVENT_ready_for_shipment(s.withCurrent("Gateway_07f90ke"));
    }

    public void GATEWAY_Gateway_0i2yujj(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Gateway_0i2yujj
        BPMNExecProcessUtils.debugOutput("Exclusive Gateway Gateway_0i2yujj");
        if (pLength.equals(-(BPMNExecTypeUtils.tonumber(1.0)))) {//[outgoing edge] Event_06urgzi - undefined length
            BPMNExecProcessUtils.logTransition("Gateway_0i2yujj", "Event_06urgzi");
            EVENT_undefined_length(s.withCurrent("Gateway_0i2yujj"));
        } else {//[outgoing edge] Activity_0iafefy - measure weight
            BPMNExecProcessUtils.logTransition("Gateway_0i2yujj", "Activity_0iafefy");
            TASK_measure_weight(s.withCurrent("Gateway_0i2yujj"));
        }
    }

    public void GATEWAY_Gateway_0u50uj6(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Gateway_0u50uj6
        BPMNExecProcessUtils.debugOutput("Exclusive Gateway Gateway_0u50uj6");
        if (consent.equals("com")) {//[outgoing edge] Activity_1njskid - sign declaration
            BPMNExecProcessUtils.logTransition("Gateway_0u50uj6", "Activity_1njskid");
            TASK_sign_declaration(s.withCurrent("Gateway_0u50uj6"));
        } else if (consent.equals("owner")) {//[outgoing edge] Activity_1nfni4r - fetch declaration
            BPMNExecProcessUtils.logTransition("Gateway_0u50uj6", "Activity_1nfni4r");
            TASK_fetch_declaration(s.withCurrent("Gateway_0u50uj6"));
        } else if (consent.equals("none")) {//[outgoing edge] Gateway_07f90ke
            BPMNExecProcessUtils.logTransition("Gateway_0u50uj6", "Gateway_07f90ke");
            GATEWAY_Gateway_07f90ke(s.withCurrent("Gateway_0u50uj6"));
        } else {
            BPMNExecProcessUtils.noDefaultCaseError(s);
        }
    }

    public void init() {
        this.input_PackageType = null;	//TODO assign input variable
        this.input_PackageWeight = null;	//TODO assign input variable
        if (this.input_PackageType == null) {
            input_PackageType = BPMNExecProcessUtils.inputs.getProperty("input_PackageType", null);
        }
        if (this.input_PackageWeight == null) {
            input_PackageWeight = BPMNExecProcessUtils.inputs.getProperty("input_PackageWeight", null);
        }
        BPMNExecProcessUtils.logInput("input_PackageType", this.input_PackageType);
        BPMNExecProcessUtils.logInput("input_PackageWeight", this.input_PackageWeight);
//parallel join initializers

    }

    public static void main(String[] args) {
        BPMNExecProcessUtils.enableTrueParallel();
        bpmn_process_Shipment process = new bpmn_process_Shipment();
        BPMNExecProcessUtils.executeProcess(process::init, process::EVENT_package_received);
    }
}