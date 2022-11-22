package com.op3r470r.jplayer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class  MusicCellController extends ListCell<Music> {

    @FXML
    private VBox root;
    @FXML
    private ImageView albumImage;
    @FXML
    private Label musicName;
    @FXML
    private Label artistName;
    @FXML
    private Label musicLength;

    @Override
    protected void updateItem(Music music, boolean empty) {
        super.updateItem(music, empty);
        if (empty || music == null) {
            setText(null);
            setGraphic(null);
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/musicCell.fxml"));
            fxmlLoader.setController(this);
            try {
                fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            musicName.setText(String.valueOf(music.getMusicName()));
            artistName.setText(String.valueOf(music.getArtistName()));
            albumImage.setImage(music.getAlbumImage());
            musicLength.setText(String.format("%02d:%02d",music.getMusicLength()/60, music.getMusicLength()%60));
            setGraphic(root);
        }
    }
}
