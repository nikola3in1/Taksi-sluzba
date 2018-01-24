package Dispicer;

import Util.Packet;
import Util.Voznja;
import Util.Zahtev;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;

import javax.xml.bind.SchemaOutputResolver;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class DispicerKlijent {
    public static Socket socket;
    public static ObjectOutputStream outputStream;

    public void init(){
        try {

            socket = new Socket("localhost", 3001);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            DispicerUpdateThread t = new DispicerUpdateThread(socket);
            t.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logInZahtev(){
        try{
            Packet packet = new Packet();
            packet.connectionRequest=true;
            outputStream.writeObject(packet);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean potvrdiVoznju(Voznja voznja){
        final FutureTask query = new FutureTask(new Callable() {
            @Override
            public Object call() throws Exception {
                ButtonType da = new ButtonType("Da", ButtonBar.ButtonData.OK_DONE);
                ButtonType ne = new ButtonType("Ne", ButtonBar.ButtonData.CANCEL_CLOSE);
                Alert alert = new Alert(Alert.AlertType.NONE,
                        "Najblizi taksi moze da pokupi musteriju za "+voznja.cekanje+" minuta."
                                + " Da li to odgovara?",
                        da,
                        ne);
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.setTitle("Potvrdite vožnju");
                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get() == da) {
                    return true;
                }else{
                    return false;
                }
            }
        });
        Platform.runLater(query);
        boolean odg = false;
        try {
            odg = (boolean) query.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
            return odg;
    }


    public static void posaljiZahtev(Zahtev zahtev, boolean uspesnost){
        try {
            Packet p = new Packet();
            p.zahtev = zahtev;
            if (uspesnost) {
                p.msg = "odobren";
            } else {
                p.msg = "odbijen";
            }
            outputStream.writeObject(p);
            System.out.println("Zahtev je obradjen");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void saljiOdg(Packet p){
        try {
            outputStream.writeObject(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void posaljiVoznju(Voznja voznja){
        try{
            Packet packet = new Packet(voznja);
            packet.voznja.novaVoznja=true;
            packet.msg = "nova voznja";
            System.out.println("Slanje voznje");
            outputStream.writeObject(packet);
            System.out.println("Voznja uspesno poslata");


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getListaVoznji(){
        try {
            Packet packet = new Packet();
            packet.getListuVoznji = true;
            outputStream.writeObject(packet);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getListuZahteva(){
        try {
            Packet packet = new Packet();
            packet.getListuZahteva = true;
            outputStream.writeObject(packet);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void upisiVoznju(Voznja voznja){
        System.out.println(voznja.toString());
        try {
            Packet packet = new Packet();
            packet.voznja = voznja;
            packet.msg = "upisi voznju";
            outputStream.writeObject(packet);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void otkaziVoznju(Voznja voznja){
        try{
            Packet packet = new Packet();
            voznja.otkaziVoznju=true;
            packet.voznja = voznja;
            outputStream.writeObject(packet);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class DispicerUpdateThread extends Thread{
    ObjectInputStream inputStream;
    Socket socket;
    public static boolean alive = true;

    DispicerUpdateThread(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            System.out.println("DispicerUpdateThread radi");
            inputStream = new ObjectInputStream(socket.getInputStream());
            while (alive) {
                Packet p = (Packet) inputStream.readObject();
                if (p.connectionIsApproved) {
                    System.out.println("Login approved");
                } else if (p.listaZahteva != null) {
                    ObservableList<Zahtev> tempZahtevi = FXCollections.observableArrayList(p.listaZahteva);
                    App.setListaZahteva(tempZahtevi);
                } else if (p.listaVoznji != null) {
                    ObservableList<Voznja> tempVoznje = FXCollections.observableArrayList(p.listaVoznji);
                    App.setListaVoznji(tempVoznje);
                } else if (p.msg.equals("voznja je otkazana")) {
                    System.out.println("voznja je otkazana");
                } else if (!p.nemaVozaca && p.msg.equals("nova voznja")) {
                    System.out.println("nova voznja");
                    //Nova voznja
                    //Odgovor
                    //Pitaj dispicera da li odgovoara voznja
                    boolean odg = DispicerKlijent.potvrdiVoznju(p.voznja);
                    System.out.println("odg je : " + odg);
                    if (odg) {
                        p.odgovor = true;
                        DispicerKlijent.saljiOdg(p);
                        System.out.println("Voznja potvrdjena");
                        p = (Packet) inputStream.readObject();
                        System.out.println(p.voznja.cekanje);
                    } else {
                        p.odgovor = false;
                        DispicerKlijent.saljiOdg(p);
                        System.out.println("Voznja je otkazana");
                    }
                } else if (p.msg.equals("nema vozaca")) {
                    App.nemaVozaca();
                    System.out.println("nema vozaca");
                }
            }
        } catch (SocketException e) {
            System.out.println("Odjava");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}