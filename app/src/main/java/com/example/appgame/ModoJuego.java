package com.example.appgame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ModoJuego extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_modojuego);

        ImageButton btnVsPlayer = findViewById(R.id.btn_vs_player);
        ImageButton btnVsAI = findViewById(R.id.btn_vs_ai);

        btnVsPlayer.setOnClickListener(v -> startGame(false));
        btnVsAI.setOnClickListener(v -> startGame(true));
    }

    private void startGame(boolean isAI) {
        Intent intent = new Intent(this, MainActivity4.class);
        intent.putExtra("isAI", isAI);
        startActivity(intent);
    }
}