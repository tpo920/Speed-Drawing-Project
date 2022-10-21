package nz.ac.auckland.se206.words;

import java.util.List;

public class WordInfo {

  private String word;
  private List<WordEntry> entries;

  /**
   * WordInfo constructor
   *
   * @param word word to be drew
   * @param entries amount of different definitions
   */
  WordInfo(String word, List<WordEntry> entries) {
    this.word = word;
    this.entries = entries;
  }

  /**
   * gets the current word
   *
   * @return word to be drew
   */
  public String getWord() {
    return word;
  }

  /**
   * gets a list contains different definitions
   *
   * @return a list contains different definitions
   */
  public List<WordEntry> getWordEntries() {
    return entries;
  }

  /**
   * gets the amount of different definitions
   *
   * @return amount of different definitions
   */
  public int getNumberOfEntries() {
    return entries.size();
  }
}
