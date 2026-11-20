package com.dlg.danceofbaldes;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.Gdx;

import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOn;
import com.leff.midi.MidiTrack;
import com.leff.midi.MidiFile;

import java.util.Comparator;
import java.util.ArrayList;
import java.io.InputStream;

public class MusicManager {
    Music musicTrack;
    Music tempoTrack;

    ArrayList<Beat> beats = new ArrayList<>();
    int currentBeatIndex = 0;
    float lastPosition = 0f;

    boolean musExists = false;
    boolean tempExists = false;
    boolean sheetExists = false;

    String musicFile = "Default";

    boolean newBeat = false;

    public MusicManager(String musicP) {
        musExists = Gdx.files.internal(musicP + "_mus.ogg").exists();
        tempExists = Gdx.files.internal(musicP + "_temp.ogg").exists();
        sheetExists = Gdx.files.internal(musicP + "_sheet.mid").exists();
        if (musExists && tempExists && sheetExists) musicFile = musicP;
        this.musicTrack = Gdx.audio.newMusic(Gdx.files.internal(musicFile + "_mus.ogg"));
        this.tempoTrack = Gdx.audio.newMusic(Gdx.files.internal(musicFile + "_temp.ogg"));
        this.musicTrack.setLooping(true);
        this.tempoTrack.setLooping(true);
        loadMidi(musicFile);
    }

    public void play() {
        this.musicTrack.play();
        this.tempoTrack.play();
    }

    public void pause() {
        this.musicTrack.pause();
        this.tempoTrack.pause();
    }

    public void stop() {
        this.musicTrack.stop();
        this.tempoTrack.stop();
    }

    public void dispose() {
        this.musicTrack.dispose();
        this.tempoTrack.dispose();
    }

    public void setVolume(float volP, boolean tempoTrack) {
        if (tempoTrack) this.tempoTrack.setVolume(volP);
        else this.musicTrack.setVolume(volP);
    }

    public float getPosition() {
        return this.musicTrack.getPosition();
    }

    private void loadMidi(String musicP) {
        try {
            InputStream midiFile = Gdx.files.internal(musicP + "_sheet.mid").read();
            MidiFile midiTrack = new MidiFile(midiFile);
            int midiRes = midiTrack.getResolution();

            ArrayList<MidiEvent> allEvents = new ArrayList<MidiEvent>();
            for (MidiTrack track : midiTrack.getTracks()) {
                allEvents.addAll(track.getEvents());
            }
            allEvents.sort(Comparator.comparingLong(MidiEvent::getTick));

            float currentBpm = 120f;
            long lastTick = 0;
            float currentTime = 0f;

            for (MidiEvent event : allEvents) {
                float deltaTick = event.getTick() - lastTick;
                if (deltaTick > 0) currentTime += (deltaTick / (float) midiRes) * (60f / currentBpm);
                lastTick = event.getTick();

                if (event instanceof Tempo tempoEvent) {
                    currentBpm = tempoEvent.getBpm();
                }

                if (event instanceof NoteOn noteEvent) {
                    if (noteEvent.getVelocity() > 0) {
                        boolean isMesureStart = (noteEvent.getNoteValue() == 72);
                        beats.add(new Beat(currentTime, isMesureStart));
                    }
                }
            }

            System.out.println("Beats chargés : " + beats.size());
        } catch (Exception e) {
            Gdx.app.error("MusicManager", "Erreur lors du chargement du MIDI", e);
        }
    }

    public void update() {
        newBeat = false;
        float deSync = Math.abs(this.musicTrack.getPosition() - this.tempoTrack.getPosition());
        if (deSync > 0.05f) this.tempoTrack.setPosition(this.musicTrack.getPosition());
        if (beats.isEmpty()) return;
        float pos = getPosition();
        if (pos < lastPosition) {
            currentBeatIndex = 0;
            newBeat = true;
        }
        while ((currentBeatIndex + 1) < beats.size() && pos >= beats.get(currentBeatIndex + 1).time()) {
            currentBeatIndex ++;
            newBeat = true;
        }
        lastPosition = pos;
    }

    public float getCurrentBeatDuration() {
        if ((currentBeatIndex + 1) < beats.size()) {
            return (beats.get(currentBeatIndex + 1).time() - beats.get(currentBeatIndex).time());
        }
        return (beats.get(currentBeatIndex).time() - beats.get(currentBeatIndex - 1).time());
    }

    public float getPulse() {
        if (beats.isEmpty()) return 0f;
        float timeSpent = getPosition() - beats.get(currentBeatIndex).time();
        float pulse = timeSpent / getCurrentBeatDuration();
        return Math.max(0f, Math.min(1f, pulse));
    }

    public boolean isMesureStart() {
        if (!beats.isEmpty()) return beats.get(currentBeatIndex).isMeasureStart();
        return false;
    }

    public boolean isNewBeat() {
        return newBeat;
    }
}
