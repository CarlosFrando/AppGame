package com.example.appgame;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class menu_puzzle extends AppCompatActivity {
    ImageButton puzzle1, puzzle2, puzzle3;
    TextView txtTiempoPuzzle1, txtTiempoPuzzle2, txtTiempoPuzzle3;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_puzzle);

        // Referencias a los controles del layout
        txtTiempoPuzzle1 = findViewById(R.id.txtTiempoPuzzle1);
        txtTiempoPuzzle2 = findViewById(R.id.txtTiempoPuzzle2);
        txtTiempoPuzzle3 = findViewById(R.id.txtTiempoPuzzle3);

        puzzle1 = findViewById(R.id.puzzle1);
        puzzle2 = findViewById(R.id.puzzle2);
        puzzle3 = findViewById(R.id.puzzle3);

        // Inicializa SharedPreferences
        preferences = getSharedPreferences("prefs", MODE_PRIVATE);

        // Recupera los mejores tiempos guardados para cada puzzle
        int mejorTiempoPuzzle1 = preferences.getInt("mejorTiempoPuzzle1", 0);
        int mejorTiempoPuzzle2 = preferences.getInt("mejorTiempoPuzzle2", 0);
        int mejorTiempoPuzzle3 = preferences.getInt("mejorTiempoPuzzle3", 0);

        // Actualiza los TextView: si el tiempo es 0, se muestra "--"
        txtTiempoPuzzle1.setText("Tiempo: " + (mejorTiempoPuzzle1 == 0 ? "--" : mejorTiempoPuzzle1 + "s"));
        txtTiempoPuzzle2.setText("Tiempo: " + (mejorTiempoPuzzle2 == 0 ? "--" : mejorTiempoPuzzle2 + "s"));
        txtTiempoPuzzle3.setText("Tiempo: " + (mejorTiempoPuzzle3 == 0 ? "--" : mejorTiempoPuzzle3 + "s"));

        // Configura los botones para ir a cada puzzle
        puzzle1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menu_puzzle.this, puzzle_1.class);
                startActivity(intent);
            }
        });

        puzzle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menu_puzzle.this, puzzle_2.class);
                startActivity(intent);
            }
        });

        puzzle3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menu_puzzle.this, puzzle_3.class);
                startActivity(intent);
            }
        });
    }
}
