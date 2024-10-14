
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

class ProcessUtils {

    public static void NoDefaultError() {
        System.exit(9999);
    }

    public static void signal(String s) {
    }

    public static void wait(String... s) {
    }
}

class dmn_dtable_GetLengthDT_result {

    Double Length;

    public dmn_dtable_GetLengthDT_result(Double Length) {
        this.Length = Length;
    }
}

class dmn_dtable_GetLengthDT {

    public static dmn_dtable_GetLengthDT_result execute(Object _Type) {

        String Type = TypeUtils.tostring(_Type);

        if (TypeUtils.tostring(Type).equals("std")) {
            return new dmn_dtable_GetLengthDT_result(/*Length*/0.5);
        } else if (TypeUtils.tostring(Type).equals("large")) {
            return new dmn_dtable_GetLengthDT_result(/*Length*/1.0);
        } else if (TypeUtils.tostring(Type).equals("xl")) {
            return new dmn_dtable_GetLengthDT_result(/*Length*/2.0);
        } else {
            return new dmn_dtable_GetLengthDT_result(/*Length*/-(TypeUtils.tonumber(1.0)));
        }
    }
}

class dmn_dtable_DetermineModeDT_result {

    String Mode;

    public dmn_dtable_DetermineModeDT_result(String Mode) {
        this.Mode = Mode;
    }
}

class dmn_dtable_DetermineModeDT {

    public static dmn_dtable_DetermineModeDT_result execute(Object _Length, Object _Weight) {

        Double Length = TypeUtils.tonumber(_Length);
        Double Weight = TypeUtils.tonumber(_Weight);

        if ((TypeUtils.tonumber(Length) > 0.0 && TypeUtils.tonumber(Length) <= 1.0) && (TypeUtils.tonumber(Weight) > 0.0 && TypeUtils.tonumber(Weight) <= 5.0)) {
            return new dmn_dtable_DetermineModeDT_result(/*Mode*/"car");
        } else if ((TypeUtils.tonumber(Length) > 1.0 && TypeUtils.tonumber(Length) <= 2.0) && (TypeUtils.tonumber(Weight) > 0.0 && TypeUtils.tonumber(Weight) <= 5.0)) {
            return new dmn_dtable_DetermineModeDT_result(/*Mode*/"truck");
        } else if ((TypeUtils.tonumber(Length) > 5.0 && TypeUtils.tonumber(Length) <= 10.0)) {
            return new dmn_dtable_DetermineModeDT_result(/*Mode*/"truck");
        } else {
            return new dmn_dtable_DetermineModeDT_result(/*Mode*/"undef");
        }
    }
}

class dmn_dtable_ChooseConsentDT_result {

    String Consent;

    public dmn_dtable_ChooseConsentDT_result(String Consent) {
        this.Consent = Consent;
    }
}

class dmn_dtable_ChooseConsentDT {

    public static dmn_dtable_ChooseConsentDT_result execute(Object _Mode, Object _Weight) {

        String Mode = TypeUtils.tostring(_Mode);
        Double Weight = TypeUtils.tonumber(_Weight);

        if (TypeUtils.tostring(Mode).equals("car") && TypeUtils.tonumber(Weight) > TypeUtils.tonumber(6.0)) {
            return new dmn_dtable_ChooseConsentDT_result(/*Consent*/"owner");
        } else if (TypeUtils.tostring(Mode).equals("truck") && TypeUtils.tonumber(Weight) > TypeUtils.tonumber(8.0)) {
            return new dmn_dtable_ChooseConsentDT_result(/*Consent*/"com");
        } else {
            return new dmn_dtable_ChooseConsentDT_result(/*Consent*/"none");
        }
    }
}

class bpmn_process_Shipment {

    Object pType;
    Object pWeight;
    Object pLength;
    Object consent;
    Object sMode;

    public void task_user_measure_weight() {
        pWeight = getPackageWeight();
    }

    public void task_generic_fetch_declaration() {
        System.out.println("task_generic_fetch declaration");
    }

    public void task_generic_sign_declaration() {
        System.out.println("task_generic_sign declaration");
    }

    public void flow_undefined_length() {//end event: undefined length;
        System.err.println("Undefined Length");
        System.exit(1);
    }

    public void flow_unsuppoted_weight() {//end event: unsuppoted weight;
        System.err.println("Unsupported Weight");
        System.exit(2);
    }

    public void flow_ready_for_shipment() {//end event: ready for shipment;
        System.exit(0);
    }

    public void flow_Gateway_07f90ke() {
        flow_ready_for_shipment();
    }

    public void flow_sign_declaration() {
        task_generic_sign_declaration();
        flow_Gateway_07f90ke();
    }

    public void flow_fetch_declaration() {
        task_generic_fetch_declaration();
        flow_Gateway_07f90ke();
    }

    public void flow_package_received() {//start event: package received;
        pType = getPackageType();
//get length;
        dmn_dtable_GetLengthDT_result getLengthResult = dmn_dtable_GetLengthDT.execute(/*Type*/pType);
        pLength = getLengthResult.Length;
        if (pLength.equals(-(TypeUtils.tonumber(1.0)))) {
            flow_undefined_length();
        } else {
            flow_measure_weight();
        };
    }

    public void flow_no_shipment() {//end event: no shipment;
        System.err.println("No Shipment");
        System.exit(3);
    }

    public void flow_choose_consent() {//choose consent;
        dmn_dtable_ChooseConsentDT_result chooseConsentResult = dmn_dtable_ChooseConsentDT.execute(/*Mode*/sMode, /*Weight*/ pWeight);
        consent = chooseConsentResult.Consent;
        if (consent.equals("com")) {
            flow_sign_declaration();
        } else if (consent.equals("owner")) {
            flow_fetch_declaration();
        } else if (consent.equals("none")) {
            flow_Gateway_07f90ke();
        } else {
            ProcessUtils_NoDefaultError();
        };
    }

    public void flow_measure_weight() {
        task_user_measure_weight();
        if (TypeUtils.tonumber(pWeight) > TypeUtils.tonumber(10.0)) {
            flow_unsuppoted_weight();
        } else {
            flow_determine_mode();
        };
    }

    public void flow_determine_mode() {//determine mode;
        dmn_dtable_DetermineModeDT_result determineModeResult = dmn_dtable_DetermineModeDT.execute(/*Length*/pLength, /*Weight*/ pWeight);
        sMode = determineModeResult.Mode;
        if (sMode.equals("undef")) {
            flow_no_shipment();
        } else {
            flow_choose_consent();
        };
    }
;
}
