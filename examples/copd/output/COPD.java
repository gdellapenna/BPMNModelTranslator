import dellapenna.personal.bpmn.exec.*;

/*
 * ****************************** DMN Generated Code *************************
 */
// wrapper class for the output of DMN table StagePatientDT
class dmn_dtable_StagePatientDT_result{Double stage;
public dmn_dtable_StagePatientDT_result(Double stage) {this.stage=stage;
}
public String toString() { String result="{"; result+="stage="+this.stage ;
return result+"}";}
}

// wrapper class for the input of DMN table StagePatientDT
class dmn_dtable_StagePatientDT_arguments{public Object BRTReport;
public Object analyzedResults;
public Object FEV1FVC;
}

// decision code for DMN table StagePatientDT
class dmn_dtable_StagePatientDT {

public static dmn_dtable_StagePatientDT_result execute(dmn_dtable_StagePatientDT_arguments args) {

Object BRTReport = args.BRTReport;
Object analyzedResults = args.analyzedResults;
Object FEV1FVC = args.FEV1FVC;

if (BPMNExecTypeUtils.tostring(BRTReport).equals("LevelA") && BPMNExecTypeUtils.tostring(analyzedResults).equals("GOOD") && BPMNExecTypeUtils.tostring(FEV1FVC).equals("Value1")) { return new dmn_dtable_StagePatientDT_result(/*stage*/1.0);} else if (BPMNExecTypeUtils.tostring(BRTReport).equals("LevelA") && BPMNExecTypeUtils.tostring(analyzedResults).equals("GOOD") && BPMNExecTypeUtils.tostring(FEV1FVC).equals("Value2")) { return new dmn_dtable_StagePatientDT_result(/*stage*/2.0);} else if (BPMNExecTypeUtils.tostring(BRTReport).equals("LevelA") && BPMNExecTypeUtils.tostring(analyzedResults).equals("BAD") && BPMNExecTypeUtils.tostring(FEV1FVC).equals("Value1")) { return new dmn_dtable_StagePatientDT_result(/*stage*/2.0);} else if (BPMNExecTypeUtils.tostring(BRTReport).equals("LevelA") && BPMNExecTypeUtils.tostring(analyzedResults).equals("BAD") && BPMNExecTypeUtils.tostring(FEV1FVC).equals("Value2")) { return new dmn_dtable_StagePatientDT_result(/*stage*/3.0);} else if (BPMNExecTypeUtils.tostring(BRTReport).equals("LevelB") && BPMNExecTypeUtils.tostring(analyzedResults).equals("GOOD") && BPMNExecTypeUtils.tostring(FEV1FVC).equals("Value1")) { return new dmn_dtable_StagePatientDT_result(/*stage*/3.0);} else if (BPMNExecTypeUtils.tostring(BRTReport).equals("LevelB") && BPMNExecTypeUtils.tostring(analyzedResults).equals("GOOD") && BPMNExecTypeUtils.tostring(FEV1FVC).equals("Value2")) { return new dmn_dtable_StagePatientDT_result(/*stage*/4.0);} else if (BPMNExecTypeUtils.tostring(BRTReport).equals("LevelB") && BPMNExecTypeUtils.tostring(analyzedResults).equals("BAD") && BPMNExecTypeUtils.tostring(FEV1FVC).equals("Value1")) { return new dmn_dtable_StagePatientDT_result(/*stage*/4.0);} else if (BPMNExecTypeUtils.tostring(BRTReport).equals("LevelB") && BPMNExecTypeUtils.tostring(analyzedResults).equals("BAD") && BPMNExecTypeUtils.tostring(FEV1FVC).equals("Value2")) { return new dmn_dtable_StagePatientDT_result(/*stage*/4.0);} else { BPMNExecProcessUtils.noDefaultCaseError(null);
 return null; }
}
}

// wrapper class for the output of DMN table InterpretSpiroResultsDT
class dmn_dtable_InterpretSpiroResultsDT_result{Boolean copd;
public dmn_dtable_InterpretSpiroResultsDT_result(Boolean copd) {this.copd=copd;
}
public String toString() { String result="{"; result+="copd="+this.copd ;
return result+"}";}
}

// wrapper class for the input of DMN table InterpretSpiroResultsDT
class dmn_dtable_InterpretSpiroResultsDT_arguments{public Object spirometryResult;
}

// decision code for DMN table InterpretSpiroResultsDT
class dmn_dtable_InterpretSpiroResultsDT {

public static dmn_dtable_InterpretSpiroResultsDT_result execute(dmn_dtable_InterpretSpiroResultsDT_arguments args) {

Object spirometryResult = args.spirometryResult;

if (BPMNExecTypeUtils.toboolean(spirometryResult).equals(true)) { return new dmn_dtable_InterpretSpiroResultsDT_result(/*copd*/true);} else if (BPMNExecTypeUtils.toboolean(spirometryResult).equals(false)) { return new dmn_dtable_InterpretSpiroResultsDT_result(/*copd*/false);} else { BPMNExecProcessUtils.noDefaultCaseError(null);
 return null; }
}
}
/*
 * ****************************** Process Code *************************
 */
 class COPD { 

//Input Variables
// READ: Activity_1gs1u22, $DMN$StagePatientDT$BRTReport
// WRITTEN: 
private Object BRTReport=null;
private final java.util.ArrayDeque<Object> BRTReport_stream=new java.util.ArrayDeque<>();
public Object getBRTReport(BPMNExecProcessUtils.ProcessStatus s, boolean readNext) {Object current = this.BRTReport;if (readNext && !this.BRTReport_stream.isEmpty()) {this.BRTReport = this.BRTReport_stream.pop();BPMNExecProcessUtils.debugOutput(s,"	 READING next input value for BRTReport");}return current;}
// READ: Activity_16mwits, Gateway_0y30j11
// WRITTEN: 
private Object COPDDiagnosed=null;
private final java.util.ArrayDeque<Object> COPDDiagnosed_stream=new java.util.ArrayDeque<>();
public Object getCOPDDiagnosed(BPMNExecProcessUtils.ProcessStatus s, boolean readNext) {Object current = this.COPDDiagnosed;if (readNext && !this.COPDDiagnosed_stream.isEmpty()) {this.COPDDiagnosed = this.COPDDiagnosed_stream.pop();BPMNExecProcessUtils.debugOutput(s,"	 READING next input value for COPDDiagnosed");}return current;}
// READ: Activity_1by7ohz, Gateway_1ifzb7i
// WRITTEN: 
private Object COPDSuspected=null;
private final java.util.ArrayDeque<Object> COPDSuspected_stream=new java.util.ArrayDeque<>();
public Object getCOPDSuspected(BPMNExecProcessUtils.ProcessStatus s, boolean readNext) {Object current = this.COPDSuspected;if (readNext && !this.COPDSuspected_stream.isEmpty()) {this.COPDSuspected = this.COPDSuspected_stream.pop();BPMNExecProcessUtils.debugOutput(s,"	 READING next input value for COPDSuspected");}return current;}
// READ: $DMN$StagePatientDT$FEV1FVC, Activity_1gs1u22
// WRITTEN: 
private Object FEV1FVC=null;
private final java.util.ArrayDeque<Object> FEV1FVC_stream=new java.util.ArrayDeque<>();
public Object getFEV1FVC(BPMNExecProcessUtils.ProcessStatus s, boolean readNext) {Object current = this.FEV1FVC;if (readNext && !this.FEV1FVC_stream.isEmpty()) {this.FEV1FVC = this.FEV1FVC_stream.pop();BPMNExecProcessUtils.debugOutput(s,"	 READING next input value for FEV1FVC");}return current;}
// READ: $DMN$StagePatientDT$analyzedResults, Activity_0vfbgoy, Activity_1gs1u22
// WRITTEN: 
private Object analyzedResults=null;
private final java.util.ArrayDeque<Object> analyzedResults_stream=new java.util.ArrayDeque<>();
public Object getAnalyzedResults(BPMNExecProcessUtils.ProcessStatus s, boolean readNext) {Object current = this.analyzedResults;if (readNext && !this.analyzedResults_stream.isEmpty()) {this.analyzedResults = this.analyzedResults_stream.pop();BPMNExecProcessUtils.debugOutput(s,"	 READING next input value for analyzedResults");}return current;}
// READ: Activity_0ck2kiz
// WRITTEN: 
private Object anamnesisReport=null;
private final java.util.ArrayDeque<Object> anamnesisReport_stream=new java.util.ArrayDeque<>();
public Object getAnamnesisReport(BPMNExecProcessUtils.ProcessStatus s, boolean readNext) {Object current = this.anamnesisReport;if (readNext && !this.anamnesisReport_stream.isEmpty()) {this.anamnesisReport = this.anamnesisReport_stream.pop();BPMNExecProcessUtils.debugOutput(s,"	 READING next input value for anamnesisReport");}return current;}
// READ: Gateway_0vpt9p6, Activity_0b5bgl9
// WRITTEN: 
private Object paCO2=null;
private final java.util.ArrayDeque<Object> paCO2_stream=new java.util.ArrayDeque<>();
public Object getPaCO2(BPMNExecProcessUtils.ProcessStatus s, boolean readNext) {Object current = this.paCO2;if (readNext && !this.paCO2_stream.isEmpty()) {this.paCO2 = this.paCO2_stream.pop();BPMNExecProcessUtils.debugOutput(s,"	 READING next input value for paCO2");}return current;}
// READ: Gateway_0vpt9p6, Activity_0b5bgl9
// WRITTEN: 
private Object paO2=null;
private final java.util.ArrayDeque<Object> paO2_stream=new java.util.ArrayDeque<>();
public Object getPaO2(BPMNExecProcessUtils.ProcessStatus s, boolean readNext) {Object current = this.paO2;if (readNext && !this.paO2_stream.isEmpty()) {this.paO2 = this.paO2_stream.pop();BPMNExecProcessUtils.debugOutput(s,"	 READING next input value for paO2");}return current;}
// READ: Activity_0f1noex, Gateway_04ijm8v
// WRITTEN: 
private Object patientAlreadyStaged=null;
private final java.util.ArrayDeque<Object> patientAlreadyStaged_stream=new java.util.ArrayDeque<>();
public Object getPatientAlreadyStaged(BPMNExecProcessUtils.ProcessStatus s, boolean readNext) {Object current = this.patientAlreadyStaged;if (readNext && !this.patientAlreadyStaged_stream.isEmpty()) {this.patientAlreadyStaged = this.patientAlreadyStaged_stream.pop();BPMNExecProcessUtils.debugOutput(s,"	 READING next input value for patientAlreadyStaged");}return current;}
// READ: Gateway_0ue46bu, Activity_0scqf41
// WRITTEN: 
private Object patientRequireHospitalization=null;
private final java.util.ArrayDeque<Object> patientRequireHospitalization_stream=new java.util.ArrayDeque<>();
public Object getPatientRequireHospitalization(BPMNExecProcessUtils.ProcessStatus s, boolean readNext) {Object current = this.patientRequireHospitalization;if (readNext && !this.patientRequireHospitalization_stream.isEmpty()) {this.patientRequireHospitalization = this.patientRequireHospitalization_stream.pop();BPMNExecProcessUtils.debugOutput(s,"	 READING next input value for patientRequireHospitalization");}return current;}
// READ: Gateway_0xkk1o6, Activity_0ck2kiz
// WRITTEN: 
private Object patientSmoker=null;
private final java.util.ArrayDeque<Object> patientSmoker_stream=new java.util.ArrayDeque<>();
public Object getPatientSmoker(BPMNExecProcessUtils.ProcessStatus s, boolean readNext) {Object current = this.patientSmoker;if (readNext && !this.patientSmoker_stream.isEmpty()) {this.patientSmoker = this.patientSmoker_stream.pop();BPMNExecProcessUtils.debugOutput(s,"	 READING next input value for patientSmoker");}return current;}
// READ: $DMN$InterpretSpiroResultsDT$spirometryResult, Activity_0vfbgoy, Activity_0wlmua1
// WRITTEN: 
private Object spirometryReport=null;
private final java.util.ArrayDeque<Object> spirometryReport_stream=new java.util.ArrayDeque<>();
public Object getSpirometryReport(BPMNExecProcessUtils.ProcessStatus s, boolean readNext) {Object current = this.spirometryReport;if (readNext && !this.spirometryReport_stream.isEmpty()) {this.spirometryReport = this.spirometryReport_stream.pop();BPMNExecProcessUtils.debugOutput(s,"	 READING next input value for spirometryReport");}return current;}


//Process Variables
// READ: Gateway_065vrg8
// WRITTEN: Activity_0vfbgoy
private Object spirometryCOPDSuggestive=null;
public Object getSpirometryCOPDSuggestive(BPMNExecProcessUtils.ProcessStatus s, boolean readNext) {return this.spirometryCOPDSuggestive; }
public void setSpirometryCOPDSuggestive(Object _value) {this.spirometryCOPDSuggestive=_value; }
// READ: Gateway_1vbgzvs
// WRITTEN: Activity_1gs1u22
private Object stage=null;
public Object getStage(BPMNExecProcessUtils.ProcessStatus s, boolean readNext) {return this.stage; }
public void setStage(Object _value) {this.stage=_value; }


//Messages
private static class Message_Scheduler_Message implements BPMNExecProcessUtils.Message {};



//Process Dynamics
public void EVENT_Event_0ltqr3s_Patient_request(BPMNExecProcessUtils.ProcessStatus s) {//Start Event Patient request [Event_0ltqr3s]
BPMNExecProcessUtils.debugOutput(s,"Start Event Patient request [Event_0ltqr3s]");
BPMNExecProcessUtils.logCurrentNode("Event_0ltqr3s","Patient request");
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Activity_0f1noex - Evaluate request
BPMNExecProcessUtils.logTransition("Event_0ltqr3s","Activity_0f1noex");
TASK_Activity_0f1noex_Evaluate_request(s.withCurrent("Event_0ltqr3s"));}
}

public void EVENT_Event_1e4tr3y(BPMNExecProcessUtils.ProcessStatus s) {//End Event Event_1e4tr3y
BPMNExecProcessUtils.debugOutput(s,"End Event Event_1e4tr3y");
BPMNExecProcessUtils.logCurrentNode("Event_1e4tr3y",null);
BPMNExecProcessUtils.success(s);
}

public void EVENT_Event_1faj9xw(BPMNExecProcessUtils.ProcessStatus s) {//End Event Event_1faj9xw
BPMNExecProcessUtils.debugOutput(s,"End Event Event_1faj9xw");
BPMNExecProcessUtils.logCurrentNode("Event_1faj9xw",null);
BPMNExecProcessUtils.success(s);
}

public void GATEWAY_Gateway_04ijm8v_Is_the_patient_already_staged_(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Is the patient already staged? [Gateway_04ijm8v]
BPMNExecProcessUtils.debugOutput(s,"Exclusive Gateway Is the patient already staged? [Gateway_04ijm8v]");
BPMNExecProcessUtils.logCurrentNode("Gateway_04ijm8v","Is the patient already staged?");
if (BPMNExecTypeUtils.equals(getPatientAlreadyStaged(s,false),false)){if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Activity_0ck2kiz - Examine and Interview patient
BPMNExecProcessUtils.logTransition("Gateway_04ijm8v","Activity_0ck2kiz");
TASK_Activity_0ck2kiz_Examine_and_Interview_patient(s.withCurrent("Gateway_04ijm8v"));}
} else if (BPMNExecTypeUtils.equals(getPatientAlreadyStaged(s,false),true)){if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Activity_0scqf41 - Evaluate hospitalization
BPMNExecProcessUtils.logTransition("Gateway_04ijm8v","Activity_0scqf41");
TASK_Activity_0scqf41_Evaluate_hospitalization(s.withCurrent("Gateway_04ijm8v"));}
} else { BPMNExecProcessUtils.noDefaultCaseError(s); }
}

public void GATEWAY_Gateway_065vrg8_Spirometry_suggestive_of_COPD_(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Spirometry suggestive of COPD? [Gateway_065vrg8]
BPMNExecProcessUtils.debugOutput(s,"Exclusive Gateway Spirometry suggestive of COPD? [Gateway_065vrg8]");
BPMNExecProcessUtils.logCurrentNode("Gateway_065vrg8","Spirometry suggestive of COPD?");
if (BPMNExecTypeUtils.equals(getSpirometryCOPDSuggestive(s,false),true)){if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Activity_182mdyp - Conduct global spirometry
BPMNExecProcessUtils.logTransition("Gateway_065vrg8","Activity_182mdyp");
TASK_Activity_182mdyp_Conduct_global_spirometry(s.withCurrent("Gateway_065vrg8"));}
} else if (BPMNExecTypeUtils.equals(getSpirometryCOPDSuggestive(s,false),false)){if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Gateway_06mnf24
BPMNExecProcessUtils.logTransition("Gateway_065vrg8","Gateway_06mnf24");
GATEWAY_Gateway_06mnf24(s.withCurrent("Gateway_065vrg8"));}
} else { BPMNExecProcessUtils.noDefaultCaseError(s); }
}

public void GATEWAY_Gateway_06mnf24(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Joining Gateway Gateway_06mnf24
BPMNExecProcessUtils.debugOutput(s,"Exclusive Joining Gateway Gateway_06mnf24");
BPMNExecProcessUtils.logCurrentNode("Gateway_06mnf24",null);
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Event_1e4tr3y
BPMNExecProcessUtils.logTransition("Gateway_06mnf24","Event_1e4tr3y");
EVENT_Event_1e4tr3y(s.withCurrent("Gateway_06mnf24"));}
}

public void GATEWAY_Gateway_07g2otl(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Joining Gateway Gateway_07g2otl
BPMNExecProcessUtils.debugOutput(s,"Exclusive Joining Gateway Gateway_07g2otl");
BPMNExecProcessUtils.logCurrentNode("Gateway_07g2otl",null);
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Gateway_1lnx0yn
BPMNExecProcessUtils.logTransition("Gateway_07g2otl","Gateway_1lnx0yn");
GATEWAY_Gateway_1lnx0yn(s.withCurrent("Gateway_07g2otl"));}
}

public void GATEWAY_Gateway_0dbyzwb(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Joining Gateway Gateway_0dbyzwb
BPMNExecProcessUtils.debugOutput(s,"Exclusive Joining Gateway Gateway_0dbyzwb");
BPMNExecProcessUtils.logCurrentNode("Gateway_0dbyzwb",null);
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Activity_19eeyaj - Plan an examination
BPMNExecProcessUtils.logTransition("Gateway_0dbyzwb","Activity_19eeyaj");
TASK_Activity_19eeyaj_Plan_an_examination(s.withCurrent("Gateway_0dbyzwb"));}
}

public void GATEWAY_Gateway_0ue46bu_Does_patient_require_hospitalization_(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Does patient require hospitalization? [Gateway_0ue46bu]
BPMNExecProcessUtils.debugOutput(s,"Exclusive Gateway Does patient require hospitalization? [Gateway_0ue46bu]");
BPMNExecProcessUtils.logCurrentNode("Gateway_0ue46bu","Does patient require hospitalization?");
if (BPMNExecTypeUtils.equals(getPatientRequireHospitalization(s,false),false)){if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Activity_0xf0odk - Prescribe therapy
BPMNExecProcessUtils.logTransition("Gateway_0ue46bu","Activity_0xf0odk");
TASK_Activity_0xf0odk_Prescribe_therapy(s.withCurrent("Gateway_0ue46bu"));}
} else if (BPMNExecTypeUtils.equals(getPatientRequireHospitalization(s,false),true)){if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Activity_1pl6nde - Treat exacerbations
BPMNExecProcessUtils.logTransition("Gateway_0ue46bu","Activity_1pl6nde");
TASK_Activity_1pl6nde_Treat_exacerbations(s.withCurrent("Gateway_0ue46bu"));}
} else { BPMNExecProcessUtils.noDefaultCaseError(s); }
}

public void GATEWAY_Gateway_0vpt9p6_PaO2_60mmHg_OR_PaCO2_45mmHg_(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway PaO2<60mmHg OR PaCO2>45mmHg? [Gateway_0vpt9p6]
BPMNExecProcessUtils.debugOutput(s,"Exclusive Gateway PaO2<60mmHg OR PaCO2>45mmHg? [Gateway_0vpt9p6]");
BPMNExecProcessUtils.logCurrentNode("Gateway_0vpt9p6","PaO2<60mmHg OR PaCO2>45mmHg?");
if ((BPMNExecTypeUtils.toboolean(BPMNExecTypeUtils.tonumber(getPaO2(s,false)) < BPMNExecTypeUtils.tonumber(60.0)) || BPMNExecTypeUtils.toboolean(BPMNExecTypeUtils.tonumber(getPaCO2(s,false)) > BPMNExecTypeUtils.tonumber(45.0)))){if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Activity_1x6l1dw - Prescribe oxygen therapy
BPMNExecProcessUtils.logTransition("Gateway_0vpt9p6","Activity_1x6l1dw");
TASK_Activity_1x6l1dw_Prescribe_oxygen_therapy(s.withCurrent("Gateway_0vpt9p6"));}
} else if (BPMNExecTypeUtils.toboolean(BPMNExecTypeUtils.tonumber(getPaO2(s,false)) >= BPMNExecTypeUtils.tonumber(60.0)) && BPMNExecTypeUtils.toboolean(BPMNExecTypeUtils.tonumber(getPaCO2(s,false)) <= BPMNExecTypeUtils.tonumber(45.0))){if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Gateway_07g2otl
BPMNExecProcessUtils.logTransition("Gateway_0vpt9p6","Gateway_07g2otl");
GATEWAY_Gateway_07g2otl(s.withCurrent("Gateway_0vpt9p6"));}
} else { BPMNExecProcessUtils.noDefaultCaseError(s); }
}

public void GATEWAY_Gateway_0x0ocwc(BPMNExecProcessUtils.ProcessStatus s) {//Parallel Joining Gateway Gateway_0x0ocwc
BPMNExecProcessUtils.debugOutput(s,"Parallel Joining Gateway Gateway_0x0ocwc");
BPMNExecProcessUtils.logCurrentNode("Gateway_0x0ocwc",null);
//[outgoing edge] Activity_053g4bq - Conduct pulmonary examination
BPMNExecProcessUtils.logTransition("Gateway_0x0ocwc","Activity_053g4bq");
//JOINS: Activity_089tja4,Gateway_197n966
BPMNExecProcessUtils.join(s,"Gateway_0x0ocwc", this::TASK_Activity_053g4bq_Conduct_pulmonary_examination);
}

public void GATEWAY_Gateway_0xkk1o6_Is_the_patient_a_smoker_(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Is the patient a smoker? [Gateway_0xkk1o6]
BPMNExecProcessUtils.debugOutput(s,"Exclusive Gateway Is the patient a smoker? [Gateway_0xkk1o6]");
BPMNExecProcessUtils.logCurrentNode("Gateway_0xkk1o6","Is the patient a smoker?");
if (BPMNExecTypeUtils.equals(getPatientSmoker(s,false),true)){if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Activity_0a2awzt - Smoking tests
BPMNExecProcessUtils.logTransition("Gateway_0xkk1o6","Activity_0a2awzt");
TASK_Activity_0a2awzt_Smoking_tests(s.withCurrent("Gateway_0xkk1o6"));}
} else if (BPMNExecTypeUtils.equals(getPatientSmoker(s,false),false)){if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Gateway_197n966
BPMNExecProcessUtils.logTransition("Gateway_0xkk1o6","Gateway_197n966");
GATEWAY_Gateway_197n966(s.withCurrent("Gateway_0xkk1o6"));}
} else { BPMNExecProcessUtils.noDefaultCaseError(s); }
}

public void GATEWAY_Gateway_0y30j11_COPD_diagnosed_(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway COPD diagnosed? [Gateway_0y30j11]
BPMNExecProcessUtils.debugOutput(s,"Exclusive Gateway COPD diagnosed? [Gateway_0y30j11]");
BPMNExecProcessUtils.logCurrentNode("Gateway_0y30j11","COPD diagnosed?");
if (BPMNExecTypeUtils.equals(getCOPDDiagnosed(s,false),false)){if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Gateway_06mnf24
BPMNExecProcessUtils.logTransition("Gateway_0y30j11","Gateway_06mnf24");
GATEWAY_Gateway_06mnf24(s.withCurrent("Gateway_0y30j11"));}
} else if (BPMNExecTypeUtils.equals(getCOPDDiagnosed(s,false),true)){if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Gateway_1ik5zts
BPMNExecProcessUtils.logTransition("Gateway_0y30j11","Gateway_1ik5zts");
GATEWAY_Gateway_1ik5zts(s.withCurrent("Gateway_0y30j11"));}
} else { BPMNExecProcessUtils.noDefaultCaseError(s); }
}

public void GATEWAY_Gateway_197n966(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Joining Gateway Gateway_197n966
BPMNExecProcessUtils.debugOutput(s,"Exclusive Joining Gateway Gateway_197n966");
BPMNExecProcessUtils.logCurrentNode("Gateway_197n966",null);
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Gateway_0x0ocwc
BPMNExecProcessUtils.logTransition("Gateway_197n966","Gateway_0x0ocwc");
GATEWAY_Gateway_0x0ocwc(s.withCurrent("Gateway_197n966"));}
}

public void GATEWAY_Gateway_1ifzb7i_COPD_suspected_(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway COPD suspected? [Gateway_1ifzb7i]
BPMNExecProcessUtils.debugOutput(s,"Exclusive Gateway COPD suspected? [Gateway_1ifzb7i]");
BPMNExecProcessUtils.logCurrentNode("Gateway_1ifzb7i","COPD suspected?");
if (BPMNExecTypeUtils.equals(getCOPDSuspected(s,false),true)){if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Activity_0wlmua1 - Conduct simple spirometry
BPMNExecProcessUtils.logTransition("Gateway_1ifzb7i","Activity_0wlmua1");
TASK_Activity_0wlmua1_Conduct_simple_spirometry(s.withCurrent("Gateway_1ifzb7i"));}
} else if (BPMNExecTypeUtils.equals(getCOPDSuspected(s,false),false)){if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Gateway_06mnf24
BPMNExecProcessUtils.logTransition("Gateway_1ifzb7i","Gateway_06mnf24");
GATEWAY_Gateway_06mnf24(s.withCurrent("Gateway_1ifzb7i"));}
} else { BPMNExecProcessUtils.noDefaultCaseError(s); }
}

public void GATEWAY_Gateway_1ik5zts(BPMNExecProcessUtils.ProcessStatus s) {//Parallel Gateway Gateway_1ik5zts
BPMNExecProcessUtils.debugOutput(s,"Parallel Gateway Gateway_1ik5zts");
BPMNExecProcessUtils.logCurrentNode("Gateway_1ik5zts",null);
//[outgoing edge] Activity_089tja4 - Conduct CAT
BPMNExecProcessUtils.logTransition("Gateway_1ik5zts","Activity_089tja4");
//[outgoing edge] Gateway_0xkk1o6 - Is the patient a smoker?
BPMNExecProcessUtils.logTransition("Gateway_1ik5zts","Gateway_0xkk1o6");
//FORKS: Activity_089tja4,Gateway_0xkk1o6
BPMNExecProcessUtils.fork(s,"Gateway_1ik5zts",this::TASK_Activity_089tja4_Conduct_CAT,this::GATEWAY_Gateway_0xkk1o6_Is_the_patient_a_smoker_);
BPMNExecProcessUtils.stopThread();
}

public void GATEWAY_Gateway_1lnx0yn(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Joining Gateway Gateway_1lnx0yn
BPMNExecProcessUtils.debugOutput(s,"Exclusive Joining Gateway Gateway_1lnx0yn");
BPMNExecProcessUtils.logCurrentNode("Gateway_1lnx0yn",null);
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Gateway_06mnf24
BPMNExecProcessUtils.logTransition("Gateway_1lnx0yn","Gateway_06mnf24");
GATEWAY_Gateway_06mnf24(s.withCurrent("Gateway_1lnx0yn"));}
}

public void GATEWAY_Gateway_1vbgzvs(BPMNExecProcessUtils.ProcessStatus s) {//Exclusive Gateway Gateway_1vbgzvs
BPMNExecProcessUtils.debugOutput(s,"Exclusive Gateway Gateway_1vbgzvs");
BPMNExecProcessUtils.logCurrentNode("Gateway_1vbgzvs",null);
if (BPMNExecTypeUtils.toboolean(BPMNExecTypeUtils.tonumber(getStage(s,false)) >= BPMNExecTypeUtils.tonumber(1.0)) && BPMNExecTypeUtils.toboolean(BPMNExecTypeUtils.tonumber(getStage(s,false)) <= BPMNExecTypeUtils.tonumber(2.0))){if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Gateway_1lnx0yn
BPMNExecProcessUtils.logTransition("Gateway_1vbgzvs","Gateway_1lnx0yn");
GATEWAY_Gateway_1lnx0yn(s.withCurrent("Gateway_1vbgzvs"));}
} else if (BPMNExecTypeUtils.toboolean(BPMNExecTypeUtils.tonumber(getStage(s,false)) >= BPMNExecTypeUtils.tonumber(3.0)) && BPMNExecTypeUtils.toboolean(BPMNExecTypeUtils.tonumber(getStage(s,false)) <= BPMNExecTypeUtils.tonumber(4.0))){if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Activity_0b5bgl9 - Conduct BGA
BPMNExecProcessUtils.logTransition("Gateway_1vbgzvs","Activity_0b5bgl9");
TASK_Activity_0b5bgl9_Conduct_BGA(s.withCurrent("Gateway_1vbgzvs"));}
} else { BPMNExecProcessUtils.noDefaultCaseError(s); }
}

public void TASK_Activity_053g4bq_Conduct_pulmonary_examination(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Conduct pulmonary examination [Activity_053g4bq]
BPMNExecProcessUtils.debugOutput(s,"Generic Task Conduct pulmonary examination [Activity_053g4bq]");
BPMNExecProcessUtils.logCurrentNode("Activity_053g4bq","Conduct pulmonary examination");
//do something
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Activity_0jrklqn - Test bronchodilator reversibility
BPMNExecProcessUtils.logTransition("Activity_053g4bq","Activity_0jrklqn");
TASK_Activity_0jrklqn_Test_bronchodilator_reversibility(s.withCurrent("Activity_053g4bq"));}
}

public void TASK_Activity_089tja4_Conduct_CAT(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Conduct CAT [Activity_089tja4]
BPMNExecProcessUtils.debugOutput(s,"Generic Task Conduct CAT [Activity_089tja4]");
BPMNExecProcessUtils.logCurrentNode("Activity_089tja4","Conduct CAT");
//do something
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Gateway_0x0ocwc
BPMNExecProcessUtils.logTransition("Activity_089tja4","Gateway_0x0ocwc");
GATEWAY_Gateway_0x0ocwc(s.withCurrent("Activity_089tja4"));}
}

public void TASK_Activity_0a2awzt_Smoking_tests(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Smoking tests [Activity_0a2awzt]
BPMNExecProcessUtils.debugOutput(s,"Generic Task Smoking tests [Activity_0a2awzt]");
BPMNExecProcessUtils.logCurrentNode("Activity_0a2awzt","Smoking tests");
//do something
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Gateway_197n966
BPMNExecProcessUtils.logTransition("Activity_0a2awzt","Gateway_197n966");
GATEWAY_Gateway_197n966(s.withCurrent("Activity_0a2awzt"));}
}

public void TASK_Activity_0b5bgl9_Conduct_BGA(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Conduct BGA [Activity_0b5bgl9]
BPMNExecProcessUtils.debugOutput(s,"Generic Task Conduct BGA [Activity_0b5bgl9]");
BPMNExecProcessUtils.logCurrentNode("Activity_0b5bgl9","Conduct BGA");
//do something
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Gateway_0vpt9p6 - PaO2<60mmHg OR PaCO2>45mmHg?
BPMNExecProcessUtils.logTransition("Activity_0b5bgl9","Gateway_0vpt9p6");
GATEWAY_Gateway_0vpt9p6_PaO2_60mmHg_OR_PaCO2_45mmHg_(s.withCurrent("Activity_0b5bgl9"));}
}

public void TASK_Activity_0ck2kiz_Examine_and_Interview_patient(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Examine and Interview patient [Activity_0ck2kiz]
BPMNExecProcessUtils.debugOutput(s,"Generic Task Examine and Interview patient [Activity_0ck2kiz]");
BPMNExecProcessUtils.logCurrentNode("Activity_0ck2kiz","Examine and Interview patient");
//do something
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Activity_1by7ohz - Make working diagnosis
BPMNExecProcessUtils.logTransition("Activity_0ck2kiz","Activity_1by7ohz");
TASK_Activity_1by7ohz_Make_working_diagnosis(s.withCurrent("Activity_0ck2kiz"));}
}

public void TASK_Activity_0f1noex_Evaluate_request(BPMNExecProcessUtils.ProcessStatus s) {//User Task Evaluate request [Activity_0f1noex]
BPMNExecProcessUtils.debugOutput(s,"User Task Evaluate request [Activity_0f1noex]");
BPMNExecProcessUtils.logCurrentNode("Activity_0f1noex","Evaluate request");
//do something
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Gateway_04ijm8v - Is the patient already staged?
BPMNExecProcessUtils.logTransition("Activity_0f1noex","Gateway_04ijm8v");
GATEWAY_Gateway_04ijm8v_Is_the_patient_already_staged_(s.withCurrent("Activity_0f1noex"));}
}

public void TASK_Activity_0jrklqn_Test_bronchodilator_reversibility(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Test bronchodilator reversibility [Activity_0jrklqn]
BPMNExecProcessUtils.debugOutput(s,"Generic Task Test bronchodilator reversibility [Activity_0jrklqn]");
BPMNExecProcessUtils.logCurrentNode("Activity_0jrklqn","Test bronchodilator reversibility");
//do something
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Activity_1gs1u22 - Stage patient
BPMNExecProcessUtils.logTransition("Activity_0jrklqn","Activity_1gs1u22");
TASK_Activity_1gs1u22_Stage_patient(s.withCurrent("Activity_0jrklqn"));}
}

public void TASK_Activity_0scqf41_Evaluate_hospitalization(BPMNExecProcessUtils.ProcessStatus s) {//User Task Evaluate hospitalization [Activity_0scqf41]
BPMNExecProcessUtils.debugOutput(s,"User Task Evaluate hospitalization [Activity_0scqf41]");
BPMNExecProcessUtils.logCurrentNode("Activity_0scqf41","Evaluate hospitalization");
//do something
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Gateway_0ue46bu - Does patient require hospitalization?
BPMNExecProcessUtils.logTransition("Activity_0scqf41","Gateway_0ue46bu");
GATEWAY_Gateway_0ue46bu_Does_patient_require_hospitalization_(s.withCurrent("Activity_0scqf41"));}
}

public void TASK_Activity_0vfbgoy_Interpret_results(BPMNExecProcessUtils.ProcessStatus s) {//Business Rule Task Interpret results [Activity_0vfbgoy]
BPMNExecProcessUtils.debugOutput(s,"Business Rule Task Interpret results [Activity_0vfbgoy]");
BPMNExecProcessUtils.logCurrentNode("Activity_0vfbgoy","Interpret results");
BPMNExecProcessUtils.debugOutput(s,"	 EXECUTING DECISION Interpret results");
dmn_dtable_InterpretSpiroResultsDT_arguments args = new dmn_dtable_InterpretSpiroResultsDT_arguments();
args.spirometryResult = getSpirometryReport(s,false);
dmn_dtable_InterpretSpiroResultsDT_result interpretSpiroResultsResult=dmn_dtable_InterpretSpiroResultsDT.execute(args);
BPMNExecProcessUtils.debugOutput(s,"	 DECISION RESULT IS %s",interpretSpiroResultsResult);
setSpirometryCOPDSuggestive(interpretSpiroResultsResult.copd);
BPMNExecProcessUtils.debugOutput(s,"	 ASSIGNED spirometryCOPDSuggestive TO %s",spirometryCOPDSuggestive);
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Gateway_065vrg8 - Spirometry suggestive of COPD?
BPMNExecProcessUtils.logTransition("Activity_0vfbgoy","Gateway_065vrg8");
GATEWAY_Gateway_065vrg8_Spirometry_suggestive_of_COPD_(s.withCurrent("Activity_0vfbgoy"));}
}

public void TASK_Activity_0wlmua1_Conduct_simple_spirometry(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Conduct simple spirometry [Activity_0wlmua1]
BPMNExecProcessUtils.debugOutput(s,"Generic Task Conduct simple spirometry [Activity_0wlmua1]");
BPMNExecProcessUtils.logCurrentNode("Activity_0wlmua1","Conduct simple spirometry");
//do something
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Activity_0vfbgoy - Interpret results
BPMNExecProcessUtils.logTransition("Activity_0wlmua1","Activity_0vfbgoy");
TASK_Activity_0vfbgoy_Interpret_results(s.withCurrent("Activity_0wlmua1"));}
}

public void TASK_Activity_0xf0odk_Prescribe_therapy(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Prescribe therapy [Activity_0xf0odk]
BPMNExecProcessUtils.debugOutput(s,"Generic Task Prescribe therapy [Activity_0xf0odk]");
BPMNExecProcessUtils.logCurrentNode("Activity_0xf0odk","Prescribe therapy");
//do something
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Gateway_0dbyzwb
BPMNExecProcessUtils.logTransition("Activity_0xf0odk","Gateway_0dbyzwb");
GATEWAY_Gateway_0dbyzwb(s.withCurrent("Activity_0xf0odk"));}
}

public void TASK_Activity_16mwits_Make_diagnosis(BPMNExecProcessUtils.ProcessStatus s) {//User Task Make diagnosis [Activity_16mwits]
BPMNExecProcessUtils.debugOutput(s,"User Task Make diagnosis [Activity_16mwits]");
BPMNExecProcessUtils.logCurrentNode("Activity_16mwits","Make diagnosis");
//do something
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Gateway_0y30j11 - COPD diagnosed?
BPMNExecProcessUtils.logTransition("Activity_16mwits","Gateway_0y30j11");
GATEWAY_Gateway_0y30j11_COPD_diagnosed_(s.withCurrent("Activity_16mwits"));}
}

public void TASK_Activity_182mdyp_Conduct_global_spirometry(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Conduct global spirometry [Activity_182mdyp]
BPMNExecProcessUtils.debugOutput(s,"Generic Task Conduct global spirometry [Activity_182mdyp]");
BPMNExecProcessUtils.logCurrentNode("Activity_182mdyp","Conduct global spirometry");
//do something
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Activity_16mwits - Make diagnosis
BPMNExecProcessUtils.logTransition("Activity_182mdyp","Activity_16mwits");
TASK_Activity_16mwits_Make_diagnosis(s.withCurrent("Activity_182mdyp"));}
}

public void TASK_Activity_19eeyaj_Plan_an_examination(BPMNExecProcessUtils.ProcessStatus s) {BPMNExecProcessUtils.forkBoundaryWatch(s, "Activity_19eeyaj",this::TASK_Activity_19eeyaj_Plan_an_examination_Standard,this::TASK_Activity_19eeyaj_Plan_an_examination_Boundary);
BPMNExecProcessUtils.stopThread();
}

public void TASK_Activity_19eeyaj_Plan_an_examination_Boundary(BPMNExecProcessUtils.ProcessStatus s) {try{ while(true) {BPMNExecProcessUtils.debugOutput(s,"	 CHECKING message on channel scheduler");Message_Scheduler_Message receivedMessage = (Message_Scheduler_Message)BPMNExecProcessUtils.receiveMessage(s,"scheduler",50,false);if (receivedMessage != null){BPMNExecProcessUtils.debugOutput(s,"	 BOUNDARY EVENT Free time slot ON ACTIVITY User Task Plan an examination [Activity_19eeyaj] HIT");BPMNExecProcessUtils.resolveBoundaryWatch(s, true);
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Activity_1nq5pji - Refresh calendar
BPMNExecProcessUtils.logTransition("Activity_19eeyaj","Activity_1nq5pji");
TASK_Activity_1nq5pji_Refresh_calendar(s.withCurrent("Activity_19eeyaj"));}
break; }} } catch(InterruptedException e) { }
}

public void TASK_Activity_19eeyaj_Plan_an_examination_Standard(BPMNExecProcessUtils.ProcessStatus s) {//User Task Plan an examination [Activity_19eeyaj]
BPMNExecProcessUtils.debugOutput(s,"User Task Plan an examination [Activity_19eeyaj]");
BPMNExecProcessUtils.logCurrentNode("Activity_19eeyaj","Plan an examination");
//do something
BPMNExecProcessUtils.resolveBoundaryWatch(s, false);
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Gateway_06mnf24
BPMNExecProcessUtils.logTransition("Activity_19eeyaj","Gateway_06mnf24");
GATEWAY_Gateway_06mnf24(s.withCurrent("Activity_19eeyaj"));}
}

public void TASK_Activity_1by7ohz_Make_working_diagnosis(BPMNExecProcessUtils.ProcessStatus s) {//User Task Make working diagnosis [Activity_1by7ohz]
BPMNExecProcessUtils.debugOutput(s,"User Task Make working diagnosis [Activity_1by7ohz]");
BPMNExecProcessUtils.logCurrentNode("Activity_1by7ohz","Make working diagnosis");
//do something
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Gateway_1ifzb7i - COPD suspected?
BPMNExecProcessUtils.logTransition("Activity_1by7ohz","Gateway_1ifzb7i");
GATEWAY_Gateway_1ifzb7i_COPD_suspected_(s.withCurrent("Activity_1by7ohz"));}
}

public void TASK_Activity_1gs1u22_Stage_patient(BPMNExecProcessUtils.ProcessStatus s) {//Business Rule Task Stage patient [Activity_1gs1u22]
BPMNExecProcessUtils.debugOutput(s,"Business Rule Task Stage patient [Activity_1gs1u22]");
BPMNExecProcessUtils.logCurrentNode("Activity_1gs1u22","Stage patient");
BPMNExecProcessUtils.debugOutput(s,"	 EXECUTING DECISION Stage patient");
dmn_dtable_StagePatientDT_arguments args = new dmn_dtable_StagePatientDT_arguments();
args.BRTReport = getBRTReport(s,false);
args.analyzedResults = getAnalyzedResults(s,false);
args.FEV1FVC = getFEV1FVC(s,false);
dmn_dtable_StagePatientDT_result stagePatientResult=dmn_dtable_StagePatientDT.execute(args);
BPMNExecProcessUtils.debugOutput(s,"	 DECISION RESULT IS %s",stagePatientResult);
setStage(stagePatientResult.stage);
BPMNExecProcessUtils.debugOutput(s,"	 ASSIGNED stage TO %s",stage);
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Gateway_1vbgzvs
BPMNExecProcessUtils.logTransition("Activity_1gs1u22","Gateway_1vbgzvs");
GATEWAY_Gateway_1vbgzvs(s.withCurrent("Activity_1gs1u22"));}
}

public void TASK_Activity_1nq5pji_Refresh_calendar(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Refresh calendar [Activity_1nq5pji]
BPMNExecProcessUtils.debugOutput(s,"Generic Task Refresh calendar [Activity_1nq5pji]");
BPMNExecProcessUtils.logCurrentNode("Activity_1nq5pji","Refresh calendar");
//do something
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Event_1faj9xw
BPMNExecProcessUtils.logTransition("Activity_1nq5pji","Event_1faj9xw");
EVENT_Event_1faj9xw(s.withCurrent("Activity_1nq5pji"));}
}

public void TASK_Activity_1pl6nde_Treat_exacerbations(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Treat exacerbations [Activity_1pl6nde]
BPMNExecProcessUtils.debugOutput(s,"Generic Task Treat exacerbations [Activity_1pl6nde]");
BPMNExecProcessUtils.logCurrentNode("Activity_1pl6nde","Treat exacerbations");
//do something
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Gateway_0dbyzwb
BPMNExecProcessUtils.logTransition("Activity_1pl6nde","Gateway_0dbyzwb");
GATEWAY_Gateway_0dbyzwb(s.withCurrent("Activity_1pl6nde"));}
}

public void TASK_Activity_1x6l1dw_Prescribe_oxygen_therapy(BPMNExecProcessUtils.ProcessStatus s) {//Generic Task Prescribe oxygen therapy [Activity_1x6l1dw]
BPMNExecProcessUtils.debugOutput(s,"Generic Task Prescribe oxygen therapy [Activity_1x6l1dw]");
BPMNExecProcessUtils.logCurrentNode("Activity_1x6l1dw","Prescribe oxygen therapy");
//do something
if (!Thread.currentThread().isInterrupted()) {//[outgoing edge] Gateway_07g2otl
BPMNExecProcessUtils.logTransition("Activity_1x6l1dw","Gateway_07g2otl");
GATEWAY_Gateway_07g2otl(s.withCurrent("Activity_1x6l1dw"));}
}

public void init() {
if (this.patientAlreadyStaged_stream.isEmpty()) java.util.Arrays.stream(BPMNExecProcessUtils.inputs.getProperty("patientAlreadyStaged", "").split(",")).forEach(i->this.patientAlreadyStaged_stream.addLast(i));
BPMNExecProcessUtils.logInput("patientAlreadyStaged",this.patientAlreadyStaged_stream);
this.patientAlreadyStaged = this.patientAlreadyStaged_stream.pop();
if (this.patientRequireHospitalization_stream.isEmpty()) java.util.Arrays.stream(BPMNExecProcessUtils.inputs.getProperty("patientRequireHospitalization", "").split(",")).forEach(i->this.patientRequireHospitalization_stream.addLast(i));
BPMNExecProcessUtils.logInput("patientRequireHospitalization",this.patientRequireHospitalization_stream);
this.patientRequireHospitalization = this.patientRequireHospitalization_stream.pop();
if (this.anamnesisReport_stream.isEmpty()) java.util.Arrays.stream(BPMNExecProcessUtils.inputs.getProperty("anamnesisReport", "").split(",")).forEach(i->this.anamnesisReport_stream.addLast(i));
BPMNExecProcessUtils.logInput("anamnesisReport",this.anamnesisReport_stream);
this.anamnesisReport = this.anamnesisReport_stream.pop();
if (this.patientSmoker_stream.isEmpty()) java.util.Arrays.stream(BPMNExecProcessUtils.inputs.getProperty("patientSmoker", "").split(",")).forEach(i->this.patientSmoker_stream.addLast(i));
BPMNExecProcessUtils.logInput("patientSmoker",this.patientSmoker_stream);
this.patientSmoker = this.patientSmoker_stream.pop();
if (this.COPDSuspected_stream.isEmpty()) java.util.Arrays.stream(BPMNExecProcessUtils.inputs.getProperty("COPDSuspected", "").split(",")).forEach(i->this.COPDSuspected_stream.addLast(i));
BPMNExecProcessUtils.logInput("COPDSuspected",this.COPDSuspected_stream);
this.COPDSuspected = this.COPDSuspected_stream.pop();
if (this.spirometryReport_stream.isEmpty()) java.util.Arrays.stream(BPMNExecProcessUtils.inputs.getProperty("spirometryReport", "").split(",")).forEach(i->this.spirometryReport_stream.addLast(i));
BPMNExecProcessUtils.logInput("spirometryReport",this.spirometryReport_stream);
this.spirometryReport = this.spirometryReport_stream.pop();
if (this.analyzedResults_stream.isEmpty()) java.util.Arrays.stream(BPMNExecProcessUtils.inputs.getProperty("analyzedResults", "").split(",")).forEach(i->this.analyzedResults_stream.addLast(i));
BPMNExecProcessUtils.logInput("analyzedResults",this.analyzedResults_stream);
this.analyzedResults = this.analyzedResults_stream.pop();
if (this.COPDDiagnosed_stream.isEmpty()) java.util.Arrays.stream(BPMNExecProcessUtils.inputs.getProperty("COPDDiagnosed", "").split(",")).forEach(i->this.COPDDiagnosed_stream.addLast(i));
BPMNExecProcessUtils.logInput("COPDDiagnosed",this.COPDDiagnosed_stream);
this.COPDDiagnosed = this.COPDDiagnosed_stream.pop();
if (this.BRTReport_stream.isEmpty()) java.util.Arrays.stream(BPMNExecProcessUtils.inputs.getProperty("BRTReport", "").split(",")).forEach(i->this.BRTReport_stream.addLast(i));
BPMNExecProcessUtils.logInput("BRTReport",this.BRTReport_stream);
this.BRTReport = this.BRTReport_stream.pop();
if (this.FEV1FVC_stream.isEmpty()) java.util.Arrays.stream(BPMNExecProcessUtils.inputs.getProperty("FEV1FVC", "").split(",")).forEach(i->this.FEV1FVC_stream.addLast(i));
BPMNExecProcessUtils.logInput("FEV1FVC",this.FEV1FVC_stream);
this.FEV1FVC = this.FEV1FVC_stream.pop();
if (this.paO2_stream.isEmpty()) java.util.Arrays.stream(BPMNExecProcessUtils.inputs.getProperty("paO2", "").split(",")).forEach(i->this.paO2_stream.addLast(i));
BPMNExecProcessUtils.logInput("paO2",this.paO2_stream);
this.paO2 = this.paO2_stream.pop();
if (this.paCO2_stream.isEmpty()) java.util.Arrays.stream(BPMNExecProcessUtils.inputs.getProperty("paCO2", "").split(",")).forEach(i->this.paCO2_stream.addLast(i));
BPMNExecProcessUtils.logInput("paCO2",this.paCO2_stream);
this.paCO2 = this.paCO2_stream.pop();

}public boolean globalAssert(BPMNExecProcessUtils.ProcessStatus s, String node_id) {
boolean success=true;

return success;

}public void execute(Object[] _patientAlreadyStaged_stream,Object[] _patientRequireHospitalization_stream,Object[] _anamnesisReport_stream,Object[] _patientSmoker_stream,Object[] _COPDSuspected_stream,Object[] _spirometryReport_stream,Object[] _analyzedResults_stream,Object[] _COPDDiagnosed_stream,Object[] _BRTReport_stream,Object[] _FEV1FVC_stream,Object[] _paO2_stream,Object[] _paCO2_stream) {if (_patientAlreadyStaged_stream != null)java.util.Arrays.stream(_patientAlreadyStaged_stream).forEach(i->this.patientAlreadyStaged_stream.addLast(i));
if (_patientRequireHospitalization_stream != null)java.util.Arrays.stream(_patientRequireHospitalization_stream).forEach(i->this.patientRequireHospitalization_stream.addLast(i));
if (_anamnesisReport_stream != null)java.util.Arrays.stream(_anamnesisReport_stream).forEach(i->this.anamnesisReport_stream.addLast(i));
if (_patientSmoker_stream != null)java.util.Arrays.stream(_patientSmoker_stream).forEach(i->this.patientSmoker_stream.addLast(i));
if (_COPDSuspected_stream != null)java.util.Arrays.stream(_COPDSuspected_stream).forEach(i->this.COPDSuspected_stream.addLast(i));
if (_spirometryReport_stream != null)java.util.Arrays.stream(_spirometryReport_stream).forEach(i->this.spirometryReport_stream.addLast(i));
if (_analyzedResults_stream != null)java.util.Arrays.stream(_analyzedResults_stream).forEach(i->this.analyzedResults_stream.addLast(i));
if (_COPDDiagnosed_stream != null)java.util.Arrays.stream(_COPDDiagnosed_stream).forEach(i->this.COPDDiagnosed_stream.addLast(i));
if (_BRTReport_stream != null)java.util.Arrays.stream(_BRTReport_stream).forEach(i->this.BRTReport_stream.addLast(i));
if (_FEV1FVC_stream != null)java.util.Arrays.stream(_FEV1FVC_stream).forEach(i->this.FEV1FVC_stream.addLast(i));
if (_paO2_stream != null)java.util.Arrays.stream(_paO2_stream).forEach(i->this.paO2_stream.addLast(i));
if (_paCO2_stream != null)java.util.Arrays.stream(_paCO2_stream).forEach(i->this.paCO2_stream.addLast(i));
BPMNExecProcessUtils.executeProcess("COPD",this::init,this::EVENT_Event_0ltqr3s_Patient_request);
}public static void main(String[] args) {
BPMNExecProcessUtils.setExternalTraceFile("COPD");BPMNExecProcessUtils.enableTrueParallel();COPD process = new COPD();
process.execute(null/*patientAlreadyStaged*/,null/*patientRequireHospitalization*/,null/*anamnesisReport*/,null/*patientSmoker*/,null/*COPDSuspected*/,null/*spirometryReport*/,null/*analyzedResults*/,null/*COPDDiagnosed*/,null/*BRTReport*/,null/*FEV1FVC*/,null/*paO2*/,null/*paCO2*/);}}

