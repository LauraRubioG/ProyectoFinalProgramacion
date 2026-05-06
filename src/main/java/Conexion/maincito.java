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

        ConexionMySQL prueba = new ConexionMySQL("root","","parque_atracciones");
        try {
            prueba.conectar();

            //variables para no tener que ir uno a uno
			/*int id_Atraccion;
            String nombre = "Papitos";
			String categoria = "Familiar";
            String Estado = "A";
            int Duracion = 5;
            Boolean FotoRide = true;
			//dentro de los parentesis le damos la sentencia para insertarla
			String sentencia = "INSERT INTO EMPLEADOS (Nombre, Categoria, Estado, Duracion_Minutos, FotoRide) VALUES ('"+nombre+"', "+categoria+", "+Estado+", "+Duracion+", "+FotoRide+")";
			prueba.ejecutarInsertDeleteUpdate(sentencia);
            */



        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
