package nz.ac.auckland.se206.words;

import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.CanvasController;
import nz.ac.auckland.se206.HiddenWordCanvasController;
import nz.ac.auckland.se206.SpreadSheetReaderWriter;
import nz.ac.auckland.se206.speech.BackgroundSound;
import nz.ac.auckland.se206.speech.TextToSpeechBackground;

public class WordPageController {

  @FXML private Button plusWordsButton;
  @FXML private Button minusWordsButton;
  @FXML private Button minusConfidenceButton;
  @FXML private Button minusAccuracyButton;
  @FXML private Button minusTimeButton;
  @FXML private Button plusTimeButton;
  @FXML private Button plusConfidenceButton;
  @FXML private Button plusAccuracyButton;

  @FXML private Text confidenceLabel;
  @FXML private Text wordsLabel;
  @FXML private Text accuracyLabel;
  @FXML private Text timeLabel;
  @FXML private Text wordToDraw;

  @FXML private Button readyButton;
  @FXML private Button modeButton;
  @FXML private Button wordButton;

  @FXML private Label textToSpeechLabel;
  @FXML private Label userLabel;
  @FXML private Label modeLabel;
  @FXML private ImageView volumeImage;
  @FXML private ImageView newImage;
  @FXML private ImageView userImage;

  private String currentWord;
  private Boolean isHiddenWordMode = false;
  private Boolean textToSpeech;
  private TextToSpeechBackground textToSpeechBackground;
  private String currentUsername = null;
  private String currentProfilePic = null;
  private int time = 60;
  private int accuracy = 3;
  private int confidence = 1;
  private int words = 1;
  private int overallDif = 1;

  /**
   * Picks a random word from the easy category using category selector
   *
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If file does not exist
   * @throws URISyntaxException If URI does not exist
   */
  public void setWordToDraw() throws IOException, URISyntaxException, CsvException {
    CategorySelector categorySelector = new CategorySelector();
    currentWord = categorySelector.setWordToDraw(currentUsername, words);
    wordToDraw.setText(currentWord);
  }

  /**
   * pass the text to speech functionality
   *
   * @param textToSpeechBackground generates tts functionality from tts class
   * @param textToSpeech activates tts functionality if is true
   */
  public void give(TextToSpeechBackground textToSpeechBackground, Boolean textToSpeech) {
    this.textToSpeechBackground = textToSpeechBackground;
    this.textToSpeech = textToSpeech;
    if (textToSpeech) { // checks if text to speech was previously enabled then will make this page
      // mirror
      textToSpeechLabel.setText("ON");
    }
  }

  /**
   * create the current username and select a profile picture
   *
   * @param username current username
   * @param profilePic profile picture selected
   */
  public void getUsername(String username, String profilePic)
      throws IOException, URISyntaxException, CsvException {
    // Check if username is not null
    if (username != null) {
      // If not null, update label as current username
      currentUsername = username;
      currentProfilePic = profilePic;
      userLabel.setText(currentUsername);
      // Set profile pic
      File file = new File(profilePic);
      Image image = new Image(file.toURI().toString());
      userImage.setImage(image);
      currentProfilePic = profilePic;
      setFirstDifficulty(currentUsername);
    } else {
      userLabel.setText("Guest");
      // Set guest pic
      File file = new File("src/main/resources/images/ProfilePics/GuestPic.png");
      Image image = new Image(file.toURI().toString());
      userImage.setImage(image);
      setDifficulty(accuracy, confidence, words, time);
    }
    setWordToDraw();
  }

  /**
   * get a new hidden word
   *
   * @throws IOException If the model cannot be found on the file system.
   * @throws URISyntaxException If the file cannot be found locally
   * @throws CsvException If the user info cannot be found locally
   */
  @FXML
  private void onNewWord() throws IOException, URISyntaxException, CsvException {
    setWordToDraw();
  }

  /**
   * switch to hidden mode
   *
   * @throws IOException If the model cannot be found on the file system.
   * @throws URISyntaxException If the file cannot be found locally
   * @throws CsvException If the user info cannot be found locally
   */
  @FXML
  private void onHiddenWordMode() throws IOException, URISyntaxException, CsvException {
    isHiddenWordMode = !isHiddenWordMode;

    if (isHiddenWordMode) {
      setWordToDraw();
      wordToDraw.setText("??????");
      modeLabel.setText("HIDDEN-WORD MODE");

      // Set style
      modeButton.setStyle(
          "-fx-border-color: black; -fx-background-color: #45f248; "
              + "-fx-border-radius: 10px; -fx-background-radius: 10px;");

      // Disable refresh button
      wordButton.setDisable(true);
      newImage.setOpacity(0.2);
    } else {
      modeLabel.setText("NORMAL MODE");
      setWordToDraw();

      // Set style
      modeButton.setStyle(
          "-fx-border-color: black; -fx-background-color: #EB4A5A; "
              + "-fx-border-radius: 10px; -fx-background-radius: 10px;");

      // Enable refresh button
      wordButton.setDisable(false);
      newImage.setOpacity(1);
    }
  }

  /** image gets larger when mouse hovers on */
  @FXML
  private void onHoverNew() {
    textToSpeechBackground.backgroundSpeak("new word button", textToSpeech);
    newImage.setFitHeight(57);
    newImage.setFitWidth(57);
  }

  /** label speaks out and button style changes when mouse hovers on */
  @FXML
  private void onHoverReady() {
    textToSpeechBackground.backgroundSpeak("Ready", textToSpeech);
    readyButton.setStyle(
        "-fx-border-radius: 10; fx-background-border: 10; -fx-background-color: #99F4B3; -fx-border-color: #99F4B3;");
  }

  /** button style restores when mouse is away */
  @FXML
  private void onHoverMode() {
    modeButton.setOpacity(0.7);
  }

  /** button style restores when mouse is away */
  @FXML
  private void onExitReady() {
    readyButton.setStyle(
        "-fx-border-radius: 10; fx-background-border: 10; -fx-background-color: transparent; -fx-border-color: white");
  }

  /** button style restores when mouse is away */
  @FXML
  private void onExitMode() {
    modeButton.setOpacity(1);
  }

  /** label speaks out when mouse hovers on */
  @FXML
  private void onHoverTextToSpeechLabel() {
    textToSpeechBackground.backgroundSpeak("ON", textToSpeech);
  }

  /** initialize or disconnect the tts feature */
  @FXML
  private void onTextToSpeech() {
    textToSpeech = !textToSpeech; // toggles text to speech
    if (textToSpeech) { // sets label to correct value
      textToSpeechLabel.setText("ON");
    } else {
      textToSpeechLabel.setText("OFF");
    }
  }

  /** label speaks out and image gets larger when mouse hovers on */
  @FXML
  private void onHoverTextToSpeech() {
    textToSpeechBackground.backgroundSpeak("toggle text to speech", textToSpeech);
    volumeImage.setFitHeight(48);
    volumeImage.setFitWidth(48);
  }

  /**
   * switch to canvas page or hidden word canvas page
   *
   * @throws IOException If the model cannot be found on the file system.
   */
  @FXML
  private void onReady() throws IOException, URISyntaxException, CsvException {
    BackgroundSound backgroundSound = new BackgroundSound();
    backgroundSound.play("/sounds/mixkit-unlock-game-notification-253_1.wav");
    Stage stage =
        (Stage) readyButton.getScene().getWindow(); // uses the ready button to fine the stage

    FXMLLoader loader; // uses the ready button to fine the stage
    if (!isHiddenWordMode) {
      loader = new FXMLLoader(App.class.getResource("/fxml/canvas.fxml"));
      Scene scene = new Scene(loader.load(), 1000, 680);
      stage.setScene(scene);
      CanvasController canvasController =
          loader.getController(); // gets the newly created controller for next
      // page
      canvasController.setTimeAccuracy(time, accuracy, confidence, words, overallDif);
      canvasController.setWordLabel(
          currentWord); // passes the current word so that the next screen can display
      // it
      canvasController.give(
          textToSpeechBackground, textToSpeech); // passes the background threaded text to
      // speech
      // and whether it is on or not
      canvasController.getUsername(currentUsername, currentProfilePic);
    } else {
      loader = new FXMLLoader(App.class.getResource("/fxml/hidden_word_canvas.fxml"));
      Scene scene = new Scene(loader.load(), 1000, 680);
      stage.setScene(scene);
      HiddenWordCanvasController hiddenWordCanvasController =
          loader.getController(); // gets the newly created
      // controller for next page
      hiddenWordCanvasController.setTimeAccuracy(time, accuracy, confidence, words, overallDif);
      hiddenWordCanvasController.give(
          textToSpeechBackground, textToSpeech); // passes the background threaded
      // text to speech
      // and whether it is on or not
      hiddenWordCanvasController.setDefinitionList(currentWord);
      hiddenWordCanvasController.getUsername(currentUsername, currentProfilePic);
    }
    stage.show();
  }

  /** image restores its size when mouse is away */
  @FXML
  private void onVolumeExit() {
    volumeImage.setFitHeight(45);
    volumeImage.setFitWidth(45);
  }

  /** image restores its size when mouse is away */
  @FXML
  private void onNewExit() {
    newImage.setFitHeight(55);
    newImage.setFitWidth(55);
  }

  /**
   * set the difficulty selection
   *
   * @param accuracy is the result within top 1/2/3 prediction
   * @param confidence confidence percentage
   * @param words word category
   * @param time time limit
   */
  public void setDifficulty(int accuracy, int confidence, int words, int time) {
    this.time = time;
    timeLabel.setText(time + "sec"); // adds secs for readability
    this.accuracy = accuracy;
    accuracyLabel.setText("Top " + accuracy);

    confidenceLabel.setText(confidence + "%"); // adds % for readability
    this.confidence = confidence;

    if (words == 1) { // sets text and opacity for buttons
      wordsLabel.setText("E"); // text for readability
      minusWordsButton.setOpacity(0.2);
      plusWordsButton.setOpacity(1);
      plusWordsButton.setDisable(false);
      minusWordsButton.setDisable(true);
    } else if (words == 2) {
      wordsLabel.setText("E,M"); // text for readability
      minusWordsButton.setOpacity(1);
      plusWordsButton.setOpacity(1);
      plusWordsButton.setDisable(false);
      minusWordsButton.setDisable(false);
    } else if (words == 3) {
      wordsLabel.setText("E,M,H"); // text for readability
      plusWordsButton.setOpacity(1);
      minusWordsButton.setOpacity(1);
      plusWordsButton.setDisable(false);
      minusWordsButton.setDisable(false);
    } else if (words == 4) {
      wordsLabel.setText("H"); // text for readability
      plusWordsButton.setOpacity(0.2);
      minusWordsButton.setOpacity(1);
      plusWordsButton.setDisable(true);
      minusWordsButton.setDisable(false);
    } else {
      wordsLabel.setText("ERROR"); // error state shouldnt be reached
    }
    this.words = words;
    setPlusMinusLabels(); // sets opacity
    overallDifficulty(accuracy, confidence, words, time); // writes to csv
  }

  /** sets the opacity of the + - labeles */
  private void setPlusMinusLabels() {
    if (time == 60) { // disables plus by greying out
      plusTimeButton.setOpacity(0.2);
      plusTimeButton.setDisable(true);
    }
    if (time == 15) { // disables minus by greying out button
      minusTimeButton.setOpacity(0.2);
      minusTimeButton.setDisable(true);
    }
    if (confidence == 50) { // disables plus by greying out
      plusConfidenceButton.setOpacity(0.2);
      plusConfidenceButton.setDisable(true);
      minusConfidenceButton.setDisable(false);
    }
    if (confidence == 1) { // disables minus by greying outbutton
      minusConfidenceButton.setOpacity(0.2);
      minusConfidenceButton.setDisable(true);
      plusConfidenceButton.setDisable(false);
    }
    if (accuracy == 1) { // disables minus by greying out
      minusAccuracyButton.setOpacity(0.2);
      minusAccuracyButton.setDisable(true);
      plusAccuracyButton.setOpacity(1); // enables plus
      plusAccuracyButton.setDisable(false);
    }
    if (accuracy == 3) { // disables plus by greying out
      plusAccuracyButton.setOpacity(0.2);
      plusAccuracyButton.setDisable(true);
      minusAccuracyButton.setOpacity(1); // enables minus
      minusAccuracyButton.setDisable(false);
    }
    if (accuracy == 2) { // enables both
      minusAccuracyButton.setOpacity(1);
      minusAccuracyButton.setDisable(false);
      plusAccuracyButton.setOpacity(1);
      plusAccuracyButton.setDisable(false);
    }
  }

  /**
   * intitaly called to gather users past settings
   *
   * @throws IOException if file is not found
   * @throws CsvException if out of the file range
   */
  private void setFirstDifficulty(String currentUsername) throws IOException, CsvException {
    SpreadSheetReaderWriter sheetReaderWriter = new SpreadSheetReaderWriter();
    accuracy = sheetReaderWriter.getUsersAccuracy(currentUsername);
    confidence =
        sheetReaderWriter.getUsersConfidence(currentUsername); // gathers conf from prev settings
    time = sheetReaderWriter.getUsersTime(currentUsername);
    words = sheetReaderWriter.getUsersWords(currentUsername);
    setDifficulty(accuracy, confidence, words, time); // sets opacitys
  }

  /**
   * pass the difficulty selection and get the level under different combination
   *
   * @param accuracy is the result within top 1/2/3 prediction
   * @param confidence confidence percentage
   * @param words word category
   * @param time time limit
   */
  private void overallDifficulty(int accuracy, int confidence, int words, int time) {
    if (words == 4 && confidence == 50 && accuracy == 1 && time == 15) { // master level
      overallDif = 4;
    } else if (words >= 3 && confidence >= 25 && accuracy == 1 && time <= 30) { // hard level
      overallDif = 3;
    } else if (words >= 2 && confidence >= 10 && accuracy <= 2 && time <= 45) { // medium level
      overallDif = 2;
    } else { // easy level
      overallDif = 1;
    }
  }

  /**
   * increments time
   *
   * @throws IOException if file is not found
   * @throws CsvException if out of the file range
   */
  @FXML
  private void onClickTimeUp() throws IOException, CsvException {
    if (time == 15) { // increase time dif
      time = 30;
      minusTimeButton.setOpacity(1);
      minusTimeButton.setDisable(false);
    } else if (time == 30) {
      time = 45;
      minusTimeButton.setDisable(false);
    } else if (time == 45) {
      time = 60; // if 60 wont increase as limit
      minusTimeButton.setDisable(false);
    }
    setDifficulty(accuracy, confidence, words, time);
    updateUserTime(time); // writes to csv
  }

  /**
   * increments accuracy
   *
   * @throws IOException if file is not found
   * @throws CsvException if out of the file range
   */
  @FXML
  private void onClickAccuracyUp() throws IOException, CsvException {
    if (accuracy != 3) { // cant increase if 3
      accuracy++;
    }
    setDifficulty(accuracy, confidence, words, time);
    updateUserAccuracy(accuracy); // writes to csv
  }

  /**
   * increments confidence
   *
   * @throws IOException if file is not found
   * @throws CsvException if out of the file range
   */
  @FXML
  private void onClickConfidenceUp() throws IOException, CsvException {
    if (confidence == 25) {
      confidence = 50;
      minusConfidenceButton.setDisable(false);
    } else if (confidence == 10) {
      confidence = 25;
      minusConfidenceButton.setDisable(false);
    } else if (confidence == 1) {
      confidence = 10;
      minusConfidenceButton.setOpacity(1); // cant decrease if 1
      minusConfidenceButton.setDisable(false);
    }
    setDifficulty(accuracy, confidence, words, time); // sets opacity for buttons
    updateUserConfidence(confidence); // writes to csv
  }

  /**
   * Decreaments time
   *
   * @throws IOException if file is not found
   * @throws CsvException if out of the file range
   */
  @FXML
  private void onClickTimeDown() throws IOException, CsvException {
    if (time == 60) { // decreases time setting
      time = 45;
      plusTimeButton.setOpacity(1);
      plusTimeButton.setDisable(false);
    } else if (time == 45) {
      time = 30;
      plusTimeButton.setDisable(false);
    } else if (time == 30) {
      time = 15;
      plusTimeButton.setDisable(false);
    } // 15 cant be decreased
    setDifficulty(accuracy, confidence, words, time); // updates opacity
    updateUserTime(time); // writes to csv
  }

  /**
   * Decreaments accuracy
   *
   * @throws IOException if file is not found
   * @throws CsvException if out of the file range
   */
  @FXML
  private void onClickAccuracyDown() throws IOException, CsvException {
    if (accuracy != 1) { // unless accuray is one will decrement
      accuracy--;
    }
    setDifficulty(accuracy, confidence, words, time);
    updateUserAccuracy(accuracy); // writes to csv
  }

  /**
   * Decreaments confidence
   *
   * @throws IOException if file is not found
   * @throws CsvException if out of the file range
   */
  @FXML
  private void onClickConfidenceDown() throws IOException, CsvException {
    if (confidence == 50) { // decrements confidence
      confidence = 25;
      plusConfidenceButton.setOpacity(1);
      plusConfidenceButton.setDisable(false);
    } else if (confidence == 25) {
      confidence = 10;
      plusConfidenceButton.setDisable(false);
    } else if (confidence == 10) {
      confidence = 1;
      plusConfidenceButton.setDisable(false);
    }
    setDifficulty(accuracy, confidence, words, time); // changes opacity
    updateUserConfidence(confidence); // writes to csv
  }

  /**
   * updates the users time therough the csv
   *
   * @param time the new input to update on csv
   * @throws IOException if file is not found
   * @throws CsvException if out of the file range
   */
  private void updateUserTime(int time) throws IOException, CsvException {
    this.time = time;
    SpreadSheetReaderWriter sheetReaderWriter = new SpreadSheetReaderWriter();
    sheetReaderWriter.updateUsersTime(time, currentUsername);
  }

  /**
   * updates the users confidence therough the csv
   *
   * @param confidence the new input to update on csv
   * @throws IOException if file is not found
   * @throws CsvException if out of the file range
   */
  private void updateUserConfidence(int confidence) throws IOException, CsvException {
    this.confidence = confidence;
    SpreadSheetReaderWriter sheetReaderWriter = new SpreadSheetReaderWriter();
    sheetReaderWriter.updateUsersConfidence(confidence, currentUsername);
  }

  /**
   * updates the users accuracy therough the csv
   *
   * @param accuracy the new input to update on csv
   * @throws IOException if file is not found
   * @throws CsvException if out of the file range
   */
  private void updateUserAccuracy(int accuracy) throws IOException, CsvException {
    this.accuracy = accuracy;
    SpreadSheetReaderWriter sheetReaderWriter = new SpreadSheetReaderWriter();
    sheetReaderWriter.updateUsersAccuracy(accuracy, currentUsername);
  }

  /**
   * when clicked word dif increases by one
   *
   * @throws IOException throwen if file isnt found
   * @throws CsvException throwen if out of bound
   */
  @FXML
  private void onClickWordsUp() throws IOException, CsvException, URISyntaxException {
    if (words != 4) {
      words++;
      updateUserWords(words); // sets opacity
    }
  }

  /**
   * decrease the word difficulty
   *
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If the user info cannot be found locally
   */
  @FXML
  private void onClickWordsDown() throws IOException, CsvException, URISyntaxException {
    if (words != 1) {
      words--;
      updateUserWords(words); // sets opacity
    }
  }

  /**
   * update word category for the current user
   *
   * @param words current word category
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If the user info cannot be found locally
   */
  private void updateUserWords(int words) throws IOException, CsvException, URISyntaxException {
    this.words = words;
    setDifficulty(accuracy, confidence, words, time);
    SpreadSheetReaderWriter sheetReaderWriter = new SpreadSheetReaderWriter();
    sheetReaderWriter.updateUsersWords(words, currentUsername);
    onNewWord();
  }
}
