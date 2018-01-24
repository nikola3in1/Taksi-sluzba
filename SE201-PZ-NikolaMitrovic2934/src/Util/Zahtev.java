package Util;

import java.io.Serializable;

public class Zahtev implements Serializable{
    public int zahtevId;
    public Vozac vozac;
    public String tipZahteva; // pauza || smena
    public static int maxZahtevId;

    @Override
    public String toString() {
        return "Zahtev{" +
                "vozac=" + vozac +
                ", tipZahteva=" + tipZahteva +
                '}';
    }

    public Zahtev(Vozac vozac, String tipZahteva, int zahtevId) {
        this.vozac = vozac;
        this.tipZahteva = tipZahteva;
        this.zahtevId = zahtevId;
    }

    public Zahtev(){

    }
}
