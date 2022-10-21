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
import java.net.URISyntaxException;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
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
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import nz.ac.auckland.se206.ml.DoodlePrediction;
import nz.ac.auckland.se206.speech.TextToSpeech;
import nz.ac.auckland.se206.speech.TextToSpeechBackground;
import nz.ac.auckland.se206.words.CategorySelector;
import nz.ac.auckland.se206.words.DictionaryLookup;
import nz.ac.auckland.se206.words.WordEntry;
import nz.ac.auckland.se206.words.WordInfo;

public class HiddenWordCanvasController {

  @FXML private Button hintButton;
  @FXML private Text hintText;
  @FXML private TextArea definitionTextArea;
  @FXML private Pane upPane;
  @FXML private Pane downPane;

  @FXML private ImageView downArrow;
  @FXML private ImageView upArrow;
  @FXML private ImageView userImage;
  @FXML private ImageView penImage;
  @FXML private ImageView eraseImage;
  @FXML private ImageView clearImage;
  @FXML private ImageView volumeImage;

  @FXML private Canvas canvas;

  @FXML private Label timerLabel;
  @FXML private Label userLabel;
  @FXML private Label topTenLabel;
  @FXML private Label textToSpeechLabel;
  @FXML private Label speakerLabel;
  @FXML private Label noHintLabel;

  @FXML private Button penButton;
  @FXML private Button eraseButton;
  @FXML private ColorPicker colorPicker;

  @FXML private Arc timerArc;

  private GraphicsContext graphic;
  private DoodlePrediction model;
  private String currentWord;

  private boolean winLose = false;
  private boolean end = false;
  private boolean pen = true;
  private boolean textToSpeech;
  private boolean startedDrawing;

  private TextToSpeechBackground textToSpeechBackground;

  private TextToSpeech textToSpeechAlert;

  private String currentUsername = null;
  private String currentProfilePic;

  private int userAccuracy;
  private int confidence;
  private int words;
  private int time;
  private int overallDif;
  private int seconds;

  private double currentX;
  private double currentY;
  private double confidenceUser;
  private double lastWordPred = 0;
  private char hint2;
  private char hint3;
  private int hints;

  /**
   * JavaFX calls this method once the GUI elements are loaded. In our case we create a listener for
   * the drawing, and we load the ML model.
   *
   * @throws ModelException If there is an error in reading the input/output of the DL model.
   * @throws IOException If the model cannot be found on the file system.
   */
  public void initialize() throws ModelException, IOException {
    graphic = canvas.getGraphicsContext2D();
    setTool(); // calls method to set pen/eraser and size

    // Set pen button
    penButton.setStyle("-fx-background-color: #99F4B3;");
    penImage.setFitHeight(71);
    penImage.setFitWidth(71);
    model = new DoodlePrediction();
    setTimerLabel(seconds); // sets timer to specified number of seconds
    doTimer();

    // Set up and down images
    upArrow.setOpacity(0.3);
    upPane.setOpacity(0.3);
    downArrow.setOpacity(0.3);
    downPane.setOpacity(0.3);
  }

  /** this method generates and sets the functionalities of pen and eraser */
  private void setTool() {
    // save coordinates when mouse is pressed on the canvas
    canvas.setOnMousePressed(
        e -> {
          currentX = e.getX();
          currentY = e.getY();
          if (!startedDrawing) {
            startedDrawing = true;
            doPredictions();
          }
        });
    canvas.setOnMouseDragged(
        e -> {
          // Brush size (you can change this, it should not be too small or too large).
          final double size = 10;
          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;
          // This is the colour of the brush.
          if (pen) {
            graphic.setStroke(colorPicker.getValue());
            graphic.setLineWidth(size);
            graphic.strokeLine(
                currentX, currentY, x, y); // Create a line that goes from the point (currentX,
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
   * Get the current snapshot of the canvas.
   *
   * @return The BufferedImage corresponding to the current canvas content.
   */
  public BufferedImage getCurrentSnapshot() {
    final Image snapshot =
        canvas.snapshot(null, null); // is the current image based on user drawing on the canvas
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

  /**
   * main Timer label is set through
   *
   * @param time value of timer
   */
  private void setTimerLabel(int time) {
    timerLabel.setText(String.valueOf(time));
    textToSpeechAlert = new TextToSpeech();

    // make sure the GUI won't freeze
    Task<Void> backgroundTask =
        new Task<>() {

          @Override
          protected Void call() {
            // speaks out how much time left
            if (time == 40) {
              textToSpeechAlert.speak("forty seconds left");
            }
            if (time == 20) {
              textToSpeechAlert.speak("twenty seconds left");
            }
            if (time == 10) {
              textToSpeechAlert.speak("ten seconds left");
            }

            return null;
          }
        };

    Thread backgroundThread = new Thread(backgroundTask);
    backgroundThread.start();
  }

  /** runs timer through timeline for 60secs until seconds = 0 */
  private void doTimer() {
    Timeline time = new Timeline();
    Timeline timeArc = new Timeline();
    time.setCycleCount(Timeline.INDEFINITE);
    timeArc.setCycleCount(Timeline.INDEFINITE);
    time.stop();
    timeArc.stop();

    KeyFrame keyFrame =
        new KeyFrame(
            Duration.seconds(1),
            actionEvent -> {
              seconds--;
              setTimerLabel(seconds); // decrements the timer and updates label
              if (end) {
                time.stop(); // if the game is over or time is up the timer stops
                timeArc.stop();
              }
              if (seconds <= 0) { // timer is over then end timer
                time.stop();
                timeArc.stop();
                end = true;
                try {
                  whenTimerEnds(); // runs to progress to next page
                } catch (IOException | CsvException e) {
                  throw new RuntimeException(e);
                }
              }
            });
    KeyValue kv = new KeyValue(timerArc.lengthProperty(), -360);
    KeyFrame kf = new KeyFrame(Duration.seconds(60), kv);
    timeArc.getKeyFrames().add(kf);
    time.getKeyFrames().add(keyFrame);
    time.playFromStart();
    timeArc.playFromStart();
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
                      if (end) {
                        time.stop(); // if the timer is up ends the predictions
                      } else {
                        List<Classification> list;
                        try {
                          list =
                              model.getPredictions(
                                  snapshot, 345); // uses the model to get predictions
                          // based on current user
                          // drawing
                        } catch (TranslateException e) {
                          throw new RuntimeException(e);
                        }
                        Platform.runLater(
                            () -> {
                              printTopTen(
                                  list); // will run these methods in the main thread as they deal
                              // wil updating javafx elements
                              try {
                                getTopX(list);
                              } catch (IOException | CsvException e) {
                                throw new RuntimeException(e);
                              }
                            });
                      }
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
   * this methods gets and reads if the result is within top x of the list.
   *
   * @param list the list stores prediction results.
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If the user info cannot be found locally
   */
  private void getTopX(List<Classifications.Classification> list) throws IOException, CsvException {
    for (int i = 0; i < userAccuracy; i++) { // cycles through top 3
      String strNew =
          list.get(i)
              .getClassName()
              .replace("_", " "); // replaces _ with spaces to ensure a standard
      // format
      if (strNew.equals(currentWord)) {
        // tests to see if the word the user is trying to draw is in the top 3
        if (list.get(i).getProbability() >= confidenceUser) {
          winLose = true;
          whenTimerEnds(); // called early to end game
          end = true;
        }
      }
    }
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
      if (i == 11) {
        break;
      }
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

  /**
   * When timer reaches 0secs, user will jump into the game over page
   *
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If the user info cannot be found locally.
   */
  private void whenTimerEnds() throws IOException, CsvException {
    BufferedImage bufferedImage = getCurrentSnapshot();
    Stage stage =
        (Stage) hintButton.getScene().getWindow(); // finds current stage from the word label
    FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/game_over.fxml"));
    Scene scene = new Scene(loader.load(), 1000, 680);
    stage.setScene(scene);
    stage.show();
    GameOverController gameOverController =
        loader.getController(); // gets controller from loader to pass through
    // information
    gameOverController.getUsername(currentUsername, currentProfilePic);
    gameOverController.give(
        textToSpeechBackground, textToSpeech, bufferedImage); // passes text to speech and
    // boolean
    gameOverController.timeLeft(seconds);
    gameOverController.setHiddenWinLoseLabel(winLose, this, overallDif);
    gameOverController.setTimeAccuracy(time, userAccuracy, confidence, words);
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

  /** image becomes slightly larger when mouse is moved on */
  @FXML
  private void onHoverClear() {
    textToSpeechBackground.backgroundSpeak("Clear Canvas", textToSpeech);
    clearImage.setFitHeight(73);
    clearImage.setFitWidth(73);
  }

  /** label speaks out when mouse is moved on */
  @FXML
  private void onHoverTimer() {
    textToSpeechBackground.backgroundSpeak(String.valueOf(seconds), textToSpeech);
  }

  /** label speaks out and image becomes slightly larger when mouse is moved on */
  @FXML
  private void onHoverPen() {
    textToSpeechBackground.backgroundSpeak(
        "pen tool", textToSpeech); // uses background task to read name
    penButton.setStyle(" -fx-background-color: #99F4B3;");
    penImage.setFitHeight(72); // enlarges button to make reactive
    penImage.setFitWidth(72);
  }

  /** label speaks out and image becomes slightly larger when mouse is moved on */
  @FXML
  public void onHoverEraser() {
    textToSpeechBackground.backgroundSpeak(
        "eraser tool", textToSpeech); // uses background thread to read name
    eraseButton.setStyle("-fx-background-color: #99F4B3;");
    eraseImage.setFitHeight(72); // makes button reactive
    eraseImage.setFitWidth(72);
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
    textToSpeechBackground.backgroundSpeak("ON", textToSpeech);
  }

  /** label speaks out and image becomes slightly larger when mouse is moved on */
  @FXML
  private void onHoverTextToSpeech() {
    textToSpeechBackground.backgroundSpeak("toggle text to speech", textToSpeech);
    volumeImage.setFitHeight(48);
    volumeImage.setFitWidth(48);
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

  /** disconnects the pen and image restores its size when clicked */
  @FXML
  private void onSwitchToEraser() { // "https://www.flaticon.com/free-icons/eraser" title="eraser
    // icons">Eraser
    // icons
    // created by Freepik - Flaticon
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

  /** This method is called when the "Clear" button is pressed. */
  @FXML
  private void onClear() {
    graphic.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
  } // https://www.flaticon.com/free-icons/recycle-bin title="recycle bin
  // icons">Recycle bin icons created by lakonicon - Flaticon

  /** image and button restores when clicked */
  @FXML
  private void exitPen() {
    if (!pen) { // if eraser is curently active
      penButton.setStyle("-fx-background-color: transparent; -fx-border-color: white");
      penImage.setFitHeight(70);
      penImage.setFitWidth(70);
    }
  }

  /** image and button restores when clicked */
  @FXML
  private void exitEraser() {
    if (pen) { // if pen too is active
      eraseButton.setStyle("-fx-background-color: transparent; -fx-border-color: white");
      eraseImage.setFitHeight(70);
      eraseImage.setFitWidth(70);
    }
  }

  /** image restores its size when mouse is away */
  @FXML
  private void exitClear() {
    clearImage.setFitHeight(80);
    clearImage.setFitWidth(80);
  }

  /** image restores its size when mouse is away */
  @FXML
  private void onVolumeExit() {
    volumeImage.setFitHeight(45);
    volumeImage.setFitWidth(45);
  }

  /**
   * set and pass the current difficulties combination
   *
   * @param time user has to draw within this time
   * @param accuracy results of drawing has to be within top x of the prediciton list
   * @param confidence user confidence percentage
   * @param words words category; E, M, H
   * @param overallDif current difficulty combination
   */
  public void setTimeAccuracy(int time, int accuracy, int confidence, int words, int overallDif) {
    seconds = time;
    this.time = time;
    userAccuracy = accuracy;
    this.confidence = confidence;
    switch (confidence) {
      case 1 -> this.confidenceUser = 0.01;
      case 10 -> this.confidenceUser = 0.1;
      case 25 -> this.confidenceUser = 0.25;
      case 50 -> this.confidenceUser = 0.5;
    }
    this.words = words;
    this.overallDif = overallDif;
  }

  /**
   * set and display the definition of the word
   *
   * @param wordToDraw current guessed word
   * @throws IOException If the model cannot be found on the file system.
   * @throws URISyntaxException cant find information
   * @throws CsvException cant find file
   */
  public void setDefinitionList(String wordToDraw)
      throws IOException, URISyntaxException, CsvException {
    currentWord = wordToDraw;
    // find definition of the word
    WordInfo wordResult = DictionaryLookup.searchWordInfo(wordToDraw);
    System.out.println("word is:" + wordResult.getWord());
    if (wordResult.getNumberOfEntries() == 0) {
      System.out.println("here 00");
      CategorySelector categorySelector = new CategorySelector();
      currentWord = categorySelector.setWordToDraw(currentUsername, words);
      setDefinitionList(currentWord);
    }
    List<WordEntry> list = wordResult.getWordEntries();
    WordEntry wordEntry = list.get(0);

    // adds the first definition to the text area
    definitionTextArea.setText(wordEntry.getDefinitions().get(0));

    // setting the hints
    String hint1 = (String.valueOf(wordToDraw.length()));
    hint2 = (wordToDraw.charAt(0));
    hint3 = (wordToDraw.charAt(wordToDraw.length() - 1));
  }

  /** new hint generated to help users on click */
  @FXML
  private void onClickHint() {
    String formattedString = this.getLength();

    if (hints == 0) { // first hint to help user
      hintText.setText(this.getLength());
      System.out.println(getLength());
      hints++;
      noHintLabel.setText(String.valueOf(hints));
    } else if (hints == 1) { // second hint to help user
      StringBuilder word = new StringBuilder(formattedString);
      word.setCharAt(0, hint2);
      hintText.setText(word.toString());
      hints++;
      noHintLabel.setText(String.valueOf(hints));
    } else if (hints == 2) { // third hint to help user
      StringBuilder word = new StringBuilder(formattedString);
      word.setCharAt(0, hint2);
      word.setCharAt((formattedString.length() - 1), hint3);
      hintText.setText(word.toString());
      hints++;
      noHintLabel.setText(String.valueOf(hints));
      hintButton.setOpacity(0.2); // grey out button
      hintButton.setDisable(true);
    }
  }

  /**
   * This method returns a formatted string for the hint
   *
   * @return String that is the length of word
   */
  private String getLength() {
    String formattedLength = "";
    int index = 0;
    // convert string to `char[]` array
    char[] letters = currentWord.toCharArray();

    // iterate over `char[]` array using enhanced for-loop
    for (char ch : letters) {
      if (index != (currentWord.length() - 1)) {
        // If Empty space
        if (ch == ' ') {
          formattedLength = formattedLength + " ";
        } else {
          formattedLength = formattedLength + "_ ";
        }
      } else {
        formattedLength = formattedLength + "_";
      }
      index++;
    }
    return formattedLength;
  }

  @FXML
  private void onHoverHint() {
    textToSpeechBackground.backgroundSpeak("hint", textToSpeech);
  }
}
