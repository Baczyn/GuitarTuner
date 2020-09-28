package com.mikolaj.guitartuner.detection;

public class FrequencyConverter {
    private final static float NOTE_STEP = 1.059463094f;  //sqrt(2,12) // o taka wartosc oddalone są dźwięki
    private final static float CENT_STEP = 1.000577789f;  //sqrt(2,1200) // o taka wartosc oddalone są centy - ogległość między A a A# to 50 centów
    private final static float A4 = 440.0f;
    private final static int A4_idx = 57;

    private final static String[] notes = {"C0", "C#0", "D0", "D#0", "E0", "F0", "F#0", "G0", "G#0", "A0", "A#0", "B0",
            "C1", "C#1", "D1", "D#1", "E1", "F1", "F#1", "G1", "G#1", "A1", "A#1", "B1",
            "C2", "C#2", "D2", "D#2", "E2", "F2", "F#2", "G2", "G#2", "A2", "A#2", "B2",
            "C3", "C#3", "D3", "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3", "B3",
            "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4",
            "C5", "C#5", "D5", "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5", "A#5", "B5",
            "C6", "C#6", "D6", "D#6", "E6", "F6", "F#6", "G6", "G#6", "A6", "A#6", "B6",
            "C7", "C#7", "D7", "D#7", "E7", "F7", "F#7", "G7", "G#7", "A7", "A#7", "B7",
            "C8", "C#8", "D8", "D#8", "E8", "F8", "F#8", "G8", "G#8", "A8", "A#8", "B8",
            "C9", "C#9", "D9", "D#9", "E9", "F9", "F#9", "G9", "G#9", "A9", "A#9", "B9"};


    public int getNote(float fEstimate) {
        Note note = new Note(A4);
        setNoteSteps(fEstimate, note);
        setCentSteps(fEstimate, note);
        note.setNote(notes[A4_idx + note.getNoteSteps()]);

        return note.getCent();
    }

    private void setNoteSteps(float fEstimate, Note note) {
        int stepNoteCounter = 0;
        float fNote = note.getFrequency();

        if (fEstimate >= fNote) {
            while (fEstimate >= NOTE_STEP * fNote) {
                fNote *= NOTE_STEP;
                stepNoteCounter++;
            }
        } else {
            while (fEstimate <= fNote / NOTE_STEP) {
                fNote /= NOTE_STEP;
                stepNoteCounter--;
            }
        }

        note.setFrequency(fNote);
        note.setNoteSteps(stepNoteCounter);
    }

    private void setCentSteps(float fEstimate, Note note) {
        int stepCentCounter = note.getCent();
        float fNote = note.getFrequency();

        if (fEstimate >= fNote) {
            while (fEstimate > CENT_STEP * fNote) {
                fNote *= CENT_STEP;
                stepCentCounter++;
            }
            if (stepCentCounter > 50) {
                int stepNoteCounter = note.getNoteSteps() + 1;
                note.setNoteSteps(stepNoteCounter);
                stepCentCounter -= 100 - 1;
            }
        } else {

            while (fEstimate < fNote / CENT_STEP) {
                fNote /= CENT_STEP;
                stepCentCounter++;
            }
            if (stepCentCounter >= 50) {
                int stepNoteCounter = note.getNoteSteps() - 1;
                note.setNoteSteps(stepNoteCounter);
                stepCentCounter = 100 - stepCentCounter;
            }
        }

        note.setCent(stepCentCounter);
    }


}
