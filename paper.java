
class prova {

    public static record dmn_GetLengthDT_result(Double Length) {

    }

    public dmn_GetLengthDT_result dmn_GetLengthDT(String Type) {

        if (Type.equals("std")) {
            return new dmn_GetLengthDT_result(/*Length*/0.5);
        } else if (Type.equals("large")) {
            return new dmn_GetLengthDT_result(/*Length*/1.0);
        } else if (Type.equals("xl")) {
            return new dmn_GetLengthDT_result(/*Length*/2.0);
        } else {
            return new dmn_GetLengthDT_result(/*Length*/-(1.0));
        }
    }

    public static record dmn_DetermineModeDT_result(String Mode) {

    }

    public dmn_DetermineModeDT_result dmn_DetermineModeDT(Double Length, Double Weight) {

        if ((Length > 0.0 && Length <= 1.0) && (Weight > 0.0 && Weight <= 5.0)) {
            return new dmn_DetermineModeDT_result(/*Mode*/"car");
        } else if ((Length > 1.0 && Length <= 2.0) && (Weight > 0.0 && Weight <= 5.0)) {
            return new dmn_DetermineModeDT_result(/*Mode*/"truck");
        } else if ((Length > 5.0 && Length <= 10.0)) {
            return new dmn_DetermineModeDT_result(/*Mode*/"truck");
        } else {
            return new dmn_DetermineModeDT_result(/*Mode*/"undef");
        }
    }

    public static record dmn_ChooseConsentDT_result(String Consent) {

    }

    public dmn_ChooseConsentDT_result dmn_ChooseConsentDT(String Mode, Double Weight) {

        if (Mode.equals("car") && Weight > 6.0) {
            return new dmn_ChooseConsentDT_result(/*Consent*/"owner");
        } else if (Mode.equals("truck") && Weight > 8.0) {
            return new dmn_ChooseConsentDT_result(/*Consent*/"com");
        } else {
            return new dmn_ChooseConsentDT_result(/*Consent*/"none");
        }
    }

    public java.lang.Void t_g_fetch_declaration() {
        System.out.println("t_g_fetch declaration");
        return null;
    }

    
    public java.lang.Void f_StartEvent_1() {
//start: package received
        var pType = "std";
//get length
        dmn_GetLengthDT_result getLengthResult = dmn_GetLengthDT(/*Type*/pType);
        var pLength = getLengthResult.Length;
        if (pLength.equals(-(1.0))) {
//end: undefined length
            System.err.println("undefined Length");
            System.exit(0);
            return null;
        } else {
            t_u_measure_weight();
            var pWeight = 4.0;
            if (pWeight > 10.0) {
//end: unsuppoted weight
                System.err.println("Unsupported Weight");
                System.exit(0);
                return null;
            } else {
//determine mode
                dmn_DetermineModeDT_result determineModeResult = dmn_DetermineModeDT(/*Length*/pLength, /*Weight*/ pWeight);
                var sMode = determineModeResult.Mode;
                if (sMode.equals("undef")) {
//end: no shipment
                    System.err.println("No Shipment");
                    System.exit(0);
                    return null;
                } else {
//choose consent
                    dmn_ChooseConsentDT_result chooseConsentResult = dmn_ChooseConsentDT(/*Mode*/sMode, /*Weight*/ pWeight);
                    var consent = chooseConsentResult.Consent;
                    if (consent.equals("com")) {
                        t_g_sign_declaration();
                        return f_Event_1pjc4df();
                    } else if (consent.equals("owner")) {
                        t_g_fetch_declaration();
                        return f_Event_1pjc4df();
                    } else if (consent.equals("none")) {
                        return f_Event_1pjc4df();
                    } else {
                        return null;
                    }
                }
            }
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
