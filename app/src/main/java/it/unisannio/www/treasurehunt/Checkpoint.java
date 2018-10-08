package it.unisannio.www.treasurehunt;

import java.io.Serializable;

public class Checkpoint implements Serializable{
    private int idCheckpoint, idPercorso;
    private double latitude, longitude;
    private String question;

    public Checkpoint(int idPercorso, int idCheckpoint, double latitude, double longitude, String question) {
        this.idPercorso = idPercorso;
        this.idCheckpoint = idCheckpoint;
        this.latitude = latitude;
        this.longitude = longitude;
        this.question = question;
    }

    public Checkpoint(){

    }

    public int getIdCheckpoint() {
        return idCheckpoint;
    }

    public void setIdCheckpoint(int idCheckpoint) {
        this.idCheckpoint = idCheckpoint;
    }

    public int getIdRun() {
        return idPercorso;
    }

    public void setIdRun(int idRun) {
        this.idPercorso = idRun;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}

