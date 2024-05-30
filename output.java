

class prova {

public static record dmn_DecisionTable_0wip7uc_result(boolean done, string message){}

public dmn_DecisionTable_0wip7uc_result dmn_DecisionTable_0wip7uc(number abs_a_, string s) {

if (contains(abs(a),constRange(3,5)) && not("1","2")) { return new dmn_DecisionTable_0wip7uc_result(/*done*/true, /*message*/"ok");} else if (abs(a) > 10 && inList(s,"a","b")) { return new dmn_DecisionTable_0wip7uc_result(/*done*/false, /*message*/"no");} else if (abs(a) > 1 && s == "c") { return new dmn_DecisionTable_0wip7uc_result(/*done*/false, /*message*/"ko");}
}

public java.lang.Void t_g_Prepara_una_carbonara() {
	System.out.println("t_g_Prepara una carbonara");
	return null;
}

public java.lang.Void t_u_Prepara_una_Bistecca() {
	System.out.println("t_u_Prepara una Bistecca");
	return null;
}

public java.lang.Void t_g_Bisogna_Mangiare() {
	System.out.println("t_g_Bisogna Mangiare");
	return null;
}

public java.lang.Void f_Activity_01p6jwz() {
t_g_Mangia();
//Prendi una decisione
	dmn_DecisionTable_0wip7uc_result dresult=dmn_DecisionTable_0wip7uc(/*a*/"a", /*s*/"ciao");
	var message=dresult.message;
	var done=dresult.done;
//end: Sei sazio
System.exit(0);return  null;
}

public java.lang.Void f_StartEvent_1() {
//start: Hai fame;
t_g_Bisogna_Mangiare();
if (preferenza == "primo"){
t_g_Prepara_una_carbonara();
return f_Activity_01p6jwz();
} else if (preferenza == "secondo"){
t_u_Prepara_una_Bistecca();
return f_Activity_01p6jwz();
} else { return null; }
}

public java.lang.Void t_g_Mangia() {
	System.out.println("t_g_Mangia");
	return null;
}
}
