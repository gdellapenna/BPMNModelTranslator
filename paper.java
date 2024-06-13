
class prova {

    public static record dmn_DecisionTable_1w1qw51_result(number Length) {

    }

    public dmn_DecisionTable_1w1qw51_result dmn_DecisionTable_1w1qw51(string Type) {

        if (Type == "std") {
            return new dmn_DecisionTable_1w1qw51_result(/*Length*/0.5);
        } else if (Type == "large") {
            return new dmn_DecisionTable_1w1qw51_result(/*Length*/1);
        } else if (Type == "xl") {
            return new dmn_DecisionTable_1w1qw51_result(/*Length*/2);
        } else if (true) {
            return new dmn_DecisionTable_1w1qw51_result(/*Length*/-(1));
        }
    }

    public static record dmn_DecisionTable_0gitgtn_result(string Mode) {

    }

    public dmn_DecisionTable_0gitgtn_result dmn_DecisionTable_0gitgtn(number Length, number Weight) {

        if (contains(Length, constRange(0, 1)) && contains(Weight, constRange(0, 5))) {
            return new dmn_DecisionTable_0gitgtn_result(/*Mode*/"car");
        } else if (contains(Length, constRange(1, 2)) && contains(Weight, constRange(0, 5))) {
            return new dmn_DecisionTable_0gitgtn_result(/*Mode*/"truck");
        } else if (true && contains(Length, constRange(5, 10))) {
            return new dmn_DecisionTable_0gitgtn_result(/*Mode*/"truck");
        } else if (true && true) {
            return new dmn_DecisionTable_0gitgtn_result(/*Mode*/"undef");
        }
    }

    public static record dmn_DecisionTable_0lsdblf_result(string Consent) {

    }

    public dmn_DecisionTable_0lsdblf_result dmn_DecisionTable_0lsdblf(string Mode, number Weight) {

        if (Mode == "car" && Weight > 6) {
            return new dmn_DecisionTable_0lsdblf_result(/*Consent*/"owner");
        } else if (Mode == "truck" && Weight > 8) {
            return new dmn_DecisionTable_0lsdblf_result(/*Consent*/"com");
        } else if (true && true) {
            return new dmn_DecisionTable_0lsdblf_result(/*Consent*/"none");
        }
    }

    public java.lang.Void t_g_fetch_declaration() {
        System.out.println("t_g_fetch declaration");
        return null;
    }

    public java.lang.Void f_StartEvent_1() {
//start: package received;
//get length
        dmn_GetLengthDT_result Length = dmn_GetLengthDT(/*Type*/pType);
        var pLength = Length;
        if (true) {
            t_u_measure_weight();
            if (pWeight > 10) {
//end: unsuppoted weight
                System.exit(0);
                return null;
            } else if (true) {
//determine mode
                dmn_DetemineModeDT_result Mode = dmn_DetemineModeDT(/*Length*/pLength, /*Weight*/ pWeight);
                var sMode = Mode;
                if (sMode == "undef") {
//end: no shipment
                    System.exit(0);
                    return null;
                } else if (true) {
//choose consent
                    dmn_ChooseConsentDT_result Consent = dmn_ChooseConsentDT(/*Mode*/sMode, /*Weight*/ pWeight);
                    var consent = Consent;
                    if (consent == "com") {
                        t_g_sign_declaration();
                        return f_Event_1pjc4df();
                    } else if (consent == "owner") {
                        t_g_fetch_declaration();
                        return f_Event_1pjc4df();
                    } else if (consent == "none") {
                        return f_Event_1pjc4df();
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else if (pLength == -(1)) {
//end: undefined length
            System.exit(0);
            return null;
        } else {
            return null;
        }
    }

    public java.lang.Void f_Event_1pjc4df() {
//end: ready for shipment
        System.exit(0);
        return null;
    }

    public java.lang.Void t_u_measure_weight() {
        System.out.println("t_u_measure weight");
        return null;
    }

    public java.lang.Void t_g_sign_declaration() {
        System.out.println("t_g_sign declaration");
        return null;
    }
}
