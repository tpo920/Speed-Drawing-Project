package nz.ac.auckland.se206;

import ai.djl.ModelException;
import ai.djl.modality.Classifications;
import ai.djl.modality.Classifications.Classification;
import ai.djl.translate.TranslateException;
import com.opencsv.exceptions.CsvException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import nz.ac.auckland.se206.ml.DoodlePrediction;
import nz.ac.auckland.se206.speech.TextToSpeechBackground;
import nz.ac.auckland.se206.words.ZenWordPageController;

public class ZenCanvasController {

  @FXML private Pane upPane;
  @FXML private Pane downPane;

  @FXML private Button backButton;
  @FXML private Button mainmenuButton;
  @FXML private Button saveButton;
  @FXML private Button penButton;
  @FXML private Button eraseButton;

  @FXML private Label wordLabel;
  @FXML private Label topTenLabel;
  @FXML private Label userLabel;
  @FXML private Label textToSpeechLabel;
  @FXML private Label speakerLabel;
  @FXML private Canvas zenCanvas;

  @FXML private ImageView volumeImage;
  @FXML private ImageView userImage;
  @FXML private ImageView penImage;
  @FXML private ImageView eraseImage;
  @FXML private ImageView clearImage;
  @FXML private ImageView upArrow;
  @FXML private ImageView downArrow;
  @FXML private ColorPicker colorPicker;

  private DoodlePrediction model;
  private GraphicsContext graphic;
  private Boolean textToSpeech = false;
  private TextToSpeechBackground textToSpeechBackground;
  private String currentUsername = null;
  private String currentProfilePic;
  private String currentWord;

  private boolean pen = true;
  private boolean startedDrawing;

  // mouse coordinates for drawings
  private double currentX;
  private double currentY;
  private double lastWordPred = 0;

  /**
   * JavaFX calls this method once the GUI elements are loaded. In our case we create a listener for
   * the drawing, and we load the ML model.
   *
   * @throws ModelException If there is an error in reading the input/output of the DL model.
   * @throws IOException If the model cannot be found on the file system.
   */
  public void initialize() throws ModelException, IOException {
    graphic = zenCanvas.getGraphicsContext2D();
    setTool();
    model = new DoodlePrediction();

    // Set pen button
    penButton.setStyle("-fx-background-color: #99F4B3;");
    penImage.setFitHeight(71);
    penImage.setFitWidth(71);

    // Set up and down images
    upArrow.setOpacity(0.3);
    upPane.setOpacity(0.3);
    downArrow.setOpacity(0.3);
    downPane.setOpacity(0.3);

    // Set predict label
    speakerLabel.setText("");
  }

  /**
   * Sets word label from word passed
   *
   * @param word is the word passed from previous screen
   */
  public void setWordLabel(String word) {
    currentWord = word;
    wordLabel.setText(word);
  }

  /** this method generates and sets the functionalities of pen and eraser */
  private void setTool() {
    // save coordinates when mouse is pressed on the canvas
    zenCanvas.setOnMousePressed(
        e -> {
          currentX = e.getX();
          currentY = e.getY();
          if (!startedDrawing) {
            startedDrawing = true;
            doPredictions();
          }
        });
    zenCanvas.setOnMouseDragged(
        e -> {
          // Brush size (you can change this, it should not be too small or too large).
          final double size = 10;
          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;
          if (pen) {
            graphic.setStroke(colorPicker.getValue());
            graphic.setLineWidth(size);
            graphic.strokeLine(
                currentX, currentY, x, y); // Create a line that goes from the point (currentX, //
            // currentY) and (x,y)
          } else { // eraser
            graphic.setFill(Color.TRANSPARENT); // sets colour so that black won't be there
            graphic.clearRect(
                e.getX() - 10,
                e.getY() - 10,
                16,
                16); // then will clear a rectangle of 5 either side
            // of the pixel the user is on
          }
          // update the coordinates
          currentX = x;
          currentY = y;
        });
  }

  /**
   * switch to main menu
   *
   * @throws IOException If the model cannot be found on the file system.
   */
  @FXML
  public void onMainMenu() throws IOException {
    Stage stage = (Stage) mainmenuButton.getScene().getWindow();
    FXMLLoader loader =
        new FXMLLoader(App.class.getResource("/fxml/main_menu.fxml")); // creates a new instance of
    // main menu
    Scene scene = new Scene(loader.load(), 1000, 680);
    MainMenuController ctrl = loader.getController(); // need controller to pass information
    // may need to add code to pass though tts here
    ctrl.give(textToSpeechBackground, textToSpeech); // passes text to speech instance and boolean
    ctrl.getUsername(currentUsername, currentProfilePic);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * switch to zen word page
   *
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If file does not exist
   */
  @FXML
  public void onBack() throws IOException, CsvException {
    Stage stage = (Stage) backButton.getScene().getWindow();
    FXMLLoader loader =
        new FXMLLoader(App.class.getResource("/fxml/zen_word_page.fxml")); // creates a new instance
    // of
    // word page
    Scene scene = new Scene(loader.load(), 1000, 680);
    ZenWordPageController ctrl = loader.getController();
    ctrl.getUsername(currentUsername, currentProfilePic);
    ctrl.give(textToSpeechBackground, textToSpeech); // passes text to speech instance and boolean
    stage.setScene(scene);
    stage.show();
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
   * get and pass user's info
   *
   * @param username current logged in username
   * @param profilePic user customized profile picture
   */
  public void getUsername(String username, String profilePic) {
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
  }

  /** disconnects the pen and image restores its size when clicked */
  @FXML
  private void onSwitchToEraser() { // "https://www.flaticon.com/free-icons/eraser" title="eraser
    // icons">Eraser
    // icons
    //  created by Freepik - Flaticon
    pen = false;
    setTool();

    // Changes button to show it is clicked
    eraseButton.setStyle("-fx-background-color: #99F4B3;");
    eraseImage.setFitHeight(72); // enlarges button
    eraseImage.setFitWidth(72);
    penButton.setStyle("-fx-background-color: transparent; -fx-border-color: white;");
    penImage.setFitHeight(70); // enlarges button
    penImage.setFitWidth(70);
  }

  /** initializes the pen and image restores its size when clicked */
  @FXML
  private void
      onSwitchToPen() { // "https://www.flaticon.com/free-icons/brush" title="brush icons">Brush
    // icons
    // created by Freepik - Flaticon
    pen = true;
    setTool();

    // Change button
    penButton.setStyle("-fx-background-color: #99F4B3;");
    penImage.setFitHeight(72); // reactive
    penImage.setFitWidth(72);
    eraseButton.setStyle("-fx-background-color: transparent; -fx-border-color: white");
    eraseImage.setFitHeight(70); // reactive
    eraseImage.setFitWidth(70);
  }

  /** This method is called when the "Clear" button is pressed. */
  @FXML
  private void onClear() {
    graphic.clearRect(0, 0, zenCanvas.getWidth(), zenCanvas.getHeight());
  }

  /** save the image by choosing own location */
  @FXML
  public void onSave() {
    Stage stage = (Stage) saveButton.getScene().getWindow(); // gets the stage from the button
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Image");
    File file =
        fileChooser.showSaveDialog(
            stage); // shows a popup to allow user to choose where to save file
    if (file != null) {
      try {
        ImageIO.write(getCurrentSnapshot(), "bmp", file); // creates a new image to save to the
        // users location
      } catch (IOException ex) {
        System.out.println(ex.getMessage());
      }
    }
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

  /**
   * Get the current snapshot of the canvas.
   *
   * @return The BufferedImage corresponding to the current canvas content.
   */
  public BufferedImage getCurrentSnapshot() {
    final Image snapshot =
        zenCanvas.snapshot(null, null); // is the current image based on user drawing on the
    // canvas
    final BufferedImage image = SwingFXUtils.fromFXImage(snapshot, null);

    // Convert into a binary image.
    final BufferedImage imageBinary =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

    final Graphics2D graphics = imageBinary.createGraphics();

    graphics.drawImage(image, 0, 0, null);

    // To release memory we dispose.
    graphics.dispose();

    return imageBinary;
  }

  /** Still needs work to not make application lag */
  private void doPredictions() {
    Timeline time = new Timeline();
    time.setCycleCount(Timeline.INDEFINITE);
    time.stop();
    KeyFrame keyFrame =
        new KeyFrame(
            Duration.seconds(1), // new keyframe every second so lists will update every
            // second
            actionEvent -> {
              BufferedImage snapshot =
                  getCurrentSnapshot(); // uses main thread to get a snapshot of users
              // drawing
              Task<Void> backgroundTask =
                  new Task<>() { // will run the rest of the task in the background thread
                    // to ensure
                    // user can draw smoothly and no lag
                    @Override
                    protected Void call() {

                      List<Classification> list;
                      try {
                        list =
                            model.getPredictions(snapshot, 10); // uses the model to get predictions
                        // based on current user
                        // drawing
                      } catch (TranslateException e) {
                        throw new RuntimeException(e);
                      }
                      Platform.runLater(
                          () -> {
                            printTopTen(
                                list); // will run these methods in the main thread as they deal
                            // with updating javafx elements
                          });

                      return null;
                    }
                  };

              Thread backgroundThread = new Thread(backgroundTask);
              backgroundThread
                  .start(); // all the ML modeling will happen in a background thread to reduce lag
            });

    time.getKeyFrames().add(keyFrame);
    time.playFromStart();
  }

  /**
   * return the top ten prediction results store in the list
   *
   * @param list the list stores prediction results.
   */
  private void printTopTen(List<Classifications.Classification> list) {
    StringBuilder sb = new StringBuilder();
    sb.append(System.lineSeparator());
    int i = 1;
    for (Classifications.Classification classification :
        list) { // cycles through list and build string to print
      // top 10
      sb.append(i)
          .append(" : ")
          .append(classification.getClassName().replace("_", " ")) // replaces _ with spaces
          // to ensure a standard
          // format
          .append(System.lineSeparator());
      i++;
    }
    topTenLabel.setText(String.valueOf(sb)); // updates label to the new top 10
    updateWordPrediction(list);
  }

  /**
   * update the prediction label and indicates if the prediction is getting closer
   *
   * @param list the list stores prediction results.
   */
  private void updateWordPrediction(List<Classification> list) {
    double wordPred = 0;
    for (Classifications.Classification classification : list) {
      if (classification.getClassName().equals(currentWord)) {
        wordPred = classification.getProbability();
        break;
      }
    }

    if (wordPred > lastWordPred) { // increase
      upArrow.setOpacity(1);
      upPane.setOpacity(1);
      downArrow.setOpacity(0.3);
      downPane.setOpacity(0.3);
      // Set predict label
      speakerLabel.setText("You're getting closer!");
    } else if (wordPred == lastWordPred) {
      upArrow.setOpacity(0.3);
      upPane.setOpacity(0.3);
      downArrow.setOpacity(0.3);
      downPane.setOpacity(0.3);
      // Set predict label
      speakerLabel.setText("");
    } else { // decrease
      upArrow.setOpacity(0.3);
      upPane.setOpacity(0.3);
      downArrow.setOpacity(1);
      downPane.setOpacity(1);
      speakerLabel.setText("You're getting further!");
    }
    lastWordPred = wordPred;
  }

  /** label speaks out and image gets larger when mouse hovers on */
  @FXML
  private void onHoverClear() {
    textToSpeechBackground.backgroundSpeak("Clear Canvas", textToSpeech);
    clearImage.setFitHeight(83);
    clearImage.setFitWidth(83);
  }

  /** label speaks out and image gets larger when mouse hovers on */
  @FXML
  private void onHoverPen() {
    textToSpeechBackground.backgroundSpeak(
        "pen tool", textToSpeech); // uses background task to read name
    penButton.setStyle(" -fx-background-color: #99F4B3;");
    penImage.setFitHeight(72); // enlarges button to make reactive
    penImage.setFitWidth(72);
  }

  /** label speaks out and image gets larger when mouse hovers on */
  @FXML
  public void onHoverEraser() {
    textToSpeechBackground.backgroundSpeak(
        "eraser tool", textToSpeech); // uses background thread to read name
    eraseButton.setStyle("-fx-background-color: #99F4B3;");
    eraseImage.setFitHeight(72); // makes button reactive
    eraseImage.setFitWidth(72);
  }

  /** buttons style changes when mouse hovers on */
  @FXML
  private void onHoverSave() {
    textToSpeechBackground.backgroundSpeak("save button", textToSpeech);
    saveButton.setStyle(
        "-fx-border-radius: 10; fx-background-border: 10; -fx-background-color: #99F4B3; -fx-border-color: #99F4B3;");
  }

  /** label speaks out when mouse hovers on */
  @FXML
  private void onHoverTextToSpeechLabel() {
    textToSpeechBackground.backgroundSpeak("ON", textToSpeech);
  }

  /** label speaks out and image gets larger when mouse hovers on */
  @FXML
  private void onHoverTextToSpeech() {
    textToSpeechBackground.backgroundSpeak("toggle text to speech", textToSpeech);
    volumeImage.setFitHeight(48);
    volumeImage.setFitWidth(48);
  }

  /** button style and image size restores when mouse is away */
  @FXML
  private void exitPen() {
    if (!pen) { // if eraser is curently active
      penButton.setStyle("-fx-background-color: transparent; -fx-border-color: white");
      penImage.setFitHeight(70);
      penImage.setFitWidth(70);
    }
  }

  /** button style and image size restores when mouse is away */
  @FXML
  private void exitEraser() {
    if (pen) { // if pen too is active
      eraseButton.setStyle("-fx-background-color: transparent; -fx-border-color: white");
      eraseImage.setFitHeight(70);
      eraseImage.setFitWidth(70);
    }
  }

  /** image size restores when mouse is away */
  @FXML
  private void exitClear() {
    clearImage.setFitHeight(80);
    clearImage.setFitWidth(80);
  }

  /** image size restores when mouse is away */
  @FXML
  private void onVolumeExit() {
    volumeImage.setFitHeight(45);
    volumeImage.setFitWidth(45);
  }

  /** button style restores when mouse is away */
  @FXML
  private void onSaveExit() {
    saveButton.setStyle(
        "-fx-text-fill: white; -fx-background-color: transparent; "
            + "-fx-border-radius: 10; -fx-border-color: white");
  }

  @FXML
  private void onHoverBackButton() {
    textToSpeechBackground.backgroundSpeak("back button", textToSpeech);
  }

  @FXML
  private void onHoverMainMenuButton() {
    textToSpeechBackground.backgroundSpeak("main menu button", textToSpeech);
  }
}
