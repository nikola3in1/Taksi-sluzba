package Util;

import java.io.Serializable;

public class Voznja implements Serializable{
    public boolean novaVoznja;

    @Override
    public String toString() {
        return "Voznja{" +
                "novaVoznja=" + novaVoznja +
                ", idVozaca=" + vozac.toString() +
                ", cekanje=" + cekanje +
                ", pocetnaTacka='" + pocetnaTacka + '\'' +
                ", krajnjaTacka='" + krajnjaTacka + '\'' +
                '}';
    }

    public boolean pripravnost;
    public int voznjaId;
    public boolean otkaziVoznju;
    public boolean upisiVoznju;
    public boolean zavrsiVoznju;
    public Vozac vozac = new Vozac();
    public int cekanje;
    public String pocetnaTacka;
    public String krajnjaTacka;
    public int cenaVoznje;
    public int predjenaDistanca;



    public Voznja(String pocetnaTacka, String krajnjaTacka) {
        this.pocetnaTacka = pocetnaTacka;
        this.krajnjaTacka = krajnjaTacka;
        this.vozac = new Vozac();
    }
    public Voznja(String pocetnaTacka, String krajnjaTacka, Vozac vozac) {
        this.pocetnaTacka = pocetnaTacka;
        this.krajnjaTacka = krajnjaTacka;
        this.vozac = vozac;
    }
    public Voznja(String pocetnaTacka, String krajnjaTacka, Vozac vozac, int voznjaId) {
        this.pocetnaTacka = pocetnaTacka;
        this.krajnjaTacka = krajnjaTacka;
        this.vozac = vozac;
        this.voznjaId = voznjaId;

    }


    public Voznja() {
    }
}
