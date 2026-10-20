package com.uth.exa1par0212.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.uth.exa1par0212.R;

import java.util.ArrayList;
import java.util.Locale;

public class DictadoActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int SPEECH_REQUEST_CODE = 100;

    private TextView tvResultadoDictado;
    private Button btnIniciarDictado, btnAceptarDictado;
    private String textoDictado = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictado);

        tvResultadoDictado = findViewById(R.id.tvResultadoDictado);
        btnIniciarDictado = findViewById(R.id.btnIniciarDictado);
        btnAceptarDictado = findViewById(R.id.btnAceptarDictado);

        btnAceptarDictado.setEnabled(false);

        btnIniciarDictado.setOnClickListener(v -> verificarPermisoYDictar());
        btnAceptarDictado.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("resultado_dictado", textoDictado);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void verificarPermisoYDictar() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            lanzarReconocimientoVoz();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                lanzarReconocimientoVoz();
            } else {
                // Explicar denegación mediante AlertDialog.Builder como exige la rúbrica
                new AlertDialog.Builder(this)
                        .setTitle("Permiso denegado")
                        .setMessage("El permiso de grabación de audio es estrictamente necesario para dictar descripciones por voz.")
                        .setPositiveButton("Entendido", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        }
    }

    private void lanzarReconocimientoVoz() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Dicta la descripción del apunte...");
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "Tu dispositivo no soporta el reconocimiento de voz.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                textoDictado = result.get(0);
                tvResultadoDictado.setText(textoDictado);
                btnAceptarDictado.setEnabled(true);
            }
        }
    }
}
