package Vozac;

import Util.Vozac;
import Util.Packet;
import Util.Voznja;
import javafx.application.Platform;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class VozacKlijent {
    private static String userId;
    public static Socket socket;
    public static ObjectOutputStream outputStream;
    public static ObjectInputStream inputStream;
    public static boolean uVoznji= false;
    private static Vozac vozacPodaci;

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

    public static Vozac getVozacPodaci() {
        return vozacPodaci;
    }

    public static void setVozacPodaci(Vozac vozacPodaci) {
        VozacKlijent.vozacPodaci = vozacPodaci;
    }



    public static void preuzmiPodatke(){
        try{
            if(vozacPodaci==null){
                Packet p = new Packet();
                p.vozacUsername = userId;
                p.msg = "podaci";
                outputStream.writeObject(p);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void updatePodatke(Voznja voznja){
        vozacPodaci.brVoznjiDoPauze+=-1;
        vozacPodaci.brOdvezenihVoznji += 1;
        vozacPodaci.doKrajaSmene += -1;
        vozacPodaci.brPredjenihM += voznja.predjenaDistanca;
    }


    public static boolean zavrsiVoznju(Voznja voznja){
        try {
            updatePodatke(voznja);
            Packet packet1 = new Packet();
            packet1.voznja = voznja;
            System.out.println(packet1.voznja.toString());
            System.out.println("Zavrsavam voznju");
            packet1.msg = "gotova voznja";
            outputStream.writeObject(packet1);
            uVoznji = false;
            System.out.println("Voznja je poslata");

        } catch (IOException e) {
            e.printStackTrace();
        }


        return true;
    }

    public static boolean prijava(String id, String password){
        boolean val = false;
        try {
            Packet packet = new Packet();
            packet.vozacUsername = id;
            packet.vozacPassword = password;
            outputStream.writeObject(packet);
            inputStream = new ObjectInputStream(socket.getInputStream());
            Packet response = (Packet) inputStream.readObject();
            val = response.connectionIsApproved;
            if(val){
                userId = id;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return val;
    }

    public static void krajPauze(){
        try{
            Packet packet = new Packet();
            packet.vozacUsername = userId;
            packet.msg = "kraj pauze";
            outputStream.writeObject(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Zahtev
    public static boolean pauza(){
        try {
            Vozac vozac = new Vozac();
            vozac.id = userId;
            Packet p = new Packet();
            p.voznja.vozac = vozacPodaci;
            p.msg = "pauza";
            outputStream.writeObject(p);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    //Zahtev
    public static boolean odjava(){
        try {
            Vozac vozac = new Vozac();
            vozac.id = userId;
            Packet p = new Packet();
            p.voznja.vozac = vozacPodaci;
            p.msg = "odjava";
            outputStream.writeObject(p);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
class UpdateThread extends Thread{
    public Socket socket;
    public ObjectInputStream inputStream;

    public UpdateThread() {
        this.socket = VozacKlijent.socket;
        this.inputStream = VozacKlijent.inputStream;
    }

    @Override
    public void run() {
        try{
            System.out.println("Update thread radi");
            while (true){
                Packet packet = (Packet) inputStream.readObject();
                if(packet!=null && ! VozacKlijent.uVoznji){
                    if(packet.voznja.novaVoznja && packet.msg.equals("nova voznja")){
                        System.out.println("Primljena nova voznja");
                        VozacKlijent.uVoznji = true;
                        Prozori.novaVoznja(packet.voznja);
                    }else if(packet.msg.equals("odjava")){
                        System.out.println("odjava");
                        Platform.exit();
                    }else if(packet.msg.contains("poruka")){
                        String poruka = packet.msg.split(",")[1];
                        if (poruka.equals("zahtev je odbijen pauza") || poruka.equals("zahtev je odbijen smena")) {
                            System.out.println("zathev je odbijen");
                        } else if (poruka.equals("zahtev je odobren pauza")) {
                            Prozori.naPauzi();
                            System.out.println("zahtev za pauzu je odobren");
                        } else if (poruka.equals("zahtev je odobren smena")) {
                            System.out.println("zahtev za kraj smene je odobren");
                            Platform.exit();
                        }
                    }else if(packet.msg.equals("podaci")){
                        System.out.println("Podaci su primljeni");
                        VozacKlijent.setVozacPodaci(packet.voznja.vozac);
                    }

                }
                else{

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if(Prozori.getIdleStage() != null){
                                Prozori.getIdleStage().close();
                            }
                            if (Prozori.getDoMusterijeStage() != null) {
                                Prozori.getDoMusterijeStage().close();
                                VozacKlijent.uVoznji = false;
                                Prozori.uVoznji = false;
                            }

                            Prozori.idle();

                        }
                    });
                }
            }
        } catch (EOFException e){}
        catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}



