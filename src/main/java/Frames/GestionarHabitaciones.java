package Frames; // Indica el paquete donde se encuentra la clase

import Conexion.ConexionMySQL; // Importamos nuestra clase de conexión para interactuar con la base de datos

import javax.swing.*; // Importa todas las clases de Swing para la interfaz gráfica
import javax.swing.table.DefaultTableModel; // Para gestionar los datos de nuestra tabla
import java.awt.event.ActionEvent; // Para los eventos de clic
import java.awt.event.ActionListener; // Interfaz para escuchar los clics
import java.sql.SQLException; // Para manejar errores de base de datos

//
// CLASE PARA GESTIONAR LAS HABITACIONES
//
// Esta clase maneja la ventana donde el usuario podrá ver, añadir,
// modificar o eliminar habitaciones de la base de datos.
public class GestionarHabitaciones { // Inicio de la clase

    //
    // COMPONENTES DE LA INTERFAZ
    //
    public JPanel panelGesHab; // Panel principal de la ventana
    private JTextField geshaNombreW; // Campo de texto para el número/nombre de la habitación
    private JComboBox<String> geshaTipoCo; // Desplegable para seleccionar el tipo de habitación (Basic, Suit, Queen)
    private JTextField geshaPrecioW; // Campo de texto para el precio por noche
    private JTextField geshaEstadoW; // Campo de texto para el estado (ej. Disponible, Ocupada)
    private JButton geshabtnRegistrarHabitacion; // Botón para registrar la habitación (lo cambiaremos a Añadir)
    
    // Estos componentes tendrás que añadirlos tú mismo en el GUI Designer:
    // private JButton btnModificar; // Botón para modificar una habitación seleccionada
    // private JButton btnEliminar; // Botón para eliminar una habitación
    // private JTable tablaHabitaciones; // Tabla para mostrar todas las habitaciones
    // private DefaultTableModel modeloTabla; // Modelo que contiene los datos de la tabla

    //
    // CONSTRUCTOR DE LA CLASE
    //
    public GestionarHabitaciones() {
        
        //
        // CONFIGURACIÓN INICIAL DEL DESPLEGABLE (COMBOBOX)
        //
        // Rellenamos el desplegable con las opciones que nos has indicado
        geshaTipoCo.addItem("Selecciona un tipo..."); // Opción por defecto
        geshaTipoCo.addItem("Basic");
        geshaTipoCo.addItem("Suit");
        geshaTipoCo.addItem("Queen");

        //
        // CONFIGURACIÓN INICIAL DE LA TABLA
        //
        // Creamos las columnas que tendrá nuestra tabla
        // modeloTabla = new DefaultTableModel();
        // modeloTabla.addColumn("ID");
        // modeloTabla.addColumn("Número/Nombre");
        // modeloTabla.addColumn("Tipo");
        // modeloTabla.addColumn("Precio");
        // modeloTabla.addColumn("Estado");
        // tablaHabitaciones.setModel(modeloTabla); // <-- DESCOMENTAR ESTO CUANDO AÑADAS LA TABLA EN EL DISEÑADOR

        // Llenamos la tabla con los datos que ya existen en la base de datos
        // cargarHabitaciones(); // <-- DESCOMENTAR ESTO CUANDO LA BASE DE DATOS TENGA LA TABLA CREADA

        //
        // ACCIÓN DEL BOTÓN: AÑADIR/REGISTRAR HABITACIÓN
        //
        geshabtnRegistrarHabitacion.addActionListener(e -> {
            // Obtenemos los valores de los campos de texto
            String numero = geshaNombreW.getText().trim();
            String tipo = (String) geshaTipoCo.getSelectedItem();
            String precioStr = geshaPrecioW.getText().trim();
            String estado = geshaEstadoW.getText().trim();

            // Validamos que ningún campo esté vacío
            if (numero.isEmpty() || (tipo != null && tipo.equals("Selecciona un tipo...")) || precioStr.isEmpty() || estado.isEmpty()) {
                JOptionPane.showMessageDialog(panelGesHab, "Por favor, rellene todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                return; // Detenemos la ejecución si hay campos vacíos
            }

            try {
                // Validamos que el precio sea un número válido
                double precio = Double.parseDouble(precioStr);

                // 1. Creamos la conexión a la base de datos
                ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L");
                conexion.conectar(); // Abrimos la conexión

                // 2. Preparamos la consulta SQL para insertar los datos
                // (Asumiendo que tu tabla en MySQL se llama 'habitaciones')
                String consulta = "INSERT INTO habitaciones (numero, tipo, precio, estado) VALUES ('" + numero + "', '" + tipo + "', " + precio + ", '" + estado + "')";

                // 3. Ejecutamos la consulta
                int filasAfectadas = conexion.ejecutarInsertDeleteUpdate(consulta);

                // 4. Verificamos si se guardó correctamente
                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(panelGesHab, "Habitación registrada correctamente.");
                    limpiarCampos(); // Limpiamos los campos de texto para poder añadir otra
                    // cargarHabitaciones(); // <-- DESCOMENTAR PARA ACTUALIZAR LA TABLA
                } else {
                    JOptionPane.showMessageDialog(panelGesHab, "No se pudo registrar la habitación.", "Error", JOptionPane.ERROR_MESSAGE);
                }

                // 5. IMPORTANTE: Cerramos la conexión
                conexion.desconectar();

            } catch (NumberFormatException ex) {
                // Si el usuario introduce letras en el campo de precio
                JOptionPane.showMessageDialog(panelGesHab, "El precio debe ser un número válido.", "Error de formato", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                // Si hay un error con la base de datos
                JOptionPane.showMessageDialog(panelGesHab, "Error en la base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        });

        //
        // EVENTO: HACER CLIC EN UNA FILA DE LA TABLA
        //
        // Esto servirá para cargar los datos de la habitación seleccionada en los campos de texto
        /* <-- DESCOMENTAR TODO ESTE BLOQUE CUANDO TENGAS LA TABLA
        tablaHabitaciones.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Obtenemos la fila que el usuario ha seleccionado
                int filaSeleccionada = tablaHabitaciones.getSelectedRow();
                
                // Verificamos que realmente se haya seleccionado una fila
                if (filaSeleccionada >= 0) {
                    // Obtenemos los valores de las celdas de esa fila y los ponemos en los campos de texto
                    // Suponiendo las columnas: ID(0), Numero(1), Tipo(2), Precio(3), Estado(4)
                    geshaNombreW.setText(modeloTabla.getValueAt(filaSeleccionada, 1).toString());
                    geshaTipoCo.setSelectedItem(modeloTabla.getValueAt(filaSeleccionada, 2).toString());
                    geshaPrecioW.setText(modeloTabla.getValueAt(filaSeleccionada, 3).toString());
                    geshaEstadoW.setText(modeloTabla.getValueAt(filaSeleccionada, 4).toString());
                }
            }
        } );
        */

        //
        // ACCIÓN DEL BOTÓN: MODIFICAR HABITACIÓN
        //
        /* <-- DESCOMENTAR TODO ESTE BLOQUE CUANDO AÑADAS EL BOTÓN EN EL DISEÑADOR
        btnModificar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Primero, verificamos si el usuario ha seleccionado alguna fila de la tabla
                int filaSeleccionada = tablaHabitaciones.getSelectedRow();
                
                if (filaSeleccionada >= 0) {
                    // Obtenemos los nuevos valores de los campos de texto
                    String numero = geshaNombreW.getText().trim();
                    String tipo = (String) geshaTipoCo.getSelectedItem();
                    String precioStr = geshaPrecioW.getText().trim();
                    String estado = geshaEstadoW.getText().trim();
                    
                    // Obtenemos el ID de la habitación seleccionada (oculto en la columna 0)
                    String idHabitacion = modeloTabla.getValueAt(filaSeleccionada, 0).toString();

                    try {
                        double precio = Double.parseDouble(precioStr);

                        ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L");
                        conexion.conectar();

                        // Preparamos la consulta para actualizar (UPDATE)
                        String consulta = "UPDATE habitaciones SET numero = '" + numero + "', tipo = '" + tipo + "', precio = " + precio + ", estado = '" + estado + "' WHERE id = " + idHabitacion;
                        
                        int filas = conexion.ejecutarInsertDeleteUpdate(consulta);

                        if (filas > 0) {
                            JOptionPane.showMessageDialog(panelGesHab, "Habitación modificada correctamente.");
                            limpiarCampos();
                            cargarHabitaciones(); // Actualizamos la tabla
                        } else {
                            JOptionPane.showMessageDialog(panelGesHab, "Error al modificar la habitación.");
                        }

                        conexion.desconectar();

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(panelGesHab, "El precio debe ser un número.");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(panelGesHab, "Error SQL: " + ex.getMessage());
                    }
                } else {
                    // Si no ha seleccionado ninguna fila, le avisamos
                    JOptionPane.showMessageDialog(panelGesHab, "Por favor, seleccione una habitación de la tabla para modificarla.");
                }
            }
        });
        */

        //
        // ACCIÓN DEL BOTÓN: ELIMINAR HABITACIÓN
        //
        /* <-- DESCOMENTAR TODO ESTE BLOQUE CUANDO AÑADAS EL BOTÓN EN EL DISEÑADOR
        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int filaSeleccionada = tablaHabitaciones.getSelectedRow();
                
                if (filaSeleccionada >= 0) {
                    // Preguntamos al usuario si está seguro de querer eliminarla
                    int confirmacion = JOptionPane.showConfirmDialog(panelGesHab, "¿Está seguro de que desea eliminar esta habitación?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
                    
                    if (confirmacion == JOptionPane.YES_OPTION) { // Si pulsa "Sí"
                        String idHabitacion = modeloTabla.getValueAt(filaSeleccionada, 0).toString();

                        try {
                            ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L");
                            conexion.conectar();

                            // Preparamos la consulta para borrar (DELETE)
                            String consulta = "DELETE FROM habitaciones WHERE id = " + idHabitacion;
                            
                            int filas = conexion.ejecutarInsertDeleteUpdate(consulta);

                            if (filas > 0) {
                                JOptionPane.showMessageDialog(panelGesHab, "Habitación eliminada correctamente.");
                                limpiarCampos();
                                cargarHabitaciones(); // Actualizamos la tabla
                            }

                            conexion.desconectar();

                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(panelGesHab, "Error SQL: " + ex.getMessage());
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(panelGesHab, "Por favor, seleccione una habitación de la tabla para eliminarla.");
                }
            }
        });
        */
    }

    //
    // MÉTODO PARA LIMPIAR LOS CAMPOS DE TEXTO
    //
    // Un método auxiliar para dejar todo en blanco después de una acción
    private void limpiarCampos() {
        geshaNombreW.setText(""); // Vaciamos el texto
        geshaTipoCo.setSelectedIndex(0); // Volvemos a "Selecciona un tipo..."
        geshaPrecioW.setText("");
        geshaEstadoW.setText("");
        // tablaHabitaciones.clearSelection(); // Desmarcamos la fila de la tabla
    }

    //
    // MÉTODO PARA CARGAR LOS DATOS EN LA TABLA
    //
    // Este método lee la base de datos y dibuja las filas en la tabla
    /* <-- DESCOMENTAR ESTE MÉTODO CUANDO TENGAS LA TABLA Y LA BASE DE DATOS LISTA
    private void cargarHabitaciones() {
        // Primero, borramos todas las filas que pueda tener la tabla actualmente
        modeloTabla.setRowCount(0);

        try {
            ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L");
            conexion.conectar();

            // Pedimos todos los datos de las habitaciones
            String consulta = "SELECT id, numero, tipo, precio, estado FROM habitaciones";
            java.sql.ResultSet resultados = conexion.ejecutarSelect(consulta);

            // Recorremos cada fila que nos devuelve la base de datos
            while (resultados.next()) {
                // Creamos un array que representará una fila en nuestra tabla
                Object[] fila = new Object[5];
                
                // Rellenamos el array con los datos de la base de datos
                fila[0] = resultados.getInt("id");
                fila[1] = resultados.getString("numero");
                fila[2] = resultados.getString("tipo");
                fila[3] = resultados.getDouble("precio");
                fila[4] = resultados.getString("estado");

                // Añadimos esa fila completa al modelo de nuestra tabla
                modeloTabla.addRow(fila);
            }

            conexion.desconectar();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panelGesHab, "Error al cargar los datos: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
    */
}
