package com.uth.exa1par0212.controller;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.uth.exa1par0212.dao.ApunteDAO;
import com.uth.exa1par0212.model.Apunte;

import java.io.IOException;
import java.util.List;

public class ApuntesController {
    private static final String TAG = "ApuntesController";
    private ApunteDAO apunteDAO;
    private MediaPlayer mediaPlayer;

    public ApuntesController(Context context) {
        apunteDAO = new ApunteDAO(context);
        apunteDAO.open();
    }

    public List<Apunte> obtenerTodos() {
        return apunteDAO.getAllApuntes();
    }

    public List<Apunte> filtrarPorAsignatura(String asignatura) {
        return apunteDAO.getApuntesByAsignatura(asignatura);
    }

    public void eliminarApunte(long id) {
        apunteDAO.deleteApunte(id);
    }

    public boolean actualizarApunte(Apunte apunte) {
        return apunteDAO.updateApunte(apunte) > 0;
    }

    public void reproducirAudio(String audioPath) {
        if (audioPath == null || audioPath.isEmpty()) return;
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(audioPath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "Error al reproducir audio de apunte", e);
        }
    }

    public void detenerAudio() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void cerrar() {
        detenerAudio();
        if (apunteDAO != null) {
            apunteDAO.close();
        }
    }
}
