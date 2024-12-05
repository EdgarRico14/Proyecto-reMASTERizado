import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class JuegoMatematico extends JFrame {
    private JLabel preguntaLabel;
    private JButton[] opcionesBotones;
    private int respuestaCorrecta;
    private int puntuacion;
    private int tiempoRestante;
    private Timer temporizador;
    private JLabel puntuacionLabel;
    private Clip clipCorrecto;
    private Clip clipIncorrecto;
    private final Scoreboard scoreboard;
    private final String nombreJugador;

    public JuegoMatematico(Scoreboard scoreboard, String nombreJugador) {
        this.scoreboard = scoreboard;
        this.nombreJugador = nombreJugador;

        setTitle("Juego Matemático");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        preguntaLabel = new JLabel("", SwingConstants.CENTER);
        panel.add(preguntaLabel, BorderLayout.NORTH);

        JPanel panelOpciones = new JPanel();
        panelOpciones.setLayout(new GridLayout(2, 2));

        opcionesBotones = new JButton[4];
        for (int i = 0; i < 4; i++) {
            opcionesBotones[i] = new JButton();
            
            // Estilo del botón
            opcionesBotones[i].setFont(new Font("Arial", Font.BOLD, 18)); // Fuente grande y negrita
            opcionesBotones[i].setBackground(new Color(29, 161, 242)); // Azul claro
            opcionesBotones[i].setForeground(Color.WHITE); // Texto blanco
            opcionesBotones[i].setFocusPainted(false); // Elimina el borde al hacer clic
            opcionesBotones[i].setBorder(BorderFactory.createLineBorder(new Color(0, 91, 171), 3)); // Borde azul oscuro
        
            // Bordes redondeados
            opcionesBotones[i].setBorder(new javax.swing.border.EmptyBorder(10, 20, 10, 20)); // Bordes internos
        
            // Efecto de Hover
            opcionesBotones[i].addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    JButton boton = (JButton) evt.getSource();
                    boton.setBackground(new Color(37, 211, 102)); // Verde al pasar el ratón
                }
        
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    JButton boton = (JButton) evt.getSource();
                    boton.setBackground(new Color(29, 161, 242)); // Azul claro original
                }
            });
        
            // Acción del botón
            opcionesBotones[i].addActionListener(this::verificarRespuesta);
        
            // Añade el botón al panel
            panelOpciones.add(opcionesBotones[i]);
        }
        

        panel.add(panelOpciones, BorderLayout.CENTER);

        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BorderLayout());
        puntuacionLabel = new JLabel("Puntuacion: " + puntuacion);
        JLabel tiempoLabel = new JLabel("Tiempo: " + tiempoRestante);

        panelInfo.add(puntuacionLabel, BorderLayout.WEST);
        panelInfo.add(tiempoLabel, BorderLayout.EAST);

        panel.add(panelInfo, BorderLayout.SOUTH);

        add(panel);

        tiempoRestante = 20; // Cronometro
        temporizador = new Timer();
        temporizador.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                tiempoRestante--;
                tiempoLabel.setText("Tiempo: " + tiempoRestante);

                if (tiempoRestante <= 0) {
                    temporizador.cancel();
                    JOptionPane.showMessageDialog(JuegoMatematico.this, "Tiempo agotado. Puntuacion final: " + puntuacion);
                    guardarPuntuacion();
                    dispose();
                }
            }
        }, 1000, 1000);

        generarPregunta();

        try {
            clipCorrecto = cargarSonido("correcto.wav");
            clipIncorrecto = cargarSonido("error.wav");
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private Clip cargarSonido(String ruta) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File soundFile = new File(ruta);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        return clip;
    }

    private void generarPregunta() {
        Random random = new Random();
        int a = random.nextInt(10) + 1; // Evita valores menores a 1
        int b = random.nextInt(10) + 1; // Evita valores menores a 1
        int operacion = random.nextInt(4); // Ahora incluye 4 operaciones
        int resultadoCorrecto = 0;
    
        switch (operacion) {
            case 0: // Suma
                resultadoCorrecto = a + b;
                preguntaLabel.setText("¿Cuánto es " + a + " + " + b + "?");
                break;
            case 1: // Multiplicación
                resultadoCorrecto = a * b;
                preguntaLabel.setText("¿Cuánto es " + a + " x " + b + "?");
                break;
            case 2: // Resta
                // Asegurar que el resultado sea positivo
                if (a < b) {
                    int temp = a;
                    a = b;
                    b = temp;
                }
                resultadoCorrecto = a - b;
                preguntaLabel.setText("¿Cuánto es " + a + " - " + b + "?");
                break;
            case 3: // División
                // Asegurar que el divisor no sea cero y que a sea divisible por b
                while (a % b != 0) {
                    a = random.nextInt(10) + 1;
                    b = random.nextInt(10) + 1;
                }
                resultadoCorrecto = a / b; // División entera
                preguntaLabel.setText("¿Cuánto es " + a + " ÷ " + b + "?");
                break;
        }
    
        respuestaCorrecta = random.nextInt(4);
        for (int i = 0; i < 4; i++) {
            if (i == respuestaCorrecta) {
                opcionesBotones[i].setText(String.valueOf(resultadoCorrecto));
            } else {
                int incorrecto;
                do {
                    incorrecto = random.nextInt(20); // Genera una opción incorrecta
                } while (incorrecto == resultadoCorrecto);
                opcionesBotones[i].setText(String.valueOf(incorrecto));
            }
        }
    }
        
    

    private void verificarRespuesta(ActionEvent e) {
        JButton botonPulsado = (JButton) e.getSource();
        int indiceRespuestaElegida = -1;
    
        // Identifica qué botón fue presionado
        for (int i = 0; i < opcionesBotones.length; i++) {
            if (opcionesBotones[i] == botonPulsado) {
                indiceRespuestaElegida = i;
                break;
            }
        }
    
        // Crear un JLabel para mostrar el mensaje temporal
        JLabel mensajeTemporal = new JLabel("", SwingConstants.CENTER);
        mensajeTemporal.setFont(new Font("Arial", Font.BOLD, 16));
        mensajeTemporal.setForeground(Color.BLUE); // Color inicial (puede cambiar)
    
        // Verifica si la respuesta es correcta
        if (indiceRespuestaElegida == respuestaCorrecta) {
            puntuacion++;
            puntuacionLabel.setText("Puntuación: " + puntuacion);
            mensajeTemporal.setText("¡Correcto!");
            mensajeTemporal.setForeground(new Color(0, 128, 0)); // Verde para correcto
    
            // Reproduce el sonido correcto
            if (clipCorrecto != null) {
                clipCorrecto.setFramePosition(0); // Reinicia el sonido al inicio
                clipCorrecto.start();
            }
        } else {
            mensajeTemporal.setText("¡Incorrecto!");
            mensajeTemporal.setForeground(Color.RED); // Rojo para incorrecto
    
            // Reproduce el sonido incorrecto
            if (clipIncorrecto != null) {
                clipIncorrecto.setFramePosition(0); // Reinicia el sonido al inicio
                clipIncorrecto.start();
            }
        }
    
        // Añade el mensaje temporal al panel principal
        getContentPane().add(mensajeTemporal, BorderLayout.NORTH);
        mensajeTemporal.setVisible(true);
    
        // Temporizador para mostrar el mensaje y luego generar la siguiente pregunta
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Oculta el mensaje y lo elimina del panel
                mensajeTemporal.setVisible(false);
                getContentPane().remove(mensajeTemporal);
    
                // Genera la siguiente pregunta
                generarPregunta();
    
                // Actualiza la interfaz para reflejar cambios
                revalidate();
                repaint();
            }
        }, 2000); // Tiempo de espera de 1 segundo
    }
    

    private void guardarPuntuacion() {
        scoreboard.addScore(nombreJugador, puntuacion);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Scoreboard scoreboard = new Scoreboard("scoreboard.txt");
            MenuInterfaz menuInterfaz = new MenuInterfaz(scoreboard);
            menuInterfaz.setVisible(true);
        });
    }
}



