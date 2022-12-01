package com.op3r470r.jplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class PlayerApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("JPlayer");
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.getIcons().add(new Image(getClass().getResource("/com.op3r470r.jplayer/view/images/logo.png").toExternalForm()));
        Parent root = FXMLLoader.load(getClass().getResource("/com.op3r470r.jplayer/view/mainView.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
