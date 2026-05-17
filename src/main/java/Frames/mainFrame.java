package Frames; // Indica que esta clase pertenece al paquete "Frames", que organiza nuestras ventanas

import javax.swing.*; // Importa todas las clases de Swing para crear las interfaces gráficas
import java.awt.event.ActionEvent; // Importa la clase que representa un evento, como hacer clic en un botón
import java.awt.event.ActionListener; // Importa la interfaz que nos permite "escuchar" y responder a esos eventos

// Esta clase gestiona la ventana principal de la aplicación,
// que es el menú que aparece después de que un usuario inicia sesión correctamente
public class mainFrame { // Define el inicio de nuestra clase

    //
    // DECLARACIÓN DE COMPONENTES VISUALES
    //
    // Estas variables conectan el código con los botones del menú principal
    public JPanel panelMain; // Declara el panel principal que sirve de contenedor
    private JButton btnGestionClientes; // Declara una variable para el botón de gestionar clientes
    private JButton btnGestionHabitaciones; // Declara una variable para el botón de gestionar habitaciones
    private JButton btnGestionReservas; // Declara una variable para el botón de gestionar reservas
    private JButton btnCheckIn; // Declara una variable para el botón de check-in

    //
    // CONSTRUCTOR DE LA CLASE
    //
    // Aquí se define el comportamiento de cada uno de los botones del menú
    public mainFrame() { // Define el inicio del constructor

        // Le asignamos una tarea al botón de "Gestionar Clientes"
        btnGestionClientes.addActionListener(new ActionListener() { // Asigna un "oyente" de acciones al botón
            @Override // Indica que estamos sobrescribiendo un método
            public void actionPerformed(ActionEvent e) { // Define el método que se ejecutará con el clic
                // Como esta parte de la aplicación todavía no está construida,
                // de momento solo mostramos un aviso al usuario
                JOptionPane.showMessageDialog(panelMain, "Funcionalidad en desarrollo", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            } // Cierra el método actionPerformed
        }); // Cierra la definición del ActionListener

        // Hacemos lo mismo para el botón de "Gestionar Habitaciones"
        btnGestionHabitaciones.addActionListener(new ActionListener() { // Asigna un "oyente" de acciones al botón
            @Override // Indica que estamos sobrescribiendo un método
            public void actionPerformed(ActionEvent e) { // Define el método que se ejecutará con el clic
                
                // CREACIÓN Y CONFIGURACIÓN DE LA VENTANA DE GESTIÓN DE HABITACIONES
                
                JFrame frame = new JFrame("Gestionar Habitaciones"); // Crea una nueva ventana con el título "Gestionar Habitaciones"
                
                frame.setContentPane(new GestionarHabitaciones().panelGesHab); // Establece el contenido de la ventana para que sea el panel de nuestra clase GestionarHabitaciones
                
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Define que la ventana solo se cierre ella misma, sin afectar al resto de la aplicación
                
                frame.pack(); // Ajusta el tamaño de la ventana automáticamente según los componentes que contiene
                
                frame.setLocationRelativeTo(null); // Centra la ventana en la pantalla
                
                frame.setVisible(true); // Hace que la ventana sea visible para el usuario

            } // Cierra el método actionPerformed
        }); // Cierra la definición del ActionListener

        // Y para el botón de "Gestionar Reservas"
        btnGestionReservas.addActionListener(new ActionListener() { // Asigna un "oyente" de acciones al botón
            @Override // Indica que estamos sobrescribiendo un método
            public void actionPerformed(ActionEvent e) { // Define el método que se ejecutará con el clic
                JOptionPane.showMessageDialog(panelMain, "Funcionalidad en desarrollo", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            } // Cierra el método actionPerformed
        }); // Cierra la definición del ActionListener

        // Y finalmente, para el botón de "Realizar Check-in"
        btnCheckIn.addActionListener(new ActionListener() { // Asigna un "oyente" de acciones al botón
            @Override // Indica que estamos sobrescribiendo un método
            public void actionPerformed(ActionEvent e) { // Define el método que se ejecutará con el clic
                JOptionPane.showMessageDialog(panelMain, "Funcionalidad en desarrollo", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            } // Cierra el método actionPerformed
        }); // Cierra la definición del ActionListener
    } // Cierra el constructor de la clase
} // Cierra la clase mainFrame
