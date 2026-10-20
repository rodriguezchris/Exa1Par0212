package com.uth.exa1par0212.controller;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class RegistroController {
    private static final String TAG = "RegistroController";
    private Context context;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String currentAudioPath;

    // Regex para fecha: AAAA-MM-DD
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    // Regex para tema sin símbolos (letras, números, espacios y acentos)
    private static final Pattern THEME_PATTERN = Pattern.compile("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s]+$");

    public RegistroController(Context context) {
        this.context = context;
    }

    public boolean validateDate(String date) {
        if (date == null || date.isEmpty()) return false;
        return DATE_PATTERN.matcher(date).matches();
    }

    public boolean validateTheme(String theme) {
        if (theme == null || theme.isEmpty()) return false;
        return THEME_PATTERN.matcher(theme).matches();
    }

    public String startRecording() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File audioDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (audioDir != null && !audioDir.exists()) {
            audioDir.mkdirs();
        }
        try {
            File audioFile = File.createTempFile("AUDIO_" + timeStamp + "_", ".3gp", audioDir);
            currentAudioPath = audioFile.getAbsolutePath();

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(currentAudioPath);
            mediaRecorder.prepare();
            mediaRecorder.start();
            Log.d(TAG, "Grabación iniciada en: " + currentAudioPath);
            return currentAudioPath;
        } catch (Exception e) {
            Log.e(TAG, "Error al iniciar grabación", e);
            if (mediaRecorder != null) {
                try {
                    mediaRecorder.release();
                } catch (Exception ignored) {
                }
                mediaRecorder = null;
            }
            return null;
        }
    }

    public void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
            } catch (Exception e) {
                Log.e(TAG, "Error al detener grabación", e);
            }
            mediaRecorder = null;
        }
    }

    public void playAudio(String audioPath) {
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
            Log.e(TAG, "Error al reproducir audio", e);
        }
    }

    public void stopPlayback() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public String getCurrentAudioPath() {
        return currentAudioPath;
    }

    public void setCurrentAudioPath(String path) {
        this.currentAudioPath = path;
    }
}
