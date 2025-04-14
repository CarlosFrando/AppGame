package com.example.appgame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;


import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;


public class ActivityAhorcado_02 extends AppCompatActivity {
    private ImageView hangmanImage;
    private TextView wordView, clueView;
    private String secretWord, clueWord;
    private char[] displayWord;
    private int errors = 0;
    private int winCount = 0;
    private long startTime;
    private GridLayout keyboardLayout;

    private MediaPlayer soundCorrect, soundWrong, soundWin, soundLose;

    private final String[][] palabras = {
            {"CIVIL", "Construcciones"},
            {"ROBOTICA", "Robots"},
            {"INDUSTRIAL", "Fabricas"},
            {"SISTEMAS", "Computadoras"},
            {"MANUFACTURA", "Elaboración"},
            {"ELECTRONICA", "Circuitos"},
            {"GESTION", "Administración"},
            {"NEGOCIOS", "Comercio"},
            {"FINANCIERA", "Inversiones"}
    };

    private boolean respuestasMostradas = false;

    private ArrayList<String> usedWords = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ahorcado02);


        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setVisibility(View.VISIBLE);

        // Referencias a vistas
        hangmanImage = findViewById(R.id.hangmanImage);
        wordView = findViewById(R.id.wordView);
        clueView = findViewById(R.id.clueView);
        keyboardLayout = findViewById(R.id.keyboardLayout);

        // Aumentar tamaño del texto para mejorar la legibilidad
        wordView.setTextSize(28);

        // Cargar efectos de sonido
        soundCorrect = MediaPlayer.create(this, R.raw.correct);
        soundWrong = MediaPlayer.create(this, R.raw.wrong);
        soundWin = MediaPlayer.create(this, R.raw.win);
        soundLose = MediaPlayer.create(this, R.raw.lose);

        startTime = System.currentTimeMillis();
        // Iniciar el juego y crear teclado
        iniciarJuego();
        createKeyboard();
    }

    private void iniciarJuego() {
        errors = 0;
        updateHangmanImage();
        loadRandomWord();
        hangmanImage.setEnabled(true);
    }

    private void loadRandomWord() {
        if (usedWords.size() == palabras.length) {
            usedWords.clear(); // Reiniciar lista cuando se usen todas las palabras
        }

        Random random = new Random();
        String selectedWord;
        String selectedClue;

        do {
            int index = random.nextInt(palabras.length);
            selectedWord = palabras[index][0];
            selectedClue = palabras[index][1];
        } while (usedWords.contains(selectedWord));

        usedWords.add(selectedWord);
        secretWord = selectedWord;
        clueWord = selectedClue;

        // Inicializar palabra oculta con guiones
        displayWord = new char[secretWord.length()];
        for (int i = 0; i < secretWord.length(); i++) {
            displayWord[i] = '_';
        }

        // Mostrar pista y palabra en formato claro
        wordView.setText(getFormattedWord());
        clueView.setText("Pista: " + clueWord);
    }

    private void createKeyboard() {
        keyboardLayout.removeAllViews();
        String letters = "QWERTYUIOPASDFGHJKLÑZXCVBNM";
        int buttonSize = 140;
        int marginSize = 5;

        for (char letter : letters.toCharArray()) {
            Button button = new Button(this);
            button.setText(String.valueOf(letter));
            button.setTextSize(24);
            button.setPadding(1, 1, 1, 1);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = buttonSize;
            params.height = buttonSize;
            params.setMargins(marginSize, marginSize, marginSize, marginSize);
            button.setLayoutParams(params);

            button.setOnClickListener(v -> checkLetter(letter));
            keyboardLayout.addView(button);
        }
    }

    private void checkLetter(char letter) {
        boolean correct = false;

        for (int i = 0; i < secretWord.length(); i++) {
            if (secretWord.charAt(i) == letter) {
                displayWord[i] = letter;
                correct = true;
            }
        }

        if (!correct) {
            errors++;
            updateHangmanImage();
            soundWrong.start();
        } else {
            soundCorrect.start();
        }

        wordView.setText(getFormattedWord());

        if (String.valueOf(displayWord).equals(secretWord)) {
            winCount++;
            soundWin.start();

            if (winCount >= 5) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                Intent intent = new Intent(this, ActivityAhorcado_03.class);
                intent.putExtra("elapsedTime", elapsedTime);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "¡Ganaste! Nueva palabra en 2 segundos...", Toast.LENGTH_LONG).show();
                new Handler().postDelayed(this::iniciarJuego, 2000);
            }
            hangmanImage.setEnabled(false);
        } else if (errors >= 6) {
            soundLose.start();

            long elapsedTime = System.currentTimeMillis() - startTime;

            Intent intent = new Intent(this, ActivityAhorcado_04.class);
            intent.putExtra("elapsedTime", elapsedTime);
            startActivity(intent);
            finish();
        }
    }

    private String getFormattedWord() {
        StringBuilder formattedWord = new StringBuilder();
        for (char c : displayWord) {
            formattedWord.append(c).append(" ");
        }
        return formattedWord.toString().trim();
    }

    private void updateHangmanImage() {
        int resID = getResources().getIdentifier("hangman_" + errors, "drawable", getPackageName());
        hangmanImage.setImageResource(resID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundCorrect != null) soundCorrect.release();
        if (soundWrong != null) soundWrong.release();
        if (soundWin != null) soundWin.release();
        if (soundLose != null) soundLose.release();
    }
}
