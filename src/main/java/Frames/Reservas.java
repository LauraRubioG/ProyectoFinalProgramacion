package Frames; // Indica la carpeta o paquete donde guardamos esta parte del programa

import Conexion.ConexionMySQL; // Traemos nuestra herramienta para poder hablar con la base de datos

import javax.swing.*; // Traemos las herramientas visuales para crear ventanas, botones y textos
import javax.swing.table.DefaultTableModel; // Herramienta para organizar la informacion en forma de tabla
import java.sql.ResultSet; // Nos permite recoger y leer la informacion que nos devuelve la base de datos
import java.sql.SQLException; // Nos ayuda a capturar y entender los errores que puedan pasar con la base de datos
import java.text.ParseException; // Nos ayuda a detectar si alguien escribe mal una fecha
import java.text.SimpleDateFormat; // Nos permite definir que la fecha debe escribirse como Ano-Mes-Dia
import java.util.Date; // Nos permite trabajar con fechas reales para compararlas

//
// CLASE PRINCIPAL PARA GESTIONAR LAS RESERVAS
//
// Esta es la pantalla principal donde el recepcionista podra crear, modificar o borrar reservas
public class Reservas { // Aqui empieza nuestra clase

    //
    // VARIABLES PARA LOS ELEMENTOS DE LA PANTALLA
    //
    // Estas variables representan cada boton, texto y lista que vemos en la ventana
    public JPanel panelReservas; // Es el lienzo principal donde se colocan todos los demas elementos
    private JTextField resDNIW; // Es el espacio en blanco donde el usuario escribe el DNI del cliente
    private JButton resbtnBuscar; // Es el boton que pulsamos para comprobar si el DNI existe
    private JSpinner resNperW; // Es la cajita con flechas para elegir cuantas personas se alojaran
    private JComboBox<String> resNHabiW; // Es la lista desplegable que mostrara los numeros de las habitaciones libres
    private JTextField resEntradaW; // Espacio donde se escribe la fecha de llegada
    private JTextField resSalidaW; // Espacio donde se escribe la fecha de salida
    private JComboBox<String> resPagoW; // Lista desplegable para elegir si la reserva esta pagada o pendiente
    private JButton resbtnNueva; // Boton para guardar una reserva totalmente nueva
    private JButton resbtnmodificar; // Boton para cambiar los datos de una reserva que ya existe
    private JButton resbtneliminar; // Boton para borrar una reserva del sistema
    private JTable resTablaReserva; // Es la cuadricula donde veremos la lista de todas las reservas
    private JLabel resNhabi; // Es simplemente un texto fijo en la pantalla

    //
    // VARIABLES INTERNAS PARA RECORDAR INFORMACION
    //
    // Estas variables no se ven en la pantalla, las usamos por debajo para que el programa recuerde cosas
    private final DefaultTableModel modeloTabla; // Es el organizador que decide cuantas columnas tiene la tabla
    private String dniClienteActual = ""; // Aqui guardamos el DNI del cliente despues de buscarlo y confirmar que existe
    private String idReservaSeleccionada = ""; // Aqui guardamos el numero de reserva cuando hacemos clic en una fila de la tabla

    //
    // CONSTRUCTOR DE LA PANTALLA
    //
    // Todo lo que hay aqui dentro se ejecuta nada mas abrir la ventana de reservas
    public Reservas() { // Inicio del constructor

        //
        // PREPARAR LA LISTA DE ESTADOS DE PAGO
        //
        // Anadimos las opciones que podra elegir el recepcionista
        resPagoW.addItem("Seleccione estado..."); // Primera opcion por defecto para obligar a elegir
        resPagoW.addItem("Pendiente"); // Opcion para cuando aun no han pagado
        resPagoW.addItem("Pagado"); // Opcion para cuando ya han abonado la habitacion

        //
        // PREPARAR EL SELECTOR DE PERSONAS
        //
        // Configuramos la cajita de flechas para que tenga sentido
        // Empieza en 1, el minimo es 1, el maximo es 5, y avanza de 1 en 1
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 5, 1); // Creamos las reglas del selector
        resNperW.setModel(spinnerModel); // Le aplicamos estas reglas a nuestro selector visual

        //
        // PREPARAR LA TABLA DE RESERVAS
        //
        // Configuramos como se va a ver la lista de reservas
        modeloTabla = new DefaultTableModel() { // Creamos un nuevo organizador de tabla
            @Override // Indicamos que vamos a cambiar el comportamiento normal de la tabla
            public boolean isCellEditable(int row, int column) { // Funcion que decide si una celda se puede escribir
                return false; // Decimos que falso, para que nadie pueda borrar datos haciendo doble clic sin querer
            } // Fin de la funcion
        }; // Fin de la creacion del organizador

        // Ahora le ponemos nombre a cada columna de la tabla
        modeloTabla.addColumn("ID Reserva"); // Primera columna para el numero de reserva
        modeloTabla.addColumn("DNI Cliente"); // Segunda columna para el DNI
        modeloTabla.addColumn("Num. Habitacion"); // Tercera columna para el numero de la habitacion
        modeloTabla.addColumn("Entrada"); // Cuarta columna para la fecha de llegada
        modeloTabla.addColumn("Salida"); // Quinta columna para la fecha de salida
        modeloTabla.addColumn("Pago"); // Sexta columna para ver si esta pagado
        modeloTabla.addColumn("Estado"); // Septima columna para ver el estado de la reserva (Pendiente, En Curso, Finalizada)

        resTablaReserva.setModel(modeloTabla); // Finalmente unimos nuestro organizador con la tabla visual

        // Llamamos a la funcion que busca en la base de datos y rellena la tabla
        cargarReservas(); // Ejecuta la carga de datos

        //
        // TAREA DEL BOTON BUSCAR CLIENTE
        //
        // Le decimos al boton que tiene que hacer cuando le hagan clic
        resbtnBuscar.addActionListener(e -> { // Escucha el evento de hacer clic
            String dniBuscar = resDNIW.getText().trim(); // Cogemos el texto que ha escrito el usuario y le quitamos los espacios de los lados

            // Si el espacio esta vacio, no hacemos nada y avisamos
            if (dniBuscar.isEmpty()) { // Comprueba si no hay texto
                JOptionPane.showMessageDialog(panelReservas, "Por favor, introduzca un DNI para buscar.", "DNI vacio", JOptionPane.WARNING_MESSAGE); // Muestra mensaje de aviso
                return; // Corta la ejecucion aqui para no seguir
            } // Fin de la comprobacion

            try { // Intentamos conectar a la base de datos con cuidado por si falla
                ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L"); // Preparamos la llave para entrar
                conexion.conectar(); // Abrimos la puerta de la base de datos

                // Preparamos la pregunta para la base de datos
                String consulta = "SELECT DNI FROM clientes WHERE DNI = '" + dniBuscar + "'"; // Le pedimos que nos de el DNI si existe uno igual al que buscamos
                ResultSet rs = conexion.ejecutarSelect(consulta); // Lanzamos la pregunta y guardamos la respuesta

                // Comprobamos que nos ha contestado la base de datos
                if (rs.next()) { // Si hay un resultado significa que el cliente existe
                    dniClienteActual = rs.getString("DNI"); // Nos guardamos el DNI en secreto para usarlo luego al guardar la reserva
                    JOptionPane.showMessageDialog(panelReservas, "Cliente encontrado. Puede continuar con la reserva.", "Exito", JOptionPane.INFORMATION_MESSAGE); // Damos buenas noticias
                } else { // Si no hay ningun resultado
                    dniClienteActual = ""; // Borramos cualquier DNI que tuvieramos guardado por seguridad
                    JOptionPane.showMessageDialog(panelReservas, "No se ha encontrado al cliente. Debe registrarlo primero.", "Cliente no existe", JOptionPane.ERROR_MESSAGE); // Avisamos del problema
                } // Fin de la comprobacion del resultado

                conexion.desconectar(); // Muy importante, cerramos la puerta de la base de datos

            } catch (SQLException ex) { // Si algo sale mal con la base de datos, lo capturamos aqui
                JOptionPane.showMessageDialog(panelReservas, "Error al buscar cliente: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE); // Mostramos el error tecnico
            } // Fin del bloque de seguridad
        }); // Fin de la tarea del boton buscar

        //
        // TAREA DEL SELECTOR DE PERSONAS
        //
        // Le decimos al selector que hacer si el usuario le da a la flechita de subir o bajar
        resNperW.addChangeListener(e -> actualizarHabitacionesDisponibles()); // Si cambia el numero, actualizamos las habitaciones que se muestran
        actualizarHabitacionesDisponibles(); // Ejecutamos esto una vez al abrir la pantalla para que la lista no este vacia de inicio

        //
        // TAREA DEL BOTON AÑADIR NUEVA RESERVA
        //
        // Logica para guardar una nueva reserva en el sistema
        resbtnNueva.addActionListener(e -> { // Escuchamos el clic

            // Primero comprobamos que el recepcionista haya buscado y confirmado al cliente
            if (dniClienteActual.isEmpty()) { // Si la variable esta vacia es que no lo ha buscado
                JOptionPane.showMessageDialog(panelReservas, "Debe buscar y validar un cliente primero.", "Aviso", JOptionPane.WARNING_MESSAGE); // Avisamos
                return; // Paramos el proceso
            } // Fin de la comprobacion

            // Luego comprobamos que haya elegido una habitacion que sea valida
            if (resNHabiW.getSelectedItem() == null || resNHabiW.getSelectedItem().toString().startsWith("No hay")) { // Comprobamos que haya seleccion y no sea el texto de error
                JOptionPane.showMessageDialog(panelReservas, "Seleccione una habitacion valida.", "Aviso", JOptionPane.WARNING_MESSAGE); // Avisamos
                return; // Paramos el proceso
            } // Fin de la comprobacion

            // Empezamos a recoger toda la informacion que ha escrito el recepcionista
            String numeroHabitacion = resNHabiW.getSelectedItem().toString(); // Cogemos el numero de habitacion de la lista
            String fechaEntrada = resEntradaW.getText().trim(); // Cogemos la fecha de entrada
            String fechaSalida = resSalidaW.getText().trim(); // Cogemos la fecha de salida
            
            // Comprobamos que haya elegido una opcion de pago real
            if (resPagoW.getSelectedItem() == null || resPagoW.getSelectedItem().toString().equals("Seleccione estado...")) { // Si dejo la opcion por defecto
                JOptionPane.showMessageDialog(panelReservas, "Por favor, seleccione un estado de pago valido.", "Aviso", JOptionPane.WARNING_MESSAGE); // Avisamos
                return; // Paramos el proceso
            } // Fin de la comprobacion
            String estadoPago = resPagoW.getSelectedItem().toString(); // Si paso la prueba, guardamos el estado del pago

            // Comprobamos que no se haya olvidado de rellenar las fechas
            if (fechaEntrada.isEmpty() || fechaSalida.isEmpty()) { // Si alguna esta vacia
                JOptionPane.showMessageDialog(panelReservas, "Por favor, rellene todos los campos de fecha.", "Campos vacios", JOptionPane.WARNING_MESSAGE); // Avisamos
                return; // Paramos el proceso
            } // Fin de la comprobacion

            // Usamos nuestra funcion especial para ver si las fechas tienen el formato de Ano-Mes-Dia
            if (!validarFormatoFecha(fechaEntrada) || !validarFormatoFecha(fechaSalida)) { // Si alguna es falsa
                JOptionPane.showMessageDialog(panelReservas, "Las fechas deben tener el formato YYYY-MM-DD.", "Error de formato", JOptionPane.ERROR_MESSAGE); // Avisamos
                return; // Paramos el proceso
            } // Fin de la comprobacion

            // Usamos nuestra funcion especial para asegurar que no se viaja en el tiempo
            if (!validarLogicaFechas(fechaEntrada, fechaSalida)) { // Si la fecha de salida no es mayor a la de entrada
                JOptionPane.showMessageDialog(panelReservas, "Error: La fecha de salida debe ser posterior a la fecha de entrada.", "Error de fechas", JOptionPane.ERROR_MESSAGE); // Avisamos
                return; // Paramos el proceso
            } // Fin de la comprobacion

            try { // Intentamos hablar con la base de datos
                ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L"); // Preparamos conexion
                conexion.conectar(); // Conectamos
                
                // Preparamos la orden para guardar los datos nuevos en la tabla reservas
                // ¡VUELTO A LA NORMALIDAD! No enviamos el ID_Reserva en la consulta INSERT.
                // MySQL lo va a generar automaticamente usando un numero (1, 2, 3...) porque lo hemos configurado como AUTO_INCREMENT
                // Tampoco mandamos el Estado_Reserva porque en la BD le pusimos que por defecto sea 'Pendiente'
                String consultaInsert = "INSERT INTO reservas (FK_Cliente, FK_Habitacion, Fecha_Entrada, Fecha_Salida, Estado_Pago) " +
                        "VALUES ('" + dniClienteActual + "', '" + numeroHabitacion + "', '" + fechaEntrada + "', '" + fechaSalida + "', '" + estadoPago + "')"; // Metemos todos los valores recogidos

                int filasAfectadas = conexion.ejecutarInsertDeleteUpdate(consultaInsert); // Ejecutamos la orden y vemos si se ha guardado alguna fila

                // Si se ha guardado correctamente, procedemos a bloquear la habitacion
                if (filasAfectadas > 0) { // Si es mayor que cero es que se guardo
                    // Preparamos la orden para cambiar el estado de la habitacion a la letra O de Ocupada
                    String consultaUpdateHab = "UPDATE habitaciones SET Estado = 'O' WHERE Numero_Hab = '" + numeroHabitacion + "'"; // Buscamos la habitacion exacta
                    conexion.ejecutarInsertDeleteUpdate(consultaUpdateHab); // Ejecutamos el cambio

                    JOptionPane.showMessageDialog(panelReservas, "Reserva creada exitosamente."); // Avisamos del exito
                    limpiarCampos(); // Vaciamos todos los textos de la pantalla
                    cargarReservas(); // Volvemos a pedir los datos para que la nueva reserva salga en la tabla
                    actualizarHabitacionesDisponibles(); // Volvemos a cargar la lista de habitaciones para que desaparezca la que acabamos de ocupar
                } else { // Si no se pudo guardar
                    JOptionPane.showMessageDialog(panelReservas, "No se pudo crear la reserva.", "Error", JOptionPane.ERROR_MESSAGE); // Avisamos del error
                } // Fin de la comprobacion de guardado

                conexion.desconectar(); // Cerramos conexion

            } catch (SQLException ex) { // Capturamos fallos tecnicos
                JOptionPane.showMessageDialog(panelReservas, "Error en la base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE); // Mostramos el fallo
            } // Fin del bloque de seguridad
        }); // Fin de la tarea de anadir reserva

        //
        // TAREA AL HACER CLIC EN UNA FILA DE LA TABLA
        //
        // Queremos que si tocan una reserva, sus datos suban a los espacios de texto para poder cambiarlos
        resTablaReserva.getSelectionModel().addListSelectionListener(e -> { // Escuchamos cuando cambia la seleccion
            if (!e.getValueIsAdjusting()) { // Esto evita que el evento salte dos veces seguidas por un solo clic
                int filaSeleccionada = resTablaReserva.getSelectedRow(); // Averiguamos que numero de fila han tocado

                if (filaSeleccionada >= 0) { // Si han tocado una fila de verdad (no al vacio)
                    // Recogemos la informacion de cada columna de esa fila
                    idReservaSeleccionada = modeloTabla.getValueAt(filaSeleccionada, 0).toString(); // Guardamos en secreto el numero de la reserva
                    resDNIW.setText(modeloTabla.getValueAt(filaSeleccionada, 1).toString()); // Ponemos el DNI en su sitio
                    resEntradaW.setText(modeloTabla.getValueAt(filaSeleccionada, 3).toString()); // Ponemos la fecha de entrada
                    resSalidaW.setText(modeloTabla.getValueAt(filaSeleccionada, 4).toString()); // Ponemos la fecha de salida
                    resPagoW.setSelectedItem(modeloTabla.getValueAt(filaSeleccionada, 5).toString()); // Seleccionamos el pago en la lista

                    // Como ahora tenemos un DNI nuevo, simulamos que el recepcionista ha hecho clic en buscar
                    // Asi validamos al cliente automaticamente
                    resbtnBuscar.doClick(); // Hacemos un clic invisible en el boton buscar
                } // Fin de la comprobacion de fila
            } // Fin de comprobacion de doble evento
        }); // Fin de la tarea de la tabla

        //
        // TAREA DEL BOTON MODIFICAR RESERVA
        //
        // Logica para cambiar los datos de una reserva que ya habiamos guardado
        resbtnmodificar.addActionListener(e -> { // Escuchamos el clic
            // Primero comprobamos que el usuario haya seleccionado una reserva de la lista
            if (idReservaSeleccionada.isEmpty()) { // Si nuestro recuerdo secreto esta vacio
                JOptionPane.showMessageDialog(panelReservas, "Primero seleccione una reserva de la tabla para modificarla.", "Aviso", JOptionPane.WARNING_MESSAGE); // Avisamos
                return; // Paramos el proceso
            } // Fin de la comprobacion

            // Volvemos a comprobar que tenemos el cliente asegurado
            if (dniClienteActual.isEmpty()) { // Si esta vacio
                JOptionPane.showMessageDialog(panelReservas, "Por favor, busque y valide el cliente primero.", "Aviso", JOptionPane.WARNING_MESSAGE); // Avisamos
                return; // Paramos el proceso
            } // Fin de la comprobacion

            // Recogemos otra vez toda la informacion de los campos por si el usuario ha cambiado algo
            String fechaEntrada = resEntradaW.getText().trim(); // Nueva fecha de entrada
            String fechaSalida = resSalidaW.getText().trim(); // Nueva fecha de salida
            
            // Validamos que el pago este seleccionado
            if (resPagoW.getSelectedItem() == null || resPagoW.getSelectedItem().toString().equals("Seleccione estado...")) { // Si no ha elegido nada
                JOptionPane.showMessageDialog(panelReservas, "Por favor, seleccione un estado de pago valido.", "Aviso", JOptionPane.WARNING_MESSAGE); // Avisamos
                return; // Paramos el proceso
            } // Fin de comprobacion
            String estadoPago = resPagoW.getSelectedItem().toString(); // Guardamos el nuevo pago

            // Comprobamos que no haya borrado las fechas
            if (fechaEntrada.isEmpty() || fechaSalida.isEmpty()) { // Si faltan fechas
                JOptionPane.showMessageDialog(panelReservas, "No deje campos vacios de fechas.", "Error", JOptionPane.WARNING_MESSAGE); // Avisamos
                return; // Paramos el proceso
            } // Fin de comprobacion

            // Volvemos a validar el formato visual de las fechas
            if (!validarFormatoFecha(fechaEntrada) || !validarFormatoFecha(fechaSalida)) { // Si no es el formato correcto
                JOptionPane.showMessageDialog(panelReservas, "Formato de fecha incorrecto (YYYY-MM-DD).", "Error", JOptionPane.ERROR_MESSAGE); // Avisamos
                return; // Paramos el proceso
            } // Fin de comprobacion

            // Volvemos a validar que el tiempo tenga sentido
            if (!validarLogicaFechas(fechaEntrada, fechaSalida)) { // Si sale antes de entrar
                JOptionPane.showMessageDialog(panelReservas, "Error: La fecha de salida debe ser posterior a la fecha de entrada.", "Error de fechas", JOptionPane.ERROR_MESSAGE); // Avisamos
                return; // Paramos el proceso
            } // Fin de comprobacion

            try { // Intentamos conectar con la base
                ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L"); // Preparamos conexion
                conexion.conectar(); // Conectamos

                // Preparamos la orden para actualizar los datos. UPDATE significa cambiar
                String consulta = "UPDATE reservas SET " +
                        "FK_Cliente = '" + dniClienteActual + "', " + // Actualizamos cliente
                        "Fecha_Entrada = '" + fechaEntrada + "', " + // Actualizamos entrada
                        "Fecha_Salida = '" + fechaSalida + "', " + // Actualizamos salida
                        "Estado_Pago = '" + estadoPago + "' " + // Actualizamos pago
                        // ¡VUELTO A LA NORMALIDAD! Ahora sabemos que el ID de reserva es un numero, por lo que no es necesario ponerlo entre comillas simples en el SQL (aunque no hace dano)
                        "WHERE ID_Reserva = " + idReservaSeleccionada; // MUY IMPORTANTE: Solo cambiamos la reserva que tenga este numero especifico

                int filas = conexion.ejecutarInsertDeleteUpdate(consulta); // Lanzamos el cambio

                // Comprobamos si el cambio funciono
                if (filas > 0) { // Si modifico alguna fila
                    JOptionPane.showMessageDialog(panelReservas, "Reserva modificada exitosamente."); // Celebramos
                    limpiarCampos(); // Limpiamos pantalla
                    cargarReservas(); // Recargamos tabla
                } else { // Si fallo
                    JOptionPane.showMessageDialog(panelReservas, "Error al modificar la reserva."); // Avisamos
                } // Fin de comprobacion de modificacion

                conexion.desconectar(); // Cerramos puerta

            } catch (SQLException ex) { // Capturamos errores
                JOptionPane.showMessageDialog(panelReservas, "Error SQL: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Mostramos error
            } // Fin de bloque de seguridad
        }); // Fin de tarea modificar

        //
        // TAREA DEL BOTON ELIMINAR RESERVA
        //
        // Logica para cancelar y borrar una reserva completamente
        resbtneliminar.addActionListener(e -> { // Escuchamos clic
            // Primero aseguramos que han seleccionado algo
            if (idReservaSeleccionada.isEmpty()) { // Si no hay recuerdo
                JOptionPane.showMessageDialog(panelReservas, "Primero seleccione una reserva de la tabla para eliminarla.", "Aviso", JOptionPane.WARNING_MESSAGE); // Avisamos
                return; // Paramos
            } // Fin de comprobacion

            // Como borrar es peligroso, le preguntamos al usuario si esta totalmente seguro
            int confirmacion = JOptionPane.showConfirmDialog(panelReservas, "¿Esta seguro de que desea eliminar la reserva?", "Confirmar", JOptionPane.YES_NO_OPTION); // Mostramos ventana de pregunta

            if (confirmacion == JOptionPane.YES_OPTION) { // Si pulsa en el boton SI
                try { // Intentamos conectar
                    ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L"); // Preparamos conexion
                    conexion.conectar(); // Conectamos

                    // Primero tenemos que averiguar que habitacion estaba usando esta reserva para poder liberarla
                    String consultaHabitacion = "SELECT FK_Habitacion FROM reservas WHERE ID_Reserva = " + idReservaSeleccionada; // Preguntamos que habitacion es
                    ResultSet rs = conexion.ejecutarSelect(consultaHabitacion); // Recogemos respuesta

                    String habitacionALiberar = ""; // Preparamos variable para guardar la habitacion
                    if (rs.next()) { // Si encontramos la reserva
                        habitacionALiberar = rs.getString("FK_Habitacion"); // Guardamos el numero de habitacion
                    } // Fin de recogida de datos

                    // Ahora si procedemos a borrar la reserva
                    String consultaDel = "DELETE FROM reservas WHERE ID_Reserva = " + idReservaSeleccionada; // Ordenamos borrar
                    int filas = conexion.ejecutarInsertDeleteUpdate(consultaDel); // Ejecutamos el borrado

                    // Si se borro bien, tenemos que liberar la habitacion
                    if (filas > 0) { // Si afecto a alguna fila
                        if (!habitacionALiberar.isEmpty()) { // Si sabemos que habitacion era
                            // Ordenamos cambiar el estado de esa habitacion a la letra D de Disponible
                            String consultaUpdateHab = "UPDATE habitaciones SET Estado = 'D' WHERE Numero_Hab = '" + habitacionALiberar + "'"; // Buscamos la habitacion y la liberamos
                            conexion.ejecutarInsertDeleteUpdate(consultaUpdateHab); // Ejecutamos liberacion
                        } // Fin de liberacion

                        JOptionPane.showMessageDialog(panelReservas, "Reserva eliminada exitosamente."); // Avisamos del exito
                        limpiarCampos(); // Limpiamos pantalla
                        cargarReservas(); // Recargamos tabla
                        actualizarHabitacionesDisponibles(); // Recargamos habitaciones porque ahora hay una mas libre
                    } // Fin de exito de borrado

                    conexion.desconectar(); // Cerramos base de datos

                } catch (SQLException ex) { // Si hay fallos
                    JOptionPane.showMessageDialog(panelReservas, "Error SQL: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Mostramos fallo
                } // Fin de bloque de seguridad
            } // Fin de confirmacion de usuario
        }); // Fin de tarea borrar
    } // Fin del constructor de la clase

    //
    // FUNCION PARA ACTUALIZAR LA LISTA DE HABITACIONES LIBRES
    //
    // Esta funcion revisa cuantas personas son y busca en la base de datos las habitaciones ideales
    private void actualizarHabitacionesDisponibles() { // Inicio de funcion
        int personas = (int) resNperW.getValue(); // Miramos que numero marca la cajita de personas
        String tipoBusqueda = ""; // Preparamos un texto para guardar el tipo de habitacion que necesitamos

        // Traducimos el numero de personas a un tipo de habitacion
        if (personas == 1) tipoBusqueda = "Individual"; // Una persona va a individual
        else if (personas == 2 || personas == 3) tipoBusqueda = "Doble"; // Dos o tres van a doble
        else if (personas >= 4) tipoBusqueda = "Suite"; // Cuatro o mas necesitan una suite

        resNHabiW.removeAllItems(); // Vaciamos la lista por completo para poner opciones nuevas y limpias

        try { // Intentamos conectar
            ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L"); // Preparamos conexion
            conexion.conectar(); // Conectamos

            // Pedimos a la base de datos el numero de las habitaciones que sean del tipo que queremos y ademas tengan estado D (disponible)
            String consulta = "SELECT Numero_Hab FROM habitaciones WHERE Tipo = '" + tipoBusqueda + "' AND Estado = 'D'"; // Preparamos consulta
            ResultSet resultados = conexion.ejecutarSelect(consulta); // Ejecutamos consulta

            boolean hayHabitaciones = false; // Bandera para recordar si hemos encontrado alguna habitacion
            
            // Repetimos este paso por cada habitacion libre que nos devuelva la base de datos
            while (resultados.next()) { // Mientras sigan quedando resultados
                hayHabitaciones = true; // Levantamos la bandera de que si hemos encontrado
                String numero = resultados.getString("Numero_Hab"); // Cogemos su numero
                resNHabiW.addItem(numero); // Metemos ese numero en la lista visual de la pantalla
            } // Fin de repeticion

            // Si despues de buscar resulta que no habia ninguna libre
            if (!hayHabitaciones) { // Si la bandera sigue bajada
                resNHabiW.addItem("No hay de tipo " + tipoBusqueda); // Ponemos un mensaje de aviso en la propia lista
            } // Fin de aviso

            conexion.desconectar(); // Cerramos base

        } catch (SQLException ex) { // Capturamos problemas
            System.out.println("Error al cargar habitaciones: " + ex.getMessage()); // Como esto pasa al fondo, lo pintamos en la consola
        } // Fin de bloque de seguridad
    } // Fin de funcion actualizar

    //
    // FUNCION PARA RELLENAR LA TABLA DE RESERVAS
    //
    // Pide la informacion al sistema y la coloca bonita en la cuadricula de la pantalla
    private void cargarReservas() { // Inicio funcion
        modeloTabla.setRowCount(0); // Borramos todos los renglones antiguos de la tabla para que no se dupliquen

        try { // Intentamos conectar
            ConexionMySQL conexion = new ConexionMySQL("root", "", "HotelM&L"); // Preparamos
            conexion.conectar(); // Conectamos

            // Esta consulta es especial. Se llama INNER JOIN y sirve para mezclar datos de tres tablas distintas
            // Asi conseguimos el DNI del cliente y el numero de la habitacion en vez de ver solo numeros internos que no entendemos
            // Añadimos r.Estado_Reserva a lo que queremos leer
            String consulta = "SELECT r.ID_Reserva, c.DNI AS DNI_Cliente, h.Numero_Hab AS Num_Habitacion, " +
                    "r.Fecha_Entrada, r.Fecha_Salida, r.Estado_Pago, r.Estado_Reserva " + // Que queremos leer
                    "FROM reservas r " + // De donde empezamos a leer
                    "INNER JOIN clientes c ON r.FK_Cliente = c.DNI " + // Cruzamos con clientes usando el DNI
                    "INNER JOIN habitaciones h ON r.FK_Habitacion = h.Numero_Hab"; // Cruzamos con habitaciones usando el numero

            ResultSet rs = conexion.ejecutarSelect(consulta); // Recogemos toda esa mezcla de datos

            // Repetimos el proceso por cada reserva encontrada
            while (rs.next()) { // Mientras haya reservas
                Object[] fila = new Object[7]; // Ampliamos la caja a 7 compartimentos
                // ¡VUELTO A LA NORMALIDAD! Leemos el ID_Reserva como un numero (int)
                fila[0] = rs.getInt("ID_Reserva"); // Metemos el numero de reserva en el primero
                fila[1] = rs.getString("DNI_Cliente"); // El DNI en el segundo
                fila[2] = rs.getString("Num_Habitacion"); // La habitacion en el tercero
                fila[3] = rs.getString("Fecha_Entrada"); // La entrada en el cuarto
                fila[4] = rs.getString("Fecha_Salida"); // La salida en el quinto
                fila[5] = rs.getString("Estado_Pago"); // El pago en el sexto
                fila[6] = rs.getString("Estado_Reserva"); // Metemos el estado de la reserva (Pendiente, En Curso...) en el septimo

                modeloTabla.addRow(fila); // Colocamos toda la caja como un nuevo renglon en la tabla visual
            } // Fin de repeticion

            conexion.desconectar(); // Cerramos

        } catch (SQLException e) { // Si hay errores
            JOptionPane.showMessageDialog(panelReservas, "Error al cargar tabla: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE); // Avisamos
        } // Fin bloque seguridad
    } // Fin funcion cargar

    //
    // FUNCION PARA LIMPIAR LA PANTALLA
    //
    // Deja todos los textos vacios como recien abiertos para poder meter datos nuevos comodamente
    private void limpiarCampos() { // Inicio funcion
        resDNIW.setText(""); // Vaciamos el DNI
        resNperW.setValue(1); // Devolvemos las personas a 1
        resEntradaW.setText(""); // Vaciamos fecha de entrada
        resSalidaW.setText(""); // Vaciamos fecha de salida
        resPagoW.setSelectedIndex(0); // Ponemos el selector de pago en la opcion por defecto
        dniClienteActual = ""; // Olvidamos el DNI
        idReservaSeleccionada = ""; // Olvidamos la reserva seleccionada
        resTablaReserva.clearSelection(); // Desmarcamos cualquier fila de la tabla
    } // Fin funcion limpiar

    //
    // FUNCION PARA COMPROBAR EL FORMATO DE LA FECHA
    //
    // Se asegura de que el usuario no escriba letras o fechas inventadas como el mes 15
    private boolean validarFormatoFecha(String fecha) { // Recibe un texto y devuelve verdadero o falso
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Creamos una plantilla de como debe ser la fecha
        sdf.setLenient(false); // Le decimos que sea estricta, que no perdone errores
        try { // Intentamos convertir el texto usando la plantilla
            sdf.parse(fecha); // Lo convertimos
            return true; // Si llega aqui es que no ha habido fallos, asi que es verdadero (fecha buena)
        } catch (ParseException e) { // Si falla al convertir
            return false; // Devolvemos falso porque la fecha esta mal escrita
        } // Fin comprobacion
    } // Fin funcion validar formato

    //
    // FUNCION PARA COMPROBAR QUE EL VIAJE TIENE SENTIDO
    //
    // Se asegura de que no queramos salir del hotel antes de haber llegado
    private boolean validarLogicaFechas(String entrada, String salida) { // Recibe dos textos y devuelve verdadero o falso
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Creamos la misma plantilla
        try { // Intentamos hacer la conversion
            Date fechaIn = sdf.parse(entrada); // Convertimos la fecha de entrada en un reloj de Java
            Date fechaOut = sdf.parse(salida); // Convertimos la fecha de salida en un reloj de Java
            
            // Pedimos a Java que compruebe si el reloj de salida va DESPUES que el reloj de entrada
            return fechaOut.after(fechaIn); // Devolvemos verdadero si la salida es mas tarde, falso si es antes o a la vez
            
        } catch (ParseException e) { // Si algo falla
            return false; // Devolvemos falso por seguridad
        } // Fin comprobacion
    } // Fin funcion validar logica
} // Fin total de la clase Reservas