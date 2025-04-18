package com.example.appgame;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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
    Spinner spinnerEscuela;
    CheckBox checkSistemas, checkCivil, checkIndustrial, checkRobotica,
            checkTecnologias, checkElectronica, checkGestionEm, checkNegocios, checkFinanciera;
    Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Permitir red en hilo principal (solo para pruebas)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Asignar vistas
        etNombre = findViewById(R.id.etNombre);
        etEdad = findViewById(R.id.etEdad);
        etCorreo = findViewById(R.id.etCorreo);
        etTelefono = findViewById(R.id.etTelefono);
        etOtro = findViewById(R.id.etOtro);

        radioGroupGenero = findViewById(R.id.radioGroupGenero);
        spinnerEscuela = findViewById(R.id.spinnerEscuela);

        checkSistemas = findViewById(R.id.check_sistemas);
        checkCivil = findViewById(R.id.check_civil);
        checkIndustrial = findViewById(R.id.check_industrial);
        checkRobotica = findViewById(R.id.check_robotica);
        checkTecnologias = findViewById(R.id.check_tecnologias);
        checkElectronica = findViewById(R.id.check_electronica);
        checkGestionEm = findViewById(R.id.check_gestionEm);
        checkNegocios = findViewById(R.id.check_negocios);
        checkFinanciera = findViewById(R.id.check_financiera);

        btnEnviar = findViewById(R.id.btnEnviar);

        // Spinner de ejemplo
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.escuelas_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEscuela.setAdapter(adapter);

        btnEnviar.setOnClickListener(view -> {
            try {
                // Obtener valores
                String nombre = etNombre.getText().toString().trim();
                String edad = etEdad.getText().toString().trim();
                String correo = etCorreo.getText().toString().trim();
                String telefono = etTelefono.getText().toString().trim();
                String otro = etOtro.getText().toString().trim();

                int selectedId = radioGroupGenero.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = findViewById(selectedId);
                String genero = selectedRadioButton != null ? selectedRadioButton.getText().toString() : "";

                String escuela = spinnerEscuela.getSelectedItem().toString();

                ArrayList<String> carreras = new ArrayList<>();
                if (checkSistemas.isChecked()) carreras.add("Sistemas");
                if (checkCivil.isChecked()) carreras.add("Civil");
                if (checkIndustrial.isChecked()) carreras.add("Industrial");
                if (checkRobotica.isChecked()) carreras.add("Robótica");
                if (checkTecnologias.isChecked()) carreras.add("Tecnologías");
                if (checkElectronica.isChecked()) carreras.add("Electrónica");
                if (checkGestionEm.isChecked()) carreras.add("Gestión Empresarial");
                if (checkNegocios.isChecked()) carreras.add("Negocios");
                if (checkFinanciera.isChecked()) carreras.add("Financiera");
                if (!otro.isEmpty()) carreras.add(otro);

                // VALIDACIONES
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

                if (escuela.equals("Selecciona tu escuela")) {
                    Toast.makeText(this, "Por favor selecciona una escuela.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Envío de datos
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
                    Toast.makeText(MainActivity.this, "Datos enviados", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Error en el envío: " + responseCode, Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
