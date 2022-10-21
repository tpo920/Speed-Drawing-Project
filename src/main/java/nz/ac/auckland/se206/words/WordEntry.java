package nz.ac.auckland.se206.words;

import java.util.List;

public class WordEntry {

  private String partOfSpeech;
  private List<String> definitions;

  /**
   * constructor: a new instance of word entry
   *
   * @param partOfSpeech part of speech
   * @param definitions word definition
   */
  public WordEntry(String partOfSpeech, List<String> definitions) {
    this.partOfSpeech = partOfSpeech;
    this.definitions = definitions;
  }

  /**
   * get the current part of speech
   *
   * @return part of speech
   */
  public String getPartOfSpeech() {
    return partOfSpeech;
  }

  /**
   * get the current word definition
   *
   * @return word definition
   */
  public List<String> getDefinitions() {
    return definitions;
  }
}
