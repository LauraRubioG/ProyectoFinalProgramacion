// La primera línea de un archivo Java siempre define a qué "paquete" o carpeta pertenece la clase.
package Frames;

//
// ZONA DE IMPORTACIONES
//
import Conexion.ConexionMySQL;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

// Esta clase define todo el comportamiento y la lógica de nuestra ventana de gestión de habitaciones.
public class GestionarHabitaciones {

    //
    // DECLARACIÓN DE COMPONENTES VISUALES (ATRIBUTOS DE LA CLASE)
    //
    public JPanel panelGesHab;
    private JTextField habNumeroHabW;
    private JComboBox<String> habTipoW;
    private JTextField habPrecioW;
    private JComboBox<String> habEstadoW;
    private JButton habBtnGuardar;
    private JButton habBtnNuevo;
    private JButton habBtnEliminar;
    private JTable habTablaHabitaciones;

    private DefaultTableModel tableModel;

    private boolean modoNuevaHabitacion = false;

    //
    // CONSTRUCTOR DE LA CLASE
    //
    public GestionarHabitaciones() {
        
        inicializarTabla();
        configurarComboBoxes();
        cargarHabitaciones();
        configurarListeners();
        actualizarEstadoFormulario(false);
    }

    //
    // MÉTODO PARA PREPARAR LA ESTRUCTURA DE LA TABLA
    //
    private void inicializarTabla() {
        
        tableModel = new DefaultTableModel() {
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableModel.addColumn("Nº Habitación");
        tableModel.addColumn("Tipo");
        tableModel.addColumn("Precio/Noche");
        tableModel.addColumn("Estado");

        habTablaHabitaciones.setModel(tableModel);
    }

    //
    // MÉTODO PARA CONFIGURAR LOS MENÚS DESPLEGABLES (JCOMBOBOX)
    //
    private void configurarComboBoxes() {
        
        habTipoW.removeAllItems();
        habEstadoW.removeAllItems();
        
        habTipoW.addItem("Individual");
        habTipoW.addItem("Doble");
        habTipoW.addItem("Suite");
        
        habEstadoW.addItem("Disponible");
        habEstadoW.addItem("Ocupada");
        habEstadoW.addItem("Mantenimiento");
    }

    //
    // MÉTODO PARA CARGAR LOS DATOS DE LAS HABITACIONES DESDE LA BASE DE DATOS
    //
    private void cargarHabitaciones() {
        
        tableModel.setRowCount(0);

        ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L");
        
        try {
            conexion.conectar();
            
            // CORRECCIÓN: Se cambia 'Habitacion' (singular) por 'habitaciones' (plural) para que coincida con la BD.
            String consulta = "SELECT * FROM habitaciones ORDER BY Numero_Hab ASC";
            
            ResultSet resultado = conexion.ejecutarSelect(consulta);

            while (resultado.next()) {
                
                String numeroHab = resultado.getString("Numero_Hab");
                String tipo = resultado.getString("Tipo");
                double precio = resultado.getDouble("Precio_Noche");
                String estadoDB = resultado.getString("Estado");
                
                String estadoTexto = convertirEstado(estadoDB, true);
                
                Object[] fila = {
                        numeroHab,
                        tipo,
                        precio,
                        estadoTexto
                };
                
                tableModel.addRow(fila);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(panelGesHab, "Error al cargar las habitaciones: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                conexion.desconectar();
            } catch (SQLException ex) {
                System.err.println("Error al cerrar la conexión: " + ex.getMessage());
            }
        }
    }

    //
    // MÉTODO PARA CONFIGURAR TODOS LOS EVENTOS
    //
    private void configurarListeners() {

        habTablaHabitaciones.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    mostrarDatosDeHabitacionSeleccionada();
                }
            }
        });

        habBtnNuevo.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                prepararFormularioParaNuevaHabitacion();
            }
        });

        habBtnGuardar.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarHabitacion();
            }
        });

        habBtnEliminar.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarHabitacion();
            }
        });
    }

    //
    // MÉTODOS CON LA LÓGICA DE LA APLICACIÓN
    //

    private void mostrarDatosDeHabitacionSeleccionada() {
        
        int filaSeleccionada = habTablaHabitaciones.getSelectedRow();

        if (filaSeleccionada != -1) {
            
            actualizarEstadoFormulario(true);
            
            habNumeroHabW.setEnabled(false);
            
            modoNuevaHabitacion = false;

            habNumeroHabW.setText(tableModel.getValueAt(filaSeleccionada, 0).toString());
            habTipoW.setSelectedItem(tableModel.getValueAt(filaSeleccionada, 1).toString());
            habPrecioW.setText(tableModel.getValueAt(filaSeleccionada, 2).toString());
            habEstadoW.setSelectedItem(tableModel.getValueAt(filaSeleccionada, 3).toString());
        }
    }

    private void prepararFormularioParaNuevaHabitacion() {
        
        habTablaHabitaciones.clearSelection();
        
        actualizarEstadoFormulario(true);
        
        habNumeroHabW.setEnabled(true);
        
        modoNuevaHabitacion = true;

        habNumeroHabW.setText("");
        habTipoW.setSelectedIndex(0);
        habPrecioW.setText("");
        habEstadoW.setSelectedIndex(0);
    }

    private void guardarHabitacion() {
        
        if (!validarCampos()) {
            return;
        }

        if (modoNuevaHabitacion) {
            insertarNuevaHabitacion();
        } else {
            actualizarHabitacionExistente();
        }
    }

    private void insertarNuevaHabitacion() {
        
        String numeroHab = habNumeroHabW.getText();
        String tipo = habTipoW.getSelectedItem().toString();
        String precio = habPrecioW.getText();
        String estado = convertirEstado(habEstadoW.getSelectedItem().toString(), false);
        
        ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L");
        try {
            conexion.conectar();
            
            // CORRECCIÓN: Se cambia 'Habitacion' (singular) por 'habitaciones' (plural).
            String consulta = "INSERT INTO habitaciones (Numero_Hab, Tipo, Precio_Noche, Estado) VALUES ('"
                    + numeroHab + "', '" + tipo + "', " + precio + ", '" + estado + "')";
            
            int filasAfectadas = conexion.ejecutarInsertDeleteUpdate(consulta);

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(panelGesHab, "Habitación guardada con éxito", "Operación completada", JOptionPane.INFORMATION_MESSAGE);
                
                cargarHabitaciones();
                
                actualizarEstadoFormulario(false);
                prepararFormularioParaNuevaHabitacion();
            } else {
                JOptionPane.showMessageDialog(panelGesHab, "No se pudo guardar la habitación", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            if (ex.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(panelGesHab, "Ya existe una habitación con ese número", "Error de duplicado", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(panelGesHab, "Error al guardar la habitación: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            }
        } finally {
            try { conexion.desconectar(); } catch (SQLException ex) { System.err.println("Error al cerrar la conexión."); }
        }
    }

    private void actualizarHabitacionExistente() {
        
        String numeroHab = habNumeroHabW.getText();
        String tipo = habTipoW.getSelectedItem().toString();
        String precio = habPrecioW.getText();
        String estado = convertirEstado(habEstadoW.getSelectedItem().toString(), false);
        
        ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L");
        try {
            conexion.conectar();
            
            // CORRECCIÓN: Se cambia 'Habitacion' (singular) por 'habitaciones' (plural).
            String consulta = "UPDATE habitaciones SET "
                    + "Tipo = '" + tipo + "', "
                    + "Precio_Noche = " + precio + ", "
                    + "Estado = '" + estado + "' "
                    + "WHERE Numero_Hab = '" + numeroHab + "'";
            
            int filasAfectadas = conexion.ejecutarInsertDeleteUpdate(consulta);

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(panelGesHab, "Habitación actualizada con éxito", "Operación completada", JOptionPane.INFORMATION_MESSAGE);
                
                cargarHabitaciones();
                
                actualizarEstadoFormulario(false);
            } else {
                JOptionPane.showMessageDialog(panelGesHab, "No se pudo actualizar la habitación. Puede que ya no exista.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(panelGesHab, "Error al actualizar la habitación: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { conexion.desconectar(); } catch (SQLException ex) { System.err.println("Error al cerrar la conexión."); }
        }
    }

    private void eliminarHabitacion() {
        
        int filaSeleccionada = habTablaHabitaciones.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(panelGesHab, "Por favor, seleccione una habitación de la tabla para eliminarla", "Ninguna habitación seleccionada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(panelGesHab, "¿Estás seguro de que quieres eliminar esta habitación? Esta acción no se puede deshacer.", "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            
            String numeroHab = habNumeroHabW.getText();

            ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L");
            try {
                conexion.conectar();
                
                // CORRECCIÓN: Se cambia 'Habitacion' (singular) por 'habitaciones' (plural).
                String consulta = "DELETE FROM habitaciones WHERE Numero_Hab = '" + numeroHab + "'";
                
                int filasAfectadas = conexion.ejecutarInsertDeleteUpdate(consulta);

                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(panelGesHab, "Habitación eliminada con éxito", "Operación completada", JOptionPane.INFORMATION_MESSAGE);
                    
                    cargarHabitaciones();
                    
                    actualizarEstadoFormulario(false);
                    prepararFormularioParaNuevaHabitacion();
                } else {
                    JOptionPane.showMessageDialog(panelGesHab, "No se pudo eliminar la habitación. Puede que ya haya sido eliminada.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                if (ex.getMessage().contains("foreign key constraint")) {
                    JOptionPane.showMessageDialog(panelGesHab, "No se puede eliminar la habitación porque tiene reservas activas o pasadas.", "Error de integridad", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(panelGesHab, "Error al eliminar la habitación: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                }
            } finally {
                try { conexion.desconectar(); } catch (SQLException ex) { System.err.println("Error al cerrar la conexión."); }
            }
        }
    }

    //
    // MÉTODO CENTRAL DE VALIDACIÓN
    //
    private boolean validarCampos() {
        
        String numeroHab = habNumeroHabW.getText();
        String precio = habPrecioW.getText();

        if (numeroHab.isEmpty() || precio.isEmpty()) {
            JOptionPane.showMessageDialog(panelGesHab, "Los campos Nº Habitación y Precio son obligatorios", "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            Double.parseDouble(precio);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(panelGesHab, "El precio solo puede contener números (use un punto para los decimales).", "Dato incorrecto", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    //
    // MÉTODO UTILITARIO PARA CONVERTIR ESTADOS
    //
    private String convertirEstado(String estado, boolean aTexto) {
        if (aTexto) {
            switch (estado) {
                case "D": return "Disponible";
                case "O": return "Ocupada";
                case "M": return "Mantenimiento";
                default: return "Desconocido";
            }
        } else {
            switch (estado) {
                case "Disponible": return "D";
                case "Ocupada": return "O";
                case "Mantenimiento": return "M";
                default: return "";
            }
        }
    }

    //
    // MÉTODO UTILITARIO PARA CONTROLAR LA INTERFAZ
    //
    private void actualizarEstadoFormulario(boolean activado) {
        habNumeroHabW.setEnabled(activado);
        habTipoW.setEnabled(activado);
        habPrecioW.setEnabled(activado);
        habEstadoW.setEnabled(activado);
        habBtnGuardar.setEnabled(activado);
        habBtnEliminar.setEnabled(activado);
    }
}
