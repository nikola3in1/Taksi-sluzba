package Vozac;

import Util.Vozac;
import Util.Voznja;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class VozacApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox();
        HBox user = new HBox();
        HBox pass = new HBox();
        TextField idField = new TextField();
        PasswordField passField = new PasswordField();

        Button login = new Button("Login");
        user.getChildren().addAll(new Text("Username:"), idField);
        pass.getChildren().addAll(new Text("Password: "), passField);
        root.getChildren().addAll(new Text("Taksi sluzba"), user, pass, login);

        user.setAlignment(Pos.CENTER);
        pass.setAlignment(Pos.CENTER);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(15);
        login.setMinSize(120,40);

        login.setOnMouseClicked(event ->{
            if (validirano(idField.getText(), passField.getText())) {
                System.out.println("logovan");
                Prozori.idle();
                primaryStage.close();
            }
        });
        primaryStage.setScene(new Scene(root, 1024, 600));
        primaryStage.show();
    }

    boolean validirano(String user, String pass) {
        if (user != null && pass != null && user.length() > 0 && pass.length() > 0) {
            VozacKlijent.init();
            if(VozacKlijent.prijava(user, pass)){ return true; }
        }

        return false;
    }
}
class Prozori{

    public static Text brVoznjiTxt = new Text("Broj odvezenih voznji: ");
    public static Text brKmTxt =new Text("Broj predjenih kilometara: ");
    public static Text doKrajaSmeneTxt = new Text("Do kraja smene: ");
    public static Text doSledecePauzeTxt = new Text("Broj voznji do sledce pauze: ");
    public static boolean uVoznji = false;
    private static Voznja voznja;
    private static Stage idleStage;

    public static Stage getDoMusterijeStage() {
        return doMusterijeStage;
    }

    public static void setDoMusterijeStage(Stage doMusterijeStage) {
        Prozori.doMusterijeStage = doMusterijeStage;
    }

    private static Stage doMusterijeStage;
    static UpdateThread updateThread;

    public static Stage getIdleStage() {
        return idleStage;
    }

    public static void setIdleStage(Stage idleStage) {
        Prozori.idleStage = idleStage;
    }

    static void idle(){
        //UpdateThread init
        if(updateThread == null){
            updateThread = new UpdateThread();
            updateThread.start();
        }
        VozacKlijent.preuzmiPodatke();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        updatePodatke(VozacKlijent.getVozacPodaci());

        if (doMusterijeStage != null && doMusterijeStage.isShowing()) {
            doMusterijeStage.close();
        }


//        Preuzimanje podataka

        idleStage= new Stage();
        HBox header = new HBox();
        VBox root = new VBox();
        VBox info = new VBox();

        Font font = new Font(40);

        brVoznjiTxt.setFont(font);
        brKmTxt.setFont(font);
        doKrajaSmeneTxt.setFont(font);
        doSledecePauzeTxt.setFont(font);




        Button zavrsiSmenu = new Button("Zavrsi smenu");
        Button traziPauzu = new Button("Pauza");

        zavrsiSmenu.setFont(new Font(25));
        zavrsiSmenu.setMinSize(150,60);
        traziPauzu.setFont(new Font(25));
        traziPauzu.setMinSize(150, 60);

        info.getChildren().addAll(brVoznjiTxt, brKmTxt, doKrajaSmeneTxt, doSledecePauzeTxt);
        info.setPadding(new Insets(50,30,10,70));
        info.setSpacing(30);
        header.setPadding(new Insets(5,5,5,0));
        header.setAlignment(Pos.TOP_CENTER);
        header.setStyle("-fx-background-color: #8a1100; -fx-border-color: black");
        header.setSpacing(650);

        //Eventi za potvrdu akcije
        EventHandler<ActionEvent> krajSmeneEvent = mouseEvent -> {
            String msg;
            if (VozacKlijent.odjava()) {
                msg = "Vas zahtev je uspesno poslat.";
            } else {
                msg = "Doslo je do greske, pokusajte ponovo.";
            }
            prikaziPoruku(msg);
        };
        EventHandler<ActionEvent> pauzaEvent = mouseEvent -> {
            String msg = "";
            if(VozacKlijent.pauza()){
                msg = "Vas zahtev je uspesno poslat.";
            }else{
                msg = "Doslo je do greske, pokusajte ponovo.";
            }
            prikaziPoruku(msg);
        };

        Potvrda smena = new Potvrda("Da li ste sigurni da želite da pošaljete zahtev za kraj smene?", krajSmeneEvent);
        zavrsiSmenu.setOnMouseClicked(event ->Platform.runLater(smena));

        Potvrda pauza = new Potvrda("Da li ste sigurni da želite da pošaljete zahtev za pauzu?", pauzaEvent);
        traziPauzu.setOnMouseClicked(mouseEvent -> Platform.runLater(pauza));

        header.getChildren().addAll(traziPauzu,zavrsiSmenu);
        root.getChildren().addAll(header,info);

        idleStage.setScene(new Scene(root, 1024, 600));
        idleStage.setResizable(false);
        idleStage.show();
    }

    static void prikaziPoruku(String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Zahtev za kraj smene");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    static void updatePodatke(Vozac vozac){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                brKmTxt.setText(brKmTxt.getText().split(":")[0]+": " + vozac.brPredjenihM);
                brVoznjiTxt.setText(brVoznjiTxt.getText().split(":")[0]+": "  + vozac.brOdvezenihVoznji);
                doKrajaSmeneTxt.setText(doKrajaSmeneTxt.getText().split(":")[0]+": "  + vozac.doKrajaSmene);
                doSledecePauzeTxt.setText(doSledecePauzeTxt.getText().split(":")[0]+": "  + vozac.brVoznjiDoPauze);
            }
        });
    }

    static void putDoMusterije(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                getIdleStage().close();

                doMusterijeStage = new Stage();
                HBox header = new HBox();
                VBox root = new VBox();
                Image slika = null;
                try {
                    slika = new Image(new FileInputStream("./src/gps.png"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                ImageView slikaView= new ImageView(slika);
                Button musterijaPreuzeta = new Button("Kreni vožnju");
                musterijaPreuzeta.setMinSize(150, 60);
                musterijaPreuzeta.setFont(new Font(17));

                musterijaPreuzeta.setOnMouseClicked(mouseEvent -> {
                    //preuzeta
                    uVoznji = true;
                    Prozori.voznja();
                    doMusterijeStage.close();
                });
                header.setStyle("-fx-background-color: #8a1100; -fx-border-color: black");
                header.setAlignment(Pos.TOP_RIGHT);
                header.setPadding(new Insets(5,5,5,0));

                header.getChildren().add(musterijaPreuzeta);
                root.getChildren().addAll(header, slikaView);
                Scene scene = new Scene(root, 1024, 600);
                doMusterijeStage.setTitle("Gps simulacija");
                doMusterijeStage.setResizable(false);
                doMusterijeStage.setScene(scene);
                doMusterijeStage.show();

            }
        });
    }

    static void voznja(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage stage = new Stage();
                VBox root = new VBox();
                HBox header = new HBox();

                Image slika = null;
                try {
                    slika = new Image(new FileInputStream("./src/gps.png"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                ImageView slikaView= new ImageView(slika);
                Button zavrsiVoznju = new Button("Završi vožnju");
                zavrsiVoznju.setFont(new Font(17));
                zavrsiVoznju.setMinSize(150, 60);

                Label cenaLab = new Label("Cena: 200");
                cenaLab.setFont(new Font(37));
                cenaLab.setPadding(new Insets(5, 120, 0, 55));
                cenaLab.setStyle("-fx-text-fill: whitesmoke");
                Label metriLab = new Label("Pređena distanca: 0m");
                metriLab.setStyle("-fx-text-fill: whitesmoke");
                metriLab.setFont(new Font(37));
                metriLab.setPadding(new Insets(5,0,0,0));

                //DEVELOPMENT ->
//                uVoznji = true;
                // <-
                //Taksimetar i Predjeni metri
                Task<?> task= new Task<Object>() {
                    @Override
                    protected Object call() throws Exception {
                        float cena=200;
                        int metri=0;
                        while(uVoznji){
                            Thread.sleep(1000);
                            int finalCena = (int) Math.floor(cena);
                            int finalMetri = metri;
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    cenaLab.setText("Cena: "+ finalCena);
                                    metriLab.setText("Pređena distanca: "+ finalMetri +"m");
                                }
                            });
                            metri+=5;
                            cena+=0.5;
                        }
                        return null;
                    }
                };

                Thread t = new Thread(task);
                t.start();

                //Event za kraj voznje
                EventHandler<ActionEvent> zavrsiVoznjuEvent = actionEvent -> {
                    uVoznji = false;
                    t.interrupt();
                    int cena = Integer.parseInt(cenaLab.getText().split(": ")[1]);
                    int metri = Integer.parseInt(metriLab.getText().split(": ")[1].split("m")[0]);
                    posaljiVoznju(cena,metri);
                    stage.close();
                };
                Potvrda p = new Potvrda("Da li ste sigurni da želite da završite vožnju?", zavrsiVoznjuEvent);
                zavrsiVoznju.setOnMouseClicked(mouseEvent -> Platform.runLater(p));

                header.setStyle("-fx-background-color: #8a1100; -fx-border-color: black");
                header.setAlignment(Pos.TOP_CENTER);
                header.setPadding(new Insets(5,5,5,0));
                header.setSpacing(25);

                header.getChildren().addAll(metriLab,cenaLab,zavrsiVoznju);
                root.getChildren().addAll(header, slikaView);
                Scene scene = new Scene(root, 1024, 600);
                stage.setTitle("Gps simulacija");
                stage.setResizable(false);
                stage.setScene(scene);
                stage.show();
            }
        });
    }

    static class Potvrda implements Runnable, EventHandler<ActionEvent> {

        public Potvrda(String msg, EventHandler<ActionEvent> potvrdniEvent) {

            this.potvrdniEvent = potvrdniEvent;
            this.msg = msg;
        }

        private  EventHandler<ActionEvent> potvrdniEvent;

        public EventHandler<? super ActionEvent> getPotvrdniEvent() {
            return potvrdniEvent;
        }

        public void setPotvrdniEvent(EventHandler<ActionEvent> potvrdniEvent) {
            this.potvrdniEvent = potvrdniEvent;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        private String msg = "";

        @Override
        public void run() {
            Stage s = new Stage();
            VBox root = new VBox();
            HBox btns = new HBox();
            Text text = new Text(msg);
            Button da = new Button("Da");
            Button ne = new Button("Ne");
            ne.setCancelButton(true);
            da.setMinSize(120, 50);
            ne.setMinSize(120, 50);

            text.setFont(new Font(25));
            btns.getChildren().addAll(da,ne);
            btns.setSpacing(100);

            root.setAlignment(Pos.CENTER);
            btns.setAlignment(Pos.CENTER);

            root.setSpacing(25);
            root.getChildren().addAll(text,btns);

            da.addEventHandler(ActionEvent.ACTION, potvrdniEvent);
            da.addEventHandler(ActionEvent.ACTION,actionEvent -> { s.close();});
            ne.setOnMouseClicked(event->{ s.close();});

            s.setScene(new Scene(root, 800, 200));
            s.show();
            s.toFront();
        }
        @Override
        public void handle(ActionEvent actionEvent) {

        }
    }

    static void posaljiVoznju(int cena, int metri){
        Stage s = new Stage();
        VBox root = new VBox();
        String txt = "Cena: " + cena + "din\nPređena distanca: " + metri+"m";
        Text text = new Text(txt);
        Button posalji = new Button("Pošalji");
        posalji.setMinSize(120, 50);

        text.setFont(new Font(25));

        root.setAlignment(Pos.CENTER);

        root.setSpacing(25);
        root.getChildren().addAll(text,posalji);

        posalji.setOnMouseClicked(event ->{
            //Salji voznju
            voznja.cenaVoznje = cena;
            voznja.predjenaDistanca = metri;
            VozacKlijent.zavrsiVoznju(voznja);
            voznja = null;
            s.close();
            idle();

        });

        s.setTitle("Račun");
        s.setScene(new Scene(root, 400, 200));
        s.show();
        s.toFront();
    }

    static boolean novaVoznja(Voznja novaVoznja){

        voznja = novaVoznja;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                Stage stage = new Stage();
                VBox root = new VBox();

                Text txt = new Text("Nova vožnja");
                Button potvrdi = new Button("Potvrdi");
                txt.setFont(new Font(40));

                String podaci = "Pocetna tacka: " + voznja.pocetnaTacka + "\n" +
                        "Krajnja tacka: " + voznja.krajnjaTacka;
                Text podaciTxt = new Text(podaci);
                podaciTxt.setFont(new Font(27));


                potvrdi.setMinSize(150,60);

                root.setAlignment(Pos.CENTER);
                root.setSpacing(15);
                root.getChildren().addAll(txt, podaciTxt,potvrdi);

                potvrdi.setOnMouseClicked(event ->{
                    VozacKlijent.uVoznji=true;
                    Prozori.putDoMusterije();
                    stage.close();
                });
                Platform.setImplicitExit(false);
                stage.setOnCloseRequest(evnet ->{
                    evnet.consume();
                    System.out.println("nope");
                });
                stage.setTitle("Nova vožnja");
                stage.setScene(new Scene(root, 800, 400));
                stage.toFront();
                stage.show();

            }
        });

        return true;
    }

    static void naPauzi(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                getIdleStage().close();


                Stage stage = new Stage();
                VBox root = new VBox();

                Label timer = new Label("Pauza traje jos: ");
                root.setAlignment(Pos.CENTER);
                timer.setFont(new Font(35));

                Task<?> task = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        int min = 45;
                        int ukupnoSec = min * 60;
                        int sec = 60;
                        for (int i = 0; i < 10; i++) {
                            Thread.sleep(1000);
                            if (i % 60 == 0) {
                                min--;
                            }
                            sec--;
                            int finalMin = min;
                            int finalSec = sec;
                            System.out.println(min +" "+sec);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    timer.setText("Pauza traje jos: "+finalMin +":"+ finalSec);
                                }
                            });
                        }
                        return true;
                    }
                };
                task.setOnSucceeded(workerStateEvent -> {
                    VozacKlijent.krajPauze();
                    stage.close();
                    idle();
                });

                Thread t = new Thread(task);
                t.start();

                root.getChildren().addAll(timer);
                Scene scene = new Scene(root, 1024, 600);
                stage.setTitle("Pauza");
                stage.setResizable(false);
                stage.setScene(scene);
                stage.show();

            }
        });
    }
}