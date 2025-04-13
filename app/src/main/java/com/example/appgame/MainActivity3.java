package com.example.appgame;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity3 extends AppCompatActivity implements SensorEventListener {

    private ImageButton[][] buttons = new ImageButton[3][3];
    private boolean player1Turn = true;
    private int roundCount = 0;
    private int player1Points = 0;
    private int player2Points = 0;
    private TextView tvTurn;
    private TextView tvPlayer1, tvPlayer2, tvTimer1, tvTimer2;
    private boolean isAI;
    private boolean gameEnded = false;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastShakeTime = 0;
    private static final int SHAKE_THRESHOLD = 800;

    private CountDownTimer timer1, timer2;
    private long timeLeft1 = 30000;
    private long timeLeft2 = 30000;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        // Inicialización de sensores
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        // Obtener datos de la actividad anterior
        Intent intent = getIntent();
        isAI = intent.getBooleanExtra("isAI", false);
        String nombre1 = intent.getStringExtra("nombre1");
        String nombre2 = isAI ? "IA" : intent.getStringExtra("nombre2");

        // Inicializar vistas
        initializeViews(nombre1, nombre2);

        // Configurar botones del tablero
        setupBoardButtons();

        // Iniciar primer temporizador
        startTimer1();
        updateTurnText();
    }

    private void initializeViews(String nombre1, String nombre2) {
        tvTurn = findViewById(R.id.turn);
        tvPlayer1 = findViewById(R.id.tvPlayer1);
        tvPlayer2 = findViewById(R.id.tvPlayer2);
        tvTimer1 = findViewById(R.id.tvTimer1);
        tvTimer2 = findViewById(R.id.tvTimer2);

        tvPlayer1.setText(nombre1);
        tvPlayer2.setText(nombre2);
    }

    private void setupBoardButtons() {
        buttons[0][0] = findViewById(R.id.a1);
        buttons[0][1] = findViewById(R.id.a2);
        buttons[0][2] = findViewById(R.id.a3);
        buttons[1][0] = findViewById(R.id.b1);
        buttons[1][1] = findViewById(R.id.b2);
        buttons[1][2] = findViewById(R.id.b3);
        buttons[2][0] = findViewById(R.id.c1);
        buttons[2][1] = findViewById(R.id.c2);
        buttons[2][2] = findViewById(R.id.c3);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final int row = i;
                final int col = j;
                buttons[i][j].setOnClickListener(v -> {
                    if (!gameEnded) {
                        onButtonClick(row, col);
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        stopAllTimers();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && !gameEnded) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float acceleration = (float) Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;

            if (acceleration > 30) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastShakeTime > 1000) {
                    lastShakeTime = currentTime;
                    resetBoard();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No implementado
    }

    private void startTimer1() {
        timer1 = new CountDownTimer(timeLeft1, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft1 = millisUntilFinished;
                updateTimer1();
            }

            @Override
            public void onFinish() {
                tvTimer1.setText("00:00");
                if (!gameEnded) {
                    playerWins(2);
                }
            }
        }.start();
    }

    private void updateTimer1() {
        int minutes = (int) (timeLeft1 / 1000) / 60;
        int seconds = (int) (timeLeft1 / 1000) % 60;
        tvTimer1.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void startTimer2() {
        timer2 = new CountDownTimer(timeLeft2, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft2 = millisUntilFinished;
                updateTimer2();
            }

            @Override
            public void onFinish() {
                tvTimer2.setText("00:00");
                if (!gameEnded) {
                    playerWins(1);
                }
            }
        }.start();
    }

    private void updateTimer2() {
        int minutes = (int) (timeLeft2 / 1000) / 60;
        int seconds = (int) (timeLeft2 / 1000) % 60;
        tvTimer2.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void stopAllTimers() {
        if (timer1 != null) {
            timer1.cancel();
        }
        if (timer2 != null) {
            timer2.cancel();
        }
    }

    private void switchTurn() {
        stopAllTimers();

        if (player1Turn) {
            startTimer2();
            tvTurn.setText("Turno de " + tvPlayer2.getText());
        } else {
            startTimer1();
            tvTurn.setText("Turno de " + tvPlayer1.getText());
        }

        player1Turn = !player1Turn;

        if (isAI && !player1Turn && !gameEnded) {
            computerMove();
        }
    }

    private void onButtonClick(int row, int col) {
        ImageButton button = buttons[row][col];

        if (button.getDrawable() != null || gameEnded) {
            return;
        }

        button.setImageResource(player1Turn ? R.drawable.i : R.drawable.a);
        playSound(R.raw.insertar);
        roundCount++;

        if (checkForWin()) {
            playerWins(player1Turn ? 1 : 2);
        } else if (roundCount == 9) {
            draw();
        } else {
            switchTurn();
        }
    }

    private boolean checkForWin() {
        // Verificar filas y columnas
        for (int i = 0; i < 3; i++) {
            if (checkThree(buttons[i][0], buttons[i][1], buttons[i][2]) ||
                    checkThree(buttons[0][i], buttons[1][i], buttons[2][i])) {
                return true;
            }
        }

        // Verificar diagonales
        return checkThree(buttons[0][0], buttons[1][1], buttons[2][2]) ||
                checkThree(buttons[0][2], buttons[1][1], buttons[2][0]);
    }

    private boolean checkThree(ImageButton b1, ImageButton b2, ImageButton b3) {
        return b1.getDrawable() != null &&
                b2.getDrawable() != null &&
                b3.getDrawable() != null &&
                b1.getDrawable().getConstantState().equals(b2.getDrawable().getConstantState()) &&
                b1.getDrawable().getConstantState().equals(b3.getDrawable().getConstantState());
    }

    private void showDialog(String message, int imageResId) {
        gameEnded = true;
        stopAllTimers();

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_resultado, null);
        ImageView ivResultado = dialogView.findViewById(R.id.ivResultado);
        TextView tvMensaje = dialogView.findViewById(R.id.tvMensaje);

        ivResultado.setImageResource(imageResId);
        tvMensaje.setText(message);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Resultado del Juego")
                .setView(dialogView)
                .setPositiveButton("Jugar otra vez", (dialog, which) -> restartGame())
                .setCancelable(false)
                .show();
    }

    private void playerWins(int player) {
        if (player == 1) {
            player1Points++;
            playSound(R.raw.ganar);
            showDialog("¡" + tvPlayer1.getText() + " gana!", R.drawable.ganar);
        } else {
            player2Points++;
            if (isAI) {
                playSound(R.raw.perder);
                showDialog("¡Has perdido!", R.drawable.perder);
            } else {
                playSound(R.raw.ganar);
                showDialog("¡" + tvPlayer2.getText() + " gana!", R.drawable.ganar);
            }
        }
    }

    private void draw() {
        playSound(R.raw.empate);
        showDialog("¡Es un empate!", R.drawable.empate);
    }

    private void resetBoard() {
        Intent intent = new Intent(MainActivity3.this, ModoJuego.class);
        intent.putExtra("nombre1", tvPlayer1.getText().toString());
        if (!isAI) {
            intent.putExtra("nombre2", tvPlayer2.getText().toString());
        }
        intent.putExtra("isAI", isAI);
        startActivity(intent);
        finish();
    }

    private void restartGame() {
        gameEnded = false;

        // Limpiar tablero
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setImageDrawable(null);
            }
        }

        // Reiniciar variables
        roundCount = 0;
        player1Turn = true;
        timeLeft1 = 30000;
        timeLeft2 = 30000;

        // Reiniciar temporizadores
        startTimer1();
        updateTurnText();
    }

    private void updateTurnText() {
        tvTurn.setText(player1Turn ? "Turno de " + tvPlayer1.getText() : "Turno de " + tvPlayer2.getText());
    }

    private void computerMove() {
        int[] move = findBestMove();
        if (move[0] != -1) {
            buttons[move[0]][move[1]].performClick(); // Simular click para mantener consistencia
        }
    }

    private int[] findBestMove() {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = {-1, -1};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getDrawable() == null) {
                    buttons[i][j].setImageResource(R.drawable.a);
                    roundCount++;
                    int score = minimax(false);
                    buttons[i][j].setImageDrawable(null);
                    roundCount--;

                    if (score > bestScore) {
                        bestScore = score;
                        bestMove[0] = i;
                        bestMove[1] = j;
                    }
                }
            }
        }

        return bestMove;
    }

    private int minimax(boolean isMaximizing) {
        if (checkForWin()) {
            return isMaximizing ? -1 : 1;
        }

        if (roundCount == 9) {
            return 0;
        }

        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getDrawable() == null) {
                    buttons[i][j].setImageResource(isMaximizing ? R.drawable.a : R.drawable.i);
                    roundCount++;
                    int score = minimax(!isMaximizing);
                    buttons[i][j].setImageDrawable(null);
                    roundCount--;

                    bestScore = isMaximizing ? Math.max(score, bestScore) : Math.min(score, bestScore);
                }
            }
        }
        return bestScore;
    }

    private void playSound(int soundResource) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, soundResource);
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAllTimers();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}