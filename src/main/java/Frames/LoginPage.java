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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

// Esta clase define todo el comportamiento y la lógica de nuestra ventana de inicio de sesión.
public class LoginPage {

    //
    // DECLARACIÓN DE COMPONENTES VISUALES (ATRIBUTOS DE LA CLASE)
    //
    // Aquí declaramos las variables que representan a cada uno de los componentes del formulario.
    private JTextField logDNIW;
    private JPasswordField logPasswordW;
    private JButton logBtnIniciarSesion;
    private JButton logRegBtn;
    public JPanel pantallaLogin;

    //
    // CONSTRUCTOR DE LA CLASE
    //
    // Es el método que se ejecuta al crear la ventana. Su misión es preparar los componentes.
    public LoginPage() {

        //
        // LÓGICA DEL BOTÓN DE INICIO DE SESIÓN
        //
        // Le decimos al botón de iniciar sesión qué debe hacer cuando un usuario le haga clic.
        logBtnIniciarSesion.addActionListener(new ActionListener() {

            // Este método se dispara justo cuando el usuario pulsa el botón.
            @Override
            public void actionPerformed(ActionEvent e) {

                // Recogemos el texto que el administrador ha introducido en los campos.
                String dni = logDNIW.getText();
                String contrasena = new String(logPasswordW.getPassword());

                //
                // BLOQUE DE VALIDACIONES RÁPIDAS
                //
                // Antes de consultar la base de datos, hacemos unas comprobaciones básicas.
                
                // ¿Ha dejado el administrador alguno de los campos en blanco?
                if (dni.isEmpty() || contrasena.isEmpty()) {
                    JOptionPane.showMessageDialog(pantallaLogin, "Por favor, rellene todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                    // Detenemos la ejecución aquí para no continuar.
                    return;
                }

                // ¿Tiene el DNI un formato que no es válido?
                if (!esDniValido(dni)) {
                    JOptionPane.showMessageDialog(pantallaLogin, "El formato del DNI no es válido", "Dato incorrecto", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                //
                // BLOQUE DE CONEXIÓN A LA BASE DE DATOS
                //
                // Si los formatos son correctos, ahora sí, verificamos si el administrador existe.
                ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L");
                try {
                    conexion.conectar();

                    // Preparamos la consulta para buscar un administrador con ese DNI y esa contraseña.
                    String consulta = "SELECT * FROM Administradores WHERE DNI = '" + dni + "' AND Contrasena = '" + contrasena + "'";
                    
                    // Ejecutamos la consulta y guardamos el resultado.
                    ResultSet resultado = conexion.ejecutarSelect(consulta);

                    // El método .next() intenta moverse a la primera fila del resultado.
                    // Si devuelve 'true', es que se encontró una coincidencia y los datos son correctos.
                    if (resultado.next()) {
                        JOptionPane.showMessageDialog(pantallaLogin, "¡Inicio de sesión correcto!");

                        // Cerramos la ventana de login.
                        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(pantallaLogin);
                        frame.dispose();

                        // Y abrimos la ventana principal de la aplicación.
                        JFrame mainFrame = new JFrame("Panel Principal de Gestión");
                        mainFrame.setContentPane(new mainFrame().panelMain);
                        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        mainFrame.pack();
                        mainFrame.setLocationRelativeTo(null);
                        mainFrame.setVisible(true);

                    } else {
                        // Si .next() devuelve 'false', no se encontró ningún administrador con esos datos.
                        JOptionPane.showMessageDialog(pantallaLogin, "DNI o contraseña incorrectos", "Error de inicio de sesión", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    // Si algo falla durante la conexión o la consulta, informamos al usuario.
                    JOptionPane.showMessageDialog(pantallaLogin, "Error con la base de datos: " + ex.getMessage(), "Error de Conexión", JOptionPane.ERROR_MESSAGE);
                } finally {
                    // Al final de todo, nos aseguramos de cerrar la conexión.
                    try {
                        conexion.desconectar();
                    } catch (SQLException ex) {
                        System.err.println("Error al cerrar la conexión: " + ex.getMessage());
                    }
                }
            }
        });

        //
        // LÓGICA DEL BOTÓN DE REGISTRO DE ADMINISTRADORES
        //
        // Le decimos al botón de registro qué debe hacer cuando un usuario le haga clic.
        logRegBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Al hacer clic, simplemente creamos y mostramos la ventana de Registro de Administradores.
                JFrame registroFrame = new JFrame("Registro de Nuevo Administrador");
                registroFrame.setContentPane(new RegistroPage().pantallaRegistro);
                // DISPOSE_ON_CLOSE hace que al cerrar la ventana de registro, no se cierre también la de login.
                registroFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                registroFrame.pack();
                registroFrame.setLocationRelativeTo(null);
                registroFrame.setVisible(true);
            }
        });
    }

    //
    // MÉTODO DE VALIDACIÓN (HERRAMIENTA)
    //
    // Un método de ayuda para comprobar el formato del DNI.
    private boolean esDniValido(String dni) {
        return dni.toUpperCase().matches("\\d{8}[A-Z]");
    }
}
