package Util;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Vozac implements Serializable {
    public String id;

    @Override
    public String toString() {
        return "Vozac{" +
                "id='" + id + '\'' +
                ", ime='" + ime + '\'' +
                ", prezime='" + prezime + '\'' +
                ", brOdvezenihVoznji=" + brOdvezenihVoznji +
                ", brPredjenihKM=" + brPredjenihM +
                ", brVoznjiDoPauze=" + brVoznjiDoPauze +
                '}';
    }

    public String ime;
    public String prezime;
    public String pass;

    public String getId() {
        return id;
    }

    public String getIme() {
        return ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public String getPass() {
        return pass;
    }

    public Vozac(String id, String ime, String prezime, String pass) {

        this.id = id;
        this.ime = ime;
        this.prezime = prezime;
        this.pass = pass;
    }

    public int brOdvezenihVoznji;
    public int brPredjenihM;
    public int brVoznjiDoPauze;
    public int doKrajaSmene;

    public Vozac(String id, int brOdvezenihVoznji, int brPredjenihKM, int brVoznjiDoPauze, int doKrajaSmene) {
        this.id = id;
        this.brOdvezenihVoznji = brOdvezenihVoznji;
        this.brPredjenihM = brPredjenihM;
        this.brVoznjiDoPauze = brVoznjiDoPauze;
        this.doKrajaSmene = doKrajaSmene;
    }

    public Vozac(String id, String ime) {
        this.id = id;
        this.ime = ime;

    }

    public Vozac(String id, String ime,String prezime) {
        this.id = id;
        this.ime = ime;
        this.prezime = prezime;
    }
    public Vozac(){}

}
