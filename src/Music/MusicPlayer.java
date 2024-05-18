package Music;

import javax.sound.midi.*;

public class MusicPlayer {
    public final int INSTRUMENT; //79
    Synthesizer midiSynth;
    Instrument[] instruments;
    MidiChannel channel;

    public MusicPlayer(int instrumentIndex) {
        INSTRUMENT = instrumentIndex;
        try {
            midiSynth = MidiSystem.getSynthesizer();
            instruments = midiSynth.getDefaultSoundbank().getInstruments();
            MidiChannel[] channels = midiSynth.getChannels();
            for (final MidiChannel midiChannel : channels) {
                if (midiChannel != null) {
                    channel = midiChannel;
                    break;
                }
            }
            midiSynth.open();
            final Instrument instrument = instruments[INSTRUMENT];
            if (!midiSynth.loadInstrument(instrument)) { //load an instrument
                System.out.println("Couldn't load");
            }
            channel.programChange(instrument.getPatch().getProgram());
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        midiSynth.close();
    }

    public void playNote(int note, int time) {
        startNote(note);
        new Thread(() -> turnOffNoteAfterTime(note, time)).start();
    }

    public void playNote(int note, int time, boolean closeAfterNote) {
        if (closeAfterNote) {
            startNote(note);
            new Thread(() -> {
                turnOffNoteAfterTime(note, time);
                close();
            }).start();
        } else {
            playNote(note, time);
        }
    }

    private void startNote(final int note) {
        channel.noteOn(note, 100);
    }

    private void turnOffNoteAfterTime(final int note, final int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        channel.noteOff(note);
    }

    public static void main(String[] args) {
        MusicPlayer musicPlayer = new MusicPlayer(79);
        musicPlayer.playNote(60, 1_000);
        musicPlayer.playNote(63, 1_000);
    }

}
