package com.example.appgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity4 extends AppCompatActivity {

    private EditText etNombre1, etNombre2;
    private TextView tvNombre2;
    private ImageButton btnJugar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        etNombre1 = findViewById(R.id.etNombre1);
        etNombre2 = findViewById(R.id.etNombre2);
        tvNombre2 = findViewById(R.id.tvNombre2);
        btnJugar = findViewById(R.id.btnJugar);

        // Obtener el modo seleccionado (Jugador vs Jugador o Jugador vs IA)
        boolean isAI = getIntent().getBooleanExtra("isAI", false);

        // Configurar la visibilidad de los campos según el modo
        if (isAI) {
            tvNombre2.setVisibility(View.GONE);
            etNombre2.setVisibility(View.GONE);
        } else {
            tvNombre2.setVisibility(View.VISIBLE);
            etNombre2.setVisibility(View.VISIBLE);
        }

        // Botón JUGAR
        btnJugar.setOnClickListener(v -> {
            String nombre1 = etNombre1.getText().toString().trim();
            String nombre2 = isAI ? "IA" : etNombre2.getText().toString().trim();

            if (nombre1.isEmpty()) {
                etNombre1.setError("Ingresa el nombre del Jugador 1");
                return;
            }

            if (!isAI && nombre2.isEmpty()) {
                etNombre2.setError("Ingresa el nombre del Jugador 2");
                return;
            }

            // Pasar los nombres y el modo a la actividad principal del juego
            Intent intent = new Intent(MainActivity4.this, MainActivity3.class);
            intent.putExtra("nombre1", nombre1);
            intent.putExtra("nombre2", nombre2);
            intent.putExtra("isAI", isAI);
            startActivity(intent);
        });
    }
}