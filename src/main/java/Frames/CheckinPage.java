package Frames; // Indica la carpeta o paquete donde guardamos esta parte del programa

import Conexion.ConexionMySQL; // Herramienta para conectar a la base de datos

import javax.swing.*; // Herramientas visuales
import javax.swing.table.DefaultTableModel; // Para organizar la tabla
import java.sql.ResultSet; // Para leer datos
import java.sql.SQLException; // Para manejar errores de base de datos

//
// CLASE PARA GESTIONAR EL CHECK-IN
//
// Pantalla que usa el recepcionista cuando llega un cliente al hotel
public class CheckinPage { // Inicio de la clase

    //
    // ELEMENTOS DE LA PANTALLA
    //
    public JPanel CheckinPage; // El panel principal de la ventana
    private JTable checkinghoyTabla; // La tabla donde veremos las llegadas pendientes
    private JButton chebtnRealizar; // Boton para confirmar que el cliente ha llegado
    private JButton chebtnCancelar; // Boton para cancelar si el cliente no aparece

    //
    // VARIABLES INTERNAS
    //
    private final DefaultTableModel modeloTabla; // Organizador de la tabla
    private String idReservaSeleccionada = ""; // Para recordar que reserva tocamos
    private String habitacionSeleccionada = ""; // Para recordar que habitacion tiene asignada

    //
    // CONSTRUCTOR
    //
    public CheckinPage() {

        //
        // PREPARAR LA TABLA DE LLEGADAS
        //
        modeloTabla = new DefaultTableModel() { // Creamos el organizador
            @Override
            public boolean isCellEditable(int row, int column) { // Bloqueamos la edicion manual
                return false;
            } // Fin de funcion
        }; // Fin de creacion

        // Anadimos las columnas necesarias
        modeloTabla.addColumn("ID Reserva"); // Columna 1
        modeloTabla.addColumn("Cliente (DNI)"); // Columna 2
        modeloTabla.addColumn("Habitacion"); // Columna 3
        modeloTabla.addColumn("Entrada"); // Columna 4
        modeloTabla.addColumn("Salida"); // Columna 5
        
        checkinghoyTabla.setModel(modeloTabla); // Unimos el organizador con la tabla visual

        // Cargamos los datos nada mas abrir la ventana
        cargarLlegadasPendientes();

        //
        // TAREA: SELECCIONAR UNA FILA DE LA TABLA
        //
        checkinghoyTabla.getSelectionModel().addListSelectionListener(e -> { // Al hacer clic en la tabla
            if (!e.getValueIsAdjusting()) { // Evitar doble clic fantasma
                int fila = checkinghoyTabla.getSelectedRow(); // Vemos que fila se ha tocado
                if (fila >= 0) { // Si es una fila valida
                    // Guardamos el ID de la reserva y el numero de la habitacion
                    idReservaSeleccionada = modeloTabla.getValueAt(fila, 0).toString(); // Guardamos el ID
                    habitacionSeleccionada = modeloTabla.getValueAt(fila, 2).toString(); // Guardamos la habitacion
                } // Fin comprobacion
            } // Fin doble clic
        }); // Fin tarea tabla

        //
        // TAREA: BOTON REALIZAR CHECK-IN
        //
        // Pasa la reserva de "Pendiente" a "En Curso"
        chebtnRealizar.addActionListener(e -> { // Al pulsar el boton
            if (idReservaSeleccionada.isEmpty()) { // Comprobamos que haya seleccionado a alguien
                JOptionPane.showMessageDialog(CheckinPage, "Seleccione una reserva de la tabla primero.", "Aviso", JOptionPane.WARNING_MESSAGE); // Avisamos
                return; // Paramos
            } // Fin comprobacion

            // Preguntamos para estar seguros
            int confirmacion = JOptionPane.showConfirmDialog(CheckinPage, "¿Confirmar el Check-In para la reserva " + idReservaSeleccionada + "?", "Confirmar", JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) { // Si dice que si
                try { // Intentamos conectar
                    ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L"); // Preparamos conexion
                    conexion.conectar(); // Conectamos

                    // ¡VUELTO A LA NORMALIDAD! El ID_Reserva ahora es numero, no hacen falta comillas simples en el SQL
                    String consulta = "UPDATE reservas SET Estado_Reserva = 'En Curso' WHERE ID_Reserva = " + idReservaSeleccionada; // Ordenamos el cambio
                    int filas = conexion.ejecutarInsertDeleteUpdate(consulta); // Lo ejecutamos

                    if (filas > 0) { // Si ha funcionado
                        JOptionPane.showMessageDialog(CheckinPage, "Check-in realizado con exito. Entregue las llaves de la habitacion " + habitacionSeleccionada + " al cliente."); // Exito
                        idReservaSeleccionada = ""; // Olvidamos la seleccion
                        habitacionSeleccionada = ""; // Olvidamos la habitacion
                        cargarLlegadasPendientes(); // Recargamos la tabla para que desaparezca
                    } else { // Si falla
                        JOptionPane.showMessageDialog(CheckinPage, "Error al realizar el Check-in."); // Avisamos
                    } // Fin comprobacion exito

                    conexion.desconectar(); // Cerramos conexion

                } catch (SQLException ex) { // Por si hay errores
                    JOptionPane.showMessageDialog(CheckinPage, "Error SQL: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Mostramos el error
                } // Fin bloque seguridad
            } // Fin confirmacion
        }); // Fin tarea Check-in

        //
        // TAREA: BOTON CANCELAR RESERVA (NO SHOW)
        //
        // Borra la reserva si el cliente nunca llego a aparecer
        chebtnCancelar.addActionListener(e -> { // Al pulsar cancelar
            if (idReservaSeleccionada.isEmpty()) { // Comprobamos seleccion
                JOptionPane.showMessageDialog(CheckinPage, "Seleccione una reserva de la tabla primero.", "Aviso", JOptionPane.WARNING_MESSAGE); // Avisamos
                return; // Paramos
            } // Fin comprobacion

            // Preguntamos para asegurar, ya que borrar es delicado
            int confirmacion = JOptionPane.showConfirmDialog(CheckinPage, "¿Esta seguro de que desea cancelar esta reserva? (El cliente no se ha presentado)", "Confirmar Cancelacion", JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) { // Si dice que si
                try { // Intentamos conectar
                    ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L"); // Preparamos conexion
                    conexion.conectar(); // Conectamos

                    // ¡VUELTO A LA NORMALIDAD! Borramos la reserva del sistema (Sin comillas porque ID_Reserva es INT)
                    String consultaDel = "DELETE FROM reservas WHERE ID_Reserva = " + idReservaSeleccionada; // Orden de borrado
                    int filas = conexion.ejecutarInsertDeleteUpdate(consultaDel); // Ejecutamos

                    if (filas > 0) { // Si se borro bien
                        // Como la reserva se cancela, la habitacion vuelve a estar disponible
                        if (!habitacionSeleccionada.isEmpty()) { // Si recordamos cual era
                            String consultaHab = "UPDATE habitaciones SET Estado = 'D' WHERE Numero_Hab = '" + habitacionSeleccionada + "'"; // Orden de liberacion
                            conexion.ejecutarInsertDeleteUpdate(consultaHab); // Ejecutamos
                        } // Fin comprobacion habitacion

                        JOptionPane.showMessageDialog(CheckinPage, "Reserva cancelada. La habitacion " + habitacionSeleccionada + " vuelve a estar libre."); // Celebramos
                        idReservaSeleccionada = ""; // Olvidamos seleccion
                        habitacionSeleccionada = ""; // Olvidamos habitacion
                        cargarLlegadasPendientes(); // Recargamos tabla
                    } // Fin comprobacion exito

                    conexion.desconectar(); // Cerramos conexion

                } catch (SQLException ex) { // Por si hay errores
                    JOptionPane.showMessageDialog(CheckinPage, "Error SQL: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Mostramos fallo
                } // Fin bloque seguridad
            } // Fin confirmacion
        }); // Fin tarea cancelar
    } // Fin del constructor

    //
    // FUNCION PARA RELLENAR LA TABLA CON LLEGADAS PENDIENTES
    //
    private void cargarLlegadasPendientes() { // Inicio funcion
        modeloTabla.setRowCount(0); // Vaciamos la tabla para que no se dupliquen datos

        try { // Intentamos conectar
            ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L"); // Preparamos conexion
            conexion.conectar(); // Conectamos

            // Buscamos SOLO las reservas Pendientes Y cuya Fecha de Entrada sea HOY
            // CURDATE() es una funcion de MySQL que da la fecha del dia de hoy automaticamente
            String consulta = "SELECT r.ID_Reserva, c.DNI AS DNI_Cliente, h.Numero_Hab AS Num_Habitacion, " +
                    "r.Fecha_Entrada, r.Fecha_Salida " + // Datos que queremos
                    "FROM reservas r " + // Tabla principal
                    "INNER JOIN clientes c ON r.FK_Cliente = c.DNI " + // Cruzamos con clientes
                    "INNER JOIN habitaciones h ON r.FK_Habitacion = h.Numero_Hab " + // Cruzamos con habitaciones
                    "WHERE r.Estado_Reserva = 'Pendiente' AND r.Fecha_Entrada = CURDATE()"; // CONDICION: Pendientes Y de HOY

            ResultSet rs = conexion.ejecutarSelect(consulta); // Ejecutamos la busqueda

            while (rs.next()) { // Recorremos los resultados uno por uno
                Object[] fila = new Object[5]; // Creamos una fila de 5 huecos
                // ¡VUELTO A LA NORMALIDAD! Volvemos a leer el ID como un numero entero (int)
                fila[0] = rs.getInt("ID_Reserva"); // Metemos el ID
                fila[1] = rs.getString("DNI_Cliente"); // Metemos el DNI
                fila[2] = rs.getString("Num_Habitacion"); // Metemos la habitacion
                fila[3] = rs.getString("Fecha_Entrada"); // Metemos la fecha de entrada
                fila[4] = rs.getString("Fecha_Salida"); // Metemos la fecha de salida

                modeloTabla.addRow(fila); // La anadimos a la tabla visual
            } // Fin repeticion

            conexion.desconectar(); // Cerramos conexion

        } catch (SQLException e) { // Por si hay errores
            JOptionPane.showMessageDialog(CheckinPage, "Error al cargar las llegadas: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE); // Avisamos
        } // Fin bloque seguridad
    } // Fin funcion
} // Fin clase