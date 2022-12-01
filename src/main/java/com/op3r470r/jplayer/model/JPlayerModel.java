package com.op3r470r.jplayer.model;

import com.op3r470r.jplayer.utils.Music;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.io.File;
import java.util.List;

public class JPlayerModel {

    private static JPlayerModel instance;

    private final ObjectProperty<Music> selectedMusic = new SimpleObjectProperty<>();
    private final ObjectProperty<ObservableList<Music>> musicList =
            new SimpleObjectProperty<>(FXCollections.observableArrayList());
    private final IntegerProperty selectedIndex = new SimpleIntegerProperty();
    private final StringProperty selectedMusicName = new SimpleStringProperty();
    private final StringProperty selectedArtistName = new SimpleStringProperty();
    private final ObjectProperty<Image> selectedAlbumImage =
            new SimpleObjectProperty<>(new Image("/com.op3r470r.jplayer/view/images/logoCircle.png"));
    private final DoubleProperty selectedMusicLength = new SimpleDoubleProperty();
    private final MusicPlayer musicPlayer;

    private final DoubleProperty volume;
    private final DoubleProperty currentTime;
    private final ObjectProperty<Status> status = new SimpleObjectProperty<>(Status.EMPTY);
    private final BooleanProperty mute;

    private Runnable onEndOfMedia;

    public static synchronized JPlayerModel getInstance() {
        if(instance == null) {
            instance = new JPlayerModel();
        }
        return instance;
    }

    public JPlayerModel() {
        onEndOfMedia = this::playNextMusic;
        musicPlayer = new MusicPlayer(status, onEndOfMedia);
        volume = musicPlayer.volumeProperty();
        currentTime = musicPlayer.currentTimeProperty();
        mute = musicPlayer.muteProperty();
    }

    public ObjectProperty<Music> selectedMusicProperty() {
        return selectedMusic;
    }


    public ObjectProperty<ObservableList<Music>> getMusicList() {
        return musicList;
    }


    public ReadOnlyStringProperty selectedMusicNameProperty() {
        return selectedMusicName;
    }

    public ReadOnlyStringProperty selectedArtistNameProperty() {
        return selectedArtistName;
    }


    public ReadOnlyObjectProperty<Image> selectedAlbumImageProperty() {
        return selectedAlbumImage;
    }


    public ReadOnlyDoubleProperty selectedMusicLengthProperty() {
        return selectedMusicLength;
    }


    public DoubleProperty volumeProperty() {
        return volume;
    }


    public DoubleProperty currentTimeProperty() {
        return currentTime;
    }

    public Status getStatus() {
        return status.get();
    }

    public ObjectProperty<Status> statusProperty() {
        return status;
    }

    public boolean isMute() {
        return mute.get();
    }

    public void setMute(boolean mute) {
        this.mute.set(mute);
    }

    public BooleanProperty muteProperty() {
        return mute;
    }

    public void addMusics(List<File> fileList) {
        if(fileList != null) {
            fileList.stream().map(Music::new)
                    .forEach(musicList.get()::add);
            if(status.get() == Status.EMPTY) {
                status.set(Status.READY);
            }
        }
    }

    public void clearList() {
        switch (status.get()){
            case PLAYING:
            case PAUSED:
                musicPlayer.pause();
                musicPlayer.dispose();
            case READY:
                selectMusic(null);
                musicList.get().clear();
                status.set(Status.EMPTY);
                break;
        }
    }

    private void selectMusic(Music music) {
        selectedMusic.set(music);
        if(music == null) {
            selectedIndex.set(0);
            selectedMusicName.set("");
            selectedArtistName.set("");
            selectedMusicLength.set(0.0);
            selectedAlbumImage.set(new Image("/com.op3r470r.jplayer/view/images/logoCircle.png"));
        } else {
            selectedIndex.set(musicList.get().indexOf(music));
            selectedMusicName.set(music.getMusicName());
            selectedArtistName.set(music.getArtistName());
            selectedMusicLength.set(music.getMusicLength());
            selectedAlbumImage.set(music.getAlbumImage());
        }
    }

    private void selectMusic(int index) {
        selectMusic(musicList.get().get(index));
    }

    public void playMusic(Music music) {
        if(music != null) {
            selectMusic(music);
            musicPlayer.fillMusic(selectedMusic.get());
            musicPlayer.play();
        }
    }

    public void playNextMusic() {
        int size = musicList.get().size();
        selectMusic((selectedIndex.get() + 1) % size); // // 利用取模实现 0~(size-1) 的循环
        musicPlayer.fillMusic(selectedMusic.get());
        musicPlayer.play();
    }

    public void playPrevMusic() {
        int size = musicList.get().size();
        selectMusic((selectedIndex.get() + size - 1) % size); // 避免对 -1 取模，加上 size
        musicPlayer.fillMusic(selectedMusic.get());
        musicPlayer.play();
    }

    public void play() {
        switch (musicPlayer.statusProperty().get()) {
            case PLAYING:
                musicPlayer.pause();
                break;
            case PAUSED:
                musicPlayer.play();
                break;
            case READY:
                musicPlayer.fillMusic(selectedMusic.get());
                musicPlayer.play();
                break;
        }
    }

    public void seek(double time) {
        switch (status.get()) {
            case PLAYING:
            case PAUSED:
                musicPlayer.seek(time);
                break;
        }
    }

    public static enum Status {
        EMPTY,
        READY,
        PLAYING,
        PAUSED;
    }

}
