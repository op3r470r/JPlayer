package com.op3r470r.jplayer;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.List;


public class DrawerController {

    private PlayerController playerController;
    @FXML
    private ListView<Music> listView;
    private int index;

    public void play() {
        int listSize = listView.getItems().size();
        if( listSize > 0 )
            listView.getSelectionModel().select(index);
    }

    public void playNext() {
        int listSize = listView.getItems().size();
        if( listSize > 0 )
            listView.getSelectionModel().select((index + 1) % listSize); // 利用取模实现 0~(listSize-1) 的循环
    }

    public void playPrev() {
        int listSize = listView.getItems().size();
        if( listSize > 0 )
            listView.getSelectionModel().select((index -1 + listSize) % listSize); // 避免对 -1 取模，加上 listSize
    }


    @FXML
    void addMusicClicked(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("mp3", "*.mp3"));
        List<File> fileList = fileChooser.showOpenMultipleDialog(playerController.getListDrawer().getScene().getWindow());
        if(fileList != null)
            fileList.stream().map(Music::new)
                .forEach(listView.getItems()::add);
    }

    @FXML
    void closeDrawer(MouseEvent event) {
        playerController.deactivateDrawer(null);
    }

    @FXML
    void initialize() {
        playerController = PlayerController.instance;
        playerController.setDrawerController(this);
        listView.setCellFactory(param -> new MusicCellController());
        listView.getSelectionModel().selectedItemProperty().addListener(((ob, oldMusic, newMusic) -> {
            if( newMusic != null) {
                index = listView.getSelectionModel().getSelectedIndex();
                playerController.play(newMusic);
            }
        }));
    }
}
