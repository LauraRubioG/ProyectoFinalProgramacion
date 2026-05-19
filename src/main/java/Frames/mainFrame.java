// La primera línea de un archivo Java siempre define a qué "paquete" o carpeta pertenece la clase.
package Frames;

//
// ZONA DE IMPORTACIONES
//
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Esta clase gestiona la ventana principal de la aplicación, que es el menú
// que aparece después de que un administrador inicia sesión correctamente.
public class mainFrame {

    //
    // DECLARACIÓN DE COMPONENTES VISUALES (ATRIBUTOS DE LA CLASE)
    //
    public JPanel panelMain;
    private JButton btnGestionClientes;
    private JButton btnGestionHabitaciones;
    private JButton btnGestionReservas;
    private JButton btnCheckIn;

    //
    // CONSTRUCTOR DE LA CLASE
    //
    public mainFrame() {

        // Le asignamos una tarea al botón de "Gestionar Clientes".
        btnGestionClientes.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                
                // Al hacer clic, creamos y mostramos la ventana para gestionar clientes.
                JFrame gestionClientesFrame = new JFrame("Gestión de Clientes");
                gestionClientesFrame.setContentPane(new GestionClientesPage().panelGestionClientes);
                gestionClientesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                gestionClientesFrame.pack();
                gestionClientesFrame.setLocationRelativeTo(null);
                gestionClientesFrame.setVisible(true);
            }
        });

        // Le asignamos una tarea al botón de "Gestionar Habitaciones".
        btnGestionHabitaciones.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                // Al hacer clic, creamos y mostramos la ventana para gestionar las habitaciones.
                JFrame gestionHabitacionesFrame = new JFrame("Gestión de Habitaciones");
                gestionHabitacionesFrame.setContentPane(new GestionarHabitaciones().panelGesHab);
                gestionHabitacionesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                gestionHabitacionesFrame.pack();
                gestionHabitacionesFrame.setLocationRelativeTo(null);
                gestionHabitacionesFrame.setVisible(true);
            }
        });

        // Hacemos lo mismo para el botón de "Gestionar Reservas".
        btnGestionReservas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Al hacer clic, creamos y mostramos la ventana para gestionar las reservas.
                JFrame gestionReservasFrame = new JFrame("Gestión de Reservas");
                gestionReservasFrame.setContentPane(new Reservas().panelReservas);
                gestionReservasFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                gestionReservasFrame.pack();
                gestionReservasFrame.setLocationRelativeTo(null);
                gestionReservasFrame.setVisible(true);
            }
        });

        // Y finalmente, para el botón de "Realizar Check-in".
        btnCheckIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Al hacer clic, creamos y mostramos la ventana para realizar el check-in.
                JFrame checkinFrame = new JFrame("Realizar Check-in");
                checkinFrame.setContentPane(new CheckinPage().CheckinPage);
                checkinFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                checkinFrame.pack();
                checkinFrame.setLocationRelativeTo(null);
                checkinFrame.setVisible(true);
            }
        });
    }
}