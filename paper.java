
class TypeUtils {

    public static Double tonumber(Object o) {
        if (o instanceof Number n) {
            return n.doubleValue();
        } else {
            try {
                return Double.valueOf(o.toString());
            } catch (NumberFormatException ex) {
                return 0.0; //should raise an exception
            }
        }
    }

    public static String tostring(Object o) {
        return o.toString();
    }

    public static Boolean toboolean(Object o) {
        if (o instanceof Boolean b) {
            return b;
        } else if (o instanceof Number n) {
            return n.doubleValue() != 0;
        } else {
            return Boolean.valueOf(o.toString());

        }
    }
}

class dmn_GetLengthDT_result {

    Double Length;

    public dmn_GetLengthDT_result(Double Length) {
        this.Length = Length;
    }
}

class dmn_GetLengthDT {

    public static dmn_GetLengthDT_result execute(Object _Type) {

        String Type = TypeUtils.tostring(_Type);

        if (TypeUtils.tostring(Type).equals("std")) {
            return new dmn_GetLengthDT_result(/*Length*/0.5);
        } else if (TypeUtils.tostring(Type).equals("large")) {
            return new dmn_GetLengthDT_result(/*Length*/1.0);
        } else if (TypeUtils.tostring(Type).equals("xl")) {
            return new dmn_GetLengthDT_result(/*Length*/2.0);
        } else {
            return new dmn_GetLengthDT_result(/*Length*/-(TypeUtils.tonumber(1.0)));
        }
    }
}

class dmn_DetermineModeDT_result {

    String Mode;

    public dmn_DetermineModeDT_result(String Mode) {
        this.Mode = Mode;
    }
}

class dmn_DetermineModeDT {

    public static dmn_DetermineModeDT_result execute(Object _Length, Object _Weight) {

        Double Length = TypeUtils.tonumber(_Length);
        Double Weight = TypeUtils.tonumber(_Weight);

        if ((TypeUtils.tonumber(Length) > 0.0 && TypeUtils.tonumber(Length) <= 1.0) && (TypeUtils.tonumber(Weight) > 0.0 && TypeUtils.tonumber(Weight) <= 5.0)) {
            return new dmn_DetermineModeDT_result(/*Mode*/"car");
        } else if ((TypeUtils.tonumber(Length) > 1.0 && TypeUtils.tonumber(Length) <= 2.0) && (TypeUtils.tonumber(Weight) > 0.0 && TypeUtils.tonumber(Weight) <= 5.0)) {
            return new dmn_DetermineModeDT_result(/*Mode*/"truck");
        } else if ((TypeUtils.tonumber(Length) > 5.0 && TypeUtils.tonumber(Length) <= 10.0)) {
            return new dmn_DetermineModeDT_result(/*Mode*/"truck");
        } else {
            return new dmn_DetermineModeDT_result(/*Mode*/"undef");
        }
    }
}

class dmn_ChooseConsentDT_result {

    String Consent;

    public dmn_ChooseConsentDT_result(String Consent) {
        this.Consent = Consent;
    }
}

class dmn_ChooseConsentDT {

    public static dmn_ChooseConsentDT_result execute(Object _Mode, Object _Weight) {

        String Mode = TypeUtils.tostring(_Mode);
        Double Weight = TypeUtils.tonumber(_Weight);

        if (TypeUtils.tostring(Mode).equals("car") && TypeUtils.tonumber(Weight) > TypeUtils.tonumber(6.0)) {
            return new dmn_ChooseConsentDT_result(/*Consent*/"owner");
        } else if (TypeUtils.tostring(Mode).equals("truck") && TypeUtils.tonumber(Weight) > TypeUtils.tonumber(8.0)) {
            return new dmn_ChooseConsentDT_result(/*Consent*/"com");
        } else {
            return new dmn_ChooseConsentDT_result(/*Consent*/"none");
        }
    }
}

class bpmn_p_Shipment {

    Object pType;
    Object pWeight;
    Object pLength;
    Object consent;
    Object sMode;

    public void t_g_fetch_declaration() {
        System.out.println("t_g_fetch declaration");

    }

    public void e_e_no_shipment() {
//end: no shipment

        System.err.println("No Shipment");
        System.exit(3);
    }

    public void e_s_package_received() {
//start: package received
        pType = getPackageType();
    }

    public void e_e_undefined_length() {
//end: undefined length

        System.err.println("Undefined Length");
        System.exit(1);
    }

    public void f_StartEvent_1() {
        e_s_package_received();
//get length
        dmn_GetLengthDT_result getLengthResult = dmn_GetLengthDT.execute(/*Type*/pType);
        pLength = getLengthResult.Length;
        if (pLength.equals(-(TypeUtils.tonumber(1.0)))) {
            e_e_undefined_length();
        } else {
            t_u_measure_weight();
            if (TypeUtils.tonumber(pWeight) > TypeUtils.tonumber(10.0)) {
                e_e_unsuppoted_weight();
            } else {
//determine mode
                dmn_DetermineModeDT_result determineModeResult = dmn_DetermineModeDT.execute(/*Length*/pLength, /*Weight*/ pWeight);
                sMode = determineModeResult.Mode;
                if (sMode.equals("undef")) {
                    e_e_no_shipment();
                } else {
//choose consent
                    dmn_ChooseConsentDT_result chooseConsentResult = dmn_ChooseConsentDT.execute(/*Mode*/sMode, /*Weight*/ pWeight);
                    consent = chooseConsentResult.Consent;
                    if (consent.equals("com")) {
                        t_g_sign_declaration();
                        f_Event_1pjc4df();
                    } else if (consent.equals("owner")) {
                        t_g_fetch_declaration();
                        f_Event_1pjc4df();
                    } else if (consent.equals("none")) {
                        f_Event_1pjc4df();
                    } else {
//no default case
                        System.exit(9999);
                    }
                }
            }
        }
    }

    public void f_Event_1pjc4df() {
//end: ready for shipment
        System.exit(0);
    }

    public void t_u_measure_weight() {
        pWeight = getPackageWeight();
    }

    public void e_e_unsuppoted_weight() {
//end: unsuppoted weight

        System.err.println("Unsupported Weight");
        System.exit(2);
    }

    public void t_g_sign_declaration() {
        System.out.println("t_g_sign declaration");
    }
}
