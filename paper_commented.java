/* 
   Classi utili a rendere eseguibile il codice. Servono a mascherare
   molti elementi specifici del linguaggio, lasciando nel codice generato
   solo chiamate molto generiche e "agnostiche". Andrebbero in classi separate,
   ma per praticità metto tutto assieme. Potete saltare questa parte perchè
   è sempre uguale.
 */

class TypeUtils {

    public static Double toNumber(Object o) {
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

    public static String toString(Object o) {
        return o.toString();
    }

    public static Boolean toBoolean(Object o) {
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

    public static void signal(String s) {
    }

    public static void wait(String... s) {
    }

    public static void error(String s, int c) {
        System.err.println("ERROR: " + s);
        System.exit(c);
    }

    public static void noDefaultCaseError() {
        error("No default branch in gateway", 9999);
    }

    public static void success(String s, int c) {
        if (s != null) {
            System.out.println("SUCCESS: " + s);
        }
        System.exit(c);
    }

    public static void success() {
        success(null, 0);
    }

    public static void debugOutput(String s) {
        System.out.println(s);
    }

}

/*
 * ****************************** Codice generato per la DMN *************************
 */
 /* Classe wrapper per l'outuput della tabella DMN GetLengthDT */
class dmn_dtable_GetLengthDT_result {

    Double Length;

    public dmn_dtable_GetLengthDT_result(Double Length) {
        this.Length = Length;
    }
}

/* Codice della tabella DMN GetLengthDT */
class dmn_dtable_GetLengthDT {

    public static dmn_dtable_GetLengthDT_result execute(Object _Type) {
        /*viene sempre eseguito un cast al tipo del linguaggio target più
          simile a quello "generico" della DMN */
        String Type = TypeUtils.toString(_Type);

        if (TypeUtils.toString(Type).equals("std")) {
            return new dmn_dtable_GetLengthDT_result(/*Length*/0.5);
        } else if (TypeUtils.toString(Type).equals("large")) {
            return new dmn_dtable_GetLengthDT_result(/*Length*/1.0);
        } else if (TypeUtils.toString(Type).equals("xl")) {
            return new dmn_dtable_GetLengthDT_result(/*Length*/2.0);
        } else {
            return new dmn_dtable_GetLengthDT_result(/*Length*/-(TypeUtils.toNumber(1.0)));
        }
    }

}

/* Classe wrapper per l'outuput della tabella DMN DetermineModeDT */
class dmn_dtable_DetermineModeDT_result {

    String Mode;

    public dmn_dtable_DetermineModeDT_result(String Mode) {
        this.Mode = Mode;
    }
}

/* Codice della tabella DMN DetermineModeDT */
class dmn_dtable_DetermineModeDT {

    public static dmn_dtable_DetermineModeDT_result execute(Object _Length, Object _Weight) {

        Double Length = TypeUtils.toNumber(_Length);
        Double Weight = TypeUtils.toNumber(_Weight);

        if ((TypeUtils.toNumber(Length) > 0.0 && TypeUtils.toNumber(Length) <= 1.0) && (TypeUtils.toNumber(Weight) > 0.0 && TypeUtils.toNumber(Weight) <= 5.0)) {
            return new dmn_dtable_DetermineModeDT_result(/*Mode*/"car");
        } else if ((TypeUtils.toNumber(Length) > 1.0 && TypeUtils.toNumber(Length) <= 2.0) && (TypeUtils.toNumber(Weight) > 0.0 && TypeUtils.toNumber(Weight) <= 5.0)) {
            return new dmn_dtable_DetermineModeDT_result(/*Mode*/"truck");
        } else if ((TypeUtils.toNumber(Length) > 5.0 && TypeUtils.toNumber(Length) <= 10.0)) {
            return new dmn_dtable_DetermineModeDT_result(/*Mode*/"truck");
        } else {
            return new dmn_dtable_DetermineModeDT_result(/*Mode*/"undef");
        }
    }
}

/* Classe wrapper per l'outuput della tabella DMN ChooseConsentDT */
class dmn_dtable_ChooseConsentDT_result {

    String Consent;

    public dmn_dtable_ChooseConsentDT_result(String Consent) {
        this.Consent = Consent;
    }
}

/* Codice della tabella DMN ChooseConsentDT */
class dmn_dtable_ChooseConsentDT {

    public static dmn_dtable_ChooseConsentDT_result execute(Object _Mode, Object _Weight) {

        String Mode = TypeUtils.toString(_Mode);
        Double Weight = TypeUtils.toNumber(_Weight);

        if (TypeUtils.toString(Mode).equals("car") && TypeUtils.toNumber(Weight) > TypeUtils.toNumber(6.0)) {
            return new dmn_dtable_ChooseConsentDT_result(/*Consent*/"owner");
        } else if (TypeUtils.toString(Mode).equals("truck") && TypeUtils.toNumber(Weight) > TypeUtils.toNumber(8.0)) {
            return new dmn_dtable_ChooseConsentDT_result(/*Consent*/"com");
        } else {
            return new dmn_dtable_ChooseConsentDT_result(/*Consent*/"none");
        }
    }
}

/*
 * ****************************** Codice generato per la BPMN *************************
 */
class bpmn_process_Shipment {

//Process Variables
    Object pType;
    Object pWeight;
    Object pLength;
    Object consent;
    Object sMode;

//Process Dynamics
    public void flow_package_received() {//start event: package received;
        pType = getPackageType(); //TO IMPLEMENT
        //questo metodo esterno è chiamato nella BPMN, e va ovviamente implementato esternamente se si vuole che sia eseguibile

        //business rule task: get length;
        dmn_dtable_GetLengthDT_result getLengthResult = dmn_dtable_GetLengthDT.execute(/*Type*/pType);
        pLength = getLengthResult.Length;
        //gateway
        if (pLength.equals(-(TypeUtils.toNumber(1.0)))) {
            flow_undefined_length();
        } else {
            flow_measure_weight();
        }
    }

    public void flow_measure_weight() {
        task_user_measure_weight();
        //gateway
        if (TypeUtils.toNumber(pWeight) > TypeUtils.toNumber(10.0)) {
            flow_unsuppoted_weight();
        } else {
            flow_determine_mode();
        }
    }

    public void flow_determine_mode() {//business rule task: determine mode;
        dmn_dtable_DetermineModeDT_result determineModeResult = dmn_dtable_DetermineModeDT.execute(/*Length*/pLength, /*Weight*/ pWeight);
        sMode = determineModeResult.Mode;
        //gateway
        if (sMode.equals("undef")) {
            flow_no_shipment();
        } else {
            flow_choose_consent();
        }
    }

    public void flow_choose_consent() {//business rule task: choose consent;
        dmn_dtable_ChooseConsentDT_result chooseConsentResult = dmn_dtable_ChooseConsentDT.execute(/*Mode*/sMode, /*Weight*/ pWeight);
        consent = chooseConsentResult.Consent;
        //gateway
        if (consent.equals("com")) {
            flow_sign_declaration();
        } else if (consent.equals("owner")) {
            flow_fetch_declaration();
        } else if (consent.equals("none")) {
            flow_Gateway_07f90ke();
        } else {
            ProcessUtils.noDefaultCaseError();
        }
    }

    public void flow_sign_declaration() {
        task_generic_sign_declaration();
        flow_Gateway_07f90ke();
    }

    public void flow_fetch_declaration() {
        task_generic_fetch_declaration();
        flow_Gateway_07f90ke();
    }

    public void flow_Gateway_07f90ke() {
        //questo è un gateway di join, che converge sul task qui sotto
        flow_ready_for_shipment();
    }

    public void task_user_measure_weight() {//user task: measure weight;
        pWeight = getPackageWeight();  //TO IMPLEMENT
        //questo metodo esterno è chiamato nella BPMN, e va ovviamente implementato esternamente se si vuole che sia eseguibile
    }

    public void task_generic_fetch_declaration() {//generic task: fetch declaration;
        //i task generici corrispondono a metodi vuoti, visto che non hanno reale dinamica al loro interno
    }

    public void task_generic_sign_declaration() {//generic task: sign declaration;
    }

    public void flow_undefined_length() {//end event: undefined length;
        ProcessUtils.error("Undefined Length", 1);
    }

    public void flow_unsuppoted_weight() {//end event: unsuppoted weight;
        ProcessUtils.error("Unsupported Weight", 2);
    }

    public void flow_no_shipment() {//end event: no shipment;
        ProcessUtils.error("No Shipment", 3);
    }

    public void flow_ready_for_shipment() {//end event: ready for shipment;
        ProcessUtils.success();
    }

    /* *********************************************************************************** */
    public static void main(String[] args) {
        //per avviare il codice generato, basta inserire una main che chiami lo start event
        bpmn_process_Shipment process = new bpmn_process_Shipment();
        process.flow_package_received();
    }
}
