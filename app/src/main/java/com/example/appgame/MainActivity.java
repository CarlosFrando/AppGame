package com.example.appgame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText etNombre, etEdad, etCorreo, etTelefono, etOtro;
    RadioGroup radioGroupGenero;
    AutoCompleteTextView autoCompleteEscuela;
    CheckBox check_mecanica, check_civil, check_manufactura, check_industrial,
            check_sistemas_electricos, check_ti, check_arquitectura,
            check_negocios, check_financiera, check_comercio, check_administracion, check_psicologia;

    Button btnEnviar;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences("prefs", MODE_PRIVATE);
        String nombrecompleto = preferences.getString("nombre", null);
        if(nombrecompleto != null){
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
            finish();
            return; // Salir de la actividad de registro
        }

        setContentView(R.layout.activity_main);

        // Asignar vistas
        etNombre = findViewById(R.id.etNombre);
        etEdad = findViewById(R.id.etEdad);
        etCorreo = findViewById(R.id.etCorreo);
        etTelefono = findViewById(R.id.etTelefono);
        etOtro = findViewById(R.id.etOtro);

        radioGroupGenero = findViewById(R.id.radioGroupGenero);
        autoCompleteEscuela = findViewById(R.id.spinnerEscuela);

        check_mecanica = findViewById(R.id.check_mecanica);
        check_civil = findViewById(R.id.check_civil);
        check_manufactura = findViewById(R.id.check_manufactura);
        check_industrial = findViewById(R.id.check_industrial);
        check_sistemas_electricos = findViewById(R.id.check_sistemas_electricos);
        check_ti = findViewById(R.id.check_ti);
        check_arquitectura = findViewById(R.id.check_arquitectura);
        check_negocios = findViewById(R.id.check_negocios);
        check_financiera = findViewById(R.id.check_financiera);
        check_comercio = findViewById(R.id.check_comercio);
        check_administracion = findViewById(R.id.check_administracion);
        check_psicologia = findViewById(R.id.check_psicologia);

        btnEnviar = findViewById(R.id.btnEnviar);

        // Configurar AutoCompleteTextView
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.escuelas_array, android.R.layout.simple_dropdown_item_1line);
        autoCompleteEscuela.setAdapter(adapter);

        btnEnviar.setOnClickListener(view -> {
            if (hayInternet()) {
                enviarDatos();
            } else {
                Toast.makeText(MainActivity.this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean hayInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void enviarDatos() {
        // Recoger valores de los campos
        String nombre = etNombre.getText().toString().trim();
        String edad = etEdad.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String otro = etOtro.getText().toString().trim();

        int selectedId = radioGroupGenero.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedId);
        String genero = selectedRadioButton != null ? selectedRadioButton.getText().toString() : "";

        String escuela = autoCompleteEscuela.getText().toString().trim();

        ArrayList<String> carreras = new ArrayList<>();
        if (check_mecanica.isChecked()) carreras.add("Mecánica, ");
        if (check_civil.isChecked()) carreras.add("Civil, ");
        if (check_manufactura.isChecked()) carreras.add("Manufactura Avanzada, ");
        if (check_industrial.isChecked()) carreras.add("Industrial, ");
        if (check_sistemas_electricos.isChecked()) carreras.add("Sistemas Eléctricos, ");
        if (check_ti.isChecked()) carreras.add("TI, ");
        if (check_arquitectura.isChecked()) carreras.add("Arquitectura, ");
        if (check_negocios.isChecked()) carreras.add("Negocios, ");
        if (check_financiera.isChecked()) carreras.add("Financiera, ");
        if (check_comercio.isChecked()) carreras.add("Comercio, ");
        if (check_administracion.isChecked()) carreras.add("Administración, ");
        if (check_psicologia.isChecked()) carreras.add("Psicología, ");
        if (!otro.isEmpty()) carreras.add(otro);

        // Validaciones
        if (nombre.isEmpty() || edad.isEmpty() || correo.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Por favor llena todos los campos obligatorios.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedId == -1) {
            Toast.makeText(this, "Por favor selecciona un género.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (carreras.isEmpty()) {
            Toast.makeText(this, "Por favor selecciona al menos una carrera.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (escuela.isEmpty()) {
            Toast.makeText(this, "Por favor selecciona una escuela.", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("nombre", nombre);
        editor.putString("edad", edad);
        editor.putString("correo", correo);
        editor.putString("telefono", telefono);
        editor.putString("genero", genero);
        editor.putString("escuela", escuela);
        editor.putString("carreras", android.text.TextUtils.join(",", carreras));
        editor.apply();

        // Si todo está bien, enviar los datos en un hilo nuevo
        new Thread(() -> {
            try {
                String carrerasString = android.text.TextUtils.join(",", carreras);

                String postData = "nombre=" + URLEncoder.encode(nombre, "UTF-8") +
                        "&edad=" + URLEncoder.encode(edad, "UTF-8") +
                        "&genero=" + URLEncoder.encode(genero, "UTF-8") +
                        "&escuela=" + URLEncoder.encode(escuela, "UTF-8") +
                        "&carreras=" + URLEncoder.encode(carrerasString, "UTF-8") +
                        "&correo=" + URLEncoder.encode(correo, "UTF-8") +
                        "&telefono=" + URLEncoder.encode(telefono, "UTF-8");

                URL url = new URL("http://200.79.182.103/registro.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Datos enviados", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(MainActivity.this, "Error en el envío: " + responseCode, Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }
}
