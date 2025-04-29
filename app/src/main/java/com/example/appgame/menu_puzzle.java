package com.example.appgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class menu_puzzle extends AppCompatActivity {
    ImageButton puzzle1, puzzle2, puzzle3;
    TextView txtTiempoPuzzle1, txtTiempoPuzzle2, txtTiempoPuzzle3;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_puzzle);

        // Permitir red en hilo principal (solo para pruebas)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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

        // Enviar los tiempos al servidor
        enviarTiemposAlServidor(mejorTiempoPuzzle1, mejorTiempoPuzzle2, mejorTiempoPuzzle3);

        // Configura los botones para ir a cada puzzle
        puzzle1.setOnClickListener(v -> {
            Intent intent = new Intent(menu_puzzle.this, puzzle_1.class);
            startActivity(intent);
        });

        puzzle2.setOnClickListener(v -> {
            Intent intent = new Intent(menu_puzzle.this, puzzle_2.class);
            startActivity(intent);
        });

        puzzle3.setOnClickListener(v -> {
            Intent intent = new Intent(menu_puzzle.this, puzzle_3.class);
            startActivity(intent);
        });
    }

    private void enviarTiemposAlServidor(int tiempo1, int tiempo2, int tiempo3) {
        try {
            // Obtener nombre para identificar al usuario
            String nombre = preferences.getString("nombre", "desconocido");

            String postData = "nombre=" + URLEncoder.encode(nombre, "UTF-8") +
                    "&tiempo1=" + URLEncoder.encode(String.valueOf(tiempo1), "UTF-8") +
                    "&tiempo2=" + URLEncoder.encode(String.valueOf(tiempo2), "UTF-8") +
                    "&tiempo3=" + URLEncoder.encode(String.valueOf(tiempo3), "UTF-8");

            URL url = new URL("http://200.79.182.103/update_tiempo.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            // Enviar los datos
            OutputStream os = conn.getOutputStream();
            os.write(postData.getBytes("UTF-8"));
            os.flush();
            os.close();

            // Leer la respuesta
            int responseCode = conn.getResponseCode();
            InputStream is = (responseCode == HttpURLConnection.HTTP_OK) ? conn.getInputStream() : conn.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Mostrar respuesta del servidor en un Toast
            Toast.makeText(this, "Respuesta del servidor: " + response.toString(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Excepción al enviar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}