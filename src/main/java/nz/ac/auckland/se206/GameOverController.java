package nz.ac.auckland.se206;

import com.opencsv.exceptions.CsvException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import nz.ac.auckland.se206.speech.BackgroundSound;
import nz.ac.auckland.se206.speech.TextToSpeech;
import nz.ac.auckland.se206.speech.TextToSpeechBackground;
import nz.ac.auckland.se206.words.WordPageController;

public class GameOverController {

  @FXML private ImageView usersImage;
  @FXML private Label timeLabel;
  @FXML private Label winLoseLabel;
  @FXML private Label textToSpeechLabel;

  @FXML private Button playAgainButton;
  @FXML private Button menuButton;
  @FXML private Button saveButton;

  @FXML private ImageView volumeImage;

  private CanvasController canvasController;

  private Boolean textToSpeech;

  private TextToSpeechBackground textToSpeechBackground;

  private TextToSpeech textToSpeechAlert;

  private String winLoseString;
  private String currentUsername;
  private String currentProfilePic;

  private int timeLeft;
  private int accuracy;
  private int time;
  private int confidence;
  private int words;

  /**
   * pass the text to speech functionality
   *
   * @param textToSpeechBackground generates tts functionality from tts class
   * @param textToSpeech activates tts functionality if is true
   */
  public void give(
      TextToSpeechBackground textToSpeechBackground,
      Boolean textToSpeech,
      BufferedImage bufferedImage) {
    this.textToSpeechBackground = textToSpeechBackground;
    this.textToSpeech = textToSpeech;
    if (textToSpeech) { // updates label to ensure it is correct
      textToSpeechLabel.setText("ON");
    }

    usersImage.setImage(convertToFxImage(bufferedImage));
  }

  /**
   * Converts the buffered image to and image to display. Found
   * https://stackoverflow.com/questions/30970005/bufferedimage-to-javafx-image on 14/10/22 posted
   * by Dan
   *
   * @param bufferedImage takes a buffered image that is a screenshot of the final drawing the user
   *     did
   * @return Image, is a image that can be used in java fx image view.
   */
  private static Image convertToFxImage(BufferedImage bufferedImage) {
    WritableImage wr = null;
    if (bufferedImage != null) {
      wr = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
      PixelWriter pw = wr.getPixelWriter();
      for (int x = 0; x < bufferedImage.getWidth(); x++) {
        for (int y = 0; y < bufferedImage.getHeight(); y++) {
          pw.setArgb(x, y, bufferedImage.getRGB(x, y));
        }
      }
    }

    return new ImageView(wr).getImage();
  }

  /**
   * set all the labels relating to the difficulty selection and game results from normal mode
   *
   * @param winLose win or lose
   * @param ctrl pass the canvas controller
   * @param overallDif current difficulty combination
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If the user cannot be found locally.
   */
  public void setWinLoseLabel(boolean winLose, CanvasController ctrl, int overallDif)
      throws IOException, CsvException {
    BackgroundSound backgroundSound = new BackgroundSound();
    if (winLose) { // if user won display message, time, and congratulate
      winLoseLabel.setText("YOU WON");
      timeLabel.setText("TIME LEFT: " + timeLeft + " seconds");
      winLoseString = "You won with " + timeLeft + "Seconds left!";
      backgroundSound.play("/sounds/mixkit-audience-light-applause-354.wav");
      Task<Void> backgroundTask =
          new Task<Void>() {

            @Override
            protected Void call() {

              textToSpeechAlert = new TextToSpeech();
              textToSpeechAlert.speak("Congratulation, you are an artist");

              return null;
            }
          };

      Thread backgroundThread = new Thread(backgroundTask);
      backgroundThread.start();

    } else { // if user looses display message and laugh at the user
      backgroundSound.play("/sounds/mixkit-arcade-retro-game-over-213.wav");
      winLoseLabel.setText("YOU LOST");
      timeLabel.setText("TIME LIMIT REACHED");
      winLoseString = "You lost!";
      textToSpeechAlert = new TextToSpeech();

      Task<Void> backgroundTask =
          new Task<Void>() {

            @Override
            protected Void call() {

              textToSpeechAlert = new TextToSpeech();
              textToSpeechAlert.speak("you lost");

              return null;
            }
          };

      Thread backgroundThread = new Thread(backgroundTask);
      backgroundThread.start();
    }
    canvasController = ctrl;
    SpreadSheetReaderWriter spreadSheetReaderWriter = new SpreadSheetReaderWriter();
    spreadSheetReaderWriter.updateResult(
        winLose, currentUsername, overallDif); // writes over old file to update
    // win/loss record
  }

  /**
   * set all the labels relating to the difficulty selection and game results from hidden mode
   *
   * @param winLose win or lose
   * @param ctrl pass the hidden mode canvas controller
   * @param overallDif current difficulty combination
   */
  public void setHiddenWinLoseLabel(
      boolean winLose, HiddenWordCanvasController ctrl, int overallDif) {
    BackgroundSound backgroundSound = new BackgroundSound();
    // hidden word mode does not count profile stats
    if (winLose) { // if user won display message and time
      winLoseLabel.setText("YOU WON");
      timeLabel.setText("TIME LEFT: " + timeLeft + " seconds");
      winLoseString = "You won with " + timeLeft + "Seconds left!";
      backgroundSound.play("/sounds/mixkit-audience-light-applause-354.wav");
      Task<Void> backgroundTask =
          new Task<Void>() {

            @Override
            protected Void call() {

              textToSpeechAlert = new TextToSpeech();
              textToSpeechAlert.speak("Congratulation, you are an artist");

              return null;
            }
          };

      Thread backgroundThread = new Thread(backgroundTask);
      backgroundThread.start();

    } else { // if user looses display message
      winLoseLabel.setText("YOU LOST");
      timeLabel.setText("TIME LIMIT REACHED");
      winLoseString = "You lost!";
      backgroundSound.play("/sounds/mixkit-audience-light-applause-354.wav");
      Task<Void> backgroundTask =
          new Task<Void>() {

            @Override
            protected Void call() {

              textToSpeechAlert = new TextToSpeech();
              textToSpeechAlert.speak("you lost");

              return null;
            }
          };

      Thread backgroundThread = new Thread(backgroundTask);
      backgroundThread.start();
    }
  }

  /**
   * calculate the amount of time spent on the sketch
   *
   * @param sec spent time
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If the user cannot be found locally.
   */
  public void timeLeft(int sec) throws IOException, CsvException {
    timeLeft = sec;
    SpreadSheetReaderWriter spreadSheetReaderWriter = new SpreadSheetReaderWriter();
    spreadSheetReaderWriter.updateTime(60 - timeLeft, currentUsername);
  }

  /**
   * pass the username and the profile picture
   *
   * @param username current username
   * @param profilePic profile picture
   */
  public void getUsername(String username, String profilePic) {
    // Check if username is not null
    if (username != null) {
      // If not null, update label as current username
      currentUsername = username;
      currentProfilePic = profilePic;
    }
  }

  /** save the image by choosing own location */
  @FXML
  private void onSave() {
    Stage stage = (Stage) playAgainButton.getScene().getWindow(); // gets the stage from the button
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Image");
    File file =
        fileChooser.showSaveDialog(
            stage); // shows a popup to allow user to choose where to save file
    if (file != null) {
      try {
        ImageIO.write(
            canvasController.getCurrentSnapshot(),
            "bmp",
            file); // creates a new image to save to the
        // users location
      } catch (IOException ex) {
        System.out.println(ex.getMessage());
      }
    }
  }

  /**
   * switch to the main menu
   *
   * @throws IOException If the model cannot be found on the file system.
   */
  @FXML
  private void onClickMenu() throws IOException {
    BackgroundSound backgroundSound = new BackgroundSound();
    backgroundSound.play("/sounds/mixkit-unlock-game-notification-253_1.wav");
    Stage stage = (Stage) menuButton.getScene().getWindow();
    FXMLLoader loader =
        new FXMLLoader(App.class.getResource("/fxml/main_menu.fxml")); // reset to a new word_page
    // where a new word will be
    // generated
    Scene scene = new Scene(loader.load(), 1000, 680);
    stage.setScene(scene);
    stage.show();

    MainMenuController ctrl =
        loader.getController(); // gets controller of new page to pass text to speech
    ctrl.give(
        textToSpeechBackground,
        textToSpeech); // passes text to speech instance and boolean to next page
    ctrl.getUsername(currentUsername, currentProfilePic);
  }

  /**
   * switch to the word page to play again
   *
   * @throws IOException If the model cannot be found on the file system.
   * @throws URISyntaxException If the URI is valid
   * @throws CsvException If the user cannot be found locally.
   */
  @FXML
  private void onPlayAgain() throws IOException, URISyntaxException, CsvException {
    BackgroundSound backgroundSound = new BackgroundSound();
    backgroundSound.play("/sounds/mixkit-unlock-game-notification-253_1.wav");
    Stage stage = (Stage) playAgainButton.getScene().getWindow();
    FXMLLoader loader =
        new FXMLLoader(App.class.getResource("/fxml/word_page.fxml")); // reset to a new word_page
    // where a new word will be
    // generated
    Scene scene = new Scene(loader.load(), 1000, 680);
    stage.setScene(scene);
    stage.show();

    WordPageController ctrl =
        loader.getController(); // gets controller of new page to pass text to speech
    ctrl.give(
        textToSpeechBackground,
        textToSpeech); // passes text to speech instance and boolean to next page
    ctrl.setDifficulty(accuracy, confidence, words, time);
    ctrl.getUsername(currentUsername, currentProfilePic);
  }

  /** initialize or disconnect the tts feature */
  @FXML
  private void onTextToSpeech() {
    textToSpeech = !textToSpeech; // inverts boolean
    if (textToSpeech) { // then sets label accordingly
      textToSpeechLabel.setText("ON");
    } else {
      textToSpeechLabel.setText("OFF");
    }
  }

  /** label speaks out when mouse is moved on */
  @FXML
  private void onHoverWinLose() {
    textToSpeechBackground.backgroundSpeak(winLoseString, textToSpeech);
  }

  /** label speaks out and button changes its style when mouse hovers on */
  @FXML
  private void onHoverSave() {
    textToSpeechBackground.backgroundSpeak("Save", textToSpeech);
    saveButton.setStyle(
        "-fx-background-radius: 10px; -fx-text-fill: white; -fx-border-radius: 10px; "
            + "-fx-background-color: #99DAF4; -fx-border-color: #99DAF4;");
  }

  /** label speaks out and button changes its style when mouse hovers on */
  @FXML
  private void onHoverPlayAgain() {
    textToSpeechBackground.backgroundSpeak("Play Again", textToSpeech);
    playAgainButton.setStyle(
        "-fx-background-radius: 10px; -fx-text-fill: white; -fx-border-radius: 10px; "
            + "-fx-background-color: #99DAF4; -fx-border-color: #99DAF4;");
  }

  /** label speaks out and button changes its style when mouse hovers on */
  @FXML
  private void onHoverMenu() {
    textToSpeechBackground.backgroundSpeak("Main Menu", textToSpeech);
    menuButton.setStyle(
        "-fx-background-radius: 10px; -fx-text-fill: white; -fx-border-radius: 10px; "
            + "-fx-background-color: #99DAF4; -fx-border-color: #99DAF4;");
  }

  /** label speaks out when mouse hovers on */
  @FXML
  private void onHoverTextToSpeechLabel() {
    textToSpeechBackground.backgroundSpeak("ON", textToSpeech);
  }

  /** label speaks out and image becomes larger slightly when mouse hovers on */
  @FXML
  private void onHoverTextToSpeech() {
    textToSpeechBackground.backgroundSpeak("toggle text to speech", textToSpeech);
    volumeImage.setFitHeight(48);
    volumeImage.setFitWidth(48);
  }

  /** image restores its size when mouse is moved away */
  @FXML
  private void onVolumeExit() {
    volumeImage.setFitHeight(45);
    volumeImage.setFitWidth(45);
  }

  /** button restores it style when mouse is moved away */
  @FXML
  private void onSaveExit() {
    saveButton.setStyle(
        "-fx-background-radius: 10px; -fx-text-fill: white; -fx-border-radius: 10px; "
            + "-fx-background-color: transparent; -fx-border-color: white; -fx-border-width: 2");
  }

  /** button restores it style when mouse is moved away */
  @FXML
  private void onMenuExit() {
    menuButton.setStyle(
        "-fx-background-radius: 10px; -fx-text-fill: white; -fx-border-radius: 10px; "
            + "-fx-background-color: transparent; -fx-border-color: white; -fx-border-width: 2");
  }

  /** button restores it style when mouse is moved away */
  @FXML
  private void onPlayAgainExit() {
    playAgainButton.setStyle(
        "-fx-background-radius: 10px; -fx-text-fill: white; -fx-border-radius: 10px; "
            + "-fx-background-color: transparent; -fx-border-color: white; -fx-border-width: 2");
  }

  /** label speaks out when mouse hovers on */
  @FXML
  public void onHoverTimeLeft() {
    textToSpeechBackground.backgroundSpeak(timeLeft + "seconds left", textToSpeech);
  }

  /** label speaks out when mouse hovers on */
  @FXML
  private void onHoverTitle() {
    textToSpeechBackground.backgroundSpeak("Just Draw", textToSpeech);
  }

  /**
   * pass the difficulties selections
   *
   * @param time user has to draw within this time
   * @param userAccuracy is the result within top x of the prediction list
   * @param confidence confidence percentages
   * @param words words category
   */
  public void setTimeAccuracy(int time, int userAccuracy, int confidence, int words) {
    this.time = time;
    this.accuracy = userAccuracy;
    this.confidence = confidence;
    this.words = words;
  }
}
