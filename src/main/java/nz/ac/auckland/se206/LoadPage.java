package nz.ac.auckland.se206;

import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nz.ac.auckland.se206.speech.TextToSpeechBackground;

public class LoadPage {

  /**
   * load the user info and text to speech functionalities into main menu
   *
   * @param textToSpeechBackground generates tts functionality from tts class
   * @param textToSpeech activates tts functionality if is true
   * @param currentUsername current username
   * @param currentProfilePic current user picture
   * @param stage the stage we need to jump in
   * @throws IOException If the model cannot be found on the file system.
   */
  public void extractedMainMenu(
      TextToSpeechBackground textToSpeechBackground,
      Boolean textToSpeech,
      String currentUsername,
      String currentProfilePic,
      Stage stage)
      throws IOException {
    FXMLLoader loader =
        new FXMLLoader(App.class.getResource("/fxml/main_menu.fxml")); // creates a new instance of
    // menu page
    Scene scene = new Scene(loader.load(), 1000, 680);
    MainMenuController ctrl = loader.getController(); // need controller to pass information
    // may need to add code to pass though tts here
    ctrl.give(textToSpeechBackground, textToSpeech); // passes text to speech instance and boolean
    ctrl.getUsername(currentUsername, currentProfilePic);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * load the user info and text to speech functionalities into profile page
   *
   * @param textToSpeechBackground generates tts functionality from tts class
   * @param textToSpeech activates tts functionality if is true
   * @param currentUsername current username
   * @param currentProfilePic current user picture
   * @param stage the stage we need to jump in
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If the user cannot be found locally.
   */
  public void extractedProfile(
      TextToSpeechBackground textToSpeechBackground,
      boolean textToSpeech,
      String currentUsername,
      String currentProfilePic,
      Stage stage)
      throws IOException, CsvException {
    FXMLLoader loader =
        new FXMLLoader(App.class.getResource("/fxml/profile_page.fxml")); // creates a new instance
    // of
    // menu page
    Scene scene = new Scene(loader.load(), 1000, 680);
    ProfilePageController ctrl = loader.getController(); // need controller to pass information
    // may need to add code to pass though tts here
    ctrl.give(textToSpeechBackground, textToSpeech); // passes text to speech instance and boolean
    ctrl.setUsername(currentUsername, currentProfilePic);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * load the user info and text to speech functionalities into login page
   *
   * @param textToSpeechBackground generates tts functionality from tts class
   * @param textToSpeech activates tts functionality if is true
   * @param currentUsername current username
   * @param currentProfilePic current user picture
   * @param stage the stage we need to jump in
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If the user cannot be found locally.
   */
  public void extractedLogin(
      TextToSpeechBackground textToSpeechBackground,
      boolean textToSpeech,
      String currentUsername,
      String currentProfilePic,
      Stage stage)
      throws IOException, CsvException {
    FXMLLoader loader =
        new FXMLLoader(App.class.getResource("/fxml/login_page.fxml")); // creates a new instance
    // of
    // login page
    Scene scene = new Scene(loader.load(), 1000, 680);
    LoginController ctrl = loader.getController(); // need controller to pass information
    // may need to add code to pass though tts here
    ctrl.give(textToSpeechBackground, textToSpeech); // passes text to speech instance and boolean
    ctrl.setUsername(currentUsername, currentProfilePic);
    ctrl.displayUsers();
    stage.setScene(scene);
    stage.show();
  }
}
