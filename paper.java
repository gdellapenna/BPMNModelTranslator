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
        public static void NoDefaultError() { System.exit(9999); }
        public static void signal(String s) {  }
        public static void wait(String... s) {  }
    }

class dmn_dtable_GetLengthDT_result{Double Length;
public dmn_dtable_GetLengthDT_result(Double Length) {this.Length=Length;
}
}

class dmn_dtable_GetLengthDT {

public static dmn_dtable_GetLengthDT_result execute(Object _Type) {

String Type = TypeUtils.tostring(_Type);

if (TypeUtils.tostring(Type).equals("std")) { return new dmn_dtable_GetLengthDT_result(/*Length*/0.5);} else if (TypeUtils.tostring(Type).equals("large")) { return new dmn_dtable_GetLengthDT_result(/*Length*/1.0);} else if (TypeUtils.tostring(Type).equals("xl")) { return new dmn_dtable_GetLengthDT_result(/*Length*/2.0);} else  { return new dmn_dtable_GetLengthDT_result(/*Length*/-(TypeUtils.tonumber(1.0)));}
}
}class dmn_dtable_DetermineModeDT_result{String Mode;
public dmn_dtable_DetermineModeDT_result(String Mode) {this.Mode=Mode;
}
}

class dmn_dtable_DetermineModeDT {

public static dmn_dtable_DetermineModeDT_result execute(Object _Length, Object _Weight) {

Double Length = TypeUtils.tonumber(_Length);
Double Weight = TypeUtils.tonumber(_Weight);

if ((TypeUtils.tonumber(Length)>0.0 && TypeUtils.tonumber(Length)<=1.0) && (TypeUtils.tonumber(Weight)>0.0 && TypeUtils.tonumber(Weight)<=5.0)) { return new dmn_dtable_DetermineModeDT_result(/*Mode*/"car");} else if ((TypeUtils.tonumber(Length)>1.0 && TypeUtils.tonumber(Length)<=2.0) && (TypeUtils.tonumber(Weight)>0.0 && TypeUtils.tonumber(Weight)<=5.0)) { return new dmn_dtable_DetermineModeDT_result(/*Mode*/"truck");} else if ((TypeUtils.tonumber(Length)>5.0 && TypeUtils.tonumber(Length)<=10.0)) { return new dmn_dtable_DetermineModeDT_result(/*Mode*/"truck");} else  { return new dmn_dtable_DetermineModeDT_result(/*Mode*/"undef");}
}
}class dmn_dtable_ChooseConsentDT_result{String Consent;
public dmn_dtable_ChooseConsentDT_result(String Consent) {this.Consent=Consent;
}
}

class dmn_dtable_ChooseConsentDT {

public static dmn_dtable_ChooseConsentDT_result execute(Object _Mode, Object _Weight) {

String Mode = TypeUtils.tostring(_Mode);
Double Weight = TypeUtils.tonumber(_Weight);

if (TypeUtils.tostring(Mode).equals("car") && TypeUtils.tonumber(Weight) > TypeUtils.tonumber(6.0)) { return new dmn_dtable_ChooseConsentDT_result(/*Consent*/"owner");} else if (TypeUtils.tostring(Mode).equals("truck") && TypeUtils.tonumber(Weight) > TypeUtils.tonumber(8.0)) { return new dmn_dtable_ChooseConsentDT_result(/*Consent*/"com");} else  { return new dmn_dtable_ChooseConsentDT_result(/*Consent*/"none");}
}
}

 class bpmn_process_Shipment { Object pType;
Object pLength;
Object sMode;
Object consent; public void task_user_measure_weight() {System.out.println("task_user_measure weight");
}

public void task_generic_fetch_declaration() {System.out.println("task_generic_fetch declaration");
}

public void task_generic_sign_declaration() {System.out.println("task_generic_sign declaration");
}; }
