package com.example.appgame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;



public class ActivityAhorcado_03 extends AppCompatActivity {

    private TextView timePlayed;
    private ImageView restartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ahorcado03);

        timePlayed = findViewById(R.id.timePlayed);
        restartButton = findViewById(R.id.restartButton);

        long elapsedTime = getIntent().getLongExtra("elapsedTime", 0);
        long seconds = (elapsedTime / 1000) % 60;
        long minutes = (elapsedTime / 1000) / 60;

        String timeText = String.format("Tiempo jugado: %02d:%02d", minutes, seconds);
        timePlayed.setText(timeText);



        restartButton.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityAhorcado_03.this, AhorcadoActivity.class);
            startActivity(intent);
            finish();
        });
    }
}