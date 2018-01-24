package Menadzer;

import Util.Izvestaj;
import Util.Vozac;
import Util.Packet;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MenadzerKlijent {
    public static Socket socket;
    public static ObjectOutputStream outputStream;
    public static ObjectInputStream inputStream;

    public static void init(){

        try {
            socket = new Socket("localhost", 3001);
            outputStream = new ObjectOutputStream(socket.getOutputStream());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Vozac> getVozaci() {
        try{
            Packet p = new Packet();
            p.msg = "get vozaci";
            outputStream.writeObject(p);
            Packet p1 = (Packet) inputStream.readObject();
            return p1.listaVozaca;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void logout() {
        try{
            Packet p = new Packet();
            p.msg = "logout";
            outputStream.writeObject(p);
            Packet p1 = (Packet) inputStream.readObject();
            if (p1.msg.equals("logout")) {
                Platform.exit();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean izbrisiVozaca(Vozac vozac) {
        try{
            Packet p = new Packet();
            p.vozac.id= vozac.id;
            p.msg = "izbrisi vozaca";
            outputStream.writeObject(p);
            Packet p1 = (Packet) inputStream.readObject();
            if(p1.msg.equals("izbrisan")){
                System.out.println("izbrisan");
                return true;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean dodajVozaca(Vozac vozac) {
        try{
            Packet p = new Packet();
            p.vozac= vozac;
            p.msg = "dodaj vozaca";
            outputStream.writeObject(p);
            Packet p1 = (Packet) inputStream.readObject();
            if(p1.msg.equals("dodat")){
                return true;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean izmeniVozaca(Vozac vozac) {
        try{
            Packet p = new Packet();
            p.vozac= vozac;
            p.msg = "izmeni vozaca";
            outputStream.writeObject(p);
            Packet p1 = (Packet) inputStream.readObject();
            if(p1.msg.equals("izmenjen")){
                return true;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void getMesecniIzvestaj() {
        try{
            Packet p = new Packet();
            p.msg = "mesecni izvestaj";
            outputStream.writeObject(p);
            Packet p1 = (Packet) inputStream.readObject();
            ArrayList<Izvestaj> izvestajs = p1.izvestaj;
            if (izvestajs != null && !izvestajs.isEmpty()) {
                GenerisiIzvestaj.csv(izvestajs);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void getDnevniIzvestaj() {
        try{
            Packet p = new Packet();
            p.msg = "dnevni izvestaj";
            outputStream.writeObject(p);
            Packet p1 = (Packet) inputStream.readObject();
            ArrayList<Izvestaj> izvestajs = p1.izvestaj;
            if (izvestajs != null && !izvestajs.isEmpty()) {
                GenerisiIzvestaj.csv(izvestajs);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static boolean prijava(String id, String password){
        boolean val = false;

        System.out.println(id +" "+password);
        try {
            Packet packet = new Packet();
            packet.menadzerUsername = id;
            packet.menadzerPassword = password;
            outputStream.writeObject(packet);
            inputStream = new ObjectInputStream(socket.getInputStream());
            Packet response = (Packet) inputStream.readObject();
            val = response.connectionIsApproved;
        }catch (EOFException e){
            return false;
        }catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return val;
    }
}

class GenerisiIzvestaj{

    static void csv(ArrayList<Izvestaj> izvestaj){
            Date d = new Date();
        System.out.println(d.toString());
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File("izvestaj - "+d.toString()+".csv"));
            StringBuilder sb = new StringBuilder();
            sb.append("Datum");
            sb.append(',');
            sb.append("VozacID");
            sb.append(',');
            sb.append("Pocetna tacka");
            sb.append(',');
            sb.append("Krajnja tacka");
            sb.append(',');
            sb.append("Cena");
            sb.append(',');
            sb.append("Trajanje");
            sb.append('\n');

            for (Izvestaj i : izvestaj) {
                sb.append(i.getDatum());
                sb.append(',');
                sb.append(i.getVozacId());
                sb.append(',');
                sb.append(i.getPocetnaTacka());
                sb.append(',');
                sb.append(i.getKrajnjaTacka());
                sb.append(',');
                sb.append(i.getCena());
                sb.append(',');
                sb.append(i.getTrajanje());
                sb.append('\n');
            }
            pw.write(sb.toString());
            pw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}