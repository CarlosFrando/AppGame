package com.example.appgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class win_puzzle_1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_puzzle1);

        // Obtener el tiempo final del intent
        int tiempoFinal = getIntent().getIntExtra("tiempoFinal", 0);

        // Asignar el tiempo al TextView
        TextView textoReglas = findViewById(R.id.textoReglas);
        textoReglas.setText("¡Felicidades! 🎉\nHas completado el puzzle en " + tiempoFinal + " segundos.");

        // Botón para cerrar y volver al menú
        Button cerrarButton = findViewById(R.id.cerrarButton);
        cerrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(win_puzzle_1.this, menu_puzzle.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
