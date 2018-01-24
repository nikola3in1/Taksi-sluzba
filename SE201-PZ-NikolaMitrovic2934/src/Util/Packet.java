package Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Packet implements Serializable {
    public boolean nemaVozaca;
    public boolean odgovor;
    public Voznja voznja = new Voznja();
    public boolean getListuVoznji;
    public boolean getListuZahteva;
    public List<Zahtev> listaZahteva;
    public List<Voznja> listaVoznji;
    public Vozac vozac = new Vozac();
    public ArrayList<Vozac> listaVozaca;
    public int minuti = 0;
    public boolean connectionRequest = false;
    public boolean connectionIsApproved = false;
    public String menadzerUsername;
    public String menadzerPassword;
    public String vozacUsername;
    public String vozacPassword;
    public String msg = "";
    public Zahtev zahtev = new Zahtev();
    public ArrayList<Izvestaj> izvestaj;


    public Packet(Voznja voznja) {
        this.voznja = voznja;
    }

    public Packet(Voznja voznja, boolean novaVoznja) {
        this.voznja = voznja;
        this.voznja.novaVoznja = novaVoznja;
    }

    public Packet() {
    }

    public Packet(boolean odgovor, int minuti) {
        this.odgovor = odgovor;
        this.minuti = minuti;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "odgovor=" + odgovor +
                ", voznja=" + voznja.toString() +
                ", getListuVoznji=" + getListuVoznji +
                ", getListuZahteva=" + getListuZahteva +
                ", listaZahteva=" + listaZahteva +
                ", listaVoznji=" + listaVoznji +
                ", listaVozaca=" + listaVozaca +
                ", minuti=" + minuti +
                ", connectionRequest=" + connectionRequest +
                ", connectionIsApproved=" + connectionIsApproved +
                '}';
    }
}
