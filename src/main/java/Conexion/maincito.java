package Conexion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class maincito {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        //creamos el objeto de la clase para usarlo
        //ConexionMySQL x = new ConexionMySQL("root","","dam");



        Scanner sr = new Scanner(System.in);

        ConexionMySQL prueba = new ConexionMySQL("root","","HotelM&L");
        try {
            prueba.conectar();


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
