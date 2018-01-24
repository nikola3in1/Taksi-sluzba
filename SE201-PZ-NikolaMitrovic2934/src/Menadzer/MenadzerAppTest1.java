package Menadzer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MenadzerAppTest1 {

    @Test
    public void testLoginValidation() {
        String a = "test";
        String b = "tset";
        boolean expResult = false;
        boolean result = new MenadzerApp().validirano(a,b);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }
    @Test
    public void testLoginValidation1() {
        String a = "";
        String b = "";
        boolean expResult = false;
        boolean result = new MenadzerApp().validirano(a,b);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }
    @Test
    public void testLoginValidation2() {
        String a = null;
        String b = null;
        boolean expResult = false;
        boolean result = new MenadzerApp().validirano(a,b);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }
    @Test
    public void testLoginValidation3() {
        String a = " ";
        String b = " ";
        boolean expResult = false;
        boolean result = new MenadzerApp().validirano(a,b);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }
    @Test
    public void testLoginValidation4() {
        String a = "nikola";
        String b = "asd";
        boolean expResult = true;
        boolean result = new MenadzerApp().validirano(a,b);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }
    @Test
    public void testLoginValidation5() {
        String a = "nikola";
        String b = "asd";
        boolean expResult = true;
        boolean result = new MenadzerApp().validirano(a,b);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }
    @Test
    public void testFormu() {
        String id = "";
        String ime = "";
        String prezime = "";
        String pass= "";
        boolean expResult = false;
        boolean result = MenadzerApp.validirajFormu(id,ime,prezime,pass);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }
    @Test
    public void testFormu1() {
        String id = null;
        String ime = null;
        String prezime = null;
        String pass= null;
        boolean expResult = false;
        boolean result = MenadzerApp.validirajFormu(id,ime,prezime,pass);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }
    @Test
    public void testFormu2() {
        String id = "nikola1";
        String ime = "nikola";
        String prezime = "mitrovic";
        String pass= "asdasdas";
        boolean expResult = true;
        boolean result = MenadzerApp.validirajFormu(id,ime,prezime,pass);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    @Test
    public void testFormu3() {
        String id = "nikola1";
        String ime = "nikola";
        String prezime = "mitrovic";
        String pass= "asd";
        boolean expResult = false;
        boolean result = MenadzerApp.validirajFormu(id,ime,prezime,pass);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }
    @Test
    public void testFormu4() {
        String id = "  ";
        String ime = "  ";
        String prezime = "  ";
        String pass= "  ";
        boolean expResult = false;
        boolean result = MenadzerApp.validirajFormu(id,ime,prezime,pass);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }



}