package Server;

import Util.Vozac;
import Util.Voznja;
import Util.Packet;
import Util.Zahtev;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Date;
import java.util.*;

import static Server.Server.dispicerThread;
import static Server.Server.vozaci;

public class Server {
    public static void main(String[]args){
        new Server();
    }


    public Server() {
        ServerSocket serverSocket = null;
        Socket socket = null;
        ObjectInputStream in = null;
        try{
            ucitajPodatke();
            DB.getInstance().dodajSmenu();
            serverSocket = new ServerSocket(3001);
            System.out.println("listening on port 3001...");


            while(true){
                socket= serverSocket.accept();
                if(socket.isConnected()){
                    in = new ObjectInputStream(socket.getInputStream());
                    System.out.println("Novi korisnik je na sistemu");
                    Packet packet = (Packet) in.readObject();

                    //Vozaci
                    if(packet.vozacUsername!=null && packet.vozacPassword!=null){
                        if (DB.getInstance().validationVozac(packet.vozacUsername, packet.vozacPassword) && !Data.zauzetiVozaci.containsKey(packet.vozacUsername)) {
                            System.out.println("Vozac: "+packet.vozacUsername+" je povezan");
                            Vozac vozac = new Vozac(packet.vozacUsername, 0, 0, 10, 25);
                            Data.dodajVozaca(vozac);
                            VozacThread thread = new VozacThread(socket, in,vozac);
                            vozaci.put(vozac.id,thread);
                            thread.start();
                        }else{
                            System.out.println("Bezuspesna validacija sa adrese "+socket.getInetAddress());
                        }
                    }
                    //Menadzer
                    else if (packet.menadzerUsername != null && packet.menadzerPassword != null) {
                        System.out.println(packet.menadzerUsername+" "+packet.menadzerPassword);
                        if (DB.getInstance().validationMenadzer(packet.menadzerUsername,packet.menadzerPassword)) {
                            System.out.println("Menadzer je prikljucen na sistem");
                            menadzerThread = new MenadzerThread(socket, in);
                            menadzerThread.start();
                        }else{
                            socket.close();
                            System.out.println("Bezuspesna validacija sa adrese "+socket.getInetAddress());
                        }
                    }
                    //Dispicer
                    else if(packet.connectionRequest && dispicerThread==null){
                        System.out.println("DispicerKlijent je prikljucen na sistem");
                        dispicerThread = new DispicerThread(socket,in);
                        dispicerThread.start();
                    }
                    else {
                        System.out.println("Dispicer je vec prijavljen");
                        //DispicerKlijent je vec prijavljen, zatvori socket
                        socket.close();
                    }
                }
                synchronized (vozaci){
                    System.out.println("Prijavljeni vozaci");
                    for (VozacThread vozac : vozaci.values()){
                        System.out.println(vozac.vozac.id);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();

        }}

    public static DispicerThread dispicerThread;
    public static MenadzerThread menadzerThread;
    public static Map<String, VozacThread> vozaci = Collections.synchronizedMap(new HashMap<String, VozacThread>());



    static void ukloniThread(String id) {
        try{
        synchronized (vozaci){
            vozaci.get(id).sleep(1000);
            vozaci.get(id).socket.close();
            vozaci.remove(id);
        }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void ucitajPodatke() {
        //Ucitava podatke iz baze etc...

//
//        FakeDB.vozaci.put("Nikola1","asdasdasd");
//        Vozac nikola = new Vozac("Nikola1", 24, 5, 4,20 );
//        FakeDB.vozaciPodaci.put(nikola.id,nikola);



//        Data.dodajVozaca(new Vozac("asd","Nikola"));
//        Data.dodajVozaca(new Vozac("2", "Janko"));
//        Data.dodajVozaca(new Vozac("@", "Petko"));
//        Data.dodajVoznju(new Voznja("Alekse Dejovića (Zvezdara)", "Ane Ahmatove (Čukarica)" ,new Vozac("asd","Nikola"),0));
//        Data.dodajVoznju(new Voznja("Trg Republike", "Aranđelovačka (Voždovac)", new Vozac("2", "Janko")));
//        Data.dodajVoznju(new Voznja("Baranjska (Zemun)", "Alekse Dejovića (Zvezdara)",new Vozac("@", "Petko"),2));
//        List<Vozac> aktivniVozaci = Data.getAktivniVozaci();
//        Random r = new Random();
//        int zahtevId = 0;
//        for (Vozac vozac : aktivniVozaci){
//            System.out.println(zahtevId);
//            Data.dodajZahtev(new Zahtev(vozac, r.nextInt(2), zahtevId));
//            zahtevId++;
//        }
    }

    public static void odjaviMenadzera(){
        synchronized (menadzerThread) {
            try{
                    menadzerThread.sleep(2000);
                    menadzerThread.socket.close();
                    menadzerThread.alive = false;
                    menadzerThread = null;
                System.out.println("Menadzer je odjavljen sa sistema");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void obradiZahtev(Zahtev zahtev, boolean vrednost) {
        synchronized (vozaci){
            if(vrednost){
                if(zahtev.tipZahteva.equals("smena")){
                    //Zahtev za kraj smene, odjavljujemo vozaca
                    Data.ukloniVozaca(zahtev.vozac);
                    vozaci.get(zahtev.vozac.id).posaljiPoruku("poruka,zahtev je odobren "+zahtev.tipZahteva);
                    ukloniThread(zahtev.vozac.id);
                }else{
                    //Zahtev za pauzu
                    Data.ukloniVozaca(zahtev.vozac);
                    vozaci.get(zahtev.vozac.id).posaljiPoruku("poruka,zahtev je odobren "+zahtev.tipZahteva);
                }

            }else {
                vozaci.get(zahtev.vozac.id).posaljiPoruku("poruka,zahtev je odbijen "+zahtev.tipZahteva);
            }
            Data.ukloniZahtev(zahtev);
        }
    }

    public static void odjaviDispicera() {
        synchronized (dispicerThread) {
            try{
                dispicerThread.sleep(2000);
                dispicerThread.socket.close();
                dispicerThread.alive = false;
                dispicerThread = null;
                System.out.println("Dispicer se iskljucio sa sitema");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class DispicerThread extends Thread{
    public Socket socket = null;
    ObjectInputStream in = null;
    public boolean alive = true;
    static ObjectOutputStream out = null;
    public boolean approved= false;

    public DispicerThread(Socket socket,ObjectInputStream in){
        this.socket = socket;
        this.in = in;

    }

    public void run(){
        try{
            out = new ObjectOutputStream(socket.getOutputStream());

            Packet packet;

            while(alive) {
                if (socket.isConnected()) {

                    if (!approved) {
                        //Thread preuzima dispicera
                        Packet p = new Packet();
                        p.connectionIsApproved = true;
                        out.writeObject(p);
                        approved=true;
                        //Posalji podatke koji ga cekaju
                        pingDispicerZahtevi();
                    }

                    packet = (Packet) in.readObject();
                    if(packet!=null){
                        if(packet.voznja.novaVoznja && packet.msg.equals("nova voznja")){
                            //Nova voznja
                            /*
                            * 1) Pronadji vozaca
                            * 2) Vrati informacije dispiceru
                            * 3) Sacekaj povratne informacije od dispicera
                            * 4) Salji voznju vozacu / Otkazi voznju
                            */
                            System.out.println("Primljena voznja za obradjivanje");
                            Packet p = new Packet();
                            //Pronadji vozaca
                            Vozac izabraniVozac = Data.getSlobodnogVozaca();
                            if(izabraniVozac!=null){
                                p.voznja = packet.voznja;
                                Random r = new Random();
                                p.voznja.cekanje = r.nextInt(10);
                                p.msg = "nova voznja";
                                out.writeObject(p);
                                //Vracanje informacija disipceru
                                System.out.println("Odgovor je poslat, cekamo potvrdu voznje...");
                                Packet response = (Packet) in.readObject();

                                //Obrada povratnih informacija
                                if(response.odgovor){
                                    p.odgovor=true;
                                    p.msg = "nova voznja";
                                    out.writeObject(p);

                                    //Dodeli voznju <----------------------------------------
                                    Voznja voznja = new Voznja(p.voznja.pocetnaTacka,p.voznja.krajnjaTacka,izabraniVozac);
                                    Data.dodajVoznju(voznja);
                                    System.out.println("VOZAC: "+vozaci.get(izabraniVozac.id));
                                    voznja.novaVoznja = true;
                                    vozaci.get(izabraniVozac.id).posaljiVoznju(voznja);

                                    System.out.println("Uspesno je dodata nova voznja");
                                }else{
                                    System.out.println("Nista");
                                }
                            }else{
                                System.out.println("Nema slobodnih vozaca");
                                Packet packetNemaVozaca = new Packet();
                                packetNemaVozaca.nemaVozaca=true;
                                packetNemaVozaca.msg = "nema vozaca";
                                out.writeObject(packetNemaVozaca);
                            }

                        }
                        else if(packet.getListuVoznji){
                            System.out.println("Lista aktivnih voznji je poslata");
                            pingDispicerVoznje();
                            //Salji listu voznji
                        }
                        else if(packet.getListuZahteva){
                            //Salji listu zahteva
                            System.out.println("Lista zahteva je poslata");
                            pingDispicerZahtevi();
                        }
                        else if (packet.voznja.otkaziVoznju) {
                            //Obrisi voznju, posalji novu listu, kazi vozacu da je voznja zavrsena
                            Data.oslobodiVozaca(packet.voznja.vozac);
                            Data.ukloniVoznju(packet.voznja);
                            vozaci.get(packet.voznja.vozac.id).posaljiPoruku("voznja je otkazana");
                            Packet p = new Packet();
                            p.msg = "voznja je otkazana";
                            out.writeObject(p);

                        }
                        else if (packet.msg.equals("odobren") || packet.msg.equals("odbijen")) {
                            System.out.println("obradjeni zahtev je dobijen");
                            boolean vrednost=false;
                            if(packet.msg.equals("odobren")){
                                vrednost = true;
                            }else if(packet.msg.equals("odbijen")){
                                vrednost = false;
                            }
                            Server.obradiZahtev(packet.zahtev,vrednost);
                        } else if (packet.msg.equals("upisi voznju")) {
                            System.out.println("Naknadno upisivanje voznje");
                            DB.getInstance().dodajOdvezenuVoznju(packet.voznja);
                        }

                    }

                }

            }
        } catch (EOFException e){
            Server.odjaviDispicera();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void pingDispicerVoznje(){
        try{
            if(dispicerThread!=null){
                Packet updateListu = new Packet();
                updateListu.listaVoznji = Data.getAktivneVoznje();
//                System.out.println("Pingamo: "+Arrays.toString(updateListu.listaVoznji.toArray()));
                out.writeObject(updateListu);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void pingDispicerZahtevi(){
        try{
            if(dispicerThread!=null){
                Packet updateListu = new Packet();
                updateListu.listaZahteva = Data.getListaZahteva();
//                System.out.println("Pingamo: "+Arrays.toString(updateListu.listaZahteva.toArray()));
                out.writeObject(updateListu);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class VozacThread extends Thread{
    Vozac vozac;
    public Socket socket = null;
    ObjectInputStream in = null;
    ObjectOutputStream out = null;
    VozacThread inputThread;
    boolean approved = false;

    public VozacThread(Socket socket,ObjectInputStream in,Vozac vozac){
        this.socket = socket;
        this.in = in;
        this.vozac = vozac;
    }

    @Override
    public void run() {
        try{
            out = new ObjectOutputStream(socket.getOutputStream());
                if (socket.isConnected()) {
                    if(!approved){
                        Packet packet = new Packet();
                        packet.connectionIsApproved = true;
                        out.writeObject(packet);
                        approved= true;

                        //RESI OVAJ THREAD
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    while(true){
                                        Packet p = (Packet) in.readObject();
                                        if(p!=null){
                                            if (p.msg.equals("gotova voznja")) {
                                                //Upis u bazu
                                                System.out.println("Gotova voznja" + p.voznja.vozac.id + " od " + p.voznja.pocetnaTacka + " do " + p.voznja.krajnjaTacka + ", cena: " + p.voznja.cenaVoznje);
                                                DB.getInstance().dodajOdvezenuVoznju(p.voznja);
                                                Data.updatePodatke(p.voznja);
                                                Data.oslobodiVozaca(p.voznja.vozac);
                                                Data.ukloniVoznju(p.voznja);


//                                                Voznja voznja = new Voznja();
//                                                voznja.pripravnost = true;
//                                                Packet p1 = new Packet(voznja);
//                                                out.writeObject(p1);
                                            } else if (p.msg.equals("odjava")) {
                                                //salji zahtev dispiceru
                                                System.out.println("Zahtev za odjavu primljen");
                                                Zahtev zahtev = new Zahtev(p.voznja.vozac, "smena", Data.getLastKeyZahtev());
                                                Data.dodajZahtev(zahtev);
                                            }else if(p.msg.equals("pauza")){
                                                System.out.println("Zahtev za pauzu je primljen");
                                                Zahtev zahtev = new Zahtev(p.voznja.vozac, "pauza", Data.getLastKeyZahtev());
                                                Data.dodajZahtev(zahtev);
                                            } else if (p.msg.equals("podaci")) {
                                                Vozac v = new Vozac(p.vozacUsername, 0, 0, 10, 25);
                                                posaljiPodatke(v);
                                            } else if (p.msg.equals("kraj pauze")) {
                                                System.out.println("Vozac je zavrsio pauzu");
                                                Data.dodajVozaca(Data.getPodatke(p.vozacUsername));
                                            }
                                        }
                                    }

                                }catch(SocketException e){
                                    System.out.println("socket is closed");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        t.start();

                    }
                }
        } catch (EOFException e){
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void posaljiPodatke(Vozac vozac){
        try{
            Packet p1 = new Packet();
            p1.msg = "podaci";
            p1.voznja.vozac = vozac;
            System.out.println("Saljemo podatke vozacu");
            out.writeObject(p1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void posaljiVoznju(Voznja voznja){
        try{
            System.out.println("Saljemo novu voznju vozacu");
            Packet packet = new Packet(voznja);
            packet.voznja.novaVoznja = true;
            packet.msg = "nova voznja";
            out.writeObject(packet);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void posaljiPoruku(String msg) {
        try{
            System.out.println("Saljemo poruku");
            Packet p = new Packet();
            p.msg = msg;
            out.writeObject(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class MenadzerThread extends Thread{
    Socket socket;
    ObjectInputStream in;
    static ObjectOutputStream out;
    public boolean alive = true;
    public boolean approved= false;

    public MenadzerThread(Socket socket,ObjectInputStream in){
        this.socket = socket;
        this.in = in;
    }

    @Override
    public void run() {
        try {
            System.out.println("");
            out = new ObjectOutputStream(socket.getOutputStream());
            Packet packet;
            while (alive) {
                if (socket.isConnected()) {
                    if (!approved) {
                        //Thread preuzima dispicera
                        Packet p = new Packet();
                        p.connectionIsApproved = true;
                        out.writeObject(p);
                        approved = true;
                    }
                    packet = (Packet) in.readObject();
                    if (packet != null) {
                        if (packet.msg.equals("get vozaci")) {
                            Packet p = new Packet();
                            p.listaVozaca = DB.getInstance().getVozace();
                            out.writeObject(p);
                        } else if (packet.msg.equals("izbrisi vozaca")) {
                            Packet p = new Packet();
                            if (DB.getInstance().izbrisiVozaca(packet.vozac)) {
                                p.msg = "izbrisan";
                            } else {
                                p.msg = "greska";
                            }
                            out.writeObject(p);
                        } else if (packet.msg.equals("dodaj vozaca")) {
                            Packet p = new Packet();
                            if (DB.getInstance().dodajVozaca(packet.vozac)) {
                                p.msg = "dodat";
                            } else {
                                p.msg = "greska";
                            }
                            out.writeObject(p);
                        } else if (packet.msg.equals("izmeni vozaca")) {
                            Packet p = new Packet();
                            if (DB.getInstance().izmeniVozaca(packet.vozac.id, packet.vozac)) {
                                p.msg = "izmenjen";
                            } else {
                                p.msg = "greska";
                            }
                            out.writeObject(p);
                        } else if (packet.msg.equals("dnevni izvestaj")) {
                            Packet p = new Packet();
                            p.izvestaj = DB.getInstance().dnevniIzvestaj();
                            out.writeObject(p);
                        } else if (packet.msg.equals("mesecni izvestaj")) {
                            Packet p = new Packet();
                            p.izvestaj = DB.getInstance().mesecniIzvestaj();
                            out.writeObject(p);
                        } else if (packet.msg.equals("logout")) {
                            Packet p = new Packet();
                            p.msg = "logout";
                            out.writeObject(p);
                            Server.odjaviMenadzera();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
