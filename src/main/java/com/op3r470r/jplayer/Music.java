package com.op3r470r.jplayer;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
import java.io.File;

public class Music {
    private String musicName;
    private String artistName;
    private long musicLength;
    private Image albumImage;
    private final File file;

    public Music(File file) {
        this.file = file;
        try {
            Mp3File mp3File = new Mp3File(file.toString());
            musicLength= mp3File.getLengthInSeconds();
            if(mp3File.hasId3v2Tag()) {  // 优先判断 ID3v2Tag
                ID3v2 id3v2Tag = mp3File.getId3v2Tag();
                musicName = id3v2Tag.getTitle();
                artistName = id3v2Tag.getArtist();
                byte[] imageTag = id3v2Tag.getAlbumImage();
                if(imageTag != null)
                    albumImage = new Image(new ByteArrayInputStream(imageTag));
            }
            else if(mp3File.hasId3v1Tag())  {
                ID3v1 id3v1Tag = mp3File.getId3v1Tag();
                musicName = id3v1Tag.getTitle();
                artistName = id3v1Tag.getArtist();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 曲名为空时，使用文件名
        if(musicName == null || musicName.equals(""))
            musicName = file.getName().replaceFirst("[.][^.]+","");

        if(artistName == null || artistName.equals(""))
            artistName = "Unknown";

        if(albumImage == null)
            albumImage = new Image("/images/logoCircle.png");
    }

    public String getMusicName() {
        return musicName;
    }

    public String getArtistName() {
        return artistName;
    }

    public long getMusicLength() {
        return musicLength;
    }

    public Image getAlbumImage() {
        return albumImage;
    }

    public File getFile() {
        return file;
    }
}
