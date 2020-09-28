package com.mikolaj.guitartuner.detection;

public class Note {

    private float frequency;
    private int cent;
    private int noteSteps; //odległość od A4
    private String note;

    public Note(float frequency) {
        this.frequency = frequency;
        this.cent = 0;
        this.noteSteps = 0;

    }

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    public int getCent() {
        return cent;
    }

    public void setCent(int cent) {
        this.cent = cent;
    }

    public int getNoteSteps() {
        return noteSteps;
    }

    public void setNoteSteps(int noteSteps) {
        this.noteSteps = noteSteps;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "note=" + note + " "+cent+" cents";

    }
}
