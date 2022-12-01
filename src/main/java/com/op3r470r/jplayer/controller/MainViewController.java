package com.op3r470r.jplayer.controller;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.events.JFXDrawerEvent;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import com.op3r470r.jplayer.model.JPlayerModel;
import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainViewController {
    private JPlayerModel model;
    private HamburgerBackArrowBasicTransition transition;
    @FXML private Label musicName;
    @FXML private Label artistName;
    @FXML private ImageView albumImage;
    @FXML private Label currentTime;
    @FXML private JFXSlider progress;
    @FXML private Label totalTime;
    @FXML private ImageView volumeBtn;
    @FXML private JFXSlider volume;
    @FXML private ImageView prevBtn;
    @FXML private ImageView playBtn;
    @FXML private ImageView nextBtn;
    @FXML private JFXHamburger listBtn;
    @FXML private JFXDrawer listDrawer;


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
        if(model.getStatus() != JPlayerModel.Status.EMPTY)
            model.playNextMusic();
    }

    @FXML
    void playBtnClicked(MouseEvent event) {
        if(model.getStatus() != JPlayerModel.Status.EMPTY)
            model.play();
    }

    @FXML
    void prevBtnClicked(MouseEvent event) {
        if(model.getStatus() != JPlayerModel.Status.EMPTY)
            model.playPrevMusic();
    }

    @FXML
    void volumeBtnClicked(MouseEvent event) {
        model.setMute(! model.isMute());
    }

    @FXML
    void listBtnClicked(MouseEvent event) {
        listDrawer.setDisable(false);
        transition.setRate(transition.getCurrentRate() * -1);
        transition.play();
        listDrawer.open();
    }

    private void initDrawer() {
        try {
            VBox vBox = FXMLLoader.load(getClass().getResource("/com.op3r470r.jplayer/view/drawerView.fxml"));
            listDrawer.setSidePane(vBox);
        } catch (IOException e) {
            e.printStackTrace();
        }
        transition = new HamburgerBackArrowBasicTransition(listBtn);
        transition.setRate(-1);
        listDrawer.setOnDrawerClosed(event -> listDrawer.setDisable(true));
    }

    @FXML
    void initialize() {
        model = JPlayerModel.getInstance();
        // 属性绑定
        musicName.textProperty().bind(model.selectedMusicNameProperty());
        artistName.textProperty().bind(model.selectedArtistNameProperty());
        albumImage.imageProperty().bind(model.selectedAlbumImageProperty());
        volume.valueProperty().bindBidirectional(model.volumeProperty());
        // mediaPlayer.getTotalDuration() 获取的时间有时会出错
        progress.maxProperty().bind(model.selectedMusicLengthProperty());
        model.currentTimeProperty().addListener((obs, ov, nv) -> {
            if(! progress.isPressed()) { // 避免进度条点击和拖拽时受到干扰
                progress.setValue(nv.doubleValue());
            }
        });
        progress.valueProperty().addListener((ob,ov,nv) -> {
            // 进度条的悬浮值
            progress.valueFactoryProperty().set(param -> new StringBinding() {
                @Override
                protected String computeValue() {
                    long time = Math.round(nv.doubleValue());
                    return String.format("%02d:%02d", time/60, time%60);
                }
            });
        });

        StringBinding currentTimeText = new StringBinding() {
            {
                super.bind(model.currentTimeProperty());
            }

            @Override
            protected String computeValue() {
                long time = Math.round(model.currentTimeProperty().get());
                return String.format("%02d:%02d", time / 60, time % 60);
            }
        };
        currentTime.textProperty().bind(currentTimeText);
        StringBinding totalTimeText = new StringBinding() {
            {
                super.bind(model.selectedMusicLengthProperty());
            }

            @Override
            protected String computeValue() {
                long time = Math.round(model.selectedMusicLengthProperty().get());
                return String.format("%02d:%02d",  time / 60, time % 60);
            }
        };
        totalTime.textProperty().bind(totalTimeText);

        model.muteProperty().addListener((observableValue, ov, nv) -> {
            if (nv) {
                volumeBtn.setImage(new Image("/com.op3r470r.jplayer/view/images/volumeOff.png"));
            } else {
                volumeBtn.setImage(new Image("/com.op3r470r.jplayer/view/images/volumeOn.png"));
            }
        });



        model.statusProperty().addListener((observableValue, oldStatus, newStatus) -> {
            switch (newStatus) {
                case EMPTY:
                    progress.setDisable(true);
                    break;
                case PLAYING:
                    progress.setDisable(false);
                    playBtn.setImage(new Image("/com.op3r470r.jplayer/view/images/play.png"));
                    break;
                case PAUSED:
                    playBtn.setImage(new Image("/com.op3r470r.jplayer/view/images/pause.png"));
                    break;
            }
        });

        progress.setOnMouseClicked( mouseEvent->  model.seek(progress.getValue()));
        progress.setOnMouseReleased(mouseEvent -> model.seek(progress.getValue()));

        // 界面初始化
        initDrawer();
        progress.setValue(0);
        volume.setMax(1.0);
        volume.setValue(0.5);
        progress.setDisable(true);
    }

}
