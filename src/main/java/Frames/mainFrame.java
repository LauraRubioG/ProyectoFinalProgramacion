package Frames;

import javax.swing.*;
import java.awt.*;

public class mainFrame {
    // Este es el método principal, el motor de arranque que Java busca para iniciar
    public static void main(String[] args) {

        // 1. Creamos una ventana de Windows/Mac vacía y le ponemos un título en la barra superior
        JFrame ventana = new JFrame("Login");

        // 2. Le decimos a la ventana vacía: "Tu contenido interior va a ser el 'panelPrincipal' que diseñé"
        // Creamos una nueva PantallaLogin() para poder acceder a ese panel
        ventana.setContentPane(new LoginPage().pantallaLogin);

        // 3. Le decimos que cuando el usuario cierre la ventana con la "X", el programa se apague completamente
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 4. ventana.pack() es un truco buenísimo: hace que la ventana se encoja o estire
        // automáticamente para ajustarse al tamaño exacto de los botones y textos que pusiste
        ventana.pack();

        // 5. Centramos la ventana justo en el medio del monitor del ordenador
        ventana.setLocationRelativeTo(null);

        // 6. ¡Encendemos las luces! Hacemos que la ventana sea visible para el usuario
        ventana.setVisible(true);
    }

}





