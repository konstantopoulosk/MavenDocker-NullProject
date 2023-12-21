package com.nullteam;
import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
public class MidiPlayer {
    static Sequencer sequencer = null;
    MidiPlayer(String file) {
        playFile(file);
    }
    public void playFile(String file) {
        File midiFile = new File(file);
        try {
            if (sequencer == null) {
                sequencer = MidiSystem.getSequencer();
            } else {
                end();
                sequencer.setSequence(MidiSystem.getSequence(midiFile));
                sequencer.open();
                sequencer.start();
            }
        } catch (MidiUnavailableException e) {
            System.err.println("Midi device unavailable:" + e);
        } catch(InvalidMidiDataException e) {
            System.err.println("Invalid MIDI data:" + e);
        } catch(IOException e) {
            System.err.println("I/O error:" + e);
        }
    }
    public boolean isPlaying() {
        return sequencer.isRunning();
    }
    public void end() {
        sequencer.stop();
        sequencer.close();
        sequencer = null;
    }
}
