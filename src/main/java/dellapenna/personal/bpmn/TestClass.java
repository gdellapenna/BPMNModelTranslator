/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dellapenna.personal.bpmn;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author giuse
 */
public class TestClass {

    public Map<String, Object> dmn_DecisionTable_0wip7uc(Map<String, Object> params) {
        var abs_a_ = params.get("abs_a_");//type: number
        var s = params.get("s");//type: string

        Map<String, Object> result = new HashMap<>();
        result.put("done", "xxx"); //type: boolean
        result.put("message", "yyy"); //type: string
        return result;
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
        Map<String, Object> params = new HashMap<>();
        params.put("a", "a");
        params.put("s", "ciao");
        Map<String, Object> dresult = dmn_DecisionTable_0wip7uc(params);
        var message = dresult.get("message");
        var done = dresult.get("done");
//end: Sei sazio
        System.exit(0);
        return null;
    }

    public java.lang.Void f_StartEvent_1() {
//start: Hai fame;
        t_g_Bisogna_Mangiare();
//        if (preferenza == "primo") { //DA DICHIARARE COME GLOBALE? 
//            t_g_Prepara_una_carbonara();
//            return f_Activity_01p6jwz();
//        } else if (preferenza == "secondo") {
            t_u_Prepara_una_Bistecca();
            return f_Activity_01p6jwz();
//        }
    }

    public java.lang.Void t_g_Mangia() {
        System.out.println("t_g_Mangia");
        return null;
    }
}
