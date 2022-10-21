package nz.ac.auckland.se206.words;

import java.util.List;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class WordPane {

  /**
   * create and display information on a new pane
   *
   * @param word word to be drew
   * @param wordResult word definition
   * @return return the pane containing relevant info
   */
  public static TitledPane generateWordPane(String word, WordInfo wordResult) {
    List<WordEntry> entries = wordResult.getWordEntries();

    VBox boxForEntries = new VBox(entries.size());
    TitledPane pane = new TitledPane("?????", boxForEntries);

    for (int e = 0; e < entries.size(); e++) {
      WordEntry entry = entries.get(e);

      TextFlow textFlow = new TextFlow();

      Text title =
          new Text(
              "Entry "
                  + (e + 1)
                  + " of "
                  + entries.size()
                  + " ["
                  + entry.getPartOfSpeech()
                  + "]:"
                  + System.lineSeparator());
      title.setFont(Font.font("Verdana", FontWeight.BOLD, 13));

      StringBuffer definitions = new StringBuffer();
      boxForEntries.getChildren().add(textFlow);

      for (String definition : entry.getDefinitions()) {
        definitions.append("  ‣ ").append(definition).append(System.lineSeparator());
      }

      Text definitionText = new Text(definitions.toString());
      textFlow.getChildren().addAll(title, definitionText);
    }

    return pane;
  }

  /**
   * creates and displays a new pane containing alerts
   *
   * @param error word not found exception
   * @return return a pane saying the exception
   */
  public static TitledPane generateErrorPane(WordNotFoundException error) {

    TextFlow textFlow = new TextFlow();
    TitledPane pane =
        new TitledPane(error.getWord() + " (" + error.getMessage().toLowerCase() + ")", textFlow);

    Text text = new Text(error.getMessage() + System.lineSeparator());
    text.setFont(Font.font("Verdana", FontWeight.BOLD, 13));

    Text message = new Text(error.getSubMessage());

    textFlow.getChildren().addAll(text, message);
    return pane;
  }
}
