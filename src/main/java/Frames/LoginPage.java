package Frames; // Indica que esta clase pertenece al paquete "Frames"

import Conexion.ConexionMySQL; // Importa la clase de conexión para poder hablar con la base de datos
import javax.swing.*; // Importa las clases de Swing para la interfaz gráfica
import java.awt.event.ActionEvent; // Importa la clase para eventos de acción
import java.awt.event.ActionListener; // Importa la interfaz para "escuchar" esos eventos
import java.sql.ResultSet; // Importa la clase para manejar los resultados de una consulta
import java.sql.SQLException; // Importa la clase para manejar errores de SQL
import java.util.regex.Pattern; // Importa la clase para trabajar con expresiones regulares

// Esta clase gestiona la ventana de inicio de sesión para los administradores del sistema
public class LoginPage { // Define el inicio de la clase

    //
    // DECLARACIÓN DE COMPONENTES VISUALES
    //
    private JTextField logDNIW; // El campo para que el administrador escriba su DNI
    private JPasswordField logPasswordW; // El campo para que el administrador escriba su contraseña
    private JButton logBtnIniciarSesion; // El botón para intentar el inicio de sesión
    private JButton logRegBtn; // El botón que lleva a la ventana de registro
    public JPanel pantallaLogin; // El panel principal que contiene todo

    //
    // CONSTRUCTOR Y LÓGICA DE BOTONES
    //
    public LoginPage() { // Define el inicio del constructor

        //
        // LÓGICA DEL BOTÓN DE INICIO DE SESIÓN
        //
        logBtnIniciarSesion.addActionListener(new ActionListener() { // Asigna un "oyente" de acciones al botón

            @Override // Indica que estamos sobrescribiendo un método
            public void actionPerformed(ActionEvent e) { // Define el método que se ejecutará con el clic

                // Se recoge el texto que el administrador ha introducido en los campos
                String dni = logDNIW.getText(); // Obtiene el texto del campo del DNI
                String contrasena = new String(logPasswordW.getPassword()); // Obtiene la contraseña

                //
                // BLOQUE DE VALIDACIONES RÁPIDAS
                //
                if (dni.isEmpty() || contrasena.isEmpty()) { // Comprueba si alguno de los campos está vacío
                    JOptionPane.showMessageDialog(pantallaLogin, "Por favor, rellene todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Detiene la ejecución del método
                }

                if (!esDniValido(dni)) { // Llama a nuestro método de validación de DNI
                    JOptionPane.showMessageDialog(pantallaLogin, "El formato del DNI no es válido", "Dato incorrecto", JOptionPane.WARNING_MESSAGE);
                    return; // Detiene el método
                }

                //
                // BLOQUE DE CONEXIÓN A LA BASE DE DATOS
                //
                ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L");
                try { // Se inicia un bloque para manejar posibles errores de base de datos
                    conexion.conectar(); // Llama al método para conectar

                    // Se prepara la consulta para buscar un administrador con ESE DNI Y ESA contraseña
                    String consulta = "SELECT * FROM Administradores WHERE DNI = '" + dni + "' AND Contrasena = '" + contrasena + "'";
                    
                    ResultSet resultado = conexion.ejecutarSelect(consulta); // Ejecuta la consulta

                    if (resultado.next()) { // Si ".next()" devuelve "true", es que los datos son correctos
                        JOptionPane.showMessageDialog(pantallaLogin, "¡Inicio de sesión correcto!");

                        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(pantallaLogin); // Obtiene la ventana actual
                        frame.dispose(); // La cierra

                        JFrame mainFrame = new JFrame("Panel Principal de Gestión"); // Crea la nueva ventana
                        mainFrame.setContentPane(new mainFrame().panelMain); // Le asigna el panel principal
                        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Define el comportamiento de cierre
                        mainFrame.pack(); // Ajusta su tamaño
                        mainFrame.setLocationRelativeTo(null); // La centra en la pantalla
                        mainFrame.setVisible(true); // La hace visible

                    } else { // Si ".next()" es "false", no se encontró ninguna coincidencia
                        JOptionPane.showMessageDialog(pantallaLogin, "DNI o contraseña incorrectos", "Error de inicio de sesión", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) { // Si algo falla durante la conexión o la consulta
                    JOptionPane.showMessageDialog(pantallaLogin, "Error con la base de datos: " + ex.getMessage(), "Error de Conexión", JOptionPane.ERROR_MESSAGE);
                } finally { // Al final de todo, sin importar si hubo éxito o error
                    try { // Se asegura de cerrar la conexión
                        conexion.desconectar(); // Llama al método para cerrar la conexión
                    } catch (SQLException ex) { // Si el cierre también falla
                        System.err.println("Error al cerrar la conexión: " + ex.getMessage());
                    }
                }
            }
        });

        //
        // LÓGICA DEL BOTÓN DE REGISTRO DE ADMINISTRADORES
        //
        logRegBtn.addActionListener(new ActionListener() { // Asigna un "oyente" al botón
            @Override // Sobrescribe el método
            public void actionPerformed(ActionEvent e) { // Define lo que pasa al hacer clic
                JFrame registroFrame = new JFrame("Registro de Nuevo Administrador"); // Crea la nueva ventana
                registroFrame.setContentPane(new RegistroPage().pantallaRegistro); // Le asigna el panel de la clase RegistroPage
                registroFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Hace que solo se cierre esta ventana
                registroFrame.pack(); // Ajusta el tamaño
                registroFrame.setLocationRelativeTo(null); // La centra
                registroFrame.setVisible(true); // La hace visible
            }
        });
    }

    //
    // MÉTODO DE VALIDACIÓN (HERRAMIENTA)
    //
    private boolean esDniValido(String dni) { // Comprueba el formato del DNI
        return dni.toUpperCase().matches("\\d{8}[A-Z]");
    }
}
