package com.op3r470r.jplayer;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.events.JFXDrawerEvent;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

public class PlayerController {
    static PlayerController instance;

    private DrawerController drawerController;
    MediaPlayer mediaPlayer;
    private HamburgerBackArrowBasicTransition transition;
    @FXML
    private Label musicName;
    @FXML
    private Label artistName;
    @FXML
    private ImageView albumImage;
    @FXML
    private Label currentTime;
    @FXML
    private JFXSlider progress;
    @FXML
    private Label totalTime;
    @FXML
    private ImageView volumeBtn;
    @FXML
    private JFXSlider volume;
    @FXML
    private ImageView prevBtn;
    @FXML
    private ImageView playBtn;
    @FXML
    private ImageView nextBtn;
    @FXML
    private JFXHamburger listBtn;
    @FXML
    private JFXDrawer listDrawer;
    private boolean isMute;


    JFXDrawer getListDrawer() { return listDrawer; }

    public void setDrawerController(DrawerController drawerController) {
        this.drawerController = drawerController;
    }

    @FXML
    void closeApp(MouseEvent event) {
        Platform.exit();
    }

    @FXML
    void minimizeApp(MouseEvent event) {
        ((Stage) ((ImageView) event.getSource()).getScene().getWindow()).setIconified(true);
    }

    @FXML
    void deactivateDrawer(JFXDrawerEvent jfxDrawerEvent) {
        if (listDrawer.isShown()) listDrawer.close();
    }


    @FXML
    void nextBtnClicked(MouseEvent event) {
        drawerController.playNext();
    }

    @FXML
    void playBtnClicked(MouseEvent event) {
        if(mediaPlayer == null) {
            drawerController.play();
        } else {
            switch (mediaPlayer.getStatus()) {
                case PLAYING:
                    playBtn.setImage(new Image("/images/pause.png"));
                    mediaPlayer.pause();
                    break;
                case PAUSED:
                    playBtn.setImage(new Image("/images/play.png"));
                    mediaPlayer.play();
                    break;
            }
        }
    }

    @FXML
    void prevBtnClicked(MouseEvent event) {
        drawerController.playPrev();

    }

    @FXML
    void volumeBtnClicked(MouseEvent event) {
        if(mediaPlayer != null)
        {
            if(isMute) {
                mediaPlayer.setMute(false);
                volumeBtn.setImage(new Image("/images/volumeOn.png"));
                isMute = false;
            } else {
                mediaPlayer.setMute(true);
                volumeBtn.setImage(new Image("/images/volumeOff.png"));
                isMute = true;

            }
        }
    }

    @FXML
    void listBtnClicked(MouseEvent event) {
        listDrawer.setDisable(false);
        transition.setRate(transition.getCurrentRate() * -1);
        transition.play();
        listDrawer.open();
    }

    @FXML
    void initialize() {
        instance = this;
        initPlayer();
        initDrawer();
    }

    private void initPlayer() {
        progress.setValue(0);
        progress.setDisable(true);
        progress.setOnMouseClicked( mouseEvent->  mediaPlayer.seek(Duration.seconds(progress.getValue())));
        progress.setOnMouseReleased(mouseEvent -> mediaPlayer.seek(Duration.seconds(progress.getValue())));
        progress.valueProperty().addListener((ob,ov,nv) -> {
            // 进度条的悬浮值
            progress.valueFactoryProperty().set(param -> new StringBinding() {
                @Override
                protected String computeValue() {
                    return String.format("%02.0f:%02.0f", nv.doubleValue()/60, nv.doubleValue()%60);
                }
            });
        });

        volume.setOnMouseReleased(mouseEvent -> mediaPlayer.setVolume(volume.getValue() / 100));
        volume.setOnMouseDragged(mouseEvent -> mediaPlayer.setVolume(volume.getValue() / 100 ));
    }


    private void initDrawer() {
        try {
            VBox vBox = FXMLLoader.load(getClass().getResource("/view/listDrawer.fxml"));
            listDrawer.setSidePane(vBox);
        } catch (IOException e) {
            e.printStackTrace();
        }
        transition = new HamburgerBackArrowBasicTransition(listBtn);
        transition.setRate(-1);
        listDrawer.setOnDrawerClosed(event -> listDrawer.setDisable(true));
    }

    public void play(Music music) {
        if (mediaPlayer != null) disposeMediaPlayer();
        mediaPlayer = new MediaPlayer(new Media(music.getFile().toURI().toString()));
        musicName.setText(music.getMusicName());
        artistName.setText(music.getArtistName());
        albumImage.setImage(music.getAlbumImage());
        // mediaPlayer.getTotalDuration() 获取的时间有时会出错
        totalTime.setText(String.format("%02d:%02d", music.getMusicLength()/60, music.getMusicLength()%60));
        progress.setDisable(false);
        progress.setMax(music.getMusicLength());
        mediaPlayer.setOnReady(() -> {
            mediaPlayer.seek(Duration.ZERO);
            mediaPlayer.setVolume(volume.getValue()/100);
            mediaPlayer.setMute(isMute);
            mediaPlayer.setOnEndOfMedia(drawerController::playNext);
            mediaPlayer.currentTimeProperty().addListener((ob, ov, nv) -> {
                if(! progress.isPressed()) { // 避免进度条点击和拖拽时受到干扰
                    progress.setValue(nv.toSeconds());
                    currentTime.setText(String.format("%02.0f:%02.0f", nv.toSeconds()/60, nv.toSeconds()%60));
                }
            });
            mediaPlayer.play();
            playBtn.setImage(new Image("/images/play.png"));
        });
    }

    private void disposeMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.dispose();
        progress.setDisable(true);
    }
}
