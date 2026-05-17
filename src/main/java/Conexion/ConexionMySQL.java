package Conexion; // Indica que esta clase pertenece al paquete "Conexion", para organizar el código de base de datos

import java.sql.Connection; // Importa la interfaz para representar una conexión con la base de datos
import java.sql.DriverManager; // Importa la clase para gestionar los drivers de la base de datos
import java.sql.ResultSet; // Importa la interfaz para manejar los resultados de una consulta SELECT
import java.sql.SQLException; // Importa la clase para manejar los errores específicos de SQL
import java.sql.Statement; // Importa la interfaz para ejecutar sentencias SQL
import java.util.Calendar; // Importa la clase para trabajar con fechas y horas
import java.util.TimeZone; // Importa la clase para manejar las zonas horarias

// Esta clase contiene todas las herramientas necesarias para conectar
// nuestra aplicación de Java con una base de datos MySQL
public class ConexionMySQL { // Define el inicio de nuestra clase

    //
    // VARIABLES DE CONEXIÓN
    //
    // Aquí se guardan los datos necesarios para establecer la conexión

    private String BD; // Declara una variable para guardar el nombre de la base de datos
    private String USUARIO; // Declara una variable para guardar el nombre de usuario
    private String PASS; // Declara una variable para guardar la contraseña
    private Connection connection; // Declara un objeto que representará la conexión activa
    private String HOST; // Declara una variable para guardar la dirección del servidor
    private TimeZone zonahoraria; // Declara una variable para guardar la zona horaria

    //
    // CONSTRUCTOR DE LA CLASE
    //
    // Se ejecuta cuando creamos un nuevo objeto de tipo "ConexionMySQL"
    public ConexionMySQL(String usuario, String pass, String bd) { // Define el constructor y sus parámetros
        this.HOST = "localhost"; // Asigna "localhost" a la variable HOST
        this.USUARIO = usuario; // Asigna el parámetro "usuario" a la variable de la clase
        this.PASS = pass; // Asigna el parámetro "pass" a la variable de la clase
        this.BD = bd; // Asigna el parámetro "bd" a la variable de la clase
        this.connection = null; // Inicializa la conexión a null para indicar que no estamos conectados
    } // Cierra el constructor

    //
    // MÉTODO PARA REGISTRAR EL DRIVER
    //
    // Este método se asegura de que la aplicación tiene disponible el "traductor" (driver JDBC)
    private void registrarDriver() throws SQLException { // Define el método, que puede lanzar un error de SQL
        try { // Inicia un bloque para manejar posibles errores
            // Intenta encontrar la clase del driver de MySQL, que es el "traductor"
            Class.forName("com.mysql.cj.jdbc.Driver"); // Carga la clase del driver en memoria
        } catch (ClassNotFoundException e) { // Si no encuentra la clase
            // Lanza un nuevo error de SQL indicando que falta el driver
            throw new SQLException("Error al conectar con MySQL: No se encontró el driver " + e.getMessage());
        } // Cierra el bloque try-catch
    } // Cierra el método registrarDriver

    //
    // MÉTODO PARA CONECTAR CON LA BASE DE DATOS
    //
    // Este es el método principal para establecer la comunicación
    public void conectar() throws SQLException { // Define el método, que puede lanzar un error de SQL
        // ¿No hay conexión o la conexión está cerrada?
        if (connection == null || connection.isClosed()) { // Comprueba el estado de la conexión
            // Si es así, se procede a conectar

            // 1. Se registra el driver
            this.registrarDriver(); // Llama al método para asegurarse de que el driver está disponible
            
            // 2. Se obtiene la zona horaria del sistema para la conexión
            Calendar now = Calendar.getInstance(); // Obtiene la fecha y hora actuales
            this.zonahoraria = now.getTimeZone(); // Extrae la zona horaria de esa fecha
            
            // 3. Se establece la conexión usando el DriverManager de JDBC
            //    Se le pasa una URL de conexión con todos los datos necesarios
            this.connection = (Connection) DriverManager.getConnection("jdbc:mysql://" + this.HOST + "/" + this.BD + "?user="
                    + this.USUARIO + "&password=" + this.PASS + "&useLegacyDatetimeCode=false&serverTimezone="
                    + this.zonahoraria.getID());
        } // Cierra el bloque if
    } // Cierra el método conectar

    //
    // MÉTODO PARA DESCONECTAR DE LA BASE DE DATOS
    //
    // Es muy importante cerrar la conexión para liberar recursos
    public void desconectar() throws SQLException { // Define el método, que puede lanzar un error de SQL
        // ¿Hay una conexión y está abierta?
        if (this.connection != null && !this.connection.isClosed()) { // Comprueba el estado de la conexión
            // Si es así, se cierra
            this.connection.close(); // Cierra la conexión
        } // Cierra el bloque if
    } // Cierra el método desconectar

    //
    // MÉTODO PARA EJECUTAR CONSULTAS DE SELECCIÓN (SELECT)
    //
    // Sirve para obtener datos de la base de datos
    public ResultSet ejecutarSelect(String consulta) throws SQLException { // Define el método y el tipo de dato que devuelve
        // Se crea un objeto "Statement", que es el encargado de ejecutar la consulta
        Statement stmt = this.connection.createStatement(); // Crea el objeto Statement
        // Se ejecuta la consulta SELECT y se devuelve el conjunto de resultados (ResultSet)
        ResultSet rset = stmt.executeQuery(consulta); // Ejecuta la consulta y guarda el resultado
        return rset; // Devuelve el resultado obtenido
    } // Cierra el método ejecutarSelect

    //
    // MÉTODO PARA EJECUTAR CONSULTAS DE MODIFICACIÓN (INSERT, DELETE, UPDATE)
    //
    // Sirve para cambiar datos en la base de datos
    public int ejecutarInsertDeleteUpdate(String consulta) throws SQLException { // Define el método y el tipo de dato que devuelve
        // Se crea el objeto "Statement"
        Statement stmt = this.connection.createStatement(); // Crea el objeto Statement
        // Se ejecuta la consulta de modificación. Este método devuelve el número de filas afectadas
        int fila = stmt.executeUpdate(consulta); // Ejecuta la consulta y guarda el número de filas
        return fila; // Devuelve el número de filas afectadas
    } // Cierra el método ejecutarInsertDeleteUpdate
} // Cierra la clase ConexionMySQL
