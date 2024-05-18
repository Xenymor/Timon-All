package Music;

import javax.sound.midi.*;

public class Music {

    public static final int INSTRUMENT = 79;

    public static void main(String[] args) {
        try {
            /* Create a new Sythesizer and open it. Most of
             * the methods you will want to use to expand on this
             * example can be found in the Java documentation here:
             * https://docs.oracle.com/javase/7/docs/api/javax/sound/midi/Synthesizer.html
             */
            Synthesizer midiSynth = MidiSystem.getSynthesizer();
            midiSynth.open();

            //get and load default instrument and channel lists
            Instrument[] instr = midiSynth.getDefaultSoundbank().getInstruments();
            /*for (final Instrument instrument : instr) {
                System.out.println(instrument.toString());
            }*/
            MidiChannel[] mChannels = midiSynth.getChannels();

            if (!midiSynth.loadInstrument(instr[INSTRUMENT])) { //load an instrument
                System.out.println("Couldn't load");
            }

            mChannels[0].programChange(instr[INSTRUMENT].getPatch().getProgram());

            for (int i = 60; i < 100; i++) {
                mChannels[0].noteOn(i, 100);//On channel 0, play note number 60 with velocity 100
                try {
                    Thread.sleep(1000); // wait time in milliseconds to control duration
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mChannels[0].noteOff(i);//turn off the note
            }

            midiSynth.close();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

}
