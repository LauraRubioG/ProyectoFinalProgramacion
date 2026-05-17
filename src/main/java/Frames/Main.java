// La primera línea de un archivo Java siempre define a qué "paquete" o carpeta pertenece la clase.
package Frames;

//
// ZONA DE IMPORTACIONES
//
// Aquí le decimos a nuestra clase qué herramientas o "librerías" externas necesita para funcionar.
import javax.swing.*;

// Esta es la clase principal de toda la aplicación.
// Su única responsabilidad es arrancar el programa y mostrar la primera ventana.
public class Main {

    // El método "main" es el punto de entrada de cualquier aplicación Java.
    // Es lo primero que se busca y se ejecuta cuando arrancamos el programa.
    public static void main(String[] args) {

        // 1. Creamos la ventana principal, que es el marco donde vivirá nuestra aplicación.
        //    Le pasamos el título que aparecerá en la barra superior de la ventana.
        JFrame ventana = new JFrame("Iniciar Sesión");

        // 2. Le decimos a la ventana que su contenido principal debe ser el panel
        //    que hemos diseñado en nuestra clase LoginPage.
        //    Para ello, creamos una nueva instancia de LoginPage y accedemos a su panel público.
        ventana.setContentPane(new LoginPage().pantallaLogin);

        // 3. Configuramos la operación de cierre.
        //    Esto es muy importante: le dice al programa que termine completamente
        //    cuando el usuario pulse la 'X' de la ventana. Si no, la ventana se cerraría
        //    pero el programa seguiría ejecutándose en segundo plano.
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 4. Ajustamos el tamaño de la ventana automáticamente.
        //    El método .pack() hace que la ventana tome el tamaño justo y necesario
        //    para que quepan todos los componentes que contiene.
        ventana.pack();

        // 5. Centramos la ventana en mitad de la pantalla del ordenador.
        //    Si le pasamos 'null', toma la pantalla principal como referencia.
        ventana.setLocationRelativeTo(null);

        // 6. Hacemos visible la ventana.
        //    Hasta este punto, la ventana existe en la memoria pero está oculta.
        //    Con esta línea, la mostramos finalmente al usuario.
        ventana.setVisible(true);
    }
}
