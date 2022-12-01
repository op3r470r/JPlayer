package com.op3r470r.jplayer.controller;

import com.op3r470r.jplayer.model.JPlayerModel;
import com.op3r470r.jplayer.utils.Music;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.List;


public class DrawerController {

    private JPlayerModel model;
    @FXML private ListView<Music> listView;


    @FXML
    void addMusicClicked(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("mp3", "*.mp3"));
        List<File> fileList = fileChooser.showOpenMultipleDialog(listView.getScene().getWindow());
        if(fileList != null) model.addMusics(fileList);
    }

    @FXML
    void clearList(MouseEvent event) {
        model.clearList();
    }

    @FXML
    void initialize() {
        model = JPlayerModel.getInstance();
        listView.setCellFactory(param -> new MusicCellController());
        listView.itemsProperty().bind(model.getMusicList());
        model.selectedMusicProperty().addListener((obs, oldMusic, newMusic) -> listView.getSelectionModel().select(newMusic));
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldMusic, newMusic) -> {
            if(newMusic != null) model.playMusic(newMusic);
        });
    }
}
