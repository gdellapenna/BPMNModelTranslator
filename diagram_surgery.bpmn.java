
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
        }
    }
}

/*
 * ****************************** BPMN Generated Code *************************
 */
class bpmn_process_Surgery {

//Input Variables
// READ: $DMN$PatientAdmissionOperatingTheatreDT$AdverseEvents, Gateway_0uzvw4h, Activity_0g3bnhh
    Object AdverseEvents = null;
// READ: Gateway_07brpq2
    Object ICU = null;
// READ: Gateway_01rw5bk
    Object PreAnestheticEvaluation = null;
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
// READ: Activity_1b92j4u, $DMN$PreAnesthesicReEvaluationDT$PreAnesthesicEvaluation
// WRITTEN: Activity_1o5csdt
    Object PreAnesthesicEvaluation = null;
// READ: $DMN$PatientAdmissionOperatingTheatreDT$PreAnesthesicReEvaluation, Activity_0g3bnhh
// WRITTEN: Activity_1b92j4u
    Object PreAnesthesicReEvaluation = null;

//Process Dynamics
    public void EVENT_Event_1dd675w(BPMNExecProcessUtils.ProcessStatus s) {//Start Event Event_1dd675w
        BPMNExecProcessUtils.debugOutput("Start Event Event_1dd675w");
        BPMNExecProcessUtils.logCurrentNode("Event_1dd675w", null);
        globalAssert(s, "Event_1dd675w");
//[outgoing edge] Activity_0do0700 - User Info
        BPMNExecProcessUtils.logTransition("Event_1dd675w", "Activity_0do0700");
        TASK_User_Info(s.withCurrent("Event_1dd675w"));
    }

    public void EVENT_Patient_rejected(BPMNExecProcessUtils.ProcessStatus s) {//End Event Patient rejected [Event_0tqfzbc]
        BPMNExecProcessUtils.debugOutput("End Event Patient rejected [Event_0tqfzbc]");
        BPMNExecProcessUtils.logCurrentNode("Event_0tqfzbc", "Patient rejected");
        globalAssert(s, "Event_0tqfzbc");
        BPMNExecProcessUtils.error(s, "Rejected", 1);
    }

    public void EVENT_Return_to_ward(BPMNExecProcessUtils.ProcessStatus s) {//End Event Return to ward [Event_0tlco0q]
        BPMNExecProcessUtils.debugOutput("End Event Return to ward [Event_0tlco0q]");
        BPMNExecProcessUtils.logCurrentNode("Event_0tlco0q", "Return to ward");
        globalAssert(s, "Event_0tlco0q");
        BPMNExecProcessUtils.error(s, "Return", 2);
    }

    public void GATEWAY_Any_adverse_event(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Any adverse event [Gateway_0uzvw4h]
        BPMNExecProcessUtils.debugOutput("Exclusive Gateway Any adverse event [Gateway_0uzvw4h]");
        BPMNExecProcessUtils.logCurrentNode("Gateway_0uzvw4h", "Any adverse event");
        globalAssert(s, "Gateway_0uzvw4h");
        if (AdverseEvents.equals(false)) {//[outgoing edge] Activity_01iv8eh - Preoperational patient preparation
            BPMNExecProcessUtils.logTransition("Gateway_0uzvw4h", "Activity_01iv8eh");
            TASK_Preoperational_patient_preparation(s.withCurrent("Gateway_0uzvw4h"));
        } else if (AdverseEvents.equals(true)) {//[outgoing edge] Event_0tlco0q - Return to ward
            BPMNExecProcessUtils.logTransition("Gateway_0uzvw4h", "Event_0tlco0q");
            EVENT_Return_to_ward(s.withCurrent("Gateway_0uzvw4h"));
        } else {
            BPMNExecProcessUtils.noDefaultCaseError(s);
        }
    }

    public void GATEWAY_Can_patient_leave_surgical_unit(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Can patient leave surgical unit [Gateway_058xy6d]
        BPMNExecProcessUtils.debugOutput("Exclusive Gateway Can patient leave surgical unit [Gateway_058xy6d]");
        BPMNExecProcessUtils.logCurrentNode("Gateway_058xy6d", "Can patient leave surgical unit");
        globalAssert(s, "Gateway_058xy6d");
        if (PatientDismissionSurgicalUnit.equals("can")) {//[outgoing edge] Event_01gw6pd - Return to ward
            BPMNExecProcessUtils.logTransition("Gateway_058xy6d", "Event_01gw6pd");
            EVENT_Return_to_ward(s.withCurrent("Gateway_058xy6d"));
        } else {//[outgoing edge] Activity_16nr0p7 - Patient dismission from surgical unit
            BPMNExecProcessUtils.logTransition("Gateway_058xy6d", "Activity_16nr0p7");
            TASK_Patient_dismission_from_surgical_unit(s.withCurrent("Gateway_058xy6d"));
        }
    }

    public void GATEWAY_ICU(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway ICU [Gateway_07brpq2]
        BPMNExecProcessUtils.debugOutput("Exclusive Gateway ICU [Gateway_07brpq2]");
        BPMNExecProcessUtils.logCurrentNode("Gateway_07brpq2", "ICU");
        globalAssert(s, "Gateway_07brpq2");
        if (ICU.equals(true)) {//[outgoing edge] Activity_0mqe4nv - Transport to ICU
            BPMNExecProcessUtils.logTransition("Gateway_07brpq2", "Activity_0mqe4nv");
            TASK_Transport_to_ICU(s.withCurrent("Gateway_07brpq2"));
        } else {//[outgoing edge] Activity_1uuxpml - Recovery room
            BPMNExecProcessUtils.logTransition("Gateway_07brpq2", "Activity_1uuxpml");
            TASK_Recovery_room(s.withCurrent("Gateway_07brpq2"));
        }
    }

    public void GATEWAY_Patient_sent_back_1(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Patient sent back 1 [Gateway_01rw5bk]
        BPMNExecProcessUtils.debugOutput("Exclusive Gateway Patient sent back 1 [Gateway_01rw5bk]");
        BPMNExecProcessUtils.logCurrentNode("Gateway_01rw5bk", "Patient sent back 1");
        globalAssert(s, "Gateway_01rw5bk");
        if (PreAnestheticEvaluation.equals(true)) {//[outgoing edge] Activity_1bfwy21 - Check adverse events
            BPMNExecProcessUtils.logTransition("Gateway_01rw5bk", "Activity_1bfwy21");
            TASK_Check_adverse_events(s.withCurrent("Gateway_01rw5bk"));
        } else if (PreAnestheticEvaluation.equals(false)) {//[outgoing edge] Event_0tlco0q - Return to ward
            BPMNExecProcessUtils.logTransition("Gateway_01rw5bk", "Event_0tlco0q");
            EVENT_Return_to_ward(s.withCurrent("Gateway_01rw5bk"));
        } else {
            BPMNExecProcessUtils.noDefaultCaseError(s);
        }
    }

    public void GATEWAY_Patient_sent_back_2(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Patient sent back 2 [Gateway_1s04agq]
        BPMNExecProcessUtils.debugOutput("Exclusive Gateway Patient sent back 2 [Gateway_1s04agq]");
        BPMNExecProcessUtils.logCurrentNode("Gateway_1s04agq", "Patient sent back 2");
        globalAssert(s, "Gateway_1s04agq");
        if (PatientAdmissionOperatingTheatre.equals("admitted")) {//[outgoing edge] Activity_0hywbyv - Surgery
            BPMNExecProcessUtils.logTransition("Gateway_1s04agq", "Activity_0hywbyv");
            TASK_Surgery(s.withCurrent("Gateway_1s04agq"));
        } else if (PatientAdmissionOperatingTheatre.equals("rejected")) {//[outgoing edge] Event_0tlco0q - Return to ward
            BPMNExecProcessUtils.logTransition("Gateway_1s04agq", "Event_0tlco0q");
            EVENT_Return_to_ward(s.withCurrent("Gateway_1s04agq"));
        } else {
            BPMNExecProcessUtils.noDefaultCaseError(s);
        }
    }

    public void GATEWAY_Recovery_room_SPLIT_GATEWAY(BPMNExecProcessUtils.ProcessStatus s) {//Inclusive Gateway Recovery room SPLIT GATEWAY [Activity_1uuxpmlXVG]
        BPMNExecProcessUtils.debugOutput("Inclusive Gateway Recovery room SPLIT GATEWAY [Activity_1uuxpmlXVG]");
        BPMNExecProcessUtils.logCurrentNode("Activity_1uuxpmlXVG", "Recovery room SPLIT GATEWAY");
        globalAssert(s, "Activity_1uuxpmlXVG");
        if (true) {//[outgoing edge] Gateway_058xy6d - Can patient leave surgical unit
            BPMNExecProcessUtils.logTransition("Activity_1uuxpmlXVG", "Gateway_058xy6d");
            GATEWAY_Can_patient_leave_surgical_unit(s.withCurrent("Activity_1uuxpmlXVG"));
        }
        if (true) {//[outgoing edge] Activity_16nr0p7 - Patient dismission from surgical unit
            BPMNExecProcessUtils.logTransition("Activity_1uuxpmlXVG", "Activity_16nr0p7");
            TASK_Patient_dismission_from_surgical_unit(s.withCurrent("Activity_1uuxpmlXVG"));
        }
    }

    public void GATEWAY_Triage(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Triage [Gateway_1hwej0w]
        BPMNExecProcessUtils.debugOutput("Exclusive Gateway Triage [Gateway_1hwej0w]");
        BPMNExecProcessUtils.logCurrentNode("Gateway_1hwej0w", "Triage");
        globalAssert(s, "Gateway_1hwej0w");
        if (PatientAdmission.equals("denied")) {//[outgoing edge] Event_0tqfzbc - Patient rejected
            BPMNExecProcessUtils.logTransition("Gateway_1hwej0w", "Event_0tqfzbc");
            EVENT_Patient_rejected(s.withCurrent("Gateway_1hwej0w"));
        } else {//[outgoing edge] Activity_1qr375k - Inser into surgeon waiting list
            BPMNExecProcessUtils.logTransition("Gateway_1hwej0w", "Activity_1qr375k");
            TASK_Inser_into_surgeon_waiting_list(s.withCurrent("Gateway_1hwej0w"));
        }
    }

    public void TASK_Check_adverse_events(BPMNExecProcessUtils.ProcessStatus s) {//User Task Check adverse events [Activity_1bfwy21]
        BPMNExecProcessUtils.debugOutput("User Task Check adverse events [Activity_1bfwy21]");
        BPMNExecProcessUtils.logCurrentNode("Activity_1bfwy21", "Check adverse events");
        globalAssert(s, "Activity_1bfwy21");
//[outgoing edge] Gateway_0uzvw4h - Any adverse event
        BPMNExecProcessUtils.logTransition("Activity_1bfwy21", "Gateway_0uzvw4h");
        GATEWAY_Any_adverse_event(s.withCurrent("Activity_1bfwy21"));
    }

    public void TASK_Inser_into_surgeon_waiting_list(BPMNExecProcessUtils.ProcessStatus s) {//Send Task Inser into surgeon waiting list [Activity_1qr375k]
        BPMNExecProcessUtils.debugOutput("Send Task Inser into surgeon waiting list [Activity_1qr375k]");
        BPMNExecProcessUtils.logCurrentNode("Activity_1qr375k", "Inser into surgeon waiting list");
        globalAssert(s, "Activity_1qr375k");
//[outgoing edge] Activity_0ba89kz - Wait for message from external software
        BPMNExecProcessUtils.logTransition("Activity_1qr375k", "Activity_0ba89kz");
        TASK_Wait_for_message_from_external_software(s.withCurrent("Activity_1qr375k"));
    }

    public void TASK_Parient_Admission(BPMNExecProcessUtils.ProcessStatus s) {//Business Rule Task Parient Admission [Activity_1mbfbnv]
        BPMNExecProcessUtils.debugOutput("Business Rule Task Parient Admission [Activity_1mbfbnv]");
        BPMNExecProcessUtils.logCurrentNode("Activity_1mbfbnv", "Parient Admission");
        globalAssert(s, "Activity_1mbfbnv");
        BPMNExecProcessUtils.debugOutput("	 EXECUTING DECISION Parient Admission");
        dmn_dtable_PatientAdmissionDT_arguments args = new dmn_dtable_PatientAdmissionDT_arguments();
        args.Hospitalizable = uHospitalizable;
        args.Emergency = uEmergency;
        args.Age = uAge;
        args.PreviouslyHospitalized = uPreviouslyHospitalized;
        dmn_dtable_PatientAdmissionDT_result PatientAdmissionResult = dmn_dtable_PatientAdmissionDT.execute(args);
        BPMNExecProcessUtils.debugOutput("	 DECISION RESULT IS %s", PatientAdmissionResult);
        PatientAdmission = PatientAdmissionResult.PatientAdmission;
        BPMNExecProcessUtils.debugOutput("	 ASSIGNING PatientAdmission TO %s", PatientAdmissionResult.PatientAdmission);
//[outgoing edge] Gateway_1hwej0w - Triage
        BPMNExecProcessUtils.logTransition("Activity_1mbfbnv", "Gateway_1hwej0w");
        GATEWAY_Triage(s.withCurrent("Activity_1mbfbnv"));
    }

    public void TASK_Patient_admission_to_operating_theatre(BPMNExecProcessUtils.ProcessStatus s) {//Business Rule Task Patient admission to operating theatre [Activity_0g3bnhh]
        BPMNExecProcessUtils.debugOutput("Business Rule Task Patient admission to operating theatre [Activity_0g3bnhh]");
        BPMNExecProcessUtils.logCurrentNode("Activity_0g3bnhh", "Patient admission to operating theatre");
        globalAssert(s, "Activity_0g3bnhh");
        BPMNExecProcessUtils.debugOutput("	 EXECUTING DECISION Patient admission to operating theatre");
        dmn_dtable_PatientAdmissionOperatingTheatreDT_arguments args = new dmn_dtable_PatientAdmissionOperatingTheatreDT_arguments();
        args.InformedConsent = uInformedConsent;
        args.PreoperationalChecklist = nPreoperationalChecklist;
        args.AdverseEvents = AdverseEvents;
        args.PreAnesthesicReEvaluation = PreAnesthesicReEvaluation;
        dmn_dtable_PatientAdmissionOperatingTheatreDT_result PatientAdmissionOperatingTheatreResult = dmn_dtable_PatientAdmissionOperatingTheatreDT.execute(args);
        BPMNExecProcessUtils.debugOutput("	 DECISION RESULT IS %s", PatientAdmissionOperatingTheatreResult);
        PatientAdmissionOperatingTheatre = PatientAdmissionOperatingTheatreResult.PatientAdmissionOperatingTheatre;
        BPMNExecProcessUtils.debugOutput("	 ASSIGNING PatientAdmissionOperatingTheatre TO %s", PatientAdmissionOperatingTheatreResult.PatientAdmissionOperatingTheatre);
//[outgoing edge] Gateway_1s04agq - Patient sent back 2
        BPMNExecProcessUtils.logTransition("Activity_0g3bnhh", "Gateway_1s04agq");
        GATEWAY_Patient_sent_back_2(s.withCurrent("Activity_0g3bnhh"));
    }

    public void TASK_Patient_discharge_from_operating_theater(BPMNExecProcessUtils.ProcessStatus s) {//User Task Patient discharge from operating theater [Activity_1v7bxvg]
        BPMNExecProcessUtils.debugOutput("User Task Patient discharge from operating theater [Activity_1v7bxvg]");
        BPMNExecProcessUtils.logCurrentNode("Activity_1v7bxvg", "Patient discharge from operating theater");
        globalAssert(s, "Activity_1v7bxvg");
//[outgoing edge] Gateway_07brpq2 - ICU
        BPMNExecProcessUtils.logTransition("Activity_1v7bxvg", "Gateway_07brpq2");
        GATEWAY_ICU(s.withCurrent("Activity_1v7bxvg"));
    }

    public void TASK_Patient_dismission_from_surgical_unit(BPMNExecProcessUtils.ProcessStatus s) {//Business Rule Task Patient dismission from surgical unit [Activity_16nr0p7]
        BPMNExecProcessUtils.debugOutput("Business Rule Task Patient dismission from surgical unit [Activity_16nr0p7]");
        BPMNExecProcessUtils.logCurrentNode("Activity_16nr0p7", "Patient dismission from surgical unit");
        globalAssert(s, "Activity_16nr0p7");
        BPMNExecProcessUtils.debugOutput("	 EXECUTING DECISION Patient dismission from surgical unit");
        dmn_dtable_PatientDismissionSurgicalUnitDT_arguments args = new dmn_dtable_PatientDismissionSurgicalUnitDT_arguments();
        args.AldreteScore = rAldreteScore;
        args.Activity = rActivity;
        args.Circulation = rCirculation;
        args.Consciousness = rConsciousness;
        args.O2Saturation = rO2Saturation;
        args.Respiration = rRespiration;
        dmn_dtable_PatientDismissionSurgicalUnitDT_result PatientDismissionSurgicalUnitResult = dmn_dtable_PatientDismissionSurgicalUnitDT.execute(args);
        BPMNExecProcessUtils.debugOutput("	 DECISION RESULT IS %s", PatientDismissionSurgicalUnitResult);
        PatientDismissionSurgicalUnit = PatientDismissionSurgicalUnitResult.PatientDismissionSurgicalUnit;
        BPMNExecProcessUtils.debugOutput("	 ASSIGNING PatientDismissionSurgicalUnit TO %s", PatientDismissionSurgicalUnitResult.PatientDismissionSurgicalUnit);
    }

    public void TASK_Pre_Anesthetic_evaluation(BPMNExecProcessUtils.ProcessStatus s) {//Business Rule Task Pre-Anesthetic evaluation [Activity_1o5csdt]
        BPMNExecProcessUtils.debugOutput("Business Rule Task Pre-Anesthetic evaluation [Activity_1o5csdt]");
        BPMNExecProcessUtils.logCurrentNode("Activity_1o5csdt", "Pre-Anesthetic evaluation");
        globalAssert(s, "Activity_1o5csdt");
        BPMNExecProcessUtils.debugOutput("	 EXECUTING DECISION Pre-Anesthetic evaluation");
        dmn_dtable_PreAnesthesicEvaluationDT_arguments args = new dmn_dtable_PreAnesthesicEvaluationDT_arguments();
        args.RequiredLaboratoryTests = pRequiredLaboratoryTests;
        args.RequiredDiagnosticTests = pRequiredDiagnosticTests;
        args.ClinicalConditions = pClinicalConditions;
        dmn_dtable_PreAnesthesicEvaluationDT_result PreAnesthesicEvaluationResult = dmn_dtable_PreAnesthesicEvaluationDT.execute(args);
        BPMNExecProcessUtils.debugOutput("	 DECISION RESULT IS %s", PreAnesthesicEvaluationResult);
        PreAnesthesicEvaluation = PreAnesthesicEvaluationResult.PreAnesthesicEvaluation;
        BPMNExecProcessUtils.debugOutput("	 ASSIGNING PreAnesthesicEvaluation TO %s", PreAnesthesicEvaluationResult.PreAnesthesicEvaluation);
//[outgoing edge] Gateway_01rw5bk - Patient sent back 1
        BPMNExecProcessUtils.logTransition("Activity_1o5csdt", "Gateway_01rw5bk");
        GATEWAY_Patient_sent_back_1(s.withCurrent("Activity_1o5csdt"));
    }

    public void TASK_Pre_Anesthetic_re_evaluation(BPMNExecProcessUtils.ProcessStatus s) {//Business Rule Task Pre-Anesthetic re-evaluation [Activity_1b92j4u]
        BPMNExecProcessUtils.debugOutput("Business Rule Task Pre-Anesthetic re-evaluation [Activity_1b92j4u]");
        BPMNExecProcessUtils.logCurrentNode("Activity_1b92j4u", "Pre-Anesthetic re-evaluation");
        globalAssert(s, "Activity_1b92j4u");
        BPMNExecProcessUtils.debugOutput("	 EXECUTING DECISION Pre-Anesthetic re-evaluation");
        dmn_dtable_PreAnesthesicReEvaluationDT_arguments args = new dmn_dtable_PreAnesthesicReEvaluationDT_arguments();
        args.RequiredLaboratoryTests = pRequiredLaboratoryTests;
        args.RequiredDiagnosticTests = pRequiredDiagnosticTests;
        args.ClinicalConditions = pClinicalConditions;
        args.PreAnesthesicEvaluation = PreAnesthesicEvaluation;
        dmn_dtable_PreAnesthesicReEvaluationDT_result PreAnesthesicReEvaluationResult = dmn_dtable_PreAnesthesicReEvaluationDT.execute(args);
        BPMNExecProcessUtils.debugOutput("	 DECISION RESULT IS %s", PreAnesthesicReEvaluationResult);
        PreAnesthesicReEvaluation = PreAnesthesicReEvaluationResult.PreAnesthesicReEvaluation;
        BPMNExecProcessUtils.debugOutput("	 ASSIGNING PreAnesthesicReEvaluation TO %s", PreAnesthesicReEvaluationResult.PreAnesthesicReEvaluation);
//[outgoing edge] Activity_0g3bnhh - Patient admission to operating theatre
        BPMNExecProcessUtils.logTransition("Activity_1b92j4u", "Activity_0g3bnhh");
        TASK_Patient_admission_to_operating_theatre(s.withCurrent("Activity_1b92j4u"));
    }

    public void TASK_Preoperational_patient_preparation(BPMNExecProcessUtils.ProcessStatus s) {//User Task Preoperational patient preparation [Activity_01iv8eh]
        BPMNExecProcessUtils.debugOutput("User Task Preoperational patient preparation [Activity_01iv8eh]");
        BPMNExecProcessUtils.logCurrentNode("Activity_01iv8eh", "Preoperational patient preparation");
        globalAssert(s, "Activity_01iv8eh");
//[outgoing edge] Activity_1b92j4u - Pre-Anesthetic re-evaluation
        BPMNExecProcessUtils.logTransition("Activity_01iv8eh", "Activity_1b92j4u");
        TASK_Pre_Anesthetic_re_evaluation(s.withCurrent("Activity_01iv8eh"));
    }

    public void TASK_Recovery_room(BPMNExecProcessUtils.ProcessStatus s) {//User Task Recovery room [Activity_1uuxpml]
        BPMNExecProcessUtils.debugOutput("User Task Recovery room [Activity_1uuxpml]");
        BPMNExecProcessUtils.logCurrentNode("Activity_1uuxpml", "Recovery room");
        globalAssert(s, "Activity_1uuxpml");
//[outgoing edge] Activity_1uuxpmlXVG - Recovery room SPLIT GATEWAY
        BPMNExecProcessUtils.logTransition("Activity_1uuxpml", "Activity_1uuxpmlXVG");
        GATEWAY_Recovery_room_SPLIT_GATEWAY(s.withCurrent("Activity_1uuxpml"));
    }

    public void TASK_Surgery(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Surgery [Activity_0hywbyv]
        BPMNExecProcessUtils.debugOutput("Generic Task Surgery [Activity_0hywbyv]");
        BPMNExecProcessUtils.logCurrentNode("Activity_0hywbyv", "Surgery");
        globalAssert(s, "Activity_0hywbyv");
//[outgoing edge] Activity_1v7bxvg - Patient discharge from operating theater
        BPMNExecProcessUtils.logTransition("Activity_0hywbyv", "Activity_1v7bxvg");
        TASK_Patient_discharge_from_operating_theater(s.withCurrent("Activity_0hywbyv"));
    }

    public void TASK_Transport_to_ICU(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Transport to ICU [Activity_0mqe4nv]
        BPMNExecProcessUtils.debugOutput("Generic Task Transport to ICU [Activity_0mqe4nv]");
        BPMNExecProcessUtils.logCurrentNode("Activity_0mqe4nv", "Transport to ICU");
        globalAssert(s, "Activity_0mqe4nv");
//[outgoing edge] Event_01gw6pd - Return to ward
        BPMNExecProcessUtils.logTransition("Activity_0mqe4nv", "Event_01gw6pd");
        EVENT_Return_to_ward(s.withCurrent("Activity_0mqe4nv"));
    }

    public void TASK_User_Info(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task User Info [Activity_0do0700]
        BPMNExecProcessUtils.debugOutput("Generic Task User Info [Activity_0do0700]");
        BPMNExecProcessUtils.logCurrentNode("Activity_0do0700", "User Info");
        globalAssert(s, "Activity_0do0700");
//[outgoing edge] Activity_1mbfbnv - Parient Admission
        BPMNExecProcessUtils.logTransition("Activity_0do0700", "Activity_1mbfbnv");
        TASK_Parient_Admission(s.withCurrent("Activity_0do0700"));
    }

    public void TASK_Wait_for_message_from_external_software(BPMNExecProcessUtils.ProcessStatus s) {//Receive Task Wait for message from external software [Activity_0ba89kz]
        BPMNExecProcessUtils.debugOutput("Receive Task Wait for message from external software [Activity_0ba89kz]");
        BPMNExecProcessUtils.logCurrentNode("Activity_0ba89kz", "Wait for message from external software");
        globalAssert(s, "Activity_0ba89kz");
//[outgoing edge] Activity_1o5csdt - Pre-Anesthetic evaluation
        BPMNExecProcessUtils.logTransition("Activity_0ba89kz", "Activity_1o5csdt");
        TASK_Pre_Anesthetic_evaluation(s.withCurrent("Activity_0ba89kz"));
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
        if (this.PreAnestheticEvaluation == null) {
            PreAnestheticEvaluation = BPMNExecProcessUtils.inputs.getProperty("PreAnestheticEvaluation", null);
        }
        BPMNExecProcessUtils.logInput("PreAnestheticEvaluation", this.PreAnestheticEvaluation);
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
//parallel join initializers

    }

    public boolean globalAssert(BPMNExecProcessUtils.ProcessStatus s, String node_id) {
        boolean success = true;

        return success;

    }

    public void execute(Object _uHospitalizable, Object _uEmergency, Object _uAge, Object _uPreviouslyHospitalized, Object _pRequiredLaboratoryTests, Object _pRequiredDiagnosticTests, Object _pClinicalConditions, Object _PreAnestheticEvaluation, Object _AdverseEvents, Object _uInformedConsent, Object _nPreoperationalChecklist, Object _ICU, Object _rAldreteScore, Object _rActivity, Object _rCirculation, Object _rConsciousness, Object _rO2Saturation, Object _rRespiration) {
        this.uHospitalizable = _uHospitalizable;
        this.uEmergency = _uEmergency;
        this.uAge = _uAge;
        this.uPreviouslyHospitalized = _uPreviouslyHospitalized;
        this.pRequiredLaboratoryTests = _pRequiredLaboratoryTests;
        this.pRequiredDiagnosticTests = _pRequiredDiagnosticTests;
        this.pClinicalConditions = _pClinicalConditions;
        this.PreAnestheticEvaluation = _PreAnestheticEvaluation;
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
        BPMNExecProcessUtils.enableTrueParallel();
        bpmn_process_Surgery process = new bpmn_process_Surgery();
        process.execute(null/*uHospitalizable*/, null/*uEmergency*/, null/*uAge*/, null/*uPreviouslyHospitalized*/, null/*pRequiredLaboratoryTests*/, null/*pRequiredDiagnosticTests*/, null/*pClinicalConditions*/, null/*PreAnestheticEvaluation*/, null/*AdverseEvents*/, null/*uInformedConsent*/, null/*nPreoperationalChecklist*/, null/*ICU*/, null/*rAldreteScore*/, null/*rActivity*/, null/*rCirculation*/, null/*rConsciousness*/, null/*rO2Saturation*/, null/*rRespiration*/);
    }
}

class Executor {

    public static void main(String[] args) {
        BPMNExecProcessUtils.enableTrueParallel();
        bpmn_process_Surgery process = new bpmn_process_Surgery();
        process.execute(null/*uHospitalizable*/, null/*uEmergency*/, null/*uAge*/, null/*uPreviouslyHospitalized*/, null/*pRequiredLaboratoryTests*/, null/*pRequiredDiagnosticTests*/, null/*pClinicalConditions*/, null/*PreAnestheticEvaluation*/, null/*AdverseEvents*/, null/*uInformedConsent*/, null/*nPreoperationalChecklist*/, null/*ICU*/, null/*rAldreteScore*/, null/*rActivity*/, null/*rCirculation*/, null/*rConsciousness*/, null/*rO2Saturation*/, null/*rRespiration*/);
    }
}
