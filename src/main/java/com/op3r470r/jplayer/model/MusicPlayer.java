package com.op3r470r.jplayer.model;

import com.op3r470r.jplayer.utils.Music;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

class MusicPlayer {
    private final DoubleProperty volume = new SimpleDoubleProperty();
    private final DoubleProperty currentTime = new SimpleDoubleProperty();
    private final ObjectProperty<JPlayerModel.Status> status ;
    private final BooleanProperty mute = new SimpleBooleanProperty();

    private MediaPlayer mediaPlayer;
    private ChangeListener<Duration> durationChangeListener;
    private ChangeListener<MediaPlayer.Status> statusChangeListener;
    private final Runnable onEndOfMedia;

    MusicPlayer(ObjectProperty<JPlayerModel.Status> status, Runnable onEndOfMedia) {
        this.status = status;
        this.onEndOfMedia = onEndOfMedia;
    }

    DoubleProperty volumeProperty() {
        return volume;
    }

    DoubleProperty currentTimeProperty() {
        return currentTime;
    }

    ObjectProperty<JPlayerModel.Status> statusProperty() {
        return status;
    }

    BooleanProperty muteProperty() {
        return mute;
    }


    void fillMusic(Music music) {
        if(status.get() == JPlayerModel.Status.PLAYING ||
                status.get() == JPlayerModel.Status.PAUSED) {
            dispose();
        }
        mediaPlayer = new MediaPlayer(new Media(music.getFile().toURI().toString()));
        mediaPlayer.volumeProperty().bindBidirectional(volumeProperty());
        mediaPlayer.muteProperty().bindBidirectional(muteProperty());
        durationChangeListener = (obs, ov, nv) -> currentTimeProperty().set(nv.toSeconds());
        mediaPlayer.setOnReady(() -> {
            mediaPlayer.seek(Duration.ZERO);
            mediaPlayer.currentTimeProperty().addListener(durationChangeListener);
        });
        statusChangeListener = (obs, oldStatus, newStatus) -> {
            switch (newStatus) {
                case PLAYING:
                    status.set(JPlayerModel.Status.PLAYING);
                    break;
                case PAUSED:
                    status.set(JPlayerModel.Status.PAUSED);
                    break;
            }
        };
        mediaPlayer.statusProperty().addListener(statusChangeListener);
        mediaPlayer.setOnEndOfMedia(onEndOfMedia);
    }

    void dispose() {
        mediaPlayer.volumeProperty().unbindBidirectional(volumeProperty());
        volumeProperty().unbindBidirectional(mediaPlayer.volumeProperty());
        mediaPlayer.muteProperty().unbindBidirectional(muteProperty());
        muteProperty().unbindBidirectional(mediaPlayer.muteProperty());
        mediaPlayer.currentTimeProperty().removeListener(durationChangeListener);
        mediaPlayer.statusProperty().removeListener(statusChangeListener);
        mediaPlayer.dispose();
        currentTime.set(0.0);
    }

    void play() {
        mediaPlayer.play();
    }

    void pause() {
        mediaPlayer.pause();
    }

    void seek(double time) {
        mediaPlayer.seek(Duration.seconds(time));
    }

}
