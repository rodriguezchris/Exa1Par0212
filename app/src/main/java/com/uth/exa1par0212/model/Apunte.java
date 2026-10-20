package com.uth.exa1par0212.model;

import java.io.Serializable;

public class Apunte implements Serializable {
    private long id;
    private String asignatura;
    private String tema;
    private String fecha;
    private String descripcion;
    private String audio; // Ruta del archivo de audio

    public Apunte() {
    }

    public Apunte(long id, String asignatura, String tema, String fecha, String descripcion, String audio) {
        this.id = id;
        this.asignatura = asignatura;
        this.tema = tema;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.audio = audio;
    }

    public Apunte(String asignatura, String tema, String fecha, String descripcion, String audio) {
        this.asignatura = asignatura;
        this.tema = tema;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.audio = audio;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }
}
