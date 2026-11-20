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

    Music musicIntro;
    Music tempoIntro;

    ArrayList<Beat> beats = new ArrayList<>();
    ArrayList<Beat> introBeats = new ArrayList<>();

    Music activeMusic;
    Music activeTempo;
    ArrayList<Beat> activeBeats;

    int currentBeatIndex = 0;
    float lastPosition = 0f;

    float resyncCooldown = 0f;

    boolean newBeat = false;

    public MusicManager(String musicP) {
        boolean musExists = Gdx.files.internal(musicP + "_mus.ogg").exists();
        boolean tempExists = Gdx.files.internal(musicP + "_temp.ogg").exists();
        boolean sheetExists = Gdx.files.internal(musicP + "_sheet.mid").exists();
        String musicFile = "Default";
        if (musExists && tempExists && sheetExists) musicFile = musicP;
        this.musicTrack = Gdx.audio.newMusic(Gdx.files.internal(musicFile + "_mus.ogg"));
        this.tempoTrack = Gdx.audio.newMusic(Gdx.files.internal(musicFile + "_temp.ogg"));
        this.musicTrack.setLooping(false);
        this.tempoTrack.setLooping(false);
        this.musicTrack.setOnCompletionListener(music -> {
            tempoTrack.stop();
            currentBeatIndex = 0;
            musicTrack.play();
            tempoTrack.play();
            this.resyncCooldown = 1f;
        });
        String introPrefix = musicFile + "_intro";
        boolean musIntroExists = Gdx.files.internal(introPrefix + "_mus.ogg").exists();
        boolean tempIntroExists = Gdx.files.internal(introPrefix + "_temp.ogg").exists();
        boolean sheetIntroExists = Gdx.files.internal(introPrefix + "_sheet.mid").exists();
        if (musIntroExists && tempIntroExists && sheetIntroExists) {
            this.musicIntro = Gdx.audio.newMusic(Gdx.files.internal(introPrefix + "_mus.ogg"));
            this.tempoIntro = Gdx.audio.newMusic(Gdx.files.internal(introPrefix + "_temp.ogg"));
            this.musicIntro.setLooping(false);
            this.tempoIntro.setLooping(false);
            this.activeMusic = musicIntro;
            this.activeTempo = tempoIntro;
            this.activeBeats = introBeats;
            this.musicIntro.setOnCompletionListener(music -> {
                this.tempoIntro.stop();
                this.activeMusic = musicTrack;
                this.activeTempo = tempoTrack;
                this.activeBeats = beats;
                this.currentBeatIndex = 0;
                this.resyncCooldown = 1f;
                activeMusic.play();
                activeTempo.play();
            });
            loadMidi(introPrefix, introBeats);
        } else {
            this.activeMusic = musicTrack;
            this.activeTempo = tempoTrack;
            this.activeBeats = beats;
        }
        loadMidi(musicFile, beats);
    }

    public void play() {
        this.activeMusic.play();
        this.activeTempo.play();
    }

    public void pause() {
        this.activeMusic.pause();
        this.activeTempo.pause();
    }

    public void stop() {
        this.activeMusic.stop();
        this.activeTempo.stop();
    }

    public void dispose() {
        this.musicTrack.dispose();
        this.tempoTrack.dispose();
        if (this.musicIntro != null) this.musicIntro.dispose();
        if (this.tempoIntro != null) this.tempoIntro.dispose();
    }

    public void setVolume(float volP, boolean tempoTrack) {
        if (tempoTrack) {
            this.tempoTrack.setVolume(volP);
            if (this.tempoIntro != null) this.tempoIntro.setVolume(volP);
        }
        else {
            this.musicTrack.setVolume(volP);
            if (this.musicIntro != null) this.musicIntro.setVolume(volP);
        }
    }

    public float getPosition() {
        return this.activeMusic.getPosition();
    }

    private void loadMidi(String musicP, ArrayList<Beat> beatsP) {
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
                        beatsP.add(new Beat(currentTime, isMesureStart));
                    }
                }
            }

            System.out.println("Beats chargés : " + beatsP.size());
        } catch (Exception e) {
            Gdx.app.error("MusicManager", "Erreur lors du chargement du MIDI", e);
        }
    }

    public void update(float delta) {
        if (resyncCooldown > 0) resyncCooldown -= delta;
        newBeat = false;
        float deSync = Math.abs(this.activeMusic.getPosition() - this.activeTempo.getPosition());

        if (activeMusic.isPlaying() && !activeTempo.isPlaying() && resyncCooldown <= 0f) {
            this.activeTempo.play();
            this.activeTempo.setPosition(this.activeMusic.getPosition());
            this.resyncCooldown = 1f;
        }

        if (deSync > 0.05f && resyncCooldown <= 0f) {
            this.activeTempo.setPosition(this.activeMusic.getPosition());
            resyncCooldown = 1f;
        }
        if (activeBeats.isEmpty()) return;
        float pos = getPosition();
        if (pos < lastPosition) {
            currentBeatIndex = 0;
            newBeat = true;
        }
        while ((currentBeatIndex + 1) < activeBeats.size() && pos >= activeBeats.get(currentBeatIndex + 1).time()) {
            currentBeatIndex ++;
            newBeat = true;
        }
        lastPosition = pos;
    }

    public float getCurrentBeatDuration() {
        if ((currentBeatIndex + 1) < activeBeats.size()) {
            return (activeBeats.get(currentBeatIndex + 1).time() - activeBeats.get(currentBeatIndex).time());
        }
        if (activeBeats.size() < 2) return 0.5f;
        return (activeBeats.get(currentBeatIndex).time() - activeBeats.get(currentBeatIndex - 1).time());
    }

    public float getPulse() {
        if (activeBeats.isEmpty()) return 0f;
        float timeSpent = getPosition() - activeBeats.get(currentBeatIndex).time();
        float pulse = timeSpent / getCurrentBeatDuration();
        return Math.max(0f, Math.min(1f, pulse));
    }

    public boolean isMesureStart() {
        if (!activeBeats.isEmpty()) return activeBeats.get(currentBeatIndex).isMeasureStart();
        return false;
    }

    public boolean isNewBeat() {
        return newBeat;
    }
}
