package com.example.appgame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity5 extends AppCompatActivity {

    private TextView tvRules;
    private CheckBox cbSeen;
    private ImageButton btnContinue;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        // Inicializar vistas
        tvRules = findViewById(R.id.tvRules);
        cbSeen = findViewById(R.id.cbSeen);
        btnContinue = findViewById(R.id.btnContinue);

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("GameRules", Context.MODE_PRIVATE);

        // Verificar si las reglas ya fueron vistas
        boolean rulesSeen = sharedPreferences.getBoolean("rulesSeen", false);
        if (rulesSeen) {
            // Si ya fueron vistas, ir directamente a MainActivity2
            startMainActivity2();
        }

        // Configurar el evento del botón "Continuar"
        btnContinue.setOnClickListener(v -> {
            // Guardar el estado del CheckBox en SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("rulesSeen", cbSeen.isChecked());
            editor.apply();

            // Ir a MainActivity2
            startMainActivity2();
        });
    }

    // Método para iniciar MainActivity2
    private void startMainActivity2() {
        Intent intent = new Intent(MainActivity5.this, ModoJuego.class);
        startActivity(intent);
        finish();
    }
}