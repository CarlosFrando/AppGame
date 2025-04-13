package com.example.appgame;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MemoramaActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private List<Carta> cartas;
    private ImageView firstSelected = null;
    private MediaPlayer mediaPlayer;
    private CountDownTimer timerJugador1;
    private CountDownTimer timerJugador2;
    private long tiempoInicioJugador1 = 0;
    private long tiempoInicioJugador2 = 0;
    private long tiempoTranscurridoJugador1 = 0;
    private long tiempoTranscurridoJugador2 = 0;
    private boolean isAnimating = false;  // Para evitar clics durante animaciones
    private boolean[] matchedCards = new boolean[18];  // Para rastrear cartas ya emparejadas
    private int pairsFound = 0;  // Contador de pares encontrados
    private boolean turnoJugador1 = true;  // Turno del jugador 1
    private boolean juegoIniciado = false;  // Para controlar el inicio del temporizador
    private int movimientosJugador1 = 0;  // Contador de movimientos del Jugador 1
    private int movimientosJugador2 = 0;  // Contador de movimientos del Jugador 2
    private int aciertosJugador1 = 0;  // Contador de aciertos del Jugador 1
    private int aciertosJugador2 = 0;  // Contador de aciertos del Jugador 2
    private TextView nombreJugador1, nombreJugador2;
    private ImageView imagenTurno;
    private String jugador1Nombre = "Jugador 1";
    private String jugador2Nombre = "Jugador 2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memorama_main);

        // Inicializar vistas
        nombreJugador1 = findViewById(R.id.nombreJugador1);
        nombreJugador2 = findViewById(R.id.nombreJugador2);
        imagenTurno = findViewById(R.id.imagenTurno);
        gridLayout = findViewById(R.id.gridLayout);

        // Mostrar el diálogo para ingresar los nombres de los jugadores
        mostrarDialogNombres();

        // Inicializar el juego
        inicializarCartas();
        mezclarCartas();
        crearVistasCartas();
        actualizarTurno();

        // Mostrar ventana de reglas al inicio (si no se ha marcado "No volver a mostrar")
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean noMostrarReglas = prefs.getBoolean("noMostrarReglas", false);
        if (!noMostrarReglas) {
            mostrarDialogReglas();
        }
    }
    private void mostrarDialogNombres() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_nombres);

        EditText editJugador1 = dialog.findViewById(R.id.editJugador1);
        EditText editJugador2 = dialog.findViewById(R.id.editJugador2);
        Button btnGuardar = dialog.findViewById(R.id.btnGuardarNombres);

        dialog.setCancelable(false); // Evita que se cierre con botón atrás
        dialog.setCanceledOnTouchOutside(false); // Evita que se cierre tocando fuera

        // Hace que el fondo del diálogo sea totalmente transparente
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Cargar la animación combinada del botón
        Animation combinedAnimation = AnimationUtils.loadAnimation(this, R.anim.combined_animation);

        btnGuardar.setOnClickListener(v -> {
            jugador1Nombre = editJugador1.getText().toString().trim();
            jugador2Nombre = editJugador2.getText().toString().trim();

            // Iniciar la animación del botón
            btnGuardar.startAnimation(combinedAnimation);

            // Validar nombres
            if (jugador1Nombre.isEmpty()) jugador1Nombre = "Jugador 1";
            if (jugador2Nombre.isEmpty()) jugador2Nombre = "Jugador 2";

            // Actualizar los nombres en la interfaz
            nombreJugador1.setText(jugador1Nombre);
            nombreJugador2.setText(jugador2Nombre);
            actualizarTurno();

            // Retrasar el cierre del diálogo para que la animación tenga tiempo de mostrarse
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss(); // Cerrar el diálogo después del retraso

                    // Aplicar animación combinada de salida a la actividad
                    overridePendingTransition(0, R.anim.slide_and_fade_out);
                }
            }, 1000); // Retraso de 700 milisegundos (ajusta este valor según la duración de tu animación)
        });

        dialog.show();
    }

    private void mostrarDialogReglas() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // Quita la barra de título
        dialog.setContentView(R.layout.dialog_reglas);

        // Hace que el fondo del diálogo sea totalmente transparente
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setCancelable(false); // Evita que se cierre con botón atrás
        dialog.setCanceledOnTouchOutside(false); // Evita que se cierre tocando fuera

        CheckBox checkNoMostrar = dialog.findViewById(R.id.checkNoMostrar);
        Button cerrarButton = dialog.findViewById(R.id.cerrarButton);

        cerrarButton.setOnClickListener(v -> {
            if (checkNoMostrar.isChecked()) {
                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("noMostrarReglas", true);
                editor.apply();
            }
            dialog.dismiss();
        });

        dialog.show();
    }


    private void iniciarTemporizadorJugador1() {
        tiempoInicioJugador1 = System.currentTimeMillis();
        timerJugador1 = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tiempoTranscurridoJugador1 += System.currentTimeMillis() - tiempoInicioJugador1;
                tiempoInicioJugador1 = System.currentTimeMillis();

            }

            @Override
            public void onFinish() {
                // No es necesario hacer nada aquí
            }
        }.start();
    }

    private void iniciarTemporizadorJugador2() {
        tiempoInicioJugador2 = System.currentTimeMillis();
        timerJugador2 = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tiempoTranscurridoJugador2 += System.currentTimeMillis() - tiempoInicioJugador2;
                tiempoInicioJugador2 = System.currentTimeMillis();

            }

            @Override
            public void onFinish() {
                // No es necesario hacer nada aquí
            }
        }.start();
    }

    private void actualizarTurno() {
        // Resalta el jugador activo
        nombreJugador1.setTextColor(turnoJugador1 ? Color.RED : Color.BLACK);
        nombreJugador2.setTextColor(turnoJugador1 ? Color.BLACK : Color.RED);

        // Cambia la imagen del turno
        imagenTurno.setImageResource(turnoJugador1 ? R.drawable.jugador1 : R.drawable.jugador2);

        // Cargar la animación de baile
        Animation danceAnimation = AnimationUtils.loadAnimation(this, R.anim.dance_animation);

        // Aplicar la animación a la imagen del turno
        imagenTurno.startAnimation(danceAnimation);
    }

    private void inicializarCartas() {
        cartas = new ArrayList<>();
        int[] imagenes = {
                R.drawable.carta_1, R.drawable.carta_2, R.drawable.carta_3, R.drawable.carta_4,
                R.drawable.carta_5, R.drawable.carta_6, R.drawable.carta_7, R.drawable.carta_8,
                R.drawable.carta_9
        };

        // Crear 16 cartas (8 pares)
        for (int i = 0; i < 9; i++) {
            cartas.add(new Carta(i, R.drawable.carta_oculta, imagenes[i]));
            cartas.add(new Carta(i + 9, R.drawable.carta_oculta, imagenes[i]));
        }
    }

    private void mezclarCartas() {
        Collections.shuffle(cartas);
    }

    private void crearVistasCartas() {
        // Obtener el ancho del GridLayout
        gridLayout.post(() -> {
            int gridWidth = gridLayout.getWidth(); // Ancho del GridLayout
            int gridHeight = gridLayout.getHeight(); // Alto del GridLayout
            int padding = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());

            int columnCount = 3; // Número de columnas
            int rowCount = 6;    // Número de filas

            // Calcular el tamaño de cada carta
            int cardWidth = (gridWidth - (padding * (columnCount + 1))) / columnCount;
            int cardHeight = (gridHeight - (padding * (rowCount + 1))) / rowCount;
            int cardSize = Math.max(Math.min(cardWidth, cardHeight) - padding, 0);
            // Usar el tamaño más pequeño para mantener la proporción
            // Configurar el GridLayout para 3 columnas y 6 filas
            gridLayout.setColumnCount(columnCount);
            gridLayout.setRowCount(rowCount);

            Log.d("Memorama", "Total cartas: " + cartas.size());

            gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
            gridLayout.setUseDefaultMargins(true);

            for (int i = 0; i < cartas.size(); i++) {
                Carta carta = cartas.get(i);
                ImageView cartaView = new ImageView(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                cartaView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                cartaView.setAdjustViewBounds(true);

                // Configurar el tamaño de las cartas
                params.width = cardSize;
                params.height = cardSize;

                // Configurar márgenes para separar las cartas
                params.setMargins(padding, padding, padding, padding);

                // Configurar la posición de la carta en el GridLayout
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // Columna con peso 1
                params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);    // Fila con peso 1

                cartaView.setLayoutParams(params);
                cartaView.setImageResource(carta.getImagenReverso());
                cartaView.setOnClickListener(v -> onCartaClick(carta, cartaView));
                gridLayout.addView(cartaView);
            }
        });
    }

    private void onCartaClick(Carta carta, ImageView cartaView) {
        if (isAnimating || matchedCards[cartas.indexOf(carta)] || cartaView.getDrawable().getConstantState().equals(getResources().getDrawable(carta.getImagenCara()).getConstantState())) {
            return;
        }

        if (!juegoIniciado) {
            juegoIniciado = true;
            iniciarTemporizadorJugador1();
        }

        reproducirSonido(R.raw.sonido_voltear);
        if (turnoJugador1) {
            movimientosJugador1++;
        } else {
            movimientosJugador2++;
        }


        flipCard(cartaView, carta.getImagenCara());
    }

    private void flipCard(ImageView card, int imageResource) {
        isAnimating = true;
        Animator flipOut = AnimatorInflater.loadAnimator(this, R.animator.flip_out);
        Animator flipIn = AnimatorInflater.loadAnimator(this, R.animator.flip_in);

        flipOut.setTarget(card);
        flipOut.start();

        flipOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                card.setImageResource(imageResource);
                flipIn.setTarget(card);
                flipIn.start();

                flipIn.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isAnimating = false;
                        if (firstSelected == null) {
                            firstSelected = card;
                        } else {
                            checkMatch(firstSelected, card);
                            firstSelected = null;
                        }
                    }

                    @Override public void onAnimationStart(Animator animation) {}
                    @Override public void onAnimationCancel(Animator animation) {}
                    @Override public void onAnimationRepeat(Animator animation) {}
                });
            }

            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });
    }

    private void shakeCard(ImageView card) {
        // Cargar la animación desde el archivo XML
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        // Aplicar la animación a la carta
        card.startAnimation(shake);
    }

    private void checkMatch(ImageView img1, ImageView img2) {
        int index1 = gridLayout.indexOfChild(img1);
        int index2 = gridLayout.indexOfChild(img2);

        if (cartas.get(index1).getImagenCara() == cartas.get(index2).getImagenCara()) {
            pairsFound++;
            matchedCards[index1] = true;
            matchedCards[index2] = true;
            if (turnoJugador1) {
                aciertosJugador1++;
            } else {
                aciertosJugador2++;
            }
            reproducirSonido(R.raw.sonido_coincidencia);
            if (pairsFound == 9) {
                detenerTemporizadores();
                reproducirSonido(R.raw.sonido_victoria);
                mostrarDialogVictoria();
            }
        } else {
            reproducirSonido(R.raw.sonido_error);
            // Aplicar la animación de shake a ambas cartas
            shakeCard(img1);
            shakeCard(img2);
            new Handler().postDelayed(() -> {
                flipBack(img1);
                flipBack(img2);
                cambiarTurno();
            }, 1000); // Retraso antes de voltear las cartas de nuevo
        }
    }

    private void detenerTemporizadores() {
        if (timerJugador1 != null) {
            timerJugador1.cancel();
        }
        if (timerJugador2 != null) {
            timerJugador2.cancel();
        }
    }

    private void flipBack(ImageView card) {
        Animator flipOut = AnimatorInflater.loadAnimator(this, R.animator.flip_out);
        Animator flipIn = AnimatorInflater.loadAnimator(this, R.animator.flip_in);

        flipOut.setTarget(card);
        flipOut.start();

        flipOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                card.setImageResource(R.drawable.carta_oculta);
                flipIn.setTarget(card);
                flipIn.start();
            }

            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });
    }

    private void cambiarTurno() {
        if (turnoJugador1) {
            if (timerJugador1 != null) {
                timerJugador1.cancel();
            }
            iniciarTemporizadorJugador2();
        } else {
            if (timerJugador2 != null) {
                timerJugador2.cancel();
            }
            iniciarTemporizadorJugador1();
        }
        turnoJugador1 = !turnoJugador1;
        actualizarTurno();
    }

    private void mostrarDialogVictoria() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_victory);

        TextView tituloGanador = dialog.findViewById(R.id.tituloGanador);
        TextView datosJugador1 = dialog.findViewById(R.id.datosJugador1);
        TextView datosJugador2 = dialog.findViewById(R.id.datosJugador2);
        Button reiniciarButton = dialog.findViewById(R.id.reiniciarButton);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setCancelable(false); // Evita que se cierre con botón atrás
        dialog.setCanceledOnTouchOutside(false); // Evita que se cierre tocando fuera

        // Determinar al ganador
        String ganador;
        if (aciertosJugador1 > aciertosJugador2) {
            ganador = jugador1Nombre;
        } else if (aciertosJugador1 < aciertosJugador2) {
            ganador = jugador2Nombre;
        } else {
            ganador = "Empate";
        }
        tituloGanador.setText("Ganador: " + ganador);

        // Mostrar datos de los jugadores
        long segundosJugador1 = tiempoTranscurridoJugador1 / 1000;
        long minutosJugador1 = segundosJugador1 / 60;
        segundosJugador1 = segundosJugador1 % 60;
        String tiempoFormateadoJugador1 = String.format(Locale.getDefault(), "%02d:%02d", minutosJugador1, segundosJugador1);

        long segundosJugador2 = tiempoTranscurridoJugador2 / 1000;
        long minutosJugador2 = segundosJugador2 / 60;
        segundosJugador2 = segundosJugador2 % 60;
        String tiempoFormateadoJugador2 = String.format(Locale.getDefault(), "%02d:%02d", minutosJugador2, segundosJugador2);

        // Texto para el Jugador 1
        String jugador1Text = jugador1Nombre + ":\n"; // Usar el nombre del Jugador 1
        String tiempo1Text = "Tiempo: " + tiempoFormateadoJugador1 + "\n";
        String movimientos1Text = "Movimientos: " + movimientosJugador1 + "\n";
        String aciertos1Text = "Aciertos: " + aciertosJugador1;

        // Texto para el Jugador 2
        String jugador2Text = jugador2Nombre + ":\n"; // Usar el nombre del Jugador 2
        String tiempo2Text = "Tiempo: " + tiempoFormateadoJugador2 + "\n";
        String movimientos2Text = "Movimientos: " + movimientosJugador2 + "\n";
        String aciertos2Text = "Aciertos: " + aciertosJugador2;

        datosJugador1.setText("jugador: " + jugador1Nombre + "\nTiempo: " + tiempoFormateadoJugador1 + "\nMovimientos: " + movimientosJugador1 + "\nAciertos: " + aciertosJugador1);
        datosJugador2.setText("jugador: " + jugador2Nombre + "\nTiempo: " + tiempoFormateadoJugador2 + "\nMovimientos: " + movimientosJugador2 + "\nAciertos: " + aciertosJugador2);

        // Reiniciar el juego
        reiniciarButton.setOnClickListener(v -> {
            reiniciarJuego();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void reiniciarJuego() {
        pairsFound = 0;
        movimientosJugador1 = 0;
        movimientosJugador2 = 0;
        aciertosJugador1 = 0;
        aciertosJugador2 = 0;
        tiempoTranscurridoJugador1 = 0;
        tiempoTranscurridoJugador2 = 0;
        matchedCards = new boolean[18];
        turnoJugador1 = true;
        juegoIniciado = false;
        actualizarTurno();
        gridLayout.removeAllViews();
        inicializarCartas();
        mezclarCartas();
        crearVistasCartas();
    }

    private void reproducirSonido(int recursoSonido) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, recursoSonido);
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        if (timerJugador1 != null) {
            timerJugador1.cancel();
        }
        if (timerJugador2 != null) {
            timerJugador2.cancel();
        }
    }
}