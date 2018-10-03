package it.unisannio.www.treasurehunt;

import java.io.Serializable;

public class Checkpoint implements Serializable{
    private int idCheckpoint, idRun;
    private double latitude, longitude;
    private String question, answer;

    public Checkpoint(int idCheckpoint, int idRun, double latitude, double longitude, String question, String answer) {

        this.idCheckpoint = idCheckpoint;
        this.idRun = idRun;
        this.latitude = latitude;
        this.longitude = longitude;
        this.question = question;
        this.answer = answer;
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
        return idRun;
    }

    public void setIdRun(int idRun) {
        this.idRun = idRun;
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }


}

