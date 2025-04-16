package com.example.appgame;


import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class puzzle_3 extends AppCompatActivity implements SensorEventListener {

    private static final String PREFS_NAME     = "prefs";
    private static final String KEY_NO_MOSTRAR = "noMostrarReglasPuzzle3";

    private ImageButton[] buttons = new ImageButton[16];
    private ImageButton emptyButton;
    private TextView txtTiempo;
    private int segundos = 0;
    private Handler handler = new Handler();
    private Runnable runnable;
    private Integer[] solucion = {
            1, 2, 3, 4,
            5, 6, 7, 8,
            9,10,11,12,
            13,14,15,16
    };
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean moved = false;
    private Button ordenarButton;
    private MediaPlayer winSound;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle3);

        // Referencias
        txtTiempo      = findViewById(R.id.tiempo);
        ordenarButton  = findViewById(R.id.ordenarButton);
        preferences    = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Inicializa botones
        for (int i = 0; i < 16; i++) {
            String buttonID = "btn" + (i + 1);
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            buttons[i] = findViewById(resID);
        }

        // Listener ordenar
        ordenarButton.setOnClickListener(v -> ordenarPuzzle());

        // Mezclar antes de empezar
        mezclarPuzzle();

        // Decide si mostrar reglas o iniciar cronómetro
        boolean noMostrar = preferences.getBoolean(KEY_NO_MOSTRAR, false);
        if (noMostrar) {
            iniciarCronometro();
        } else {
            mostrarDialogReglas();
        }

        // Sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        winSound = MediaPlayer.create(this, R.raw.win_sound);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        if (winSound != null) winSound.release();
    }

    private void iniciarCronometro() {
        runnable = new Runnable() {
            @Override
            public void run() {
                segundos++;
                txtTiempo.setText("Tiempo: " + segundos + "s");
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    private void mezclarPuzzle() {
        List<Integer> numeros = Arrays.asList(solucion.clone());
        Collections.shuffle(numeros);

        for (int i = 0; i < 16; i++) {
            int numero = numeros.get(i);
            buttons[i].setTag(numero);
            int resID = getResources().getIdentifier("toribio" + numero, "drawable", getPackageName());
            if (resID != 0) {
                buttons[i].setImageResource(resID);
            } else {
                Log.e("Puzzle3", "No se encontró la imagen: toribio" + numero);
            }
        }

        emptyButton = buttons[15];
        emptyButton.setVisibility(View.INVISIBLE);
        emptyButton.setTag(0);
    }

    private void ordenarPuzzle() {
        for (int i = 0; i < 16; i++) {
            int valor = solucion[i];
            if (valor == 0) {
                buttons[i].setImageDrawable(null);
                buttons[i].setTag(0);
                buttons[i].setVisibility(View.INVISIBLE);
                emptyButton = buttons[i];
            } else {
                int resID = getResources().getIdentifier("toribio" + valor, "drawable", getPackageName());
                buttons[i].setImageResource(resID);
                buttons[i].setTag(valor);
                buttons[i].setVisibility(View.VISIBLE);
            }
        }
        verificarGanador();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float threshold = 3.0f;

        if (!moved) {
            if (y < -threshold) mover(-4);
            else if (y > threshold) mover(4);
            else if (x > threshold) mover(-1);
            else if (x < -threshold) mover(1);
        }

        if (Math.abs(x) < 1 && Math.abs(y) < 1) {
            moved = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void mover(int movimiento) {
        int emptyIndex = getButtonIndex(emptyButton);
        int newIndex = emptyIndex + movimiento;

        if (newIndex >= 0 && newIndex < 16 && esMovimientoValido(emptyIndex, newIndex)) {
            ImageButton clicked = buttons[newIndex];
            emptyButton.setImageDrawable(clicked.getDrawable());
            emptyButton.setTag(clicked.getTag());
            emptyButton.setVisibility(View.VISIBLE);

            clicked.setImageDrawable(null);
            clicked.setTag(0);
            clicked.setVisibility(View.INVISIBLE);

            emptyButton = clicked;
            moved = true;

            verificarGanador();
        }
    }

    private boolean esMovimientoValido(int emptyIndex, int newIndex) {
        int rowEmpty = emptyIndex / 4;
        int colEmpty = emptyIndex % 4;
        int rowNew   = newIndex   / 4;
        int colNew   = newIndex   % 4;
        return (Math.abs(rowEmpty - rowNew) == 1 && colEmpty == colNew) ||
                (Math.abs(colEmpty - colNew) == 1 && rowEmpty == rowNew);
    }

    private int getButtonIndex(ImageButton button) {
        for (int i = 0; i < 16; i++) {
            if (buttons[i] == button) return i;
        }
        return -1;
    }

    private void verificarGanador() {
        Integer[] estadoActual = new Integer[16];
        for (int i = 0; i < 16; i++) {
            estadoActual[i] = (Integer) buttons[i].getTag();
        }

        if (Arrays.equals(estadoActual, solucion)) {
            handler.removeCallbacks(runnable);
            if (winSound != null) winSound.start();
            // Guardar mejor tiempo
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            int mejorGuardado = prefs.getInt("mejorTiempoPuzzle3", 0);
            if (mejorGuardado == 0 || segundos < mejorGuardado) {
                prefs.edit()
                        .putInt("mejorTiempoPuzzle3", segundos)
                        .apply();
            }
            mostrarDialogoVictoria();
        }
    }

    private void mostrarDialogoVictoria() {
        Dialog dialog = new Dialog(this);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.activity_win_puzzle1, null);
        dialog.setContentView(layout);

        TextView textoReglas = layout.findViewById(R.id.textoReglas);
        textoReglas.setText("¡Felicidades! 🎉\nHas completado el puzzle en " + segundos + " segundos.");

        Button cerrarButton = layout.findViewById(R.id.cerrarButton);
        cerrarButton.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(puzzle_3.this, menu_puzzle.class));
            finish();
        });

        dialog.show();
    }

    // --- NUEVO: DIÁLOGO DE REGLAS ---
    private void mostrarDialogReglas() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_reglas3);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );
        }

        CheckBox cb = dialog.findViewById(R.id.checkNoMostrar);
        Button cerrar = dialog.findViewById(R.id.cerrarButton);
        cerrar.setOnClickListener(v -> {
            if (cb.isChecked()) {
                preferences.edit()
                        .putBoolean(KEY_NO_MOSTRAR, true)
                        .apply();
            }
            dialog.dismiss();
            iniciarCronometro();
        });

        dialog.show();
    }
}
