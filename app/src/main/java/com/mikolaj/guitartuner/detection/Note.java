package com.mikolaj.guitartuner.detection;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Note)) return false;
        Note note1 = (Note) o;
        return Float.compare(note1.frequency, frequency) == 0 &&
                cent == note1.cent &&
                noteSteps == note1.noteSteps &&
                note.equals(note1.note);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(frequency, cent, noteSteps, note);
    }
}
