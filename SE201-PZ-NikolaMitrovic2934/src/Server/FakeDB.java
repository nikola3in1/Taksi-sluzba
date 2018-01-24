//package Server;
//
//import Util.Vozac;
//import Util.Voznja;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//
//public class FakeDB {
//
//    static String menadzerUser = "nikola";
//    static String menadzerPass = "asd";
//
//    static Map<String, String> vozaci =  Collections.synchronizedMap(new HashMap<String,String>());
//
//
//
//    public static boolean validation(String user, String password){
//        if (user.equals(menadzerUser) && password.equals(menadzerPass)) {
//            System.out.println("cao");
//            return true;
//        } else if (vozaci.containsKey(user) && vozaci.get(user).equals(password) && !Data.vozacJePrijavljen(user)) {
//            Vozac noviVozac = getPodatke(user);
//            Data.dodajVozaca(noviVozac);
//            return true;
//        }
//        return false;
//    }
//}
