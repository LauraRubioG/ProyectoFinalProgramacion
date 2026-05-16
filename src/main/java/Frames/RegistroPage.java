package Frames; // Indica que esta clase pertenece al paquete "Frames"

import Conexion.ConexionMySQL; // Importa la clase de conexión para poder hablar con la base de datos
import javax.swing.*; // Importa las clases de Swing para la interfaz gráfica
import java.awt.event.ActionEvent; // Importa la clase para eventos de acción
import java.awt.event.ActionListener; // Importa la interfaz para "escuchar" esos eventos
import java.sql.SQLException; // Importa la clase para manejar errores de SQL
import java.util.regex.Pattern; // Importa la clase para trabajar con expresiones regulares

// Esta clase se encarga de toda la funcionalidad de la ventana de registro de nuevos administradores
public class RegistroPage { // Define el inicio de la clase

    //
    // DECLARACIÓN DE COMPONENTES VISUALES
    //
    // Se elimina la variable regCredencialW que ya no existe en el formulario

    private JTextField regDNIW; // Campo para el DNI
    private JTextField regNombreW; // Campo para el nombre
    private JTextField regApellidosW; // Campo para los apellidos
    private JTextField regEmailW; // Campo para el email
    private JTextField regTelefonoW; // Campo para el teléfono
    private JTextField regDireccionW; // Campo para la calle
    private JTextField regCiudadW; // Campo para la ciudad
    private JTextField regCodigoPostalW; // Campo para el código postal
    private JPasswordField regContrasenaW; // Campo para la contraseña
    private JPasswordField regClaveMaestraW; // Campo para la clave de registro secreta
    private JButton regBtn; // El botón para registrar al nuevo administrador
    public JPanel pantallaRegistro; // El panel principal que contiene todo

    //
    // CONSTRUCTOR DE LA CLASE
    //
    public RegistroPage() { // Define el inicio del constructor

        // Se le asigna una tarea al botón de registrar para cuando el usuario haga clic
        regBtn.addActionListener(new ActionListener() { // Asigna un "oyente" de acciones al botón

            @Override // Indica que estamos sobrescribiendo un método
            public void actionPerformed(ActionEvent e) { // Define el método que se ejecutará con el clic

                // Se llama al método que procesa el registro
                procesarRegistroAdministrador(); // Llama al método que hace el trabajo
            } // Cierra el método actionPerformed
        }); // Cierra la definición del ActionListener
    } // Cierra el constructor

    //
    // MÉTODO PARA PROCESAR EL REGISTRO DE UN NUEVO ADMINISTRADOR
    //
    private void procesarRegistroAdministrador() { // Define el inicio del método

        // Se recoge el texto de los campos del formulario
        // Se elimina la línea que recogía la credencial
        String dni = regDNIW.getText(); // Obtiene el DNI
        String nombre = regNombreW.getText(); // Obtiene el nombre
        String apellidos = regApellidosW.getText(); // Obtiene los apellidos
        String email = regEmailW.getText(); // Obtiene el email
        String telefono = regTelefonoW.getText(); // Obtiene el teléfono
        String direccion = regDireccionW.getText(); // Obtiene la dirección
        String ciudad = regCiudadW.getText(); // Obtiene la ciudad
        String codigoPostal = regCodigoPostalW.getText(); // Obtiene el código postal
        String contrasena = new String(regContrasenaW.getPassword()); // Obtiene la contraseña
        String claveMaestra = new String(regClaveMaestraW.getPassword()); // Obtiene la clave de registro

        //
        // BLOQUE DE VALIDACIONES DE DATOS
        //
        
        // 1. Se valida la clave de registro secreta
        if (!claveMaestra.equals("HotelM&L2026")) { // Compara la clave introducida con la clave correcta
            JOptionPane.showMessageDialog(pantallaRegistro, "La clave de registro es incorrecta", "Acceso denegado", JOptionPane.ERROR_MESSAGE);
            return; // Detiene el método inmediatamente si la clave es incorrecta
        }

        // 2. Se comprueba que los datos obligatorios no estén vacíos
        // Se elimina la credencial de la comprobación
        if (dni.isEmpty() || nombre.isEmpty() || contrasena.isEmpty()) { // Comprueba los campos NOT NULL de la BD
            JOptionPane.showMessageDialog(pantallaRegistro, "Los campos DNI, Nombre y Contraseña son obligatorios", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return; // Detiene el método
        }

        // 3. Se realizan otras validaciones de formato
        if (!esDniValido(dni)) { // Valida el formato del DNI
            JOptionPane.showMessageDialog(pantallaRegistro, "El formato del DNI no es válido (debe ser 8 números y 1 letra)", "Dato incorrecto", JOptionPane.WARNING_MESSAGE);
            return; // Detiene el método
        }

        if (!email.isEmpty() && !esEmailValido(email)) { // Si el email no está vacío, valida su formato
            JOptionPane.showMessageDialog(pantallaRegistro, "El formato del email no es válido", "Dato incorrecto", JOptionPane.WARNING_MESSAGE);
            return; // Detiene el método
        }

        //
        // BLOQUE DE INSERCIÓN EN LA BASE DE DATOS
        //
        ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L"); // Crea el objeto de conexión
        try { // Inicia el bloque para manejar errores de base de datos
            conexion.conectar(); // Conecta a la base de datos

            // Se prepara la instrucción SQL para insertar un nuevo registro en la tabla "Administradores"
            // Se elimina la columna Credencial_Empleado de la consulta
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

            int filasAfectadas = conexion.ejecutarInsertDeleteUpdate(consulta); // Ejecuta la inserción

            if (filasAfectadas > 0) { // Si es mayor que cero, todo fue bien
                JOptionPane.showMessageDialog(pantallaRegistro, "Administrador registrado con éxito", "Registro completado", JOptionPane.INFORMATION_MESSAGE);
                
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(pantallaRegistro); // Obtiene la ventana actual
                frame.dispose(); // La cierra
                
            } else { // Si no se afectó ninguna fila
                JOptionPane.showMessageDialog(pantallaRegistro, "No se pudo registrar al administrador", "Error de registro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) { // Si ocurre un error de SQL
            if (ex.getMessage().contains("Duplicate entry")) { // Comprueba el mensaje de error
                JOptionPane.showMessageDialog(pantallaRegistro, "El DNI o el Email ya existen en la base de datos", "Error de duplicado", JOptionPane.ERROR_MESSAGE);
            } else { // Para cualquier otro error
                JOptionPane.showMessageDialog(pantallaRegistro, "Error con la base de datos: " + ex.getMessage(), "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            }
        } finally { // Al final, siempre se cierra la conexión
            try {
                conexion.desconectar(); // Cierra la conexión
            } catch (SQLException ex) {
                System.err.println("Error al cerrar la conexión: " + ex.getMessage());
            }
        }
    }

    //
    // MÉTODOS DE VALIDACIÓN (HERRAMIENTAS)
    //
    private boolean esDniValido(String dni) { // Comprueba el formato del DNI
        return dni.toUpperCase().matches("\\d{8}[A-Z]");
    }

    private boolean esEmailValido(String email) { // Comprueba el formato del email
        String patronEmail = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return Pattern.compile(patronEmail).matcher(email).matches();
    }
    
    private void createUIComponents() { // Método autogenerado por Swing
        // No se necesita por ahora
    }
}
