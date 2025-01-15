
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

// wrapper class for the input of DMN table GetLengthDT
class dmn_dtable_GetLengthDT_arguments {

    public Object Type;
}

// decision code for DMN table GetLengthDT
class dmn_dtable_GetLengthDT {

    public static dmn_dtable_GetLengthDT_result execute(dmn_dtable_GetLengthDT_arguments args) {

        Object Type = args.Type;

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

// wrapper class for the input of DMN table DetermineModeDT
class dmn_dtable_DetermineModeDT_arguments {

    public Object Length;
    public Object Weight;
}

// decision code for DMN table DetermineModeDT
class dmn_dtable_DetermineModeDT {

    public static dmn_dtable_DetermineModeDT_result execute(dmn_dtable_DetermineModeDT_arguments args) {

        Object Length = args.Length;
        Object Weight = args.Weight;

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

// wrapper class for the input of DMN table ChooseConsentDT
class dmn_dtable_ChooseConsentDT_arguments {

    public Object Mode;
    public Object Weight;
}

// decision code for DMN table ChooseConsentDT
class dmn_dtable_ChooseConsentDT {

    public static dmn_dtable_ChooseConsentDT_result execute(dmn_dtable_ChooseConsentDT_arguments args) {

        Object Mode = args.Mode;
        Object Weight = args.Weight;

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
// READ: $DMN$GetLengthDT$Type, Activity_0h04jo2
    Object pType = null;
// READ: $DMN$DetermineModeDT$Weight, Gateway_1tgxmu2, Activity_1cbdv9z, $DMN$ChooseConsentDT$Weight, Activity_1ol43bw
    Object pWeight = null;

//Process Variables
// READ: Gateway_0u50uj6
// WRITTEN: Activity_1cbdv9z
    Object consent = null;
// READ: $DMN$DetermineModeDT$Length, Activity_1ol43bw, Gateway_0i2yujj
// WRITTEN: Activity_0h04jo2
    Object pLength = null;
// READ: $DMN$ChooseConsentDT$Mode, Gateway_1ocbjca, Activity_1cbdv9z
// WRITTEN: Activity_1ol43bw
    Object sMode = null;

//Process Dynamics
    public void EVENT_Event_06urgzi_undefined_length(BPMNExecProcessUtils.ProcessStatus s) {//End Event undefined length [Event_06urgzi]
        BPMNExecProcessUtils.debugOutput("End Event undefined length [Event_06urgzi]");
        BPMNExecProcessUtils.logCurrentNode("Event_06urgzi", "undefined length");
        BPMNExecProcessUtils.error(s, "Undefined Length", 1);
    }

    public void EVENT_Event_0wjo1ye_unsuppoted_weight(BPMNExecProcessUtils.ProcessStatus s) {//End Event unsuppoted weight [Event_0wjo1ye]
        BPMNExecProcessUtils.debugOutput("End Event unsuppoted weight [Event_0wjo1ye]");
        BPMNExecProcessUtils.logCurrentNode("Event_0wjo1ye", "unsuppoted weight");
        BPMNExecProcessUtils.error(s, "Unsupported Weight", 2);
    }

    public void EVENT_Event_19ylwnc_no_shipment(BPMNExecProcessUtils.ProcessStatus s) {//End Event no shipment [Event_19ylwnc]
        BPMNExecProcessUtils.debugOutput("End Event no shipment [Event_19ylwnc]");
        BPMNExecProcessUtils.logCurrentNode("Event_19ylwnc", "no shipment");
        BPMNExecProcessUtils.error(s, "No Shipment", 3);
    }

    public void EVENT_Event_1pjc4df_ready_for_shipment(BPMNExecProcessUtils.ProcessStatus s) {//End Event ready for shipment [Event_1pjc4df]
        BPMNExecProcessUtils.debugOutput("End Event ready for shipment [Event_1pjc4df]");
        BPMNExecProcessUtils.logCurrentNode("Event_1pjc4df", "ready for shipment");
        BPMNExecProcessUtils.success(s);
    }

    public void EVENT_StartEvent_1_package_received(BPMNExecProcessUtils.ProcessStatus s) {//Start Event package received [StartEvent_1]
        BPMNExecProcessUtils.debugOutput("Start Event package received [StartEvent_1]");
        BPMNExecProcessUtils.logCurrentNode("StartEvent_1", "package received");
//[outgoing edge] Activity_0h04jo2 - get length
        BPMNExecProcessUtils.logTransition("StartEvent_1", "Activity_0h04jo2");
        TASK_Activity_0h04jo2_get_length(s.withCurrent("StartEvent_1"));
    }

    public void GATEWAY_Gateway_07f90ke(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Joining Gateway Gateway_07f90ke
        BPMNExecProcessUtils.debugOutput("Exclusive Joining Gateway Gateway_07f90ke");
        BPMNExecProcessUtils.logCurrentNode("Gateway_07f90ke", null);
//[outgoing edge] Event_1pjc4df - ready for shipment
        BPMNExecProcessUtils.logTransition("Gateway_07f90ke", "Event_1pjc4df");
        EVENT_Event_1pjc4df_ready_for_shipment(s.withCurrent("Gateway_07f90ke"));
    }

    public void GATEWAY_Gateway_0i2yujj(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Gateway_0i2yujj
        BPMNExecProcessUtils.debugOutput("Exclusive Gateway Gateway_0i2yujj");
        BPMNExecProcessUtils.logCurrentNode("Gateway_0i2yujj", null);
        if (pLength.equals(-(BPMNExecTypeUtils.tonumber(1.0)))) {//[outgoing edge] Event_06urgzi - undefined length
            BPMNExecProcessUtils.logTransition("Gateway_0i2yujj", "Event_06urgzi");
            EVENT_Event_06urgzi_undefined_length(s.withCurrent("Gateway_0i2yujj"));
        } else {//[outgoing edge] Activity_0iafefy - measure weight
            BPMNExecProcessUtils.logTransition("Gateway_0i2yujj", "Activity_0iafefy");
            TASK_Activity_0iafefy_measure_weight(s.withCurrent("Gateway_0i2yujj"));
        }
    }

    public void GATEWAY_Gateway_0u50uj6(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Gateway_0u50uj6
        BPMNExecProcessUtils.debugOutput("Exclusive Gateway Gateway_0u50uj6");
        BPMNExecProcessUtils.logCurrentNode("Gateway_0u50uj6", null);
        if (consent.equals("com")) {//[outgoing edge] Activity_1njskid - sign declaration
            BPMNExecProcessUtils.logTransition("Gateway_0u50uj6", "Activity_1njskid");
            TASK_Activity_1njskid_sign_declaration(s.withCurrent("Gateway_0u50uj6"));
        } else if (consent.equals("owner")) {//[outgoing edge] Activity_1nfni4r - fetch declaration
            BPMNExecProcessUtils.logTransition("Gateway_0u50uj6", "Activity_1nfni4r");
            TASK_Activity_1nfni4r_fetch_declaration(s.withCurrent("Gateway_0u50uj6"));
        } else if (consent.equals("none")) {//[outgoing edge] Gateway_07f90ke
            BPMNExecProcessUtils.logTransition("Gateway_0u50uj6", "Gateway_07f90ke");
            GATEWAY_Gateway_07f90ke(s.withCurrent("Gateway_0u50uj6"));
        } else {
            BPMNExecProcessUtils.noDefaultCaseError(s);
        }
    }

    public void GATEWAY_Gateway_1ocbjca(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Gateway_1ocbjca
        BPMNExecProcessUtils.debugOutput("Exclusive Gateway Gateway_1ocbjca");
        BPMNExecProcessUtils.logCurrentNode("Gateway_1ocbjca", null);
        if (sMode.equals("undef")) {//[outgoing edge] Event_19ylwnc - no shipment
            BPMNExecProcessUtils.logTransition("Gateway_1ocbjca", "Event_19ylwnc");
            EVENT_Event_19ylwnc_no_shipment(s.withCurrent("Gateway_1ocbjca"));
        } else {//[outgoing edge] Activity_1cbdv9z - choose consent
            BPMNExecProcessUtils.logTransition("Gateway_1ocbjca", "Activity_1cbdv9z");
            TASK_Activity_1cbdv9z_choose_consent(s.withCurrent("Gateway_1ocbjca"));
        }
    }

    public void GATEWAY_Gateway_1tgxmu2(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Gateway_1tgxmu2
        BPMNExecProcessUtils.debugOutput("Exclusive Gateway Gateway_1tgxmu2");
        BPMNExecProcessUtils.logCurrentNode("Gateway_1tgxmu2", null);
        if (BPMNExecTypeUtils.tonumber(pWeight) > BPMNExecTypeUtils.tonumber(10.0)) {//[outgoing edge] Event_0wjo1ye - unsuppoted weight
            BPMNExecProcessUtils.logTransition("Gateway_1tgxmu2", "Event_0wjo1ye");
            EVENT_Event_0wjo1ye_unsuppoted_weight(s.withCurrent("Gateway_1tgxmu2"));
        } else {//[outgoing edge] Activity_1ol43bw - determine mode
            BPMNExecProcessUtils.logTransition("Gateway_1tgxmu2", "Activity_1ol43bw");
            TASK_Activity_1ol43bw_determine_mode(s.withCurrent("Gateway_1tgxmu2"));
        }
    }

    public void TASK_Activity_0h04jo2_get_length(BPMNExecProcessUtils.ProcessStatus s) {//Business Rule Task get length [Activity_0h04jo2]
        BPMNExecProcessUtils.debugOutput("Business Rule Task get length [Activity_0h04jo2]");
        BPMNExecProcessUtils.logCurrentNode("Activity_0h04jo2", "get length");
        BPMNExecProcessUtils.debugOutput("	 EXECUTING DECISION get length");
        dmn_dtable_GetLengthDT_arguments args = new dmn_dtable_GetLengthDT_arguments();
        args.Type = pType;
        dmn_dtable_GetLengthDT_result getLengthResult = dmn_dtable_GetLengthDT.execute(args);
        BPMNExecProcessUtils.debugOutput("	 DECISION RESULT IS %s", getLengthResult);
        pLength = getLengthResult.Length;
        BPMNExecProcessUtils.debugOutput("	 ASSIGNING pLength TO %s", getLengthResult.Length);
//[outgoing edge] Gateway_0i2yujj
        BPMNExecProcessUtils.logTransition("Activity_0h04jo2", "Gateway_0i2yujj");
        GATEWAY_Gateway_0i2yujj(s.withCurrent("Activity_0h04jo2"));
    }

    public void TASK_Activity_0iafefy_measure_weight(BPMNExecProcessUtils.ProcessStatus s) {//User Task measure weight [Activity_0iafefy]
        BPMNExecProcessUtils.debugOutput("User Task measure weight [Activity_0iafefy]");
        BPMNExecProcessUtils.logCurrentNode("Activity_0iafefy", "measure weight");
//[outgoing edge] Gateway_1tgxmu2
        BPMNExecProcessUtils.logTransition("Activity_0iafefy", "Gateway_1tgxmu2");
        GATEWAY_Gateway_1tgxmu2(s.withCurrent("Activity_0iafefy"));
    }

    public void TASK_Activity_1cbdv9z_choose_consent(BPMNExecProcessUtils.ProcessStatus s) {//Business Rule Task choose consent [Activity_1cbdv9z]
        BPMNExecProcessUtils.debugOutput("Business Rule Task choose consent [Activity_1cbdv9z]");
        BPMNExecProcessUtils.logCurrentNode("Activity_1cbdv9z", "choose consent");
        BPMNExecProcessUtils.debugOutput("	 EXECUTING DECISION choose consent");
        dmn_dtable_ChooseConsentDT_arguments args = new dmn_dtable_ChooseConsentDT_arguments();
        args.Mode = sMode;
        args.Weight = pWeight;
        dmn_dtable_ChooseConsentDT_result chooseConsentResult = dmn_dtable_ChooseConsentDT.execute(args);
        BPMNExecProcessUtils.debugOutput("	 DECISION RESULT IS %s", chooseConsentResult);
        consent = chooseConsentResult.Consent;
        BPMNExecProcessUtils.debugOutput("	 ASSIGNING consent TO %s", chooseConsentResult.Consent);
//[outgoing edge] Gateway_0u50uj6
        BPMNExecProcessUtils.logTransition("Activity_1cbdv9z", "Gateway_0u50uj6");
        GATEWAY_Gateway_0u50uj6(s.withCurrent("Activity_1cbdv9z"));
    }

    public void TASK_Activity_1nfni4r_fetch_declaration(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task fetch declaration [Activity_1nfni4r]
        BPMNExecProcessUtils.debugOutput("Generic Task fetch declaration [Activity_1nfni4r]");
        BPMNExecProcessUtils.logCurrentNode("Activity_1nfni4r", "fetch declaration");
//[outgoing edge] Gateway_07f90ke
        BPMNExecProcessUtils.logTransition("Activity_1nfni4r", "Gateway_07f90ke");
        GATEWAY_Gateway_07f90ke(s.withCurrent("Activity_1nfni4r"));
    }

    public void TASK_Activity_1njskid_sign_declaration(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task sign declaration [Activity_1njskid]
        BPMNExecProcessUtils.debugOutput("Generic Task sign declaration [Activity_1njskid]");
        BPMNExecProcessUtils.logCurrentNode("Activity_1njskid", "sign declaration");
//[outgoing edge] Gateway_07f90ke
        BPMNExecProcessUtils.logTransition("Activity_1njskid", "Gateway_07f90ke");
        GATEWAY_Gateway_07f90ke(s.withCurrent("Activity_1njskid"));
    }

    public void TASK_Activity_1ol43bw_determine_mode(BPMNExecProcessUtils.ProcessStatus s) {//Business Rule Task determine mode [Activity_1ol43bw]
        BPMNExecProcessUtils.debugOutput("Business Rule Task determine mode [Activity_1ol43bw]");
        BPMNExecProcessUtils.logCurrentNode("Activity_1ol43bw", "determine mode");
        BPMNExecProcessUtils.debugOutput("	 EXECUTING DECISION determine mode");
        dmn_dtable_DetermineModeDT_arguments args = new dmn_dtable_DetermineModeDT_arguments();
        args.Length = pLength;
        args.Weight = pWeight;
        dmn_dtable_DetermineModeDT_result determineModeResult = dmn_dtable_DetermineModeDT.execute(args);
        BPMNExecProcessUtils.debugOutput("	 DECISION RESULT IS %s", determineModeResult);
        sMode = determineModeResult.Mode;
        BPMNExecProcessUtils.debugOutput("	 ASSIGNING sMode TO %s", determineModeResult.Mode);
//[outgoing edge] Gateway_1ocbjca
        BPMNExecProcessUtils.logTransition("Activity_1ol43bw", "Gateway_1ocbjca");
        GATEWAY_Gateway_1ocbjca(s.withCurrent("Activity_1ol43bw"));
    }

    public void init() {
        if (this.pType == null) {
            pType = BPMNExecProcessUtils.inputs.getProperty("pType", null);
        }
        BPMNExecProcessUtils.logInput("pType", this.pType);
        if (this.pWeight == null) {
            pWeight = BPMNExecProcessUtils.inputs.getProperty("pWeight", null);
        }
        BPMNExecProcessUtils.logInput("pWeight", this.pWeight);
//parallel join initializers

    }

    public boolean globalAssert(BPMNExecProcessUtils.ProcessStatus s, String node_id) {
        boolean success = true;

        return success;

    }

    public void execute(Object _pType, Object _pWeight) {
        this.pType = _pType;
        this.pWeight = _pWeight;
        BPMNExecProcessUtils.executeProcess("Shipment", this::init, this::EVENT_StartEvent_1_package_received);
    }

    public static void main(String[] args) {
        BPMNExecProcessUtils.enableTrueParallel();
        bpmn_process_Shipment process = new bpmn_process_Shipment();
        process.execute(null/*pType*/, null/*pWeight*/);
    }
}
