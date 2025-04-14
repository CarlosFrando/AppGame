package com.example.appgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AhorcadoActivity extends AppCompatActivity {
    private CheckBox checkBoxNoMostrar;
    private static final String PREFS_NAME = "GamePrefs";
    private static final String KEY_SHOW_RULES = "showRules";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean showRules = preferences.getBoolean(KEY_SHOW_RULES, true);

        // Si el usuario marcó que no se muestren las reglas, saltamos directo al juego
        if (!showRules) {
            goToMainActivity();
            return;
        }

        setContentView(R.layout.activity_ahorcado);

        TextView rulesTextView = findViewById(R.id.rulesText);
        checkBoxNoMostrar = findViewById(R.id.checkBoxNoMostrar);
        Button startGameButton = findViewById(R.id.startGameButton);


        rulesTextView.setText(
                "1. Se te mostrará una palabra oculta.\n" +
                        "2. Debes adivinarla letra por letra.\n" +
                        "3. Cada intento fallido dibuja una parte del ahorcado.\n" +
                        "4. Si cometes 6 errores, pierdes el juego.\n" +
                        "5. Si completas la palabra antes de los 6 errores, ganas.\n" +
                        "¡Diviértete!"
        );

        // Configurar el botón para iniciar el juego
        startGameButton.setOnClickListener(v -> {
            // Guardar la preferencia si el usuario marca "No volver a mostrar"
            if (checkBoxNoMostrar.isChecked()) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(KEY_SHOW_RULES, false);
                editor.apply();
            }

            goToMainActivity();
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, ActivityAhorcado_02.class);
        startActivity(intent);
        finish(); // Cierra esta actividad para que no regrese a las reglas
    }
}
