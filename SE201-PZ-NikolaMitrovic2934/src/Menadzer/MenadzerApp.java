package Menadzer;

import Util.Vozac;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MenadzerApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    private static TableView tabela;


    @Override
    public void start(Stage primaryStage) {
        tabela = new TableView();
        VBox root = new VBox();
        HBox user = new HBox();
        HBox pass = new HBox();
        TextField idField = new TextField();
        PasswordField passField = new PasswordField();

        Button login = new Button("Login");
        user.getChildren().addAll(new Text("Username:"), idField);
        pass.getChildren().addAll(new Text("Password: "), passField);
        root.getChildren().addAll(new Text("Taksi služba"), user, pass, login);

        user.setAlignment(Pos.CENTER);
        pass.setAlignment(Pos.CENTER);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(15);
        login.setMinSize(120, 40);

        login.setOnMouseClicked(event -> {
            if (validirano(idField.getText(), passField.getText())) {
                System.out.println("logovan");
                ArrayList<Vozac> vozaci = MenadzerKlijent.getVozaci();
                if (vozaci != null) {
                    mainScreen(vozaci);
                }
                primaryStage.close();
            }
        });
        primaryStage.setTitle("Taxi služba");
        primaryStage.setScene(new Scene(root, 880, 600));
        primaryStage.show();
    }

    boolean validirano(String user, String pass) {
        if (user != null && pass != null && user.length() > 0 && pass.length() > 0) {
            MenadzerKlijent.init();
            if (MenadzerKlijent.prijava(user, pass)) {
                return true;
            }
        }
        return false;
    }


    private static final ObservableList<Vozac> data =
            FXCollections.observableArrayList();


    static void updateData(ArrayList<Vozac> vozaci){
        data.clear();
        data.addAll(vozaci);
    }

    static void mainScreen(ArrayList<Vozac> vozaci) {
        data.addAll(vozaci);
        Stage s = new Stage();
        BorderPane root = new BorderPane();
        GridPane forma = new GridPane();
        HBox header = new HBox();
        HBox crud = new HBox();
        VBox desnaStrana = new VBox();
        VBox izvestaji = new VBox();

        Image slika = null;
        try {
            slika = new Image(new FileInputStream("./src/taxi.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ImageView slikaView = new ImageView(slika);
        //Btns
        Button dnevniIzvestaj = new Button("Dnevni izvestaj");
        Button mesecniIzvestaj = new Button("Mesecni izvestaj");
        Button izmeni = new Button("Ažuriraj");
        Button izbrisi = new Button("Izbriši");
        Button dodaj = new Button("Dodaj");
        Button logout = new Button("Odjava");

        dnevniIzvestaj.setMinWidth(230);
        mesecniIzvestaj.setMinWidth(230);

        //Forma
        TextField imeTf = new TextField("");
        TextField prezimeTf = new TextField("");
        TextField idTf = new TextField("");
        TextField passTf = new TextField("");

        forma.add(new Label("Ime: "), 0, 0);
        forma.add(new Label("Prezime: "), 0, 1);
        forma.add(new Label("Korisnički id: "), 0, 2);
        forma.add(new Label("Šifra: "), 0, 3);
        forma.add(imeTf, 1, 0);
        forma.add(prezimeTf, 1, 1);
        forma.add(idTf, 1, 2);
        forma.add(passTf, 1, 3);
        forma.setVgap(5);

        //Tabela
        TableColumn ime = new TableColumn("Ime");
        TableColumn prezime = new TableColumn("Prezime");
        TableColumn id = new TableColumn("Korisnički ID");
        TableColumn pass = new TableColumn("Šifra");
        id.setMinWidth(120);
        ime.setMinWidth(120);
        prezime.setMinWidth(120);
        pass.setMinWidth(120);
        ime.setCellValueFactory(new PropertyValueFactory<Vozac, String>("ime"));
        prezime.setCellValueFactory(new PropertyValueFactory<Vozac, String>("prezime"));
        id.setCellValueFactory(new PropertyValueFactory<Vozac, String>("id"));
        pass.setCellValueFactory(new PropertyValueFactory<Vozac, String>("pass"));
        tabela.setMaxWidth(482);
        tabela.getColumns().addAll(ime, prezime, id, pass);
        tabela.setItems(data);

        tabela.setRowFactory(tv -> {
            TableRow<Vozac> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY) {
                    //&& event.getClickCount() == 1
                    Vozac vozac = row.getItem();
                    //KOD
                    System.out.println("Asd");
                    System.out.println(vozac.ime);
                    imeTf.setText(vozac.ime);
                    prezimeTf.setText(vozac.prezime);
                    idTf.setText(vozac.id);
                    passTf.setText(vozac.pass);
                }
            });
            return row;
        });

        izbrisi.setOnMouseClicked(mouseEvent -> {
            if (idTf.getText() != null && !idTf.getText().isEmpty() && idTf.getText().length() >= 5) {
                Vozac v = new Vozac(idTf.getText(),"","","");
                if(MenadzerKlijent.izbrisiVozaca(v)){
                    updateData(MenadzerKlijent.getVozaci());
                }else{
                    System.out.println("doslo je do greske");
                }
            }
        });

        dodaj.setOnMouseClicked(mouseEvent -> {
            if (validirajFormu(idTf.getText(),imeTf.getText(),prezimeTf.getText(),passTf.getText())) {
                Vozac v = new Vozac(idTf.getText(), imeTf.getText(), prezimeTf.getText(), passTf.getText());
                if (MenadzerKlijent.dodajVozaca(v)) {
                    updateData(MenadzerKlijent.getVozaci());
                } else {
                    System.out.println("doso je do greske");
                }
            }
        });

        izmeni.setOnMouseClicked(mouseEvent -> {
            if (validirajFormu(idTf.getText(),imeTf.getText(),prezimeTf.getText(),passTf.getText())) {
                Vozac v = new Vozac(idTf.getText(), imeTf.getText(), prezimeTf.getText(), passTf.getText());
                if (MenadzerKlijent.izmeniVozaca(v)) {
                    updateData(MenadzerKlijent.getVozaci());
                } else {
                    System.out.println("doso je do greske");
                }
            }
        });

        mesecniIzvestaj.setOnMouseClicked(mouseEvent -> {
            MenadzerKlijent.getMesecniIzvestaj();
        });

        dnevniIzvestaj.setOnMouseClicked(mouseEvent -> {
            MenadzerKlijent.getDnevniIzvestaj();
        });

        logout.setOnMouseClicked(mouseEvent -> {
            MenadzerKlijent.logout();
        });

        header.setStyle("-fx-background-color: #8a1100; -fx-border-color: black");
        header.setAlignment(Pos.TOP_RIGHT);
        header.getChildren().add(logout);
        header.setPadding(new Insets(2, 2, 2, 0));
        tabela.setPadding(new Insets(2, 2, 2, 2));
        forma.setPadding(new Insets(0, 80, 5, 0));
        izvestaji.setPadding(new Insets(23, 80, 0, 0));

        izvestaji.setSpacing(7);
        izvestaji.setAlignment(Pos.CENTER);

        crud.setSpacing(37);
        desnaStrana.setSpacing(7);

        izvestaji.getChildren().addAll(dnevniIzvestaj, mesecniIzvestaj);
        crud.getChildren().addAll(dodaj, izmeni, izbrisi);
        desnaStrana.getChildren().addAll(slikaView, forma, crud, izvestaji);
        root.setRight(desnaStrana);
        root.setTop(header);
        root.setLeft(tabela);
        s.setResizable(false);
        s.setScene(new Scene(root, 880, 600));
        s.setTitle("Taxi služba");
        s.show();
    }
    static boolean validirajFormu(String id, String ime,String preizme, String pass){
        if (id != null && !id.isEmpty() &&
                ime != null && !ime.isEmpty() &&
                preizme != null && !preizme.isEmpty() &&
                pass != null && !pass.isEmpty() &&
                pass.length() >= 8 && id.length() >= 5) {
            return true;
        }
        return false;
    }

}
