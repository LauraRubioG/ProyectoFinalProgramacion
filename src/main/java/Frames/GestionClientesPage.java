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
import java.util.regex.Pattern;

// Esta clase define todo el comportamiento y la lógica de nuestra ventana de gestión de clientes.
public class GestionClientesPage {

    //
    // DECLARACIÓN DE COMPONENTES VISUALES (ATRIBUTOS DE LA CLASE)
    //
    public JPanel panelGestionClientes;
    // Se corrige el nombre de la variable a gesDNIW para mantener la coherencia.
    private JTextField gesDNIW;
    private JTextField gesNombreW;
    private JTextField gesApellidosW;
    private JTextField gesEmailW;
    private JTextField gesTelefonoW;
    private JTextField gesDireccionW;
    private JTextField gesCiudadW;
    private JTextField gesCodigoPostalW;
    private JButton gesBtnGuardar;
    private JButton gesBtnNuevo;
    private JButton gesBtnEliminar;
    private JTable gesTablaClientes;
    
    private DefaultTableModel tableModel;

    private boolean modoNuevoCliente = false;

    //
    // CONSTRUCTOR DE LA CLASE
    //
    public GestionClientesPage() {
        
        inicializarTabla();
        cargarClientes();
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

        tableModel.addColumn("DNI");
        tableModel.addColumn("Nombre");
        tableModel.addColumn("Apellidos");
        tableModel.addColumn("Email");
        tableModel.addColumn("Teléfono");
        tableModel.addColumn("Dirección");
        tableModel.addColumn("Ciudad");
        tableModel.addColumn("C. Postal");

        gesTablaClientes.setModel(tableModel);
    }

    //
    // MÉTODO PARA CARGAR LOS DATOS DE LOS CLIENTES DESDE LA BASE DE DATOS
    //
    private void cargarClientes() {
        
        tableModel.setRowCount(0);

        ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L");
        
        try {
            conexion.conectar();
            
            String consulta = "SELECT * FROM clientes";
            
            ResultSet resultado = conexion.ejecutarSelect(consulta);

            while (resultado.next()) {
                
                Object[] fila = {
                        resultado.getString("DNI"),
                        resultado.getString("Nombre"),
                        resultado.getString("Apellidos"),
                        resultado.getString("Email"),
                        resultado.getString("Telefono"),
                        resultado.getString("Direccion_Calle"),
                        resultado.getString("Direccion_Ciudad"),
                        resultado.getString("Direccion_CP")
                };
                
                tableModel.addRow(fila);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(panelGestionClientes, "Error al cargar los clientes: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                conexion.desconectar();
            } catch (SQLException ex) {
                System.err.println("Error al cerrar la conexión: " + ex.getMessage());
            }
        }
    }

    //
    // MÉTODO PARA CONFIGURAR LOS "OYENTES" DE EVENTOS
    //
    private void configurarListeners() {

        gesTablaClientes.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    mostrarDatosDelClienteSeleccionado();
                }
            }
        });

        gesBtnNuevo.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                prepararFormularioParaNuevoCliente();
            }
        });

        gesBtnGuardar.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarCliente();
            }
        });

        gesBtnEliminar.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarCliente();
            }
        });
    }

    //
    // MÉTODOS CON LA LÓGICA DE LA APLICACIÓN
    //

    private void mostrarDatosDelClienteSeleccionado() {
        
        int filaSeleccionada = gesTablaClientes.getSelectedRow();

        if (filaSeleccionada != -1) {
            
            actualizarEstadoFormulario(true);
            
            // Se corrige el nombre de la variable a gesDNIW.
            gesDNIW.setEnabled(false);
            
            modoNuevoCliente = false;

            // Se corrige el nombre de la variable a gesDNIW.
            gesDNIW.setText(tableModel.getValueAt(filaSeleccionada, 0).toString());
            gesNombreW.setText(tableModel.getValueAt(filaSeleccionada, 1).toString());
            gesApellidosW.setText(tableModel.getValueAt(filaSeleccionada, 2).toString());
            gesEmailW.setText(tableModel.getValueAt(filaSeleccionada, 3).toString());
            gesTelefonoW.setText(tableModel.getValueAt(filaSeleccionada, 4).toString());
            gesDireccionW.setText(tableModel.getValueAt(filaSeleccionada, 5).toString());
            gesCiudadW.setText(tableModel.getValueAt(filaSeleccionada, 6).toString());
            gesCodigoPostalW.setText(tableModel.getValueAt(filaSeleccionada, 7).toString());
        }
    }

    private void prepararFormularioParaNuevoCliente() {
        
        gesTablaClientes.clearSelection();
        
        actualizarEstadoFormulario(true);
        
        // Se corrige el nombre de la variable a gesDNIW.
        gesDNIW.setEnabled(true);
        
        modoNuevoCliente = true;

        // Se corrige el nombre de la variable a gesDNIW.
        gesDNIW.setText("");
        gesNombreW.setText("");
        gesApellidosW.setText("");
        gesEmailW.setText("");
        gesTelefonoW.setText("");
        gesDireccionW.setText("");
        gesCiudadW.setText("");
        gesCodigoPostalW.setText("");
    }

    private void guardarCliente() {
        
        if (!validarCampos()) {
            return;
        }

        if (modoNuevoCliente) {
            insertarNuevoCliente();
        } else {
            actualizarClienteExistente();
        }
    }

    private void insertarNuevoCliente() {
        
        // Se corrige el nombre de la variable a gesDNIW.
        String dni = gesDNIW.getText();
        
        ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L");
        try {
            conexion.conectar();
            
            String consulta = "INSERT INTO clientes (DNI, Nombre, Apellidos, Email, Telefono, Direccion_Calle, Direccion_Ciudad, Direccion_CP) VALUES ('"
                    + dni + "', '" + gesNombreW.getText() + "', '" + gesApellidosW.getText() + "', '" + gesEmailW.getText() + "', '"
                    + gesTelefonoW.getText() + "', '" + gesDireccionW.getText() + "', '" + gesCiudadW.getText() + "', '" + gesCodigoPostalW.getText() + "')";
            
            int filasAfectadas = conexion.ejecutarInsertDeleteUpdate(consulta);

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(panelGestionClientes, "Cliente guardado con éxito", "Operación completada", JOptionPane.INFORMATION_MESSAGE);
                
                cargarClientes();
                
                actualizarEstadoFormulario(false);
                prepararFormularioParaNuevoCliente();
            } else {
                JOptionPane.showMessageDialog(panelGestionClientes, "No se pudo guardar el cliente", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            if (ex.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(panelGestionClientes, "Ya existe un cliente con ese DNI", "Error de duplicado", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(panelGestionClientes, "Error al guardar el cliente: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            }
        } finally {
            try { conexion.desconectar(); } catch (SQLException ex) { System.err.println("Error al cerrar la conexión."); }
        }
    }

    private void actualizarClienteExistente() {
        
        // Se corrige el nombre de la variable a gesDNIW.
        String dni = gesDNIW.getText();
        
        ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L");
        try {
            conexion.conectar();
            
            String consulta = "UPDATE clientes SET "
                    + "Nombre = '" + gesNombreW.getText() + "', "
                    + "Apellidos = '" + gesApellidosW.getText() + "', "
                    + "Email = '" + gesEmailW.getText() + "', "
                    + "Telefono = '" + gesTelefonoW.getText() + "', "
                    + "Direccion_Calle = '" + gesDireccionW.getText() + "', "
                    + "Direccion_Ciudad = '" + gesCiudadW.getText() + "', "
                    + "Direccion_CP = '" + gesCodigoPostalW.getText() + "' "
                    + "WHERE DNI = '" + dni + "'";
            
            int filasAfectadas = conexion.ejecutarInsertDeleteUpdate(consulta);

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(panelGestionClientes, "Cliente actualizado con éxito", "Operación completada", JOptionPane.INFORMATION_MESSAGE);
                
                cargarClientes();
                
                actualizarEstadoFormulario(false);
            } else {
                JOptionPane.showMessageDialog(panelGestionClientes, "No se pudo actualizar el cliente. Puede que ya no exista.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(panelGestionClientes, "Error al actualizar el cliente: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { conexion.desconectar(); } catch (SQLException ex) { System.err.println("Error al cerrar la conexión."); }
        }
    }

    private void eliminarCliente() {
        
        int filaSeleccionada = gesTablaClientes.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(panelGestionClientes, "Por favor, seleccione un cliente de la tabla para eliminarlo", "Ningún cliente seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(panelGestionClientes, "¿Estás seguro de que quieres eliminar a este cliente? Esta acción no se puede deshacer.", "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            
            // Se corrige el nombre de la variable a gesDNIW.
            String dni = gesDNIW.getText();

            ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L");
            try {
                conexion.conectar();
                
                String consulta = "DELETE FROM clientes WHERE DNI = '" + dni + "'";
                
                int filasAfectadas = conexion.ejecutarInsertDeleteUpdate(consulta);

                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(panelGestionClientes, "Cliente eliminado con éxito", "Operación completada", JOptionPane.INFORMATION_MESSAGE);
                    
                    cargarClientes();
                    
                    actualizarEstadoFormulario(false);
                    prepararFormularioParaNuevoCliente();
                } else {
                    JOptionPane.showMessageDialog(panelGestionClientes, "No se pudo eliminar el cliente. Puede que ya haya sido eliminado.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(panelGestionClientes, "Error al eliminar el cliente: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            } finally {
                try { conexion.desconectar(); } catch (SQLException ex) { System.err.println("Error al cerrar la conexión."); }
            }
        }
    }

    //
    // MÉTODO CENTRAL DE VALIDACIÓN
    //
    private boolean validarCampos() {
        
        // Se corrige el nombre de la variable a gesDNIW.
        String dni = gesDNIW.getText();
        String nombre = gesNombreW.getText();
        String email = gesEmailW.getText();
        String telefono = gesTelefonoW.getText();
        String codigoPostal = gesCodigoPostalW.getText();

        if (dni.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(panelGestionClientes, "Los campos DNI y Nombre son obligatorios", "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (modoNuevoCliente && !esDniValido(dni)) {
            JOptionPane.showMessageDialog(panelGestionClientes, "El formato del DNI no es válido (debe ser 8 números y 1 letra)", "Dato incorrecto", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!email.isEmpty() && !esEmailValido(email)) {
            JOptionPane.showMessageDialog(panelGestionClientes, "El formato del email no es válido", "Dato incorrecto", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!telefono.isEmpty() && !esTelefonoValido(telefono)) {
            JOptionPane.showMessageDialog(panelGestionClientes, "El formato del teléfono no es válido (debe contener solo números, 9 dígitos)", "Dato incorrecto", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (!codigoPostal.isEmpty() && !esCodigoPostalValido(codigoPostal)) {
            JOptionPane.showMessageDialog(panelGestionClientes, "El formato del Código Postal no es válido (debe contener 5 dígitos)", "Dato incorrecto", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    //
    // MÉTODOS DE VALIDACIÓN (HERRAMIENTAS)
    //
    private boolean esDniValido(String dni) {
        return dni.toUpperCase().matches("\\d{8}[A-Z]");
    }

    private boolean esEmailValido(String email) {
        String patronEmail = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return Pattern.compile(patronEmail).matcher(email).matches();
    }

    private boolean esTelefonoValido(String telefono) {
        return telefono.matches("\\d{9}");
    }
    
    private boolean esCodigoPostalValido(String cp) {
        return cp.matches("\\d{5}");
    }

    //
    // MÉTODO UTILITARIO PARA CONTROLAR LA INTERFAZ
    //
    private void actualizarEstadoFormulario(boolean activado) {
        // Se corrige el nombre de la variable a gesDNIW.
        gesDNIW.setEnabled(activado);
        gesNombreW.setEnabled(activado);
        gesApellidosW.setEnabled(activado);
        gesEmailW.setEnabled(activado);
        gesTelefonoW.setEnabled(activado);
        gesDireccionW.setEnabled(activado);
        gesCiudadW.setEnabled(activado);
        gesCodigoPostalW.setEnabled(activado);
        gesBtnGuardar.setEnabled(activado);
        gesBtnEliminar.setEnabled(activado);
    }
}
