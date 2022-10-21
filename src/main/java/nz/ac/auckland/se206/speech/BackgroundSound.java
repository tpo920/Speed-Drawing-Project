package nz.ac.auckland.se206.speech;

import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class BackgroundSound {

  public void play(String musicFile) {
    Task<Void> backgroundTask =
        new Task<>() {
          @Override
          protected Void call() {
            Media sound = new Media(this.getClass().getResource(musicFile).toExternalForm());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
            mediaPlayer.setVolume(0.4);
            return null;
          }
        };
    Thread backgroundThread = new Thread(backgroundTask);
    backgroundThread.start(); // proccess
  }
}
