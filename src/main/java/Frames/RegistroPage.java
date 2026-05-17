// La primera línea de un archivo Java siempre define a qué "paquete" o carpeta pertenece la clase.
package Frames;

//
// ZONA DE IMPORTACIONES
//
// Aquí le decimos a nuestra clase qué herramientas o "librerías" externas necesita para funcionar.
import Conexion.ConexionMySQL;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.regex.Pattern;

// Esta clase define todo el comportamiento y la lógica de nuestra ventana de registro de administradores.
public class RegistroPage {

    //
    // DECLARACIÓN DE COMPONENTES VISUALES (ATRIBUTOS DE LA CLASE)
    //
    // Aquí declaramos las variables que representan a cada uno de los componentes del formulario.
    private JTextField regDNIW;
    private JTextField regNombreW;
    private JTextField regApellidosW;
    private JTextField regEmailW;
    private JTextField regTelefonoW;
    private JTextField regDireccionW;
    private JTextField regCiudadW;
    private JTextField regCodigoPostalW;
    private JPasswordField regContrasenaW;
    private JPasswordField regClaveMaestraW;
    private JButton regBtn;
    public JPanel pantallaRegistro;

    //
    // CONSTRUCTOR DE LA CLASE
    //
    // Es el método que se ejecuta al crear la ventana. Su misión es preparar los componentes.
    public RegistroPage() {
        
        // Le decimos al botón de registrar qué debe hacer cuando un usuario le haga clic.
        // El método .addActionListener() es como "ponerle una oreja" al botón para que escuche los clics.
        regBtn.addActionListener(new ActionListener() {
            
            // Este método se dispara justo cuando el usuario pulsa el botón.
            @Override
            public void actionPerformed(ActionEvent e) {
                // Para mantener el código organizado, llamamos a un método separado que hace todo el trabajo.
                procesarRegistroAdministrador();
            }
        });
    }

    //
    // MÉTODO PARA PROCESAR EL REGISTRO DE UN NUEVO ADMINISTRADOR
    //
    // Aquí se realizan las validaciones y la inserción en la tabla "Administradores".
    private void procesarRegistroAdministrador() {

        // Primero, recogemos el texto que el usuario ha escrito en cada campo.
        String dni = regDNIW.getText();
        String nombre = regNombreW.getText();
        String apellidos = regApellidosW.getText();
        String email = regEmailW.getText();
        String telefono = regTelefonoW.getText();
        String direccion = regDireccionW.getText();
        String ciudad = regCiudadW.getText();
        String codigoPostal = regCodigoPostalW.getText();
        // Para los JPasswordField, se usa .getPassword() que devuelve un array de caracteres por seguridad.
        // Lo convertimos a String para poder usarlo.
        String contrasena = new String(regContrasenaW.getPassword());
        String claveMaestra = new String(regClaveMaestraW.getPassword());

        //
        // BLOQUE DE VALIDACIONES DE DATOS
        //
        
        // 1. Validamos la clave de registro secreta. Es la primera barrera de seguridad.
        // El método .equals() es la forma correcta de comparar si dos Strings son idénticos.
        if (!claveMaestra.equals("HotelM&L2026")) {
            JOptionPane.showMessageDialog(pantallaRegistro, "La clave de registro es incorrecta", "Acceso denegado", JOptionPane.ERROR_MESSAGE);
            // Si la clave es incorrecta, detenemos todo el proceso aquí.
            return;
        }

        // 2. Comprobamos que los datos obligatorios (los que en la BD no pueden ser NULL) no estén vacíos.
        if (dni.isEmpty() || nombre.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(pantallaRegistro, "Los campos DNI, Nombre y Contraseña son obligatorios", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Realizamos otras validaciones de formato usando nuestros métodos de ayuda.
        if (!esDniValido(dni)) {
            JOptionPane.showMessageDialog(pantallaRegistro, "El formato del DNI no es válido (debe ser 8 números y 1 letra)", "Dato incorrecto", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // El email es opcional, así que solo lo validamos si el usuario ha escrito algo.
        if (!email.isEmpty() && !esEmailValido(email)) {
            JOptionPane.showMessageDialog(pantallaRegistro, "El formato del email no es válido", "Dato incorrecto", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //
        // BLOQUE DE INSERCIÓN EN LA BASE DE DATOS
        //
        ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L");
        try {
            conexion.conectar();

            // Preparamos la instrucción SQL para insertar un nuevo registro.
            String consulta = "INSERT INTO Administradores (DNI, Nombre, Apellidos, Email, Telefono, Direccion_Calle, Direccion_Ciudad, Direccion_CP, Contrasena) VALUES ('"
                    + dni + "', '"
                    + nombre + "', '"
                    + apellidos + "', '"
                    + email + "', '"
                    + telefono + "', '"
                    + direccion + "', '"
                    + ciudad + "', '"
                    + codigoPostal + "', '"
                    + contrasena + "')";

            // Ejecutamos la consulta. El método devuelve el número de filas que se han insertado.
            int filasAfectadas = conexion.ejecutarInsertDeleteUpdate(consulta);

            // Si se ha insertado al menos una fila, la operación fue un éxito.
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(pantallaRegistro, "Administrador registrado con éxito", "Registro completado", JOptionPane.INFORMATION_MESSAGE);
                
                // Cerramos la ventana de registro para volver a la de login.
                // SwingUtilities.getWindowAncestor() es una forma de obtener la ventana (JFrame) que contiene nuestro panel.
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(pantallaRegistro);
                // .dispose() cierra la ventana y libera los recursos que estaba usando.
                frame.dispose();
                
            } else {
                JOptionPane.showMessageDialog(pantallaRegistro, "No se pudo registrar al administrador", "Error de registro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            // Si el error de SQL contiene "Duplicate entry", es porque una clave única (DNI o Email) se está repitiendo.
            if (ex.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(pantallaRegistro, "El DNI o el Email ya existen en la base de datos", "Error de duplicado", JOptionPane.ERROR_MESSAGE);
            } else {
                // Para cualquier otro error, mostramos el mensaje que nos da la base de datos.
                JOptionPane.showMessageDialog(pantallaRegistro, "Error con la base de datos: " + ex.getMessage(), "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            }
        } finally {
            try {
                conexion.desconectar();
            } catch (SQLException ex) {
                System.err.println("Error al cerrar la conexión: " + ex.getMessage());
            }
        }
    }

    //
    // MÉTODOS DE VALIDACIÓN (HERRAMIENTAS)
    //
    // Estos métodos nos ayudan a comprobar si un texto tiene un formato específico.
    
    // Comprueba si un DNI tiene 8 números y una letra.
    private boolean esDniValido(String dni) {
        // .toUpperCase() convierte la letra a mayúscula para que la validación no falle.
        // .matches() comprueba si el texto encaja con el patrón de la expresión regular.
        return dni.toUpperCase().matches("\\d{8}[A-Z]");
    }

    // Comprueba si un email tiene un formato estándar.
    private boolean esEmailValido(String email) {
        // Esta es una expresión regular estándar para validar emails.
        String patronEmail = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        // Comparamos el email del usuario con este patrón.
        return Pattern.compile(patronEmail).matcher(email).matches();
    }
    
    // Método autogenerado por el diseñador de Swing. No lo usamos por ahora.
    private void createUIComponents() {

    }
}
