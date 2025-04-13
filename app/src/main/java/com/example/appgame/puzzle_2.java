package com.example.appgame;


import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

public class puzzle_2 extends AppCompatActivity {

    private static final String PREFS_NAME       = "prefs";
    private static final String KEY_NO_MOSTRAR   = "noMostrarReglasPuzzle2";

    private ImageButton[] buttons = new ImageButton[16];
    private ImageButton emptyButton;
    private TextView txtTiempo;
    private int segundos = 0;
    private Handler handler = new Handler();
    private Runnable runnable;

    // Solución en espiral para un puzzle 4x4
    private Integer[] solucion = {
            1, 2, 3, 4,
            12,13,14, 5,
            11, 0,15, 6,
            10, 9, 8, 7
    };

    private SharedPreferences preferences;
    private MediaPlayer moveSound, winSound;
    private Button ordenarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle2);

        // Referencias
        txtTiempo      = findViewById(R.id.tiempo);
        ordenarButton  = findViewById(R.id.ordenarButton);
        preferences    = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        moveSound      = MediaPlayer.create(this, R.raw.move_sound);
        winSound       = MediaPlayer.create(this, R.raw.win_sound);

        // Listener del botón de ordenar
        ordenarButton.setOnClickListener(v -> ordenarPuzzle());

        // Inicializa los 16 botones y sus listeners
        for (int i = 0; i < 16; i++) {
            String buttonID = "btn" + (i + 1);
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            buttons[i] = findViewById(resID);
            final int idx = i;
            buttons[i].setOnClickListener(view -> {
                int emptyIdx = getButtonIndex(emptyButton);
                if (esMovimientoValido(emptyIdx, idx)) {
                    mover(idx);
                }
            });
        }

        // Mezcla antes de empezar
        mezclarPuzzle();

        // Decide si mostrar reglas o arrancar cronómetro
        boolean noMostrar = preferences.getBoolean(KEY_NO_MOSTRAR, false);
        if (noMostrar) {
            iniciarCronometro();
        } else {
            mostrarDialogReglas();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        if (moveSound != null) moveSound.release();
        if (winSound  != null) winSound.release();
    }

    // --- CRONÓMETRO ---
    private void iniciarCronometro() {
        runnable = () -> {
            segundos++;
            txtTiempo.setText("Tiempo: " + segundos + "s");
            handler.postDelayed(runnable, 1000);
        };
        handler.postDelayed(runnable, 1000);
    }

    // --- MEZCLAR PUZZLE ---
    private void mezclarPuzzle() {
        List<Integer> nums = Arrays.asList(solucion.clone());
        Collections.shuffle(nums);
        for (int i = 0; i < 16; i++) {
            int num = nums.get(i);
            buttons[i].setTag(num);
            if (num == 0) {
                buttons[i].setImageDrawable(null);
                buttons[i].setVisibility(View.INVISIBLE);
                emptyButton = buttons[i];
            } else {
                int res = getResources().getIdentifier("pieza" + num, "drawable", getPackageName());
                if (res != 0) buttons[i].setImageResource(res);
                else Log.e("Puzzle2", "No se encontró imagen pieza" + num);
                buttons[i].setVisibility(View.VISIBLE);
            }
        }
    }

    // --- ORDENAR PUZZLE ---
    private void ordenarPuzzle() {
        for (int i = 0; i < 16; i++) {
            if (solucion[i] == 0) {
                buttons[i].setImageDrawable(null);
                buttons[i].setTag(0);
                buttons[i].setVisibility(View.INVISIBLE);
                emptyButton = buttons[i];
            } else {
                int res = getResources().getIdentifier("pieza" + solucion[i], "drawable", getPackageName());
                buttons[i].setImageResource(res);
                buttons[i].setTag(solucion[i]);
                buttons[i].setVisibility(View.VISIBLE);
            }
        }
        verificarGanador();
    }

    // --- MOVER PIEZAS ---
    private void mover(int newIndex) {
        int emptyIdx = getButtonIndex(emptyButton);
        if (esMovimientoValido(emptyIdx, newIndex)) {
            ImageButton clicked = buttons[newIndex];

            emptyButton.setImageDrawable(clicked.getDrawable());
            emptyButton.setTag(clicked.getTag());
            emptyButton.setVisibility(View.VISIBLE);

            clicked.setImageDrawable(null);
            clicked.setTag(0);
            clicked.setVisibility(View.INVISIBLE);

            emptyButton = clicked;
            if (moveSound != null) moveSound.start();
            verificarGanador();
        }
    }

    private boolean esMovimientoValido(int emptyIndex, int newIndex) {
        int re = emptyIndex / 4, ce = emptyIndex % 4;
        int rn = newIndex   / 4, cn = newIndex   % 4;
        return (Math.abs(re - rn) == 1 && ce == cn) ||
                (Math.abs(ce - cn) == 1 && re == rn);
    }

    private int getButtonIndex(ImageButton btn) {
        for (int i = 0; i < 16; i++) {
            if (buttons[i] == btn) return i;
        }
        return -1;
    }

    // --- GANAR ---
    private void verificarGanador() {
        Integer[] estado = new Integer[16];
        for (int i = 0; i < 16; i++) {
            estado[i] = buttons[i].getTag() == null ? 0 : (Integer)buttons[i].getTag();
        }
        if (Arrays.equals(estado, solucion)) {
            handler.removeCallbacks(runnable);
            if (winSound != null) winSound.start();
            guardarMejorTiempo();
            mostrarDialogVictoria();
        }
    }

    private void guardarMejorTiempo() {
        int mejor = preferences.getInt("mejorTiempoPuzzle2", Integer.MAX_VALUE);
        if (segundos < mejor) {
            preferences.edit()
                    .putInt("mejorTiempoPuzzle2", segundos)
                    .apply();
        }
    }

    private void mostrarDialogVictoria() {
        Dialog d = new Dialog(this);
        View layout = LayoutInflater.from(this)
                .inflate(R.layout.activity_win_puzzle1, null);
        d.setContentView(layout);
        TextView tv = layout.findViewById(R.id.textoReglas);
        tv.setText("¡Felicidades! 🎉\nHas completado el puzzle en " + segundos + " segundos.");
        layout.findViewById(R.id.cerrarButton).setOnClickListener(v -> {
            d.dismiss();
            startActivity(new Intent(puzzle_2.this, menu_puzzle.class));
            finish();
        });
        d.show();
    }

    // --- DIÁLOGO DE REGLAS (con fondo transparente y cronómetro diferido) ---
    private void mostrarDialogReglas() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_reglas2);

        // Quita el fondo gris predeterminado
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
            iniciarCronometro();  // Arranca el cronómetro solo al cerrar
        });

        dialog.show();
    }
}
