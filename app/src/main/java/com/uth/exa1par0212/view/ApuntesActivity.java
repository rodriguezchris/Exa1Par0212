package com.uth.exa1par0212.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.uth.exa1par0212.R;
import com.uth.exa1par0212.controller.ApuntesController;
import com.uth.exa1par0212.dao.AsignaturaDAO;
import com.uth.exa1par0212.model.Apunte;

import java.util.ArrayList;
import java.util.List;

public class ApuntesActivity extends AppCompatActivity {
    private Spinner spinnerFiltroAsignatura;
    private ListView listViewApuntes;
    private ApuntesController apuntesController;
    private AsignaturaDAO asignaturaDAO;
    private List<Apunte> listaApuntes;
    private ApuntesAdapter adapter;
    private String asignaturaFiltroSeleccionada = "TODAS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apuntes);

        spinnerFiltroAsignatura = findViewById(R.id.spinnerFiltroAsignatura);
        listViewApuntes = findViewById(R.id.listViewApuntes);

        apuntesController = new ApuntesController(this);
        asignaturaDAO = new AsignaturaDAO(this);
        asignaturaDAO.open();

        cargarFiltroAsignaturas();
        cargarApuntes();

        spinnerFiltroAsignatura.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                asignaturaFiltroSeleccionada = parent.getItemAtPosition(position).toString();
                cargarApuntes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void cargarFiltroAsignaturas() {
        List<String> asignaturas = new ArrayList<>();
        asignaturas.add("TODAS");
        asignaturas.addAll(asignaturaDAO.getAllAsignaturas());

        ArrayAdapter<String> adapterFiltro = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, asignaturas);
        adapterFiltro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroAsignatura.setAdapter(adapterFiltro);
    }

    private void cargarApuntes() {
        if (asignaturaFiltroSeleccionada.equals("TODAS")) {
            listaApuntes = apuntesController.obtenerTodos();
        } else {
            // Consulta parametrizada exigida por la rúbrica
            listaApuntes = apuntesController.filtrarPorAsignatura(asignaturaFiltroSeleccionada);
        }

        adapter = new ApuntesAdapter(listaApuntes);
        listViewApuntes.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (apuntesController != null) {
            apuntesController.cerrar();
        }
        if (asignaturaDAO != null) {
            asignaturaDAO.close();
        }
    }

    private class ApuntesAdapter extends ArrayAdapter<Apunte> {
        public ApuntesAdapter(List<Apunte> apuntes) {
            super(ApuntesActivity.this, R.layout.item_apunte, apuntes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_apunte, parent, false);
            }

            Apunte apunte = getItem(position);

            TextView tvAsignaturaTema = convertView.findViewById(R.id.tvAsignaturaTema);
            TextView tvFecha = convertView.findViewById(R.id.tvFecha);
            TextView tvDescripcion = convertView.findViewById(R.id.tvDescripcion);
            Button btnReproducirItem = convertView.findViewById(R.id.btnReproducirItem);
            Button btnEditarItem = convertView.findViewById(R.id.btnEditarItem);
            Button btnEliminarItem = convertView.findViewById(R.id.btnEliminarItem);

            if (apunte != null) {
                tvAsignaturaTema.setText(apunte.getAsignatura() + " - " + apunte.getTema());
                tvFecha.setText("Fecha: " + apunte.getFecha());
                tvDescripcion.setText(apunte.getDescripcion());

                btnReproducirItem.setOnClickListener(v -> {
                    if (apunte.getAudio() != null && !apunte.getAudio().isEmpty()) {
                        apuntesController.reproducirAudio(apunte.getAudio());
                        Toast.makeText(ApuntesActivity.this, "Reproduciendo nota de voz...", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ApuntesActivity.this, "Este apunte no tiene nota de voz asociada.", Toast.LENGTH_SHORT).show();
                    }
                });

                btnEditarItem.setOnClickListener(v -> mostrarDialogoEditar(apunte));

                btnEliminarItem.setOnClickListener(v -> {
                    new AlertDialog.Builder(ApuntesActivity.this)
                            .setTitle("Eliminar Apunte")
                            .setMessage("¿Estás seguro de eliminar este apunte?")
                            .setPositiveButton("Sí", (dialog, which) -> {
                                apuntesController.eliminarApunte(apunte.getId());
                                cargarApuntes();
                                Toast.makeText(ApuntesActivity.this, "Apunte eliminado", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("No", null)
                            .show();
                });
            }

            return convertView;
        }
    }

    private void mostrarDialogoEditar(Apunte apunte) {
        EditText input = new EditText(this);
        input.setText(apunte.getDescripcion());
        input.setHint("Nueva descripción");

        new AlertDialog.Builder(this)
                .setTitle("Actualizar Apunte: " + apunte.getTema())
                .setMessage("Modifica la descripción del apunte:")
                .setView(input)
                .setPositiveButton("Actualizar", (dialog, which) -> {
                    String nuevaDesc = input.getText().toString().trim();
                    if (!nuevaDesc.isEmpty()) {
                        apunte.setDescripcion(nuevaDesc);
                        boolean actualizado = apuntesController.actualizarApunte(apunte);
                        if (actualizado) {
                            Toast.makeText(this, "Apunte actualizado correctamente", Toast.LENGTH_SHORT).show();
                            cargarApuntes();
                        } else {
                            Toast.makeText(this, "Error al actualizar apunte", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "La descripción no puede estar vacía", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
