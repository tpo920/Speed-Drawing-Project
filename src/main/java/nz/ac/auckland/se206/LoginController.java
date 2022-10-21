package nz.ac.auckland.se206;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import nz.ac.auckland.se206.speech.TextToSpeechBackground;

public class LoginController {

  // Set name of the file to set user data
  private static final String fileName = "userdata.csv";

  /**
   * This method creates a blank csv file
   *
   * @throws IOException throws if there is no name
   */
  public static void createDataBase() throws IOException {
    // file object creation
    File file = new File(fileName);

    // file creation
    file.createNewFile();
  }

  /**
   * This method searches in the csv file for the username passed and returns a flag
   *
   * @param username the name of the person using the app
   * @return if name was found or not
   * @throws IOException if file name was empty
   */
  private static Boolean searchUsername(String username) throws IOException {
    boolean flag = false;
    FileReader fr;

    fr = new FileReader(fileName); // starts a file reader to scan the spread sheet for the username
    BufferedReader br = new BufferedReader(fr);

    String line;
    while ((line = br.readLine()) != null) {
      // Check if current line contains the username to be found
      String[] record = line.split(",");
      String tempUsername = record[0]; // username is stored in first pos of array
      tempUsername = tempUsername.substring(1, (tempUsername.length() - 1));

      if (username.equals(tempUsername)) {
        flag = true;
      }
    }
    br.close();
    return flag;
  }

  @FXML private Button newuserButton;
  @FXML private Button loginButton;
  @FXML private Button logoutButton;
  @FXML private Button backButton;
  @FXML private TextField usernameText;
  @FXML private ImageView volumeImage;
  @FXML private ImageView userImage;
  @FXML private ListView<String> userListView;
  @FXML private Label outputLabel;
  @FXML private Label userLabel;
  @FXML private Label textToSpeechLabel;
  @FXML private Label profileLabel;

  private String currentUsername = null; // The username currently logged in
  private String currentProfilePic = null;
  private ArrayList<String> profilePicData;
  private Boolean textToSpeech;
  private TextToSpeechBackground textToSpeechBackground;

  /**
   * create the current username and select a profile picture
   *
   * @param username current username
   * @param profilePic profile picture selected
   */
  public void setUsername(String username, String profilePic) {
    // Set current username
    currentUsername = username;

    // Check if user is signed in
    if (currentUsername != null) {
      profileLabel.setText(currentUsername);
      userLabel.setText(currentUsername);
      // Set profile pic
      File file = new File(profilePic);
      Image image = new Image(file.toURI().toString());
      userImage.setImage(image);
      currentProfilePic = profilePic;
    } else {
      // load guest mode
      profileLabel.setText("Guest");
      userLabel.setText("Guest");
      File file = new File("src/main/resources/images/ProfilePics/GuestPic.png");
      Image image = new Image(file.toURI().toString());
      userImage.setImage(image);
    }
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
   * search and display usernames and picture
   *
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvValidationException If file name does not exist
   */
  public void displayUsers() throws IOException, CsvValidationException {
    ArrayList<String> usernameData = new ArrayList<String>();
    this.profilePicData = new ArrayList<String>();
    CSVReader csvReader = new CSVReader(new FileReader(fileName));

    // read line by line
    String[] record = null;
    while ((record = csvReader.readNext()) != null) {
      usernameData.add(record[0]);
      profilePicData.add(record[16]);
    }

    // Display list of users if not empty
    if (usernameData.size() > 0) {
      userListView.getItems().addAll(usernameData);
    }
  }

  /**
   * Methods goes to new user page
   *
   * @throws IOException If the model cannot be found on the file system.
   */
  @FXML
  private void onNewUser() throws IOException {
    Stage stage = (Stage) newuserButton.getScene().getWindow();
    FXMLLoader loader =
        new FXMLLoader(App.class.getResource("/fxml/create_user_page.fxml")); // creates a new
    // instance of
    // word page
    Scene scene = new Scene(loader.load(), 1000, 680);
    CreateUserController ctrl = loader.getController(); // need controller to pass information
    ctrl.give(textToSpeechBackground, textToSpeech);
    ctrl.setUsername(currentUsername, currentProfilePic);
    stage.setScene(scene);
    stage.show();
  }

  /** Method to display selected word in list ofusers */
  @FXML
  private void onSelectWord() {
    String word = userListView.getSelectionModel().getSelectedItem();

    if (word == null || word.isEmpty()) {
      profileLabel.setText("Guest");
    } else {
      profileLabel.setText(word);
      int currentIndex = userListView.getSelectionModel().getSelectedIndex();

      // Set profile pic
      File file = new File(profilePicData.get(currentIndex));
      Image image = new Image(file.toURI().toString());
      userImage.setImage(image);
    }
  }

  /** show the notifications to the user that whether logged in successfully */
  @FXML
  private void onLogin() {
    String username = userListView.getSelectionModel().getSelectedItem();

    if (username == null || username.isEmpty()) { // if user not selected
      outputLabel.setText("User not selected");
      outputLabel.setStyle("-fx-text-fill: red;");
      outputLabel.setOpacity(0.5);
    } else {
      if (!username.equals(currentUsername)) {
        // logged in successfully
        outputLabel.setText("Login Success");
        outputLabel.setStyle("-fx-text-fill: green;");
        outputLabel.setOpacity(0.5);
        userLabel.setText(username);

        // Set username and profile picture
        int currentIndex = userListView.getSelectionModel().getSelectedIndex();
        currentProfilePic = profilePicData.get(currentIndex);
        this.setUsername(username, currentProfilePic);
      } else {
        // user already logged in
        outputLabel.setText("Already logged in");
        outputLabel.setStyle("-fx-text-fill: red;");
        outputLabel.setOpacity(0.5);
      }
    }
  }

  /** show the notifications to the user that whether logged out successfully */
  @FXML
  private void onLogout() {
    if (currentUsername != null) { // logs user out sets to guest
      currentUsername = null;
      outputLabel.setText("Logout Success");
      outputLabel.setStyle("-fx-text-fill: green;");
      outputLabel.setOpacity(0.5);
      userLabel.setText("Guest");

      // Update label
      profileLabel.setText("Guest");
      this.setUsername(currentUsername, null);

    } else { // user was previously a guest
      outputLabel.setText("You are not signed in");
      outputLabel.setStyle("-fx-text-fill: red;");
      outputLabel.setOpacity(0.5);
    }
  }

  /**
   * This method goes to the previous page
   *
   * @throws IOException if name of file is not found
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

  /** label speaks out when mouse is moved on */
  @FXML
  private void onHoverTextToSpeechLabel() {
    textToSpeechBackground.backgroundSpeak("toggle text to speech", textToSpeech);
  }

  /** label speaks out and image becomes slightly larger when mouse is moved on */
  @FXML
  private void onHoverTextToSpeech() {
    textToSpeechBackground.backgroundSpeak("On", textToSpeech);
    volumeImage.setFitHeight(48);
    volumeImage.setFitWidth(48);
  }

  /** label speaks out and button style changes when mouse hovers on */
  @FXML
  private void onHoverLogin() {
    textToSpeechBackground.backgroundSpeak("Login", textToSpeech);
    loginButton.setStyle(
        "-fx-background-radius: 10;  -fx-background-color: #EB4A5A; -fx-text-fill: white; "
            + "-fx-border-color: white; -fx-border-radius: 10; -fx-border-width: 3; -fx-opacity: 0.5;");
  }

  /** label speaks out and button style changes when mouse hovers on */
  @FXML
  private void onHoverNewUser() {
    textToSpeechBackground.backgroundSpeak("Create", textToSpeech);
    newuserButton.setStyle(
        "-fx-background-radius: 10;  -fx-background-color: #99DAF4; "
            + "-fx-text-fill: white; -fx-border-color: #99DAF4; -fx-border-radius: 10; -fx-border-width: 3;");
  }

  /** label speaks out and button style changes when mouse hovers on */
  @FXML
  private void onHoverLogout() {
    textToSpeechBackground.backgroundSpeak("Logout", textToSpeech);
    logoutButton.setStyle(
        "-fx-background-radius: 10; -fx-background-color: #EB4A5A;"
            + " -fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 10; -fx-border-width: 3; -fx-opacity: 0.5;");
  }

  /** label speaks out and button style changes when mouse hovers on */
  @FXML
  private void onHoverBack() {
    textToSpeechBackground.backgroundSpeak("Back", textToSpeech);
    backButton.setStyle(
        "-fx-background-radius: 100px;  -fx-text-fill: white; "
            + "-fx-border-radius: 100px; -fx-background-color: #99DAF4; -fx-border-color: #99DAF4;");
  }

  /** button style changes when mouse is away */
  @FXML
  private void onLoginExit() {
    loginButton.setStyle(
        "-fx-background-radius: 10; -fx-background-color: #EB4A5A; "
            + "-fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 10; -fx-border-width: 3; -fx-opacity: 1;");
  }

  /** button style changes when mouse is away */
  @FXML
  private void onNewUserExit() {
    newuserButton.setStyle(
        "-fx-background-radius: 10; -fx-background-color: #EB4A5A; "
            + "-fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 10; -fx-border-width: 3");
  }

  /** button style changes when mouse is away */
  @FXML
  private void onLogoutExit() {
    logoutButton.setStyle(
        "-fx-background-radius: 10; -fx-background-color: #EB4A5A; "
            + "-fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 10; -fx-border-width: 3; -fx-opacity: 1;");
  }

  /** button style changes when mouse is away */
  @FXML
  private void onBackExit() {
    backButton.setStyle(
        "-fx-background-radius: 100px; -fx-background-color: #EB4A5A; "
            + "-fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 100px;");
  }

  /** image restores its size when mouse is away */
  @FXML
  private void onVolumeExit() {
    volumeImage.setFitHeight(45);
    volumeImage.setFitWidth(45);
  }
}
