package Frames; // Indica que esta clase pertenece al paquete "Frames", que organiza nuestras ventanas

import javax.swing.*; // Importa todas las clases de Swing para crear las interfaces gráficas

// Esta es la clase principal de toda la aplicación
// Su única responsabilidad es arrancar el programa y mostrar la primera ventana
public class Main { // Define el inicio de nuestra clase

    // El método "main" es el punto de entrada, lo primero que Java busca para ejecutar
    public static void main(String[] args) { // Define el método main

        // 1. Se crea la ventana principal, que es el marco de la aplicación
        JFrame ventana = new JFrame("Iniciar Sesión"); // Crea un objeto JFrame con un título

        // 2. Se le dice a la ventana que su contenido debe ser el panel de la clase LoginPage
        ventana.setContentPane(new LoginPage().pantallaLogin); // Crea una instancia de LoginPage y obtiene su panel público

        // 3. Se configura la operación de cierre.
        //    Esto hace que cuando el usuario pulse la 'X' de la ventana, el programa termine completamente
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Define el comportamiento al cerrar

        // 4. Se ajusta el tamaño de la ventana automáticamente
        //    El método pack() hace que la ventana tome el tamaño justo para que quepan sus componentes
        ventana.pack(); // Ajusta el tamaño de la ventana

        // 5. Se centra la ventana en mitad de la pantalla del ordenador
        ventana.setLocationRelativeTo(null); // Pasa "null" para centrarla en la pantalla

        // 6. Se hace visible la ventana
        //    Hasta este punto, la ventana existe pero está oculta. Ahora se muestra al usuario
        ventana.setVisible(true); // Hace que la ventana aparezca en pantalla
    } // Cierra el método main
} // Cierra la clase Main
