package Util;

import java.io.Serializable;

public class Izvestaj implements Serializable{
    private String datum;
    private String vozacId;
    private String pocetnaTacka;
    private String krajnjaTacka;
    private int cena;
    private int trajanje;

    @Override
    public String toString() {
        return "Izvestaj{" +
                "datum='" + datum + '\'' +
                ", vozacId='" + vozacId + '\'' +
                ", pocetnaTacka='" + pocetnaTacka + '\'' +
                ", krajnjaTacka='" + krajnjaTacka + '\'' +
                ", cena=" + cena +
                ", trajanje=" + trajanje +
                '}';
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getVozacId() {
        return vozacId;
    }

    public void setVozacId(String vozacId) {
        this.vozacId = vozacId;
    }

    public String getPocetnaTacka() {
        return pocetnaTacka;
    }

    public void setPocetnaTacka(String pocetnaTacka) {
        this.pocetnaTacka = pocetnaTacka;
    }

    public String getKrajnjaTacka() {
        return krajnjaTacka;
    }

    public void setKrajnjaTacka(String krajnjaTacka) {
        this.krajnjaTacka = krajnjaTacka;
    }

    public int getCena() {
        return cena;
    }

    public void setCena(int cena) {
        this.cena = cena;
    }

    public int getTrajanje() {
        return trajanje;
    }

    public void setTrajanje(int trajanje) {
        this.trajanje = trajanje;
    }

    public Izvestaj(String datum, String vozacId, String pocetnaTacka, String krajnjaTacka, int cena, int trajanje) {
        this.datum = datum;
        this.vozacId = vozacId;
        this.pocetnaTacka = pocetnaTacka;
        this.krajnjaTacka = krajnjaTacka;
        this.cena = cena;
        this.trajanje = trajanje;
    }

}
