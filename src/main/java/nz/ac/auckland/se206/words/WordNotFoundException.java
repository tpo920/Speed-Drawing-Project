package nz.ac.auckland.se206.words;

public class WordNotFoundException extends Exception {

  private static final long serialVersionUID = 1L;
  private String word;
  private String subMessage;

  /**
   * Exception constructor
   *
   * @param word word to be drew
   * @param message alert message 1
   * @param subMessage alert message 2
   */
  WordNotFoundException(String word, String message, String subMessage) {
    super(message);
    this.word = word;
    this.subMessage = subMessage;
  }

  /**
   * gets the word to be drew
   *
   * @return word to be drew
   */
  public String getWord() {
    return word;
  }

  /**
   * get the second alert message
   *
   * @return alert message 2
   */
  public String getSubMessage() {
    return subMessage;
  }
}
