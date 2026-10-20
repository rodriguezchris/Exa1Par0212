package com.uth.exa1par0212;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.uth.exa1par0212.controller.RegistroController;
import com.uth.exa1par0212.dao.ApunteDAO;
import com.uth.exa1par0212.dao.AsignaturaDAO;
import com.uth.exa1par0212.model.Apunte;
import com.uth.exa1par0212.view.ApuntesActivity;
import com.uth.exa1par0212.view.DictadoActivity;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int DICTADO_REQUEST_CODE = 1001;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 300;

    private TextView tvTimer;
    private Spinner spinnerAsignatura;
    private Button btnAgregarAsignatura;
    private EditText etTema, etFecha, etDescripcion;
    private Button btnDictar, btnGrabar, btnReproducirGrabacion, btnSalvarApunte, btnVerApuntes;

    private RegistroController registroController;
    private AsignaturaDAO asignaturaDAO;
    private ApunteDAO apunteDAO;
    private boolean isRecording = false;

    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private long startTime = 0L;
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registroController = new RegistroController(this);
        asignaturaDAO = new AsignaturaDAO(this);
        asignaturaDAO.open();
        apunteDAO = new ApunteDAO(this);
        apunteDAO.open();

        tvTimer = findViewById(R.id.tvTimer);
        spinnerAsignatura = findViewById(R.id.spinnerAsignatura);
        btnAgregarAsignatura = findViewById(R.id.btnAgregarAsignatura);
        etTema = findViewById(R.id.etTema);
        etFecha = findViewById(R.id.etFecha);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnDictar = findViewById(R.id.btnDictar);
        btnGrabar = findViewById(R.id.btnGrabar);
        btnReproducirGrabacion = findViewById(R.id.btnReproducirGrabacion);
        btnSalvarApunte = findViewById(R.id.btnSalvarApunte);
        btnVerApuntes = findViewById(R.id.btnVerApuntes);

        cargarAsignaturasSpinner();

        // 1 pto extra: agregar asignaturas
        btnAgregarAsignatura.setOnClickListener(v -> mostrarDialogoAgregarAsignatura());

        btnDictar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DictadoActivity.class);
            startActivityForResult(intent, DICTADO_REQUEST_CODE);
        });

        btnGrabar.setOnClickListener(v -> verificarPermisoYGrabar());

        btnReproducirGrabacion.setOnClickListener(v -> {
            if (registroController.getCurrentAudioPath() != null) {
                registroController.playAudio(registroController.getCurrentAudioPath());
                Toast.makeText(this, "Reproduciendo nota de voz...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No hay ninguna nota de voz grabada aún.", Toast.LENGTH_SHORT).show();
            }
        });

        btnSalvarApunte.setOnClickListener(v -> validarYGuardarApunte());

        btnVerApuntes.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ApuntesActivity.class);
            startActivity(intent);
        });
    }

    private void verificarPermisoYGrabar() {
        if (!isRecording) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_AUDIO_PERMISSION);
                return;
            }
        }
        ejecutarGrabacion();
    }

    private void ejecutarGrabacion() {
        if (!isRecording) {
            String path = registroController.startRecording();
            if (path != null) {
                isRecording = true;
                btnGrabar.setText("⏹ Detener Grabación");
                startTime = System.currentTimeMillis();
                timerHandler.postDelayed(timerRunnable, 0);
                Toast.makeText(this, "Grabando nota de voz...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al iniciar grabación. Verifique el permiso de micrófono.", Toast.LENGTH_SHORT).show();
            }
        } else {
            registroController.stopRecording();
            isRecording = false;
            btnGrabar.setText("🔴 Grabar");
            timerHandler.removeCallbacks(timerRunnable);
            tvTimer.setText("00:00");
            Toast.makeText(this, "Nota de voz guardada temporalmente", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ejecutarGrabacion();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Permiso denegado")
                        .setMessage("El permiso de grabación de audio es necesario para grabar notas de voz asociadas al apunte.")
                        .setPositiveButton("Entendido", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        }
    }

    private void cargarAsignaturasSpinner() {
        List<String> asignaturas = asignaturaDAO.getAllAsignaturas();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, asignaturas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAsignatura.setAdapter(adapter);
    }

    private void mostrarDialogoAgregarAsignatura() {
        EditText input = new EditText(this);
        input.setHint("Nombre de la asignatura");

        new AlertDialog.Builder(this)
                .setTitle("Agregar Nueva Asignatura")
                .setMessage("Ingrese el nombre de la nueva asignatura (1 pto extra):")
                .setView(input)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nuevaAsignatura = input.getText().toString().trim();
                    if (!nuevaAsignatura.isEmpty()) {
                        boolean insertado = asignaturaDAO.insertAsignatura(nuevaAsignatura);
                        if (insertado) {
                            Toast.makeText(this, "Asignatura agregada exitosamente", Toast.LENGTH_SHORT).show();
                            cargarAsignaturasSpinner();
                        } else {
                            Toast.makeText(this, "La asignatura ya existe o hubo un error", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void validarYGuardarApunte() {
        String asignatura = spinnerAsignatura.getSelectedItem() != null ? spinnerAsignatura.getSelectedItem().toString() : "";
        String tema = etTema.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String audioPath = registroController.getCurrentAudioPath();

        // Validar campos obligatorios
        if (tema.isEmpty() || fecha.isEmpty() || descripcion.isEmpty()) {
            mostrarAlerta("Campos obligatorios", "Debe completar el tema, la fecha y la descripción.");
            return;
        }

        // Validar audio obligatorio
        if (audioPath == null || audioPath.isEmpty()) {
            mostrarAlerta("Nota de voz requerida", "Debe grabar una nota de voz antes de salvar el apunte.");
            return;
        }

        // Validar tema mediante expresión regular (sin símbolos)
        if (!registroController.validateTheme(tema)) {
            mostrarAlerta("Tema inválido", "El tema no debe contener símbolos especiales (solo letras, números y espacios).");
            return;
        }

        // Validar fecha mediante expresión regular (AAAA-MM-DD)
        if (!registroController.validateDate(fecha)) {
            mostrarAlerta("Fecha inválida", "La fecha debe tener estrictamente el formato AAAA-MM-DD (Ej. 2026-10-17).");
            return;
        }

        // Guardar en SQLite vía DAO
        Apunte apunte = new Apunte(asignatura, tema, fecha, descripcion, audioPath);
        long id = apunteDAO.insertApunte(apunte);

        if (id != -1) {
            Toast.makeText(this, "¡Apunte guardado exitosamente!", Toast.LENGTH_LONG).show();
            limpiarFormulario();
        } else {
            Toast.makeText(this, "Error al guardar el apunte en la base de datos.", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void limpiarFormulario() {
        etTema.setText("");
        etFecha.setText("");
        etDescripcion.setText("");
        registroController.setCurrentAudioPath(null);
        btnGrabar.setText("🔴 Grabar");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DICTADO_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String textoDictado = data.getStringExtra("resultado_dictado");
            if (textoDictado != null) {
                etDescripcion.setText(textoDictado);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerHandler != null && timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
        if (registroController != null) {
            registroController.stopPlayback();
        }
        if (asignaturaDAO != null) {
            asignaturaDAO.close();
        }
        if (apunteDAO != null) {
            apunteDAO.close();
        }
    }
}
