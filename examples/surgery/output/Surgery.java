
import dellapenna.personal.bpmn.exec.*;

/*
 * ****************************** DMN Generated Code *************************
 */
// wrapper class for the output of DMN table PatientDismissionSurgicalUnitDT
class dmn_dtable_PatientDismissionSurgicalUnitDT_result {

    String PatientDismissionSurgicalUnit;

    public dmn_dtable_PatientDismissionSurgicalUnitDT_result(String PatientDismissionSurgicalUnit) {
        this.PatientDismissionSurgicalUnit = PatientDismissionSurgicalUnit;
    }

    public String toString() {
        String result = "{";
        result += "PatientDismissionSurgicalUnit=" + this.PatientDismissionSurgicalUnit;
        return result + "}";
    }
}

// wrapper class for the input of DMN table PatientDismissionSurgicalUnitDT
class dmn_dtable_PatientDismissionSurgicalUnitDT_arguments {

    public Object AldreteScore;
    public Object Activity;
    public Object Circulation;
    public Object Consciousness;
    public Object O2Saturation;
    public Object Respiration;
}

// decision code for DMN table PatientDismissionSurgicalUnitDT
class dmn_dtable_PatientDismissionSurgicalUnitDT {

    public static dmn_dtable_PatientDismissionSurgicalUnitDT_result execute(dmn_dtable_PatientDismissionSurgicalUnitDT_arguments args) {

        Object AldreteScore = args.AldreteScore;
        Object Activity = args.Activity;
        Object Circulation = args.Circulation;
        Object Consciousness = args.Consciousness;
        Object O2Saturation = args.O2Saturation;
        Object Respiration = args.Respiration;

        if (BPMNExecTypeUtils.tonumber(AldreteScore) >= BPMNExecTypeUtils.tonumber(8.0) && BPMNExecTypeUtils.tonumber(Activity) > BPMNExecTypeUtils.tonumber(0.0) && BPMNExecTypeUtils.tonumber(Circulation) > BPMNExecTypeUtils.tonumber(0.0) && BPMNExecTypeUtils.tonumber(Consciousness) > BPMNExecTypeUtils.tonumber(0.0) && BPMNExecTypeUtils.tonumber(O2Saturation) > BPMNExecTypeUtils.tonumber(0.0) && BPMNExecTypeUtils.tonumber(Respiration) > BPMNExecTypeUtils.tonumber(0.0)) {
            return new dmn_dtable_PatientDismissionSurgicalUnitDT_result(/*PatientDismissionSurgicalUnit*/"can");
        } else if (BPMNExecTypeUtils.tonumber(AldreteScore) < BPMNExecTypeUtils.tonumber(8.0) && BPMNExecTypeUtils.tonumber(Activity) > BPMNExecTypeUtils.tonumber(0.0) && BPMNExecTypeUtils.tonumber(Circulation) > BPMNExecTypeUtils.tonumber(0.0) && BPMNExecTypeUtils.tonumber(Consciousness) > BPMNExecTypeUtils.tonumber(0.0) && BPMNExecTypeUtils.tonumber(O2Saturation) > BPMNExecTypeUtils.tonumber(0.0) && BPMNExecTypeUtils.tonumber(Respiration) > BPMNExecTypeUtils.tonumber(0.0)) {
            return new dmn_dtable_PatientDismissionSurgicalUnitDT_result(/*PatientDismissionSurgicalUnit*/"cannot");
        } else {
            BPMNExecProcessUtils.noDefaultCaseError(null);
            return null;
        }
    }
}

// wrapper class for the output of DMN table PatientAdmissionOperatingTheatreDT
class dmn_dtable_PatientAdmissionOperatingTheatreDT_result {

    String PatientAdmissionOperatingTheatre;

    public dmn_dtable_PatientAdmissionOperatingTheatreDT_result(String PatientAdmissionOperatingTheatre) {
        this.PatientAdmissionOperatingTheatre = PatientAdmissionOperatingTheatre;
    }

    public String toString() {
        String result = "{";
        result += "PatientAdmissionOperatingTheatre=" + this.PatientAdmissionOperatingTheatre;
        return result + "}";
    }
}

// wrapper class for the input of DMN table PatientAdmissionOperatingTheatreDT
class dmn_dtable_PatientAdmissionOperatingTheatreDT_arguments {

    public Object InformedConsent;
    public Object PreoperationalChecklist;
    public Object AdverseEvents;
    public Object PreAnesthesicReEvaluation;
}

// decision code for DMN table PatientAdmissionOperatingTheatreDT
class dmn_dtable_PatientAdmissionOperatingTheatreDT {

    public static dmn_dtable_PatientAdmissionOperatingTheatreDT_result execute(dmn_dtable_PatientAdmissionOperatingTheatreDT_arguments args) {

        Object InformedConsent = args.InformedConsent;
        Object PreoperationalChecklist = args.PreoperationalChecklist;
        Object AdverseEvents = args.AdverseEvents;
        Object PreAnesthesicReEvaluation = args.PreAnesthesicReEvaluation;

        if (BPMNExecTypeUtils.tostring(InformedConsent).equals("absent")) {
            return new dmn_dtable_PatientAdmissionOperatingTheatreDT_result(/*PatientAdmissionOperatingTheatre*/"rejected");
        } else if (BPMNExecTypeUtils.tostring(PreoperationalChecklist).equals("incomplete")) {
            return new dmn_dtable_PatientAdmissionOperatingTheatreDT_result(/*PatientAdmissionOperatingTheatre*/"rejected");
        } else if (BPMNExecTypeUtils.tostring(AdverseEvents).equals("present")) {
            return new dmn_dtable_PatientAdmissionOperatingTheatreDT_result(/*PatientAdmissionOperatingTheatre*/"rejected");
        } else if (BPMNExecTypeUtils.toboolean(PreAnesthesicReEvaluation).equals(false)) {
            return new dmn_dtable_PatientAdmissionOperatingTheatreDT_result(/*PatientAdmissionOperatingTheatre*/"rejected");
        } else if (BPMNExecTypeUtils.tostring(InformedConsent).equals("present") && BPMNExecTypeUtils.tostring(PreoperationalChecklist).equals("complete") && BPMNExecTypeUtils.tostring(AdverseEvents).equals("absent") && BPMNExecTypeUtils.toboolean(PreAnesthesicReEvaluation).equals(true)) {
            return new dmn_dtable_PatientAdmissionOperatingTheatreDT_result(/*PatientAdmissionOperatingTheatre*/"admitted");
        } else {
            BPMNExecProcessUtils.noDefaultCaseError(null);
            return null;
        }
    }
}

// wrapper class for the output of DMN table PatientAdmissionDT
class dmn_dtable_PatientAdmissionDT_result {

    String PatientAdmission;

    public dmn_dtable_PatientAdmissionDT_result(String PatientAdmission) {
        this.PatientAdmission = PatientAdmission;
    }

    public String toString() {
        String result = "{";
        result += "PatientAdmission=" + this.PatientAdmission;
        return result + "}";
    }
}

// wrapper class for the input of DMN table PatientAdmissionDT
class dmn_dtable_PatientAdmissionDT_arguments {

    public Object Hospitalizable;
    public Object Emergency;
    public Object Age;
    public Object PreviouslyHospitalized;
}

// decision code for DMN table PatientAdmissionDT
class dmn_dtable_PatientAdmissionDT {

    public static dmn_dtable_PatientAdmissionDT_result execute(dmn_dtable_PatientAdmissionDT_arguments args) {

        Object Hospitalizable = args.Hospitalizable;
        Object Emergency = args.Emergency;
        Object Age = args.Age;
        Object PreviouslyHospitalized = args.PreviouslyHospitalized;

        if (BPMNExecTypeUtils.toboolean(Hospitalizable).equals(true) && BPMNExecTypeUtils.toboolean(Emergency).equals(true) && BPMNExecTypeUtils.tonumber(Age) < BPMNExecTypeUtils.tonumber(18.0)) {
            return new dmn_dtable_PatientAdmissionDT_result(/*PatientAdmission*/"emergency");
        } else if (BPMNExecTypeUtils.toboolean(Hospitalizable).equals(true) && BPMNExecTypeUtils.toboolean(Emergency).equals(true) && BPMNExecTypeUtils.tonumber(Age) >= BPMNExecTypeUtils.tonumber(18.0) && BPMNExecTypeUtils.toboolean(PreviouslyHospitalized).equals(true)) {
            return new dmn_dtable_PatientAdmissionDT_result(/*PatientAdmission*/"emergency");
        } else if (BPMNExecTypeUtils.toboolean(Hospitalizable).equals(true) && BPMNExecTypeUtils.toboolean(Emergency).equals(false) && BPMNExecTypeUtils.tonumber(Age) < BPMNExecTypeUtils.tonumber(18.0)) {
            return new dmn_dtable_PatientAdmissionDT_result(/*PatientAdmission*/"hospitalization");
        } else if (BPMNExecTypeUtils.toboolean(Hospitalizable).equals(true) && BPMNExecTypeUtils.toboolean(Emergency).equals(false) && BPMNExecTypeUtils.tonumber(Age) >= BPMNExecTypeUtils.tonumber(18.0) && BPMNExecTypeUtils.toboolean(PreviouslyHospitalized).equals(true)) {
            return new dmn_dtable_PatientAdmissionDT_result(/*PatientAdmission*/"hospitalization");
        } else if (BPMNExecTypeUtils.toboolean(Hospitalizable).equals(true) && BPMNExecTypeUtils.tonumber(Age) >= BPMNExecTypeUtils.tonumber(18.0) && BPMNExecTypeUtils.toboolean(PreviouslyHospitalized).equals(false)) {
            return new dmn_dtable_PatientAdmissionDT_result(/*PatientAdmission*/"denied");
        } else if (BPMNExecTypeUtils.toboolean(Hospitalizable).equals(false)) {
            return new dmn_dtable_PatientAdmissionDT_result(/*PatientAdmission*/"denied");
        } else {
            BPMNExecProcessUtils.noDefaultCaseError(null);
            return null;
        }
    }
}

// wrapper class for the output of DMN table PreAnesthesicEvaluationDT
class dmn_dtable_PreAnesthesicEvaluationDT_result {

    Boolean PreAnesthesicEvaluation;

    public dmn_dtable_PreAnesthesicEvaluationDT_result(Boolean PreAnesthesicEvaluation) {
        this.PreAnesthesicEvaluation = PreAnesthesicEvaluation;
    }

    public String toString() {
        String result = "{";
        result += "PreAnesthesicEvaluation=" + this.PreAnesthesicEvaluation;
        return result + "}";
    }
}

// wrapper class for the input of DMN table PreAnesthesicEvaluationDT
class dmn_dtable_PreAnesthesicEvaluationDT_arguments {

    public Object RequiredLaboratoryTests;
    public Object RequiredDiagnosticTests;
    public Object ClinicalConditions;
}

// decision code for DMN table PreAnesthesicEvaluationDT
class dmn_dtable_PreAnesthesicEvaluationDT {

    public static dmn_dtable_PreAnesthesicEvaluationDT_result execute(dmn_dtable_PreAnesthesicEvaluationDT_arguments args) {

        Object RequiredLaboratoryTests = args.RequiredLaboratoryTests;
        Object RequiredDiagnosticTests = args.RequiredDiagnosticTests;
        Object ClinicalConditions = args.ClinicalConditions;

        if (BPMNExecTypeUtils.toboolean(RequiredLaboratoryTests).equals(false)) {
            return new dmn_dtable_PreAnesthesicEvaluationDT_result(/*PreAnesthesicEvaluation*/false);
        } else if (BPMNExecTypeUtils.toboolean(RequiredDiagnosticTests).equals(false)) {
            return new dmn_dtable_PreAnesthesicEvaluationDT_result(/*PreAnesthesicEvaluation*/false);
        } else if (BPMNExecTypeUtils.tostring(ClinicalConditions).equals("negative")) {
            return new dmn_dtable_PreAnesthesicEvaluationDT_result(/*PreAnesthesicEvaluation*/false);
        } else if (BPMNExecTypeUtils.toboolean(RequiredLaboratoryTests).equals(true) && BPMNExecTypeUtils.toboolean(RequiredDiagnosticTests).equals(true) && BPMNExecTypeUtils.tostring(ClinicalConditions).equals("positive")) {
            return new dmn_dtable_PreAnesthesicEvaluationDT_result(/*PreAnesthesicEvaluation*/true);
        } else {
            BPMNExecProcessUtils.noDefaultCaseError(null);
            return null;
        }
    }
}

// wrapper class for the output of DMN table PreAnesthesicReEvaluationDT
class dmn_dtable_PreAnesthesicReEvaluationDT_result {

    Boolean PreAnesthesicReEvaluation;

    public dmn_dtable_PreAnesthesicReEvaluationDT_result(Boolean PreAnesthesicReEvaluation) {
        this.PreAnesthesicReEvaluation = PreAnesthesicReEvaluation;
    }

    public String toString() {
        String result = "{";
        result += "PreAnesthesicReEvaluation=" + this.PreAnesthesicReEvaluation;
        return result + "}";
    }
}

// wrapper class for the input of DMN table PreAnesthesicReEvaluationDT
class dmn_dtable_PreAnesthesicReEvaluationDT_arguments {

    public Object RequiredLaboratoryTests;
    public Object RequiredDiagnosticTests;
    public Object ClinicalConditions;
    public Object PreAnesthesicEvaluation;
}

// decision code for DMN table PreAnesthesicReEvaluationDT
class dmn_dtable_PreAnesthesicReEvaluationDT {

    public static dmn_dtable_PreAnesthesicReEvaluationDT_result execute(dmn_dtable_PreAnesthesicReEvaluationDT_arguments args) {

        Object RequiredLaboratoryTests = args.RequiredLaboratoryTests;
        Object RequiredDiagnosticTests = args.RequiredDiagnosticTests;
        Object ClinicalConditions = args.ClinicalConditions;
        Object PreAnesthesicEvaluation = args.PreAnesthesicEvaluation;

        if (BPMNExecTypeUtils.toboolean(RequiredLaboratoryTests).equals(false)) {
            return new dmn_dtable_PreAnesthesicReEvaluationDT_result(/*PreAnesthesicReEvaluation*/false);
        } else if (BPMNExecTypeUtils.toboolean(RequiredDiagnosticTests).equals(false)) {
            return new dmn_dtable_PreAnesthesicReEvaluationDT_result(/*PreAnesthesicReEvaluation*/false);
        } else if (BPMNExecTypeUtils.tostring(ClinicalConditions).equals("negative")) {
            return new dmn_dtable_PreAnesthesicReEvaluationDT_result(/*PreAnesthesicReEvaluation*/false);
        } else if (BPMNExecTypeUtils.toboolean(PreAnesthesicEvaluation).equals(false)) {
            return new dmn_dtable_PreAnesthesicReEvaluationDT_result(/*PreAnesthesicReEvaluation*/false);
        } else if (BPMNExecTypeUtils.toboolean(RequiredLaboratoryTests).equals(true) && BPMNExecTypeUtils.toboolean(RequiredDiagnosticTests).equals(true) && BPMNExecTypeUtils.tostring(ClinicalConditions).equals("positive") && BPMNExecTypeUtils.toboolean(PreAnesthesicEvaluation).equals(true)) {
            return new dmn_dtable_PreAnesthesicReEvaluationDT_result(/*PreAnesthesicReEvaluation*/true);
        } else {
            BPMNExecProcessUtils.noDefaultCaseError(null);
            return null;
        }
    }
}

/*
 * ****************************** Process Code *************************
 */
class Surgery {

//Input Variables
// READ: $DMN$PatientAdmissionOperatingTheatreDT$AdverseEvents, Gateway_0uzvw4h, Activity_0g3bnhh
    Object AdverseEvents = null;
// READ: Gateway_07brpq2
    Object ICU = null;
// READ: Activity_0g3bnhh, $DMN$PatientAdmissionOperatingTheatreDT$PreoperationalChecklist
    Object nPreoperationalChecklist = null;
// READ: $DMN$PreAnesthesicEvaluationDT$ClinicalConditions, Activity_1o5csdt, $DMN$PreAnesthesicReEvaluationDT$ClinicalConditions, Activity_1b92j4u
    Object pClinicalConditions = null;
// READ: $DMN$PreAnesthesicReEvaluationDT$RequiredDiagnosticTests, $DMN$PreAnesthesicEvaluationDT$RequiredDiagnosticTests, Activity_1o5csdt, Activity_1b92j4u
    Object pRequiredDiagnosticTests = null;
// READ: $DMN$PreAnesthesicReEvaluationDT$RequiredLaboratoryTests, Activity_1b92j4u, Activity_1o5csdt, $DMN$PreAnesthesicEvaluationDT$RequiredLaboratoryTests
    Object pRequiredLaboratoryTests = null;
// READ: Activity_16nr0p7, $DMN$PatientDismissionSurgicalUnitDT$Activity
    Object rActivity = null;
// READ: $DMN$PatientDismissionSurgicalUnitDT$AldreteScore, Activity_16nr0p7
    Object rAldreteScore = null;
// READ: Activity_16nr0p7, $DMN$PatientDismissionSurgicalUnitDT$Circulation
    Object rCirculation = null;
// READ: $DMN$PatientDismissionSurgicalUnitDT$Consciousness, Activity_16nr0p7
    Object rConsciousness = null;
// READ: $DMN$PatientDismissionSurgicalUnitDT$O2Saturation, Activity_16nr0p7
    Object rO2Saturation = null;
// READ: $DMN$PatientDismissionSurgicalUnitDT$Respiration, Activity_16nr0p7
    Object rRespiration = null;
// READ: $DMN$PatientAdmissionDT$Age, Activity_1mbfbnv
    Object uAge = null;
// READ: $DMN$PatientAdmissionDT$Emergency, Activity_1mbfbnv
    Object uEmergency = null;
// READ: Activity_1mbfbnv, $DMN$PatientAdmissionDT$Hospitalizable
    Object uHospitalizable = null;
// READ: $DMN$PatientAdmissionOperatingTheatreDT$InformedConsent, Activity_0g3bnhh
    Object uInformedConsent = null;
// READ: $DMN$PatientAdmissionDT$PreviouslyHospitalized, Activity_1mbfbnv
    Object uPreviouslyHospitalized = null;

//Process Variables
// READ: Gateway_1hwej0w
// WRITTEN: Activity_1mbfbnv
    Object PatientAdmission = null;
// READ: Gateway_1s04agq
// WRITTEN: Activity_0g3bnhh
    Object PatientAdmissionOperatingTheatre = null;
// READ: Gateway_058xy6d
// WRITTEN: Activity_16nr0p7
    Object PatientDismissionSurgicalUnit = null;
// READ: Activity_1b92j4u, Gateway_01rw5bk, $DMN$PreAnesthesicReEvaluationDT$PreAnesthesicEvaluation
// WRITTEN: Activity_1o5csdt
    Object PreAnesthesicEvaluation = null;
// READ: $DMN$PatientAdmissionOperatingTheatreDT$PreAnesthesicReEvaluation, Activity_0g3bnhh
// WRITTEN: Activity_1b92j4u
    Object PreAnesthesicReEvaluation = null;

//Messages
    private static class Message_addPatient implements BPMNExecProcessUtils.Message {
    };

//Process Dynamics
    public void EVENT_Event_01gw6pd_Return_to_ward__success_(BPMNExecProcessUtils.ProcessStatus s) {//End Event Return to ward (success) [Event_01gw6pd]
        BPMNExecProcessUtils.debugOutput(s, "End Event Return to ward (success) [Event_01gw6pd]");
        BPMNExecProcessUtils.logCurrentNode("Event_01gw6pd", "Return to ward (success)");
        BPMNExecProcessUtils.success(s);
    }

    public void EVENT_Event_0tlco0q_Return_to_ward__exception_(BPMNExecProcessUtils.ProcessStatus s) {//End Event Return to ward (exception) [Event_0tlco0q]
        BPMNExecProcessUtils.debugOutput(s, "End Event Return to ward (exception) [Event_0tlco0q]");
        BPMNExecProcessUtils.logCurrentNode("Event_0tlco0q", "Return to ward (exception)");
        BPMNExecProcessUtils.error(s, "Return", 2);
    }

    public void EVENT_Event_0tqfzbc_Patient_rejected(BPMNExecProcessUtils.ProcessStatus s) {//End Event Patient rejected [Event_0tqfzbc]
        BPMNExecProcessUtils.debugOutput(s, "End Event Patient rejected [Event_0tqfzbc]");
        BPMNExecProcessUtils.logCurrentNode("Event_0tqfzbc", "Patient rejected");
        BPMNExecProcessUtils.error(s, "Rejected", 1);
    }

    public void EVENT_Event_1dd675w(BPMNExecProcessUtils.ProcessStatus s) {//Start Event Event_1dd675w
        BPMNExecProcessUtils.debugOutput(s, "Start Event Event_1dd675w");
        BPMNExecProcessUtils.logCurrentNode("Event_1dd675w", null);
//[outgoing edge] Activity_0do0700 - User Info
        BPMNExecProcessUtils.logTransition("Event_1dd675w", "Activity_0do0700");
        TASK_Activity_0do0700_User_Info(s.withCurrent("Event_1dd675w"));
    }

    public void GATEWAY_Gateway_01rw5bk_Patient_sent_back_1(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Patient sent back 1 [Gateway_01rw5bk]
        BPMNExecProcessUtils.debugOutput(s, "Exclusive Gateway Patient sent back 1 [Gateway_01rw5bk]");
        BPMNExecProcessUtils.logCurrentNode("Gateway_01rw5bk", "Patient sent back 1");
        if (BPMNExecTypeUtils.equals(PreAnesthesicEvaluation, true)) {//[outgoing edge] Activity_1bfwy21 - Check adverse events
            BPMNExecProcessUtils.logTransition("Gateway_01rw5bk", "Activity_1bfwy21");
            TASK_Activity_1bfwy21_Check_adverse_events(s.withCurrent("Gateway_01rw5bk"));
        } else if (BPMNExecTypeUtils.equals(PreAnesthesicEvaluation, false)) {//[outgoing edge] Event_0tlco0q - Return to ward (exception)
            BPMNExecProcessUtils.logTransition("Gateway_01rw5bk", "Event_0tlco0q");
            EVENT_Event_0tlco0q_Return_to_ward__exception_(s.withCurrent("Gateway_01rw5bk"));
        } else {
            BPMNExecProcessUtils.noDefaultCaseError(s);
        }
    }

    public void GATEWAY_Gateway_058xy6d_Can_patient_leave_surgical_unit(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Can patient leave surgical unit [Gateway_058xy6d]
        BPMNExecProcessUtils.debugOutput(s, "Exclusive Gateway Can patient leave surgical unit [Gateway_058xy6d]");
        BPMNExecProcessUtils.logCurrentNode("Gateway_058xy6d", "Can patient leave surgical unit");
        if (BPMNExecTypeUtils.equals(PatientDismissionSurgicalUnit, "can")) {//[outgoing edge] Event_01gw6pd - Return to ward (success)
            BPMNExecProcessUtils.logTransition("Gateway_058xy6d", "Event_01gw6pd");
            EVENT_Event_01gw6pd_Return_to_ward__success_(s.withCurrent("Gateway_058xy6d"));
        } else {//[outgoing edge] Activity_1uuxpml - Recovery room
            BPMNExecProcessUtils.logTransition("Gateway_058xy6d", "Activity_1uuxpml");
            TASK_Activity_1uuxpml_Recovery_room(s.withCurrent("Gateway_058xy6d"));
        }
    }

    public void GATEWAY_Gateway_07brpq2_ICU(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway ICU [Gateway_07brpq2]
        BPMNExecProcessUtils.debugOutput(s, "Exclusive Gateway ICU [Gateway_07brpq2]");
        BPMNExecProcessUtils.logCurrentNode("Gateway_07brpq2", "ICU");
        if (BPMNExecTypeUtils.equals(ICU, true)) {//[outgoing edge] Activity_0mqe4nv - Transport to ICU
            BPMNExecProcessUtils.logTransition("Gateway_07brpq2", "Activity_0mqe4nv");
            TASK_Activity_0mqe4nv_Transport_to_ICU(s.withCurrent("Gateway_07brpq2"));
        } else {//[outgoing edge] Activity_1uuxpml - Recovery room
            BPMNExecProcessUtils.logTransition("Gateway_07brpq2", "Activity_1uuxpml");
            TASK_Activity_1uuxpml_Recovery_room(s.withCurrent("Gateway_07brpq2"));
        }
    }

    public void GATEWAY_Gateway_0uzvw4h_Any_adverse_event(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Any adverse event [Gateway_0uzvw4h]
        BPMNExecProcessUtils.debugOutput(s, "Exclusive Gateway Any adverse event [Gateway_0uzvw4h]");
        BPMNExecProcessUtils.logCurrentNode("Gateway_0uzvw4h", "Any adverse event");
        if (BPMNExecTypeUtils.equals(AdverseEvents, "absent")) {//[outgoing edge] Activity_01iv8eh - Preoperational patient preparation
            BPMNExecProcessUtils.logTransition("Gateway_0uzvw4h", "Activity_01iv8eh");
            TASK_Activity_01iv8eh_Preoperational_patient_preparation(s.withCurrent("Gateway_0uzvw4h"));
        } else if (BPMNExecTypeUtils.equals(AdverseEvents, "present")) {//[outgoing edge] Event_0tlco0q - Return to ward (exception)
            BPMNExecProcessUtils.logTransition("Gateway_0uzvw4h", "Event_0tlco0q");
            EVENT_Event_0tlco0q_Return_to_ward__exception_(s.withCurrent("Gateway_0uzvw4h"));
        } else {
            BPMNExecProcessUtils.noDefaultCaseError(s);
        }
    }

    public void GATEWAY_Gateway_1hwej0w_Triage(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Triage [Gateway_1hwej0w]
        BPMNExecProcessUtils.debugOutput(s, "Exclusive Gateway Triage [Gateway_1hwej0w]");
        BPMNExecProcessUtils.logCurrentNode("Gateway_1hwej0w", "Triage");
        if (BPMNExecTypeUtils.equals(PatientAdmission, "denied")) {//[outgoing edge] Event_0tqfzbc - Patient rejected
            BPMNExecProcessUtils.logTransition("Gateway_1hwej0w", "Event_0tqfzbc");
            EVENT_Event_0tqfzbc_Patient_rejected(s.withCurrent("Gateway_1hwej0w"));
        } else {//[outgoing edge] Activity_1qr375k - Insert into surgeon waiting list
            BPMNExecProcessUtils.logTransition("Gateway_1hwej0w", "Activity_1qr375k");
            TASK_Activity_1qr375k_Insert_into_surgeon_waiting_list(s.withCurrent("Gateway_1hwej0w"));
        }
    }

    public void GATEWAY_Gateway_1s04agq_Patient_sent_back_2(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Patient sent back 2 [Gateway_1s04agq]
        BPMNExecProcessUtils.debugOutput(s, "Exclusive Gateway Patient sent back 2 [Gateway_1s04agq]");
        BPMNExecProcessUtils.logCurrentNode("Gateway_1s04agq", "Patient sent back 2");
        if (BPMNExecTypeUtils.equals(PatientAdmissionOperatingTheatre, "admitted")) {//[outgoing edge] Activity_0hywbyv - Surgery
            BPMNExecProcessUtils.logTransition("Gateway_1s04agq", "Activity_0hywbyv");
            TASK_Activity_0hywbyv_Surgery(s.withCurrent("Gateway_1s04agq"));
        } else if (BPMNExecTypeUtils.equals(PatientAdmissionOperatingTheatre, "rejected")) {//[outgoing edge] Event_0tlco0q - Return to ward (exception)
            BPMNExecProcessUtils.logTransition("Gateway_1s04agq", "Event_0tlco0q");
            EVENT_Event_0tlco0q_Return_to_ward__exception_(s.withCurrent("Gateway_1s04agq"));
        } else {
            BPMNExecProcessUtils.noDefaultCaseError(s);
        }
    }

    public void TASK_Activity_01iv8eh_Preoperational_patient_preparation(BPMNExecProcessUtils.ProcessStatus s) {//User Task Preoperational patient preparation [Activity_01iv8eh]
        BPMNExecProcessUtils.debugOutput(s, "User Task Preoperational patient preparation [Activity_01iv8eh]");
        BPMNExecProcessUtils.logCurrentNode("Activity_01iv8eh", "Preoperational patient preparation");
//[outgoing edge] Activity_1b92j4u - Pre-Anesthesic re-evaluation
        BPMNExecProcessUtils.logTransition("Activity_01iv8eh", "Activity_1b92j4u");
        TASK_Activity_1b92j4u_Pre_Anesthesic_re_evaluation(s.withCurrent("Activity_01iv8eh"));
    }

    public void TASK_Activity_0ba89kz_Wait_for_message_from_external_software(BPMNExecProcessUtils.ProcessStatus s) {//Receive Task Wait for message from external software [Activity_0ba89kz]
        BPMNExecProcessUtils.debugOutput(s, "Receive Task Wait for message from external software [Activity_0ba89kz]");
        BPMNExecProcessUtils.logCurrentNode("Activity_0ba89kz", "Wait for message from external software");
        BPMNExecProcessUtils.debugOutput(s, "	 ASSUMING RECEPTION of message on channel SurgeonListMessages");
//[outgoing edge] Activity_1o5csdt - Pre-Anesthesic evaluation
        BPMNExecProcessUtils.logTransition("Activity_0ba89kz", "Activity_1o5csdt");
        TASK_Activity_1o5csdt_Pre_Anesthesic_evaluation(s.withCurrent("Activity_0ba89kz"));
    }

    public void TASK_Activity_0do0700_User_Info(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task User Info [Activity_0do0700]
        BPMNExecProcessUtils.debugOutput(s, "Generic Task User Info [Activity_0do0700]");
        BPMNExecProcessUtils.logCurrentNode("Activity_0do0700", "User Info");
//[outgoing edge] Activity_1mbfbnv - Parient Admission
        BPMNExecProcessUtils.logTransition("Activity_0do0700", "Activity_1mbfbnv");
        TASK_Activity_1mbfbnv_Parient_Admission(s.withCurrent("Activity_0do0700"));
    }

    public void TASK_Activity_0g3bnhh_Patient_admission_to_operating_theatre(BPMNExecProcessUtils.ProcessStatus s) {//Business Rule Task Patient admission to operating theatre [Activity_0g3bnhh]
        BPMNExecProcessUtils.debugOutput(s, "Business Rule Task Patient admission to operating theatre [Activity_0g3bnhh]");
        BPMNExecProcessUtils.logCurrentNode("Activity_0g3bnhh", "Patient admission to operating theatre");
        BPMNExecProcessUtils.debugOutput(s, "	 EXECUTING DECISION Patient admission to operating theatre");
        dmn_dtable_PatientAdmissionOperatingTheatreDT_arguments args = new dmn_dtable_PatientAdmissionOperatingTheatreDT_arguments();
        args.InformedConsent = uInformedConsent;
        args.PreoperationalChecklist = nPreoperationalChecklist;
        args.AdverseEvents = AdverseEvents;
        args.PreAnesthesicReEvaluation = PreAnesthesicReEvaluation;
        dmn_dtable_PatientAdmissionOperatingTheatreDT_result PatientAdmissionOperatingTheatreResult = dmn_dtable_PatientAdmissionOperatingTheatreDT.execute(args);
        BPMNExecProcessUtils.debugOutput(s, "	 DECISION RESULT IS %s", PatientAdmissionOperatingTheatreResult);
        PatientAdmissionOperatingTheatre = PatientAdmissionOperatingTheatreResult.PatientAdmissionOperatingTheatre;
        BPMNExecProcessUtils.debugOutput(s, "	 ASSIGNING PatientAdmissionOperatingTheatre TO %s", PatientAdmissionOperatingTheatreResult.PatientAdmissionOperatingTheatre);
//[outgoing edge] Gateway_1s04agq - Patient sent back 2
        BPMNExecProcessUtils.logTransition("Activity_0g3bnhh", "Gateway_1s04agq");
        GATEWAY_Gateway_1s04agq_Patient_sent_back_2(s.withCurrent("Activity_0g3bnhh"));
    }

    public void TASK_Activity_0hywbyv_Surgery(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Surgery [Activity_0hywbyv]
        BPMNExecProcessUtils.debugOutput(s, "Generic Task Surgery [Activity_0hywbyv]");
        BPMNExecProcessUtils.logCurrentNode("Activity_0hywbyv", "Surgery");
//[outgoing edge] Activity_1v7bxvg - Patient discharge from operating theater
        BPMNExecProcessUtils.logTransition("Activity_0hywbyv", "Activity_1v7bxvg");
        TASK_Activity_1v7bxvg_Patient_discharge_from_operating_theater(s.withCurrent("Activity_0hywbyv"));
    }

    public void TASK_Activity_0mqe4nv_Transport_to_ICU(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Transport to ICU [Activity_0mqe4nv]
        BPMNExecProcessUtils.debugOutput(s, "Generic Task Transport to ICU [Activity_0mqe4nv]");
        BPMNExecProcessUtils.logCurrentNode("Activity_0mqe4nv", "Transport to ICU");
//[outgoing edge] Event_01gw6pd - Return to ward (success)
        BPMNExecProcessUtils.logTransition("Activity_0mqe4nv", "Event_01gw6pd");
        EVENT_Event_01gw6pd_Return_to_ward__success_(s.withCurrent("Activity_0mqe4nv"));
    }

    public void TASK_Activity_16nr0p7_Patient_dismission_from_surgical_unit(BPMNExecProcessUtils.ProcessStatus s) {//Business Rule Task Patient dismission from surgical unit [Activity_16nr0p7]
        BPMNExecProcessUtils.debugOutput(s, "Business Rule Task Patient dismission from surgical unit [Activity_16nr0p7]");
        BPMNExecProcessUtils.logCurrentNode("Activity_16nr0p7", "Patient dismission from surgical unit");
        BPMNExecProcessUtils.debugOutput(s, "	 EXECUTING DECISION Patient dismission from surgical unit");
        dmn_dtable_PatientDismissionSurgicalUnitDT_arguments args = new dmn_dtable_PatientDismissionSurgicalUnitDT_arguments();
        args.AldreteScore = rAldreteScore;
        args.Activity = rActivity;
        args.Circulation = rCirculation;
        args.Consciousness = rConsciousness;
        args.O2Saturation = rO2Saturation;
        args.Respiration = rRespiration;
        dmn_dtable_PatientDismissionSurgicalUnitDT_result PatientDismissionSurgicalUnitResult = dmn_dtable_PatientDismissionSurgicalUnitDT.execute(args);
        BPMNExecProcessUtils.debugOutput(s, "	 DECISION RESULT IS %s", PatientDismissionSurgicalUnitResult);
        PatientDismissionSurgicalUnit = PatientDismissionSurgicalUnitResult.PatientDismissionSurgicalUnit;
        BPMNExecProcessUtils.debugOutput(s, "	 ASSIGNING PatientDismissionSurgicalUnit TO %s", PatientDismissionSurgicalUnitResult.PatientDismissionSurgicalUnit);
//[outgoing edge] Gateway_058xy6d - Can patient leave surgical unit
        BPMNExecProcessUtils.logTransition("Activity_16nr0p7", "Gateway_058xy6d");
        GATEWAY_Gateway_058xy6d_Can_patient_leave_surgical_unit(s.withCurrent("Activity_16nr0p7"));
    }

    public void TASK_Activity_1b92j4u_Pre_Anesthesic_re_evaluation(BPMNExecProcessUtils.ProcessStatus s) {//Business Rule Task Pre-Anesthesic re-evaluation [Activity_1b92j4u]
        BPMNExecProcessUtils.debugOutput(s, "Business Rule Task Pre-Anesthesic re-evaluation [Activity_1b92j4u]");
        BPMNExecProcessUtils.logCurrentNode("Activity_1b92j4u", "Pre-Anesthesic re-evaluation");
        BPMNExecProcessUtils.debugOutput(s, "	 EXECUTING DECISION Pre-Anesthesic re-evaluation");
        dmn_dtable_PreAnesthesicReEvaluationDT_arguments args = new dmn_dtable_PreAnesthesicReEvaluationDT_arguments();
        args.RequiredLaboratoryTests = pRequiredLaboratoryTests;
        args.RequiredDiagnosticTests = pRequiredDiagnosticTests;
        args.ClinicalConditions = pClinicalConditions;
        args.PreAnesthesicEvaluation = PreAnesthesicEvaluation;
        dmn_dtable_PreAnesthesicReEvaluationDT_result PreAnesthesicReEvaluationResult = dmn_dtable_PreAnesthesicReEvaluationDT.execute(args);
        BPMNExecProcessUtils.debugOutput(s, "	 DECISION RESULT IS %s", PreAnesthesicReEvaluationResult);
        PreAnesthesicReEvaluation = PreAnesthesicReEvaluationResult.PreAnesthesicReEvaluation;
        BPMNExecProcessUtils.debugOutput(s, "	 ASSIGNING PreAnesthesicReEvaluation TO %s", PreAnesthesicReEvaluationResult.PreAnesthesicReEvaluation);
//[outgoing edge] Activity_0g3bnhh - Patient admission to operating theatre
        BPMNExecProcessUtils.logTransition("Activity_1b92j4u", "Activity_0g3bnhh");
        TASK_Activity_0g3bnhh_Patient_admission_to_operating_theatre(s.withCurrent("Activity_1b92j4u"));
    }

    public void TASK_Activity_1bfwy21_Check_adverse_events(BPMNExecProcessUtils.ProcessStatus s) {//User Task Check adverse events [Activity_1bfwy21]
        BPMNExecProcessUtils.debugOutput(s, "User Task Check adverse events [Activity_1bfwy21]");
        BPMNExecProcessUtils.logCurrentNode("Activity_1bfwy21", "Check adverse events");
//[outgoing edge] Gateway_0uzvw4h - Any adverse event
        BPMNExecProcessUtils.logTransition("Activity_1bfwy21", "Gateway_0uzvw4h");
        GATEWAY_Gateway_0uzvw4h_Any_adverse_event(s.withCurrent("Activity_1bfwy21"));
    }

    public void TASK_Activity_1mbfbnv_Parient_Admission(BPMNExecProcessUtils.ProcessStatus s) {//Business Rule Task Parient Admission [Activity_1mbfbnv]
        BPMNExecProcessUtils.debugOutput(s, "Business Rule Task Parient Admission [Activity_1mbfbnv]");
        BPMNExecProcessUtils.logCurrentNode("Activity_1mbfbnv", "Parient Admission");
        BPMNExecProcessUtils.debugOutput(s, "	 EXECUTING DECISION Parient Admission");
        dmn_dtable_PatientAdmissionDT_arguments args = new dmn_dtable_PatientAdmissionDT_arguments();
        args.Hospitalizable = uHospitalizable;
        args.Emergency = uEmergency;
        args.Age = uAge;
        args.PreviouslyHospitalized = uPreviouslyHospitalized;
        dmn_dtable_PatientAdmissionDT_result PatientAdmissionResult = dmn_dtable_PatientAdmissionDT.execute(args);
        BPMNExecProcessUtils.debugOutput(s, "	 DECISION RESULT IS %s", PatientAdmissionResult);
        PatientAdmission = PatientAdmissionResult.PatientAdmission;
        BPMNExecProcessUtils.debugOutput(s, "	 ASSIGNING PatientAdmission TO %s", PatientAdmissionResult.PatientAdmission);
//[outgoing edge] Gateway_1hwej0w - Triage
        BPMNExecProcessUtils.logTransition("Activity_1mbfbnv", "Gateway_1hwej0w");
        GATEWAY_Gateway_1hwej0w_Triage(s.withCurrent("Activity_1mbfbnv"));
    }

    public void TASK_Activity_1o5csdt_Pre_Anesthesic_evaluation(BPMNExecProcessUtils.ProcessStatus s) {//Business Rule Task Pre-Anesthesic evaluation [Activity_1o5csdt]
        BPMNExecProcessUtils.debugOutput(s, "Business Rule Task Pre-Anesthesic evaluation [Activity_1o5csdt]");
        BPMNExecProcessUtils.logCurrentNode("Activity_1o5csdt", "Pre-Anesthesic evaluation");
        BPMNExecProcessUtils.debugOutput(s, "	 EXECUTING DECISION Pre-Anesthesic evaluation");
        dmn_dtable_PreAnesthesicEvaluationDT_arguments args = new dmn_dtable_PreAnesthesicEvaluationDT_arguments();
        args.RequiredLaboratoryTests = pRequiredLaboratoryTests;
        args.RequiredDiagnosticTests = pRequiredDiagnosticTests;
        args.ClinicalConditions = pClinicalConditions;
        dmn_dtable_PreAnesthesicEvaluationDT_result PreAnesthesicEvaluationResult = dmn_dtable_PreAnesthesicEvaluationDT.execute(args);
        BPMNExecProcessUtils.debugOutput(s, "	 DECISION RESULT IS %s", PreAnesthesicEvaluationResult);
        PreAnesthesicEvaluation = PreAnesthesicEvaluationResult.PreAnesthesicEvaluation;
        BPMNExecProcessUtils.debugOutput(s, "	 ASSIGNING PreAnesthesicEvaluation TO %s", PreAnesthesicEvaluationResult.PreAnesthesicEvaluation);
//[outgoing edge] Gateway_01rw5bk - Patient sent back 1
        BPMNExecProcessUtils.logTransition("Activity_1o5csdt", "Gateway_01rw5bk");
        GATEWAY_Gateway_01rw5bk_Patient_sent_back_1(s.withCurrent("Activity_1o5csdt"));
    }

    public void TASK_Activity_1qr375k_Insert_into_surgeon_waiting_list(BPMNExecProcessUtils.ProcessStatus s) {//Send Task Insert into surgeon waiting list [Activity_1qr375k]
        BPMNExecProcessUtils.debugOutput(s, "Send Task Insert into surgeon waiting list [Activity_1qr375k]");
        BPMNExecProcessUtils.logCurrentNode("Activity_1qr375k", "Insert into surgeon waiting list");
        Message_addPatient m = new Message_addPatient();
        BPMNExecProcessUtils.debugOutput(s, "	 SENDING message on channel SurgeonListMessages");
        BPMNExecProcessUtils.sendMessage(s, "SurgeonListMessages", m);
//[outgoing edge] Activity_0ba89kz - Wait for message from external software
        BPMNExecProcessUtils.logTransition("Activity_1qr375k", "Activity_0ba89kz");
        TASK_Activity_0ba89kz_Wait_for_message_from_external_software(s.withCurrent("Activity_1qr375k"));
    }

    public void TASK_Activity_1uuxpml_Recovery_room(BPMNExecProcessUtils.ProcessStatus s) {//User Task Recovery room [Activity_1uuxpml]
        BPMNExecProcessUtils.debugOutput(s, "User Task Recovery room [Activity_1uuxpml]");
        BPMNExecProcessUtils.logCurrentNode("Activity_1uuxpml", "Recovery room");
//[outgoing edge] Activity_16nr0p7 - Patient dismission from surgical unit
        BPMNExecProcessUtils.logTransition("Activity_1uuxpml", "Activity_16nr0p7");
        TASK_Activity_16nr0p7_Patient_dismission_from_surgical_unit(s.withCurrent("Activity_1uuxpml"));
    }

    public void TASK_Activity_1v7bxvg_Patient_discharge_from_operating_theater(BPMNExecProcessUtils.ProcessStatus s) {//User Task Patient discharge from operating theater [Activity_1v7bxvg]
        BPMNExecProcessUtils.debugOutput(s, "User Task Patient discharge from operating theater [Activity_1v7bxvg]");
        BPMNExecProcessUtils.logCurrentNode("Activity_1v7bxvg", "Patient discharge from operating theater");
//[outgoing edge] Gateway_07brpq2 - ICU
        BPMNExecProcessUtils.logTransition("Activity_1v7bxvg", "Gateway_07brpq2");
        GATEWAY_Gateway_07brpq2_ICU(s.withCurrent("Activity_1v7bxvg"));
    }

    public void init() {
        if (this.uHospitalizable == null) {
            uHospitalizable = BPMNExecProcessUtils.inputs.getProperty("uHospitalizable", null);
        }
        BPMNExecProcessUtils.logInput("uHospitalizable", this.uHospitalizable);
        if (this.uEmergency == null) {
            uEmergency = BPMNExecProcessUtils.inputs.getProperty("uEmergency", null);
        }
        BPMNExecProcessUtils.logInput("uEmergency", this.uEmergency);
        if (this.uAge == null) {
            uAge = BPMNExecProcessUtils.inputs.getProperty("uAge", null);
        }
        BPMNExecProcessUtils.logInput("uAge", this.uAge);
        if (this.uPreviouslyHospitalized == null) {
            uPreviouslyHospitalized = BPMNExecProcessUtils.inputs.getProperty("uPreviouslyHospitalized", null);
        }
        BPMNExecProcessUtils.logInput("uPreviouslyHospitalized", this.uPreviouslyHospitalized);
        if (this.pRequiredLaboratoryTests == null) {
            pRequiredLaboratoryTests = BPMNExecProcessUtils.inputs.getProperty("pRequiredLaboratoryTests", null);
        }
        BPMNExecProcessUtils.logInput("pRequiredLaboratoryTests", this.pRequiredLaboratoryTests);
        if (this.pRequiredDiagnosticTests == null) {
            pRequiredDiagnosticTests = BPMNExecProcessUtils.inputs.getProperty("pRequiredDiagnosticTests", null);
        }
        BPMNExecProcessUtils.logInput("pRequiredDiagnosticTests", this.pRequiredDiagnosticTests);
        if (this.pClinicalConditions == null) {
            pClinicalConditions = BPMNExecProcessUtils.inputs.getProperty("pClinicalConditions", null);
        }
        BPMNExecProcessUtils.logInput("pClinicalConditions", this.pClinicalConditions);
        if (this.AdverseEvents == null) {
            AdverseEvents = BPMNExecProcessUtils.inputs.getProperty("AdverseEvents", null);
        }
        BPMNExecProcessUtils.logInput("AdverseEvents", this.AdverseEvents);
        if (this.uInformedConsent == null) {
            uInformedConsent = BPMNExecProcessUtils.inputs.getProperty("uInformedConsent", null);
        }
        BPMNExecProcessUtils.logInput("uInformedConsent", this.uInformedConsent);
        if (this.nPreoperationalChecklist == null) {
            nPreoperationalChecklist = BPMNExecProcessUtils.inputs.getProperty("nPreoperationalChecklist", null);
        }
        BPMNExecProcessUtils.logInput("nPreoperationalChecklist", this.nPreoperationalChecklist);
        if (this.ICU == null) {
            ICU = BPMNExecProcessUtils.inputs.getProperty("ICU", null);
        }
        BPMNExecProcessUtils.logInput("ICU", this.ICU);
        if (this.rAldreteScore == null) {
            rAldreteScore = BPMNExecProcessUtils.inputs.getProperty("rAldreteScore", null);
        }
        BPMNExecProcessUtils.logInput("rAldreteScore", this.rAldreteScore);
        if (this.rActivity == null) {
            rActivity = BPMNExecProcessUtils.inputs.getProperty("rActivity", null);
        }
        BPMNExecProcessUtils.logInput("rActivity", this.rActivity);
        if (this.rCirculation == null) {
            rCirculation = BPMNExecProcessUtils.inputs.getProperty("rCirculation", null);
        }
        BPMNExecProcessUtils.logInput("rCirculation", this.rCirculation);
        if (this.rConsciousness == null) {
            rConsciousness = BPMNExecProcessUtils.inputs.getProperty("rConsciousness", null);
        }
        BPMNExecProcessUtils.logInput("rConsciousness", this.rConsciousness);
        if (this.rO2Saturation == null) {
            rO2Saturation = BPMNExecProcessUtils.inputs.getProperty("rO2Saturation", null);
        }
        BPMNExecProcessUtils.logInput("rO2Saturation", this.rO2Saturation);
        if (this.rRespiration == null) {
            rRespiration = BPMNExecProcessUtils.inputs.getProperty("rRespiration", null);
        }
        BPMNExecProcessUtils.logInput("rRespiration", this.rRespiration);

    }

    public boolean globalAssert(BPMNExecProcessUtils.ProcessStatus s, String node_id) {
        boolean success = true;

        return success;

    }

    public void execute(Object _uHospitalizable, Object _uEmergency, Object _uAge, Object _uPreviouslyHospitalized, Object _pRequiredLaboratoryTests, Object _pRequiredDiagnosticTests, Object _pClinicalConditions, Object _AdverseEvents, Object _uInformedConsent, Object _nPreoperationalChecklist, Object _ICU, Object _rAldreteScore, Object _rActivity, Object _rCirculation, Object _rConsciousness, Object _rO2Saturation, Object _rRespiration) {
        this.uHospitalizable = _uHospitalizable;
        this.uEmergency = _uEmergency;
        this.uAge = _uAge;
        this.uPreviouslyHospitalized = _uPreviouslyHospitalized;
        this.pRequiredLaboratoryTests = _pRequiredLaboratoryTests;
        this.pRequiredDiagnosticTests = _pRequiredDiagnosticTests;
        this.pClinicalConditions = _pClinicalConditions;
        this.AdverseEvents = _AdverseEvents;
        this.uInformedConsent = _uInformedConsent;
        this.nPreoperationalChecklist = _nPreoperationalChecklist;
        this.ICU = _ICU;
        this.rAldreteScore = _rAldreteScore;
        this.rActivity = _rActivity;
        this.rCirculation = _rCirculation;
        this.rConsciousness = _rConsciousness;
        this.rO2Saturation = _rO2Saturation;
        this.rRespiration = _rRespiration;
        BPMNExecProcessUtils.executeProcess("Surgery", this::init, this::EVENT_Event_1dd675w);
    }

    public static void main(String[] args) {
        BPMNExecProcessUtils.setExternalTraceFile("Surgery");
        BPMNExecProcessUtils.enableTrueParallel();
        Surgery process = new Surgery();
        process.execute(null/*uHospitalizable*/, null/*uEmergency*/, null/*uAge*/, null/*uPreviouslyHospitalized*/, null/*pRequiredLaboratoryTests*/, null/*pRequiredDiagnosticTests*/, null/*pClinicalConditions*/, null/*AdverseEvents*/, null/*uInformedConsent*/, null/*nPreoperationalChecklist*/, null/*ICU*/, null/*rAldreteScore*/, null/*rActivity*/, null/*rCirculation*/, null/*rConsciousness*/, null/*rO2Saturation*/, null/*rRespiration*/);
    }
}
