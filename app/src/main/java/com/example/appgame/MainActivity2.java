package com.example.appgame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    ImageButton btnGato, btnPuzzle, btnAhorcado, btnMemo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Asegúrate de que este sea el nombre correcto del layout

        // Referencias a los botones
        btnGato = findViewById(R.id.btnGato);
        btnPuzzle = findViewById(R.id.btnPuzzle);
        btnAhorcado = findViewById(R.id.btnAhorcado);
        btnMemo = findViewById(R.id.btnMemo);

        // Listeners
        btnGato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity2.this, GatoActivity.class));
            }
        });

        btnPuzzle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity2.this, PuzzleActivity.class));
            }
        });

        btnAhorcado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity2.this, AhorcadoActivity.class));
            }
        });

        btnMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity2.this, MemoramaActivity.class));
            }
        });
    }
}
