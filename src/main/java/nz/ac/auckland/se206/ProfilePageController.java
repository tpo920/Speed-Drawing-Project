package nz.ac.auckland.se206;

import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import nz.ac.auckland.se206.speech.TextToSpeechBackground;

public class ProfilePageController {

  @FXML private Label masterWinLabel;
  @FXML private Label hardWinLabel;
  @FXML private Label mediumWinLabel;
  @FXML private Label easyWinLabel;

  @FXML private Button backButton;
  @FXML private Label usernameLabel;
  @FXML private Label winLabel;
  @FXML private Label gameLabel;
  @FXML private Label winrateLabel;
  @FXML private Label fastestLabel;
  @FXML private Label textToSpeechLabel;
  @FXML private Label badgeLabel;
  @FXML private Label badgePercentage;
  @FXML private Label historyLabel;
  @FXML private Label winstreakLabel;
  @FXML private ImageView volumeImage;
  @FXML private ImageView userImage;
  // Lists
  @FXML private ListView<String> historyListView;
  // Badges
  @FXML private ImageView secTen, secThirty;
  @FXML private ImageView fiveGames;
  @FXML private ImageView tenGames;
  @FXML private ImageView fiftyGames;
  @FXML private ImageView hundredGames;
  @FXML private ImageView fiveStreak;
  @FXML private ImageView tenStreak;
  @FXML private ImageView fiftyStreak;
  @FXML private ImageView hundredStreak;
  @FXML private ImageView easyWins;
  @FXML private ImageView mediumWins;
  @FXML private ImageView hardWins;
  @FXML private ImageView masterWins;
  @FXML private ImageView godArtist;
  @FXML private ProgressBar badgeProgress;

  private Boolean textToSpeech;
  private TextToSpeechBackground textToSpeechBackground;
  private String currentUsername;
  private String currentProfilePic;
  private int usersWins;
  private int totalGames;
  private int fastestTime;
  private double winRate;
  private final DecimalFormat df = new DecimalFormat("#.#");
  private int numberBadges = 0;

  /** label speaks out when mouse hovers on */
  public void onHoverTextToSpeechLabel() {
    textToSpeechBackground.backgroundSpeak("toggle text to speech", textToSpeech);
  }

  /** label speaks out and image gets larger when mouse hovers on */
  public void onHoverTextToSpeech() {
    textToSpeechBackground.backgroundSpeak("On", textToSpeech);
    volumeImage.setFitHeight(48);
    volumeImage.setFitWidth(48);
  }

  /** label speaks out when mouse hovers on */
  @FXML
  private void onHoverNumBadges() {
    textToSpeechBackground.backgroundSpeak("You have " + numberBadges + "badges", textToSpeech);
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
   * create the current username and the corresponding info and select a profile picture
   *
   * @param username current username
   * @param profilePic profile picture selected
   */
  public void setUsername(String username, String profilePic) throws IOException, CsvException {
    // Check if username is not null
    if (username != null) {
      // If not null, update label as current username
      currentUsername = username;
      currentProfilePic = profilePic;
      this.usernameLabel.setText(currentUsername);
      // Set profile pic
      File file = new File(profilePic);
      Image image = new Image(file.toURI().toString());
      userImage.setImage(image);
      currentProfilePic = profilePic;

      SpreadSheetReaderWriter spreadSheetReaderWriter = new SpreadSheetReaderWriter();
      int streak = spreadSheetReaderWriter.getStreak(currentUsername);

      // Assign wins
      usersWins = spreadSheetReaderWriter.getWins(currentUsername);
      int usersLosses = spreadSheetReaderWriter.getLosses(currentUsername);
      fastestTime = spreadSheetReaderWriter.getFastest(currentUsername);
      String[] historyWords = spreadSheetReaderWriter.getHistory(currentUsername).split(",");
      String currentWord = spreadSheetReaderWriter.getHistory(currentUsername);

      // Calculate games
      totalGames = usersWins + usersLosses;

      if (totalGames > 0) {
        winRate = (((double) usersWins * 100) / (double) totalGames);
      }

      // Update Labels

      winLabel.setText(Integer.toString(usersWins));
      gameLabel.setText(Integer.toString(totalGames));
      winstreakLabel.setText(Integer.toString(streak));
      winrateLabel.setText(df.format(winRate) + "%");

      if (fastestTime == 100) { // value will be 100 by default eg they must play a game
        fastestLabel.setText("-");
      } else {
        fastestLabel.setText(fastestTime + "s");
      }

      // Add current word to history of words
      if (!currentWord.equals("none")) {
        historyListView.getItems().addAll(historyWords);
      }
      // Set badges
      setBadges();
      badgeLabel.setText("You've unlocked " + numberBadges + "/15" + " Badges");

      // Calculate percentage of badges completed
      double badgeRatio = (((double) numberBadges * 100) / (double) 15);
      badgePercentage.setText("(" + String.format("%.0f", badgeRatio) + "%)");
      badgeProgress.setProgress(badgeRatio / 100);

    } else {
      // If user is not signed in
      this.usernameLabel.setText("Guest");
      File file = new File("src/main/resources/images/ProfilePics/GuestPic.png");
      Image image = new Image(file.toURI().toString());
      userImage.setImage(image);

      winLabel.setText("-");
      fastestLabel.setText("-");
      gameLabel.setText("-");
      winrateLabel.setText("-");
      winstreakLabel.setText("-");
      easyWinLabel.setText("-");
      mediumWinLabel.setText("-");
      hardWinLabel.setText("-");
      masterWinLabel.setText("-");

      // Set badges
      badgeLabel.setText("You've unlocked 0/15 Badges");
      badgePercentage.setText("(0%)");
      setAllBadgesClear();
    }
  }

  /**
   * switch to previous page
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

  /** initialize or disconnect the tts feature */
  @FXML
  private void onTextToSpeech() {
    textToSpeech = !textToSpeech; // inverts boolean of text to speech
    if (textToSpeech) { // sets label accordingly
      textToSpeechLabel.setText("ON");
    } else {
      textToSpeechLabel.setText("OFF");
    }
  }

  /** Method to display selected word in list of words */
  @FXML
  private void onSelectWord() {
    String word = historyListView.getSelectionModel().getSelectedItem();

    if (word == null || word.isEmpty()) {
      historyLabel.setText("Nothing Selected");
    } else {
      historyLabel.setText(word);
    }
  }

  /** set all badges */
  private void setAllBadgesClear() {
    // streak badges set to transparent
    fiveStreak.setOpacity(0.2);
    tenStreak.setOpacity(0.2);
    fiftyStreak.setOpacity(0.2);
    hundredStreak.setOpacity(0.2);

    // Games played to be added
    fiveGames.setOpacity(0.2);
    tenGames.setOpacity(0.2);
    fiftyGames.setOpacity(0.2);
    hundredGames.setOpacity(0.2);
    // difficulty badges set to transparent
    easyWins.setOpacity(0.2);
    mediumWins.setOpacity(0.2);
    hardWins.setOpacity(0.2);
    masterWins.setOpacity(0.2);

    // fastest game badges set to transparent
    secThirty.setOpacity(0.2);
    secTen.setOpacity(0.2);

    // Special badges set to transparent
    godArtist.setOpacity(0.2);
  }

  /**
   * initialize all the badges
   *
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If the user cannot be found on the file system.
   */
  private void setBadges() throws IOException, CsvException {
    setTimeBadges();
    setGamesPlayedBadges();
    setWinStreakBadges();
    setDifficultWinBadges();
    setExtraBadges();
  }

  /**
   * initialize extra badges
   *
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If the user cannot be found on the file system.
   */
  private void setExtraBadges() throws IOException, CsvException {
    godArtist.setOpacity(0.2);

    SpreadSheetReaderWriter sheetReaderWriter = new SpreadSheetReaderWriter();
    int[] difficultyWins = sheetReaderWriter.getDifWins(currentUsername);
    int count = 0;
    for (int i : difficultyWins) {
      if (i >= 100) {
        count++;
      }
    }
    if (count == 4) {
      godArtist.setOpacity(1);
    }
  }

  /**
   * initialize difficulties and win/loss badges
   *
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If the user cannot be found on the file system.
   */
  private void setDifficultWinBadges() throws IOException, CsvException {
    easyWins.setOpacity(0.2);
    mediumWins.setOpacity(0.2);
    hardWins.setOpacity(0.2);
    masterWins.setOpacity(0.2);

    SpreadSheetReaderWriter sheetReaderWriter = new SpreadSheetReaderWriter();
    int[] difficultyWins = sheetReaderWriter.getDifWins(currentUsername);
    easyWinLabel.setText(String.valueOf(difficultyWins[0]));
    mediumWinLabel.setText(String.valueOf(difficultyWins[1]));
    hardWinLabel.setText(String.valueOf(difficultyWins[2]));
    masterWinLabel.setText(String.valueOf(difficultyWins[3]));
    if (difficultyWins[0] >= 10) { // easy wins
      easyWins.setOpacity(1);
    }
    if (difficultyWins[1] >= 10) { // medium wins
      mediumWins.setOpacity(1);
    }
    if (difficultyWins[2] >= 10) { // hard wins
      hardWins.setOpacity(1);
    }
    if (difficultyWins[3] >= 10) { // master wins
      masterWins.setOpacity(1);
    }
  }

  /**
   * initialize win streak badges
   *
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If the user cannot be found on the file system.
   */
  private void setWinStreakBadges() throws IOException, CsvException {
    SpreadSheetReaderWriter sheetReaderWriter = new SpreadSheetReaderWriter();
    int streak = sheetReaderWriter.getStreak(currentUsername);
    // set all transparent
    fiveStreak.setOpacity(0.2);
    tenStreak.setOpacity(0.2);
    fiftyStreak.setOpacity(0.2);
    hundredStreak.setOpacity(0.2);
    if (streak >= 5) { // 5 win streak
      fiveStreak.setOpacity(1);
      numberBadges++;
    }
    if (streak >= 10) { // 10 win streak
      tenStreak.setOpacity(1);
      numberBadges++;
    }
    if (streak >= 50) { // 50 win streak
      fiftyStreak.setOpacity(1);
      numberBadges++;
    }
    if (streak >= 100) {
      hundredStreak.setOpacity(1);
      numberBadges++;
    }
  }

  /**
   * initialize the badges relate to the number of game played
   *
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If the user cannot be found on the file system.
   */
  private void setGamesPlayedBadges() throws IOException, CsvException {
    SpreadSheetReaderWriter sheetReaderWriter = new SpreadSheetReaderWriter();
    int games =
        sheetReaderWriter.getWins(currentUsername) + sheetReaderWriter.getLosses(currentUsername);
    // set all transparent
    fiveGames.setOpacity(0.2);
    tenGames.setOpacity(0.2);
    fiftyGames.setOpacity(0.2);
    hundredGames.setOpacity(0.2);

    if (games >= 5) { // 5 games played badge
      fiveGames.setOpacity(1);
      numberBadges++;
    }
    if (games >= 10) { // 10 games played badge
      tenGames.setOpacity(1);
      numberBadges++;
    }
    if (games >= 50) { // 50 games played badge
      fiftyGames.setOpacity(1);
      numberBadges++;
    }
    if (games >= 100) { // 100 games played badge
      hundredGames.setOpacity(1);
      numberBadges++;
    }
  }

  /**
   * initialize the badges relate to remaining time
   *
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If the user cannot be found on the file system.
   */
  private void setTimeBadges() throws IOException, CsvException {
    SpreadSheetReaderWriter sheetReaderWriter = new SpreadSheetReaderWriter();
    int fastest = sheetReaderWriter.getFastest(currentUsername);
    // set all transparent
    secThirty.setOpacity(0.2);
    secTen.setOpacity(0.2);
    if (fastest <= 30) {
      secThirty.setOpacity(1);
      numberBadges++;
      // 30 second badge
    }
    if (fastest <= 10) {
      secTen.setOpacity(1);
      numberBadges++;
      // 10 second badge
    }
  }

  /** label speaks out and button style changes when mouse hovers on */
  @FXML
  private void onHoverBack() {
    textToSpeechBackground.backgroundSpeak("Back Button", textToSpeech);
    backButton.setStyle(
        "-fx-background-radius: 100px; -fx-text-fill: white; "
            + "-fx-border-radius: 100px; -fx-background-color: #99DAF4; -fx-border-color: #99DAF4;");
  }

  /** button style changes when mouse hovers on */
  @FXML
  private void onBackExit() {
    backButton.setStyle(
        "-fx-background-radius: 100px; -fx-background-color: #EB4A5A;"
            + " -fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 100px;");
  }

  /** image restores its size when mouse is away */
  @FXML
  private void onVolumeExit() {
    volumeImage.setFitHeight(45);
    volumeImage.setFitWidth(45);
  }

  /** label speaks out when mouse hovers on */
  @FXML
  private void onHoverUser() {
    textToSpeechBackground.backgroundSpeak(currentUsername, textToSpeech);
  }

  /** label speaks out when mouse hovers on */
  @FXML
  private void onHoverWinRate() {
    textToSpeechBackground.backgroundSpeak(winRate + "win rate", textToSpeech);
  }

  /** label speaks out when mouse hovers on */
  @FXML
  private void onHoverHistory() {
    textToSpeechBackground.backgroundSpeak("History of words played", textToSpeech);
  }

  /** label speaks out when mouse hovers on */
  @FXML
  private void onHoverTitle() {
    textToSpeechBackground.backgroundSpeak("Just Draw", textToSpeech);
  }

  /** label speaks out when mouse hovers on */
  @FXML
  private void onHoverGamesPlayed() {
    textToSpeechBackground.backgroundSpeak("Played" + totalGames + "Games", textToSpeech);
  }

  /** label speaks out when mouse hovers on */
  @FXML
  private void onHoverGamesWon() {
    textToSpeechBackground.backgroundSpeak("you have won" + usersWins, textToSpeech);
  }

  /** label speaks out when mouse hovers on */
  @FXML
  private void onHoverFastest() {
    textToSpeechBackground.backgroundSpeak(
        "fastest game was " + fastestTime + "seconds", textToSpeech);
  }
}
