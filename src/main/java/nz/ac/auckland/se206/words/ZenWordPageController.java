package nz.ac.auckland.se206.words;

import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;
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
import nz.ac.auckland.se206.LoadPage;
import nz.ac.auckland.se206.SpreadSheetReaderWriter;
import nz.ac.auckland.se206.ZenCanvasController;
import nz.ac.auckland.se206.speech.TextToSpeechBackground;

public class ZenWordPageController {

  @FXML private Label lostWord1;
  @FXML private Label lostWord2;
  @FXML private Label lostWord3;
  @FXML private Label lostWord4;
  @FXML private Label lostWord5;
  @FXML private Label textToSpeechLabel;
  @FXML private Label userLabel;
  @FXML private Button readyButton;
  @FXML private Button backButton;
  @FXML private Text wordLabel;
  @FXML private ImageView newImage;
  @FXML private ImageView userImage;
  @FXML private ImageView volumeImage;

  private String currentWord;
  private String currentUsername = null;
  private String currentProfilePic;
  private Boolean textToSpeech = false;
  private TextToSpeechBackground textToSpeechBackground;

  /**
   * initialize the word page
   *
   * @throws IOException If the model cannot be found on the file system.
   * @throws URISyntaxException If the file cannot be found locally
   * @throws CsvException If the user info cannot be found locally
   */
  public void initialize() throws IOException, URISyntaxException, CsvException {
    setWordToDraw();
  }

  /**
   * pass the text to speech functionality
   *
   * @param textToSpeechBackground generates tts functionality from tts class
   * @param textToSpeech activates tts functionality if is true
   */
  public void give(TextToSpeechBackground textToSpeechBackground, Boolean textToSpeech) {
    this.textToSpeech = textToSpeech;
    this.textToSpeechBackground = (textToSpeechBackground);
    if (textToSpeech) { // updates text to speech label to ensure it is up-to-date
      textToSpeechLabel.setText("ON");
    }
  }

  /**
   * create the current username and select a profile picture
   *
   * @param username current username
   * @param profilePic profile picture selected
   */
  public void getUsername(String username, String profilePic) throws IOException, CsvException {
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
    } else {
      userLabel.setText("Guest");
      // Set guest pic
      File file = new File("src/main/resources/images/ProfilePics/GuestPic.png");
      Image image = new Image(file.toURI().toString());
      userImage.setImage(image);
    }
    setPreviousWords();
  }

  /**
   * set the words that the user drew unsuccessfully
   *
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If the user info cannot be found locally
   */
  private void setPreviousWords() throws IOException, CsvException {
    SpreadSheetReaderWriter spreadSheetReaderWriter = new SpreadSheetReaderWriter();
    String[] lastWords = spreadSheetReaderWriter.getUsersLostWords(currentUsername);
    if (currentUsername == null) {
      lostWord1.setOpacity(0.2);
      lostWord2.setOpacity(0.2);
      lostWord3.setOpacity(0.2);
      lostWord4.setOpacity(0.2);
      lostWord5.setOpacity(0.2);
    }
    lostWord1.setText("1. " + lastWords[0]);
    lostWord2.setText("2. " + lastWords[1]);
    lostWord3.setText("3. " + lastWords[2]);
    lostWord4.setText("4. " + lastWords[3]);
    lostWord5.setText("5. " + lastWords[4]);
  }

  /**
   * generate and display the word from all category
   *
   * @throws IOException If the model cannot be found on the file system.
   * @throws URISyntaxException If the file cannot be found locally
   * @throws CsvException If the user info cannot be found locally
   */
  public void setWordToDraw() throws IOException, URISyntaxException, CsvException {
    CategorySelector categorySelector = new CategorySelector(); // picks random easy word
    String[] words = new String[3];
    words[0] = categorySelector.getRandomCategory(CategorySelector.Difficulty.E);
    words[1] = categorySelector.getRandomCategory(CategorySelector.Difficulty.M);
    words[2] = categorySelector.getRandomCategory(CategorySelector.Difficulty.H);
    currentWord = words[new Random().nextInt(words.length)]; // random of the 3 words
    wordLabel.setText(currentWord);
  }

  /**
   * switch to zen canvas page
   *
   * @throws IOException If the model cannot be found on the file system.
   */
  @FXML
  public void onZenCanvas() throws IOException {
    Stage stage = (Stage) readyButton.getScene().getWindow();
    FXMLLoader loader =
        new FXMLLoader(App.class.getResource("/fxml/zen_canvas.fxml")); // creates a new instance of
    // zen canvas
    Scene scene = new Scene(loader.load(), 1000, 680);
    stage.setScene(scene);
    stage.show();
    ZenCanvasController ctrl = loader.getController();
    ctrl.give(textToSpeechBackground, textToSpeech); // passes text to speech instance and boolean
    ctrl.getUsername(currentUsername, currentProfilePic);
    ctrl.setWordLabel(currentWord);
  }

  /**
   * refresh and get a new word
   *
   * @throws IOException If the model cannot be found on the file system.
   * @throws URISyntaxException If the file cannot be found locally
   * @throws CsvException If the user info cannot be found locally
   */
  public void onNewWord() throws IOException, URISyntaxException, CsvException {
    setWordToDraw();
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

  /** get the 1st previous word */
  @FXML
  private void onClickPlay1() {
    if (currentUsername == null || lostWord1.getText().equals("1. Play normal mode first")) {
      return; // guest
    }
    currentWord = lostWord1.getText().substring(3);
    wordLabel.setText(currentWord);
  }

  /** get the 2nd previous word */
  @FXML
  private void onClickPlay2() {
    if (currentUsername == null || lostWord2.getText().equals("2. Play normal mode first")) {
      return; // guest
    }
    currentWord = lostWord2.getText().substring(3);
    wordLabel.setText(currentWord);
  }

  /** get the 3rd previous word */
  @FXML
  private void onClickPlay3() {
    if (currentUsername == null || lostWord3.getText().equals("3. Play normal mode first")) {
      return; // guest
    }
    currentWord = lostWord3.getText().substring(3);
    wordLabel.setText(currentWord);
  }

  /** get the 4th previous word */
  @FXML
  private void onClickPlay4() {
    if (currentUsername == null || lostWord4.getText().equals("4. Play normal mode first")) {
      return; // guest
    }
    currentWord = lostWord4.getText().substring(3);
    wordLabel.setText(currentWord);
  }

  /** get the 5th previous word */
  @FXML
  private void onClickPlay5() {
    if (currentUsername == null || lostWord5.getText().equals("5. Play normal mode first")) {
      return; // guest
    }
    currentWord = lostWord5.getText().substring(3);
    wordLabel.setText(currentWord);
  }

  /**
   * switch to main menu
   *
   * @throws IOException If the model cannot be found on the file system.
   */
  @FXML
  private void onBack() throws IOException {
    Stage stage = (Stage) backButton.getScene().getWindow();
    LoadPage loadPage = new LoadPage();
    loadPage.extractedMainMenu(
        textToSpeechBackground, textToSpeech, currentUsername, currentProfilePic, stage);
  }

  /** label speaks out and image gets larger when mouse hovers on */
  @FXML
  private void onHoverTextToSpeech() {
    textToSpeechBackground.backgroundSpeak("toggle text to speech", textToSpeech);
    volumeImage.setFitHeight(48);
    volumeImage.setFitWidth(48);
  }

  /** label speaks out when mouse hovers on */
  @FXML
  private void onHoverTextToSpeechLabel() {
    textToSpeechBackground.backgroundSpeak("ON", textToSpeech);
  }

  /** image gets larger when mouse hovers on */
  @FXML
  private void onHoverNew() {
    textToSpeechBackground.backgroundSpeak("New Word Button", textToSpeech);
    newImage.setFitHeight(57);
    newImage.setFitWidth(57);
    newImage.setLayoutX(20);
  }

  /** button style changes when mouser hovers on */
  @FXML
  private void onHoverReady() {
    textToSpeechBackground.backgroundSpeak("Ready Button", textToSpeech);
    readyButton.setStyle(
        "-fx-border-radius: 10; fx-background-border: 10; -fx-background-color: #99F4B3; -fx-border-color: #99F4B3;");
  }

  /** label speaks out and button style changes when mouse hovers on */
  @FXML
  private void onHoverBack() {
    textToSpeechBackground.backgroundSpeak("Back Button", textToSpeech);
    backButton.setStyle(
        "-fx-background-radius: 100px; -fx-text-fill: white; -fx-border-radius: 100px; "
            + "-fx-background-color: #99DAF4; -fx-border-color: #99DAF4;");
  }

  /** image restores when mouse is away */
  @FXML
  private void onNewExit() {
    newImage.setFitHeight(55);
    newImage.setFitWidth(55);
  }

  /** button style restores when mouse is away */
  @FXML
  private void onReadyExit() {
    readyButton.setStyle(
        "-fx-border-radius: 10; fx-background-border: 10; -fx-background-color: transparent; -fx-border-color: white");
  }

  /** button style restores when mouse is away */
  @FXML
  private void onBackExit() {
    backButton.setStyle(
        "-fx-background-radius: 100px; -fx-background-color: #EB4A5A; "
            + "-fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 100px;");
  }

  /** image restores when mouse is away */
  @FXML
  private void onVolumeExit() {
    volumeImage.setFitHeight(45);
    volumeImage.setFitWidth(45);
  }

  @FXML
  private void onHoverWordLabel() {
    textToSpeechBackground.backgroundSpeak("Draw a " + wordLabel.getText(), textToSpeech);
  }
}
