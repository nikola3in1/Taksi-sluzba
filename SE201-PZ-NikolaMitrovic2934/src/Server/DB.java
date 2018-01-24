package Server;
import Util.Izvestaj;
import Util.Vozac;
import Util.Voznja;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


class Main{
    public static void main(String[]args){
        Vozac v = new Vozac();
        v.id = "dzoni";
//        DB.getInstance().izbrisiVozaca(v);
        DB.getInstance().dnevniIzvestaj();
    }
}


public class DB {

    private static DB instance = null;
    private Connection connection;
    private PreparedStatement ps;
    private final String url = "jdbc:mysql://localhost/TaksiSluzba";
    private final String user = "root";
    private final String pass = "n14031997";
    private String query = "";
    private java.sql.Date datum = new java.sql.Date(new Date().getTime());

    public java.sql.Date getDatum() {
        return datum;
    }

    public void setDatum(java.sql.Date datum) {
        this.datum = datum;
    }

    public static DB getInstance() {
        if (instance == null) {
            instance = new DB();
        }
        return instance;
    }

    private DB() {
        try {
            connection = (Connection) DriverManager.getConnection(url, user, pass);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnnection() throws SQLException {
        connection.close();
    }



    public ArrayList<Izvestaj> dnevniIzvestaj(){
        String dan = datum.toString();
        String query = "SELECT ODVEZENA_DATUM, ODVEZENA.VOZAC_ID, VOZNJA.VOZNJA_POCETNA_TACKA , VOZNJA.VOZNJA_KRAJNJA_TACKA, VOZNJA.VOZNJA_CENA, VOZNJA.VOZNJA_TRAJANJE FROM ODVEZENA JOIN VOZNJA ON ODVEZENA.VOZNJA_ID = VOZNJA.VOZNJA_ID WHERE ODVEZENA_DATUM LIKE '%" + dan + "%' ";
        ArrayList<Izvestaj> izvestajs = new ArrayList<>();
        try{
            ps = (PreparedStatement) connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                izvestajs.add(new Izvestaj(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(6)));
            }
            System.out.println(Arrays.toString(izvestajs.toArray()));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return izvestajs;
    }

    public ArrayList<Izvestaj> mesecniIzvestaj(){
        String mesec = datum.toString().substring(0,7);
        String query = "SELECT ODVEZENA_DATUM, ODVEZENA.VOZAC_ID, VOZNJA.VOZNJA_POCETNA_TACKA , VOZNJA.VOZNJA_KRAJNJA_TACKA, VOZNJA.VOZNJA_CENA, VOZNJA.VOZNJA_TRAJANJE FROM ODVEZENA JOIN VOZNJA ON ODVEZENA.VOZNJA_ID = VOZNJA.VOZNJA_ID WHERE ODVEZENA_DATUM LIKE '%"+ mesec +"%' ";
        ArrayList<Izvestaj> izvestajs = new ArrayList<>();
        try{
            ps = (PreparedStatement) connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                izvestajs.add(new Izvestaj(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(6)));
            }
            System.out.println(Arrays.toString(izvestajs.toArray()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return izvestajs;
    }

    public boolean dodajVozaca(Vozac vozac) {
        String query = "INSERT INTO `VOZAC`(`VOZAC_ID`, `VOZAC_IME`, `VOZAC_PREZIME`, `VOZAC_SIFRA`) VALUES (?,?,?,MD5(?))";
        try {
            ps = (PreparedStatement) connection.prepareStatement(query);
            ps.setString(1, vozac.id);
            ps.setString(2,vozac.ime);
            ps.setString(3, vozac.prezime);
            ps.setString(4, vozac.pass);
            ps.execute();
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private void dodajVoznju(Voznja voznja) {
        String query = "INSERT INTO `VOZNJA` (`VOZNJA_POCETNA_TACKA`, `VOZNJA_KRAJNJA_TACKA`, `VOZNJA_CENA`,`VOZNJA_TRAJANJE`) VALUES (?, ?, ?, ?)";
        try {
            ps = (PreparedStatement) connection.prepareStatement(query);
            ps.setString(1, voznja.pocetnaTacka);
            ps.setString(2,voznja.krajnjaTacka);
            ps.setInt(3, voznja.cenaVoznje);
            ps.setInt(4, voznja.predjenaDistanca);
            ps.execute();

        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void dodajSmenu(java.sql.Date date){
        String query = "INSERT INTO `SMENA` (`SMENA_ID`) VALUES (?)";
        try {
            ps = (PreparedStatement) connection.prepareStatement(query);
            ps.setDate(1, date);
            ps.execute();

        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void dodajSmenu(){
        java.sql.Date date = new java.sql.Date(new Date().getTime());
        String query = "INSERT INTO `SMENA` (`SMENA_ID`) VALUES (?) ON DUPLICATE KEY UPDATE `SMENA_ID` = ?";
        try {
            ps = (PreparedStatement) connection.prepareStatement(query);
            ps.setDate(1, date);
            ps.setDate(2, date);
            ps.execute();

        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void dodajOdvezenuVoznju(Voznja voznja) {
        dodajVoznju(voznja);
        java.sql.Date d = new java.sql.Date(new Date().getTime());
        if (!getDatum().toString().equals(d.toString())) {
            System.out.println("novi dan be");
            setDatum(d);
            dodajSmenu(datum);
        }

        String prviQuery = "SELECT MAX(VOZNJA_ID) FROM VOZNJA";
        String query = "INSERT INTO `ODVEZENA` (`ODVEZENA_DATUM`, `VOZNJA_ID`, `VOZAC_ID`) VALUES (?, ?, ?)";
        try {
            ps = (PreparedStatement) connection.prepareStatement(prviQuery);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            int voznjaId = 0;
            if(rs.next()){
                voznjaId= rs.getInt(1);
            }
            System.out.println(voznjaId);
            ps = (PreparedStatement) connection.prepareStatement(query);
            ps.setString(1, getDatum().toString());
            ps.setInt(2, voznjaId);
            ps.setString(3,voznja.vozac.id);
            ps.execute();

        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<Vozac> getVozace(){
        ArrayList<Vozac> vozaci = new ArrayList<Vozac>();
        String query = "SELECT * FROM `VOZAC`";
        try{
            ps = (PreparedStatement) connection.prepareStatement(query);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            while(rs.next()){
                vozaci.add(new Vozac(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }
        return vozaci;
    }

    public boolean izmeniVozaca(String id, Vozac noviVozac) {
        String query = "UPDATE `VOZAC` SET `VOZAC_IME`=?,`VOZAC_PREZIME`=?,`VOZAC_SIFRA`=MD5(?) WHERE `VOZAC`.`VOZAC_ID` = ?";
        try{
            ps = (PreparedStatement) connection.prepareStatement(query);
            ps.setString(1, noviVozac.ime);
            ps.setString(2, noviVozac.prezime);
            ps.setString(3, noviVozac.pass);
            ps.setString(4, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean izbrisiVozaca(Vozac vozac){
        String query = "DELETE FROM `VOZAC` WHERE `VOZAC`.`VOZAC_ID`= ?";
        try{
            ps = (PreparedStatement) connection.prepareStatement(query);
            System.out.println(vozac.id);
            ps.setString(1, vozac.id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean validationVozac(String vozacUsername, String vozacPassword) {
        String query = "SELECT * FROM `VOZAC` WHERE `VOZAC`.`VOZAC_ID` = ? AND `VOZAC`.`VOZAC_SIFRA`= MD5(?)";
        try{
            ps = (PreparedStatement) connection.prepareStatement(query);
            ps.setString(1, vozacUsername);
            ps.setString(2, vozacPassword);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    public boolean validationMenadzer(String menadzerUsername, String menadzerPassword) {
        String query = "SELECT * FROM `MENADZER` WHERE `MENADZER`.`MENADZER_ID` = ? AND `MENADZER`.`MENADZER_SIFRA`= MD5(?)";
        try{
            ps = (PreparedStatement) connection.prepareStatement(query);
            ps.setString(1, menadzerUsername);
            ps.setString(2, menadzerPassword);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;


    }
}