package Dispicer;

import Util.Vozac;
import Util.Voznja;
import Util.Zahtev;
import Vozac.VozacKlijent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class App extends Application {
    private static ListView<Voznja> voznje;
    private static ListView<Zahtev> zahtevi;
    private static ObservableList<Voznja> listaVoznji = FXCollections.observableArrayList();
    private static ObservableList<String> ulice = FXCollections.observableArrayList("asd");
    private static ObservableList<Zahtev> listaZahteva = FXCollections.observableArrayList();


    public static void main(String[] args) {
        launch(args);
    }

    public static void setListaVoznji(ObservableList<Voznja> novaLista) {

        if (voznje != null) {
            voznje.getItems().clear();
            listaVoznji.setAll(novaLista);
        }

    }

    public static void setListaZahteva(ObservableList<Zahtev> novaLista) {
        if (zahtevi != null) {
            zahtevi.getItems().clear();
            listaZahteva.setAll(novaLista);
        } else {
            listaZahteva.setAll(novaLista);
        }
    }

    public void preload() {
        DispicerKlijent d = new DispicerKlijent();
        d.init();
        d.logInZahtev();


        ArrayList<String> tempUlice = new ArrayList<>();///home/nikola3in1/IdeaProjects/SE201-PZ-NikolaMitrovic2934FX/src/Dispicer
        try (BufferedReader br = Files.newBufferedReader(Paths.get("./src/Dispicer/UliceBeograd.txt"), StandardCharsets.UTF_8)) {
            for (String line = null; (line = br.readLine()) != null; ) {
                if (!line.isEmpty())
                    tempUlice.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObservableList<String> ulice = FXCollections.observableArrayList(tempUlice);
        setUlice(ulice);

        DispicerKlijent.getListuZahteva();
        DispicerKlijent.getListaVoznji();

    }

    public void setUlice(ObservableList<String> ulice) {
        this.ulice = ulice;
    }

    public ObservableList<Zahtev> getListaZahteva() {
        return listaZahteva;
    }

    public static void upisiVoznju(){
        Stage s = new Stage();
        GridPane root = new GridPane();
        TextField id = new TextField();
        ComboBox<String> pocetnaTacka = new ComboBox<String>();
        ComboBox<String> krajnjaTacka = new ComboBox<String>();
        TextField cena = new TextField();
        TextField distanca = new TextField();
        Button salji = new Button("Salji");


        pocetnaTacka.setEditable(true);
        pocetnaTacka.setMaxWidth(250);
        FilteredList<String> filteredItems = new FilteredList<String>(ulice, p -> true);
        pocetnaTacka.getEditor().textProperty().addListener(new UliceListener(pocetnaTacka, filteredItems));
        pocetnaTacka.setItems(filteredItems);

        krajnjaTacka.setMaxWidth(250);
        krajnjaTacka.setEditable(true);
        FilteredList<String> filteredItems2 = new FilteredList<String>(ulice, p -> true);
        krajnjaTacka.getEditor().textProperty().addListener(new UliceListener(krajnjaTacka, filteredItems2));
        krajnjaTacka.setItems(filteredItems2);



        root.add(new Text("VozacID"), 0, 0);
        root.add(new Text("Pocetna tacka"), 0, 1);
        root.add(new Text("Krajnja tacka"), 0, 2);
        root.add(new Text("Cena"), 0, 3);
        root.add(new Text("Trajanje"), 0, 4);

        root.add(id,1,0);
        root.add(pocetnaTacka,1,1);
        root.add(krajnjaTacka,1,2);
        root.add(cena,1,3);
        root.add(distanca,1,4);
        root.add(salji, 1, 5);

        salji.setOnMouseClicked(mouseEvent -> {
            if (id.getText() != null && !id.getText().isEmpty() && id.getText().length() >= 5 &&
                    pocetnaTacka.getValue() != null && !pocetnaTacka.getValue().isEmpty() &&
                    krajnjaTacka.getValue() != null && !krajnjaTacka.getValue().isEmpty() &&
                    cena.getText() != null && !cena.getText().isEmpty() &&
                    distanca.getText() != null && !distanca.getText().isEmpty()) {

                Voznja v = new Voznja();
                Vozac v1 = new Vozac(id.getText(), "", "", "");
                v.vozac =v1;
                v.pocetnaTacka = pocetnaTacka.getValue();
                v.krajnjaTacka = krajnjaTacka.getValue();
                v.cenaVoznje = Integer.parseInt(cena.getText());
                v.predjenaDistanca = Integer.parseInt(cena.getText());
                DispicerKlijent.upisiVoznju(v);
            }

        });
        salji.setMinSize(250,40);

        root.setVgap(5);

        root.setAlignment(Pos.CENTER);



        s.setTitle("Upisi voznju");
        s.setScene(new Scene(root, 600, 250));
        s.show();


    }




    @Override
    public void start(Stage primaryStage) {
        preload();

        VBox root = new VBox();
        root.setPadding(new Insets(8));
        root.setSpacing(14);

        //Input
        HBox voznjaInput = new HBox();
        ComboBox<String> pocetnaTacka = new ComboBox<String>();
        ComboBox<String> krajnjaTacka = new ComboBox<String>();
        Button saljiVoznju = new Button("Salji");
        Button upisiVoznju = new Button("Upisi voznju");
        saljiVoznju.setOnMousePressed(mouseEvent -> {
            if (validirajUnos(pocetnaTacka.getValue(), krajnjaTacka.getValue())) {
                Voznja voznja = new Voznja(pocetnaTacka.getValue(), krajnjaTacka.getValue());
                DispicerKlijent.posaljiVoznju(voznja);
            }
        });
        upisiVoznju.setOnMouseClicked(mouseEvent -> {
            upisiVoznju();
        });

        saljiVoznju.setPrefWidth(80);
        saljiVoznju.setMinWidth(80);
        voznjaInput.setAlignment(Pos.CENTER);
        voznjaInput.setPadding(new Insets(0, 50, 20, 50));
        voznjaInput.setSpacing(14);

        pocetnaTacka.setEditable(true);
        pocetnaTacka.setMaxWidth(250);
        FilteredList<String> filteredItems = new FilteredList<String>(ulice, p -> true);
        pocetnaTacka.getEditor().textProperty().addListener(new UliceListener(pocetnaTacka, filteredItems));
        pocetnaTacka.setItems(filteredItems);

        krajnjaTacka.setMaxWidth(250);
        krajnjaTacka.setEditable(true);
        FilteredList<String> filteredItems2 = new FilteredList<String>(ulice, p -> true);
        krajnjaTacka.getEditor().textProperty().addListener(new UliceListener(krajnjaTacka, filteredItems2));
        krajnjaTacka.setItems(filteredItems2);

        //Liste
        HBox liste = new HBox();
        voznje = new ListView<Voznja>(listaVoznji);
        zahtevi = new ListView<>(listaZahteva);
        liste.setAlignment(Pos.CENTER);
        liste.setPadding(new Insets(0, 0, 0, 0));
        liste.setSpacing(20);

        zahtevi.setMinWidth(350);
        zahtevi.setCellFactory(new Callback<ListView<Zahtev>, ListCell<Zahtev>>() {
            @Override
            public ListCell<Zahtev> call(ListView<Zahtev> param) {
                return new Obavestenja();
            }
        });

        voznje.setMinWidth(520);
        voznje.setCellFactory(new Callback<ListView<Voznja>, ListCell<Voznja>>() {
            @Override
            public ListCell<Voznja> call(ListView<Voznja> param) {
                return new Voznje();
            }
        });

        Platform.setImplicitExit(false);
        primaryStage.setOnCloseRequest(evnet ->{
            evnet.consume();
            try {
                DispicerKlijent.socket.close();
                DispicerUpdateThread.sleep(1000);
                DispicerUpdateThread.alive = false;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Platform.exit();

        });


        //Labele
        HBox labeleListe = new HBox();
        Label voznjeLab = new Label("Lista trenutnih voznji");
        Label zahteviLab= new Label("Lista zahteva");
        labeleListe.setSpacing(380);
        labeleListe.setAlignment(Pos.CENTER);

        HBox labeleVoznje = new HBox();
        Label pocetnaLab = new Label("Pocetna tacka");
        Label krajnjaLab = new Label("Krajnja tacka");
        labeleVoznje.setSpacing(170);
        labeleVoznje.setAlignment(Pos.CENTER_LEFT);
        labeleVoznje.setPadding(new Insets(15,0,0,220));

        //Dodavanje cvorova
        voznjaInput.getChildren().addAll(pocetnaTacka, krajnjaTacka, saljiVoznju, upisiVoznju);
        liste.getChildren().addAll(voznje, zahtevi);
        labeleVoznje.getChildren().addAll(pocetnaLab,krajnjaLab);
        labeleListe.getChildren().addAll(voznjeLab, zahteviLab);
        root.getChildren().addAll(labeleVoznje,voznjaInput,labeleListe,liste);

        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 1024, 600));
        primaryStage.show();

    }

    class Voznje extends ListCell<Voznja> {
        HBox hbox = new HBox();
        Label label = new Label("(empty)");
        Pane pane = new Pane();
        Button button = new Button("Otkazi");
        Voznja voznja;

        public Voznje() {
            super();
            hbox.getChildren().addAll(label, pane, button);
            HBox.setHgrow(pane, Priority.ALWAYS);
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    ButtonType da = new ButtonType("Da", ButtonBar.ButtonData.OK_DONE);
                    ButtonType ne = new ButtonType("Ne", ButtonBar.ButtonData.CANCEL_CLOSE);
                    Alert alert = new Alert(Alert.AlertType.NONE,
                            "Pocetna tacka: " + voznja.pocetnaTacka
                                    + "\nKrajnja tacka: " + voznja.krajnjaTacka
                                    + "\nVozac ID: " + voznja.vozac.id,
                            da,
                            ne);
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.setTitle("Potvrdite otkazivanje vožnje");
                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isPresent() && result.get() == da) {
                        voznja.novaVoznja = false;
                        DispicerKlijent.otkaziVoznju(voznja);
                        voznje.getItems().remove(voznja);
                    }
                }
            });
        }

        @Override
        protected void updateItem(Voznja item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);  // No text in label of super class
            if (empty) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        voznja = null;
                        setGraphic(null);

                    }
                });
            } else {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        voznja = item;
                        label.setText(item.vozac.id + ": " + item.pocetnaTacka + " - " + item.krajnjaTacka);
                        setGraphic(hbox);

                    }
                });
            }
        }

    }

    class Obavestenja extends ListCell<Zahtev> {
        HBox hbox = new HBox();
        Label label = new Label("(empty)");
        Pane pane = new Pane();
        Button button = new Button("Proveri");
        Zahtev zahtev;

        public Obavestenja() {
            super();
            hbox.getChildren().addAll(label, pane, button);
            HBox.setHgrow(pane, Priority.ALWAYS);
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    ButtonType da = new ButtonType("Da", ButtonBar.ButtonData.OK_DONE);
                    ButtonType ne = new ButtonType("Ne", ButtonBar.ButtonData.CANCEL_CLOSE);
                    Alert alert = new Alert(Alert.AlertType.NONE,
                            "Id vozaca: " +zahtev.vozac.id + "\n Broj odvezenih voznji: "+zahtev.vozac.brOdvezenihVoznji + "\n Broj voznji do pauze: "+ zahtev.vozac.brVoznjiDoPauze
                                    + "\n Broj voznji do kraja smene: "+ zahtev.vozac.doKrajaSmene,
                            da,
                            ne);
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.setTitle("Odobrite zahtev");
                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isPresent() && result.get() == da) {
                        DispicerKlijent.posaljiZahtev(zahtev, true);
                        zahtevi.getItems().remove(zahtev);
                    } else if (result.isPresent() && result.get() == ne) {
                        DispicerKlijent.posaljiZahtev(zahtev, false);
                        zahtevi.getItems().remove(zahtev);
                    }
                }
            });
        }

        @Override
        protected void updateItem(Zahtev item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);  // No text in label of super class
            if (empty) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        zahtev = null;
                        setGraphic(null);
                    }
                });

            } else {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        zahtev = item;
                        label.setText(item.tipZahteva.equals("pauza") ? item.vozac.id + "- zahteva pauzu" : item.vozac.id + "- zahteva dozvolu za kraj smene");
                        setGraphic(hbox);
                    }
                });

            }
        }
    }

    public static void nemaVozaca(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,"Trenutno nema slobodnih vozača.");
                alert.setHeaderText(null);
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.setTitle("Nema slobodnih vozaca");
                Optional<ButtonType> result = alert.showAndWait();
            }
        });



//        if (result.isPresent() && result.get() == da) {
//            DispicerKlijent.posaljiZahtev(zahtev, true);
//            zahtevi.getItems().remove(zahtev);
//        } else if (result.isPresent() && result.get() == ne) {
//            DispicerKlijent.posaljiZahtev(zahtev, false);
//            zahtevi.getItems().remove(zahtev);
//        }
    }

    public boolean validirajUnos(String a, String b) {
        return a != null && b != null && !a.equals(b);
    }
}
