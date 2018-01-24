package Menadzer;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

class MenadzerAppTest {


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

}