package Server;

import Util.Vozac;
import Util.Voznja;
import Util.Zahtev;

import java.util.*;

public class Data{


    private static int lastKeyVoznja=0;
    private static int lastKeyZahtev=0;
    public static Map<String, Vozac> zauzetiVozaci = Collections.synchronizedMap(new HashMap<String, Vozac>());
//    public static List<Vozac> zauzetiVozaci = Collections.synchronizedList(new ArrayList<Vozac>());
    private static Map<Integer, Voznja> aktivneVoznje = Collections.synchronizedMap(new HashMap<Integer, Voznja>());
    private static Map<Integer, Zahtev> listaZahteva = Collections.synchronizedMap(new HashMap<Integer, Zahtev>());
    private static Map<String, Vozac> aktivniVozaci = Collections.synchronizedMap(new HashMap<String, Vozac>());
    public Data() {}
    public static void dodajVoznju(Voznja novaVoznja){
        novaVoznja.voznjaId = lastKeyVoznja;
        System.out.println(novaVoznja.vozac.id);
        aktivneVoznje.put(lastKeyVoznja,novaVoznja);
        lastKeyVoznja++;
        Vozac zauzetiVozac = novaVoznja.vozac;
        zauzetiVozaci.put(zauzetiVozac.id,zauzetiVozac);
        DispicerThread.pingDispicerVoznje();
    }
    public static void dodajZahtev(Zahtev noviZahtev){
        listaZahteva.put(lastKeyZahtev,noviZahtev);
        lastKeyZahtev++;
        DispicerThread.pingDispicerZahtevi();
    }
    public static void dodajVozaca(Vozac noviVozac){
        aktivniVozaci.put(noviVozac.id,noviVozac);
        vozaciPodaci.put(noviVozac.id, noviVozac);
    }
    public static void ukloniVozaca(Vozac vozac){ aktivniVozaci.remove(vozac.id); }
    public static void ukloniZahtev(Zahtev zahtev){
        listaZahteva.remove(zahtev.zahtevId);
    }
    public static boolean vozacJePrijavljen(String id){ return aktivniVozaci.containsKey(id);}

    public static Vozac getSlobodnogVozaca(){
        Vozac kandidat=null;
        ArrayList<Vozac> listaVozaca = new ArrayList<Vozac>();
        ArrayList<Vozac> listaZauzetiVozaci = new ArrayList<Vozac>();
        synchronized (aktivniVozaci){
            listaVozaca.addAll( aktivniVozaci.values());
        }
        synchronized (zauzetiVozaci){
            listaZauzetiVozaci.addAll(zauzetiVozaci.values());
        }
        listaVozaca.removeAll(listaZauzetiVozaci);
        if(listaVozaca.size()>1){
            Random r = new Random();
            int index = r.nextInt(listaVozaca.size());
            kandidat = listaVozaca.get(index);
        }else if(listaVozaca.size()==1){
            kandidat = listaVozaca.get(0);
        }
        return kandidat;
    }

    public static void oslobodiVozaca(Vozac vozac){
        synchronized (zauzetiVozaci){
            zauzetiVozaci.remove(vozac.id);
        }
    }

    public static int getLastKeyVoznja() {
        return lastKeyVoznja;
    }

    public static int getLastKeyZahtev() {
        return lastKeyZahtev;
    }

    public static void ukloniVoznju(Voznja voznja){
        System.out.println(voznja.voznjaId);
        aktivneVoznje.remove(voznja.voznjaId);
        DispicerThread.pingDispicerVoznje();
    }
    public static List<Voznja> getAktivneVoznje() { return new ArrayList<Voznja>(aktivneVoznje.values()); }
    public static List<Zahtev> getListaZahteva() {return new ArrayList<Zahtev>(listaZahteva.values());}
    public static List<Vozac> getAktivniVozaci() {return new ArrayList<Vozac>(aktivniVozaci.values());}


    static Map<String, Vozac> vozaciPodaci = Collections.synchronizedMap(new HashMap<String,Vozac>());
    public static Vozac getPodatke(String user){
        synchronized (vozaciPodaci){
            return vozaciPodaci.get(user);
        }
    }
    public static void updatePodatke(Voznja voznja) {
        synchronized (vozaciPodaci){
            Vozac v = vozaciPodaci.get(voznja.vozac.id);
            v.brOdvezenihVoznji += 1;
            v.brPredjenihM += voznja.predjenaDistanca;
            v.doKrajaSmene -= 1;
            v.brVoznjiDoPauze -= 1;
        }
    }
}
