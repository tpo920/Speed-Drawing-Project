package nz.ac.auckland.se206.words;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import nz.ac.auckland.se206.SpreadSheetReaderWriter;

public class CategorySelector {

  public enum Difficulty {
    E,
    M,
    H
  }

  private final Map<Difficulty, List<String>> difficultyListMap;

  /**
   * select a category; E, M, or H
   *
   * @throws IOException If the model cannot be found on the file system.
   * @throws URISyntaxException If URI does not exist
   * @throws CsvException If file does not exist
   */
  public CategorySelector() throws IOException, URISyntaxException, CsvException {
    difficultyListMap = new HashMap<>();
    for (Difficulty difficulty :
        Difficulty.values()) { // Creates 3 array lists one for each difficulty
      difficultyListMap.put(difficulty, new ArrayList<>());
    }
    for (String[] line :
        getLines()) { // cycles through the csv file line by line to group each word into its
      // category
      difficultyListMap.get(Difficulty.valueOf(line[1])).add(line[0]);
    }
  }

  /**
   * gets all words from the chosen category
   *
   * @return all words from indicated category
   * @throws IOException If the model cannot be found on the file system.
   * @throws URISyntaxException If URI does not exist
   * @throws CsvException If file does not exist
   */
  protected List<String[]> getLines() throws IOException, CsvException, URISyntaxException {
    File file = new File(CategorySelector.class.getResource("/category_difficulty.csv").toURI());
    try (FileReader fr = new FileReader(file, StandardCharsets.UTF_8);
        CSVReader reader = new CSVReader(fr)) {
      return reader.readAll();
    }
  }

  /**
   * gets a random word from the chosen category
   *
   * @param difficulty word category; E, M, H
   * @return a random word
   */
  public String getRandomCategory(Difficulty difficulty) {
    return difficultyListMap
        .get(difficulty)
        .get(new Random().nextInt(difficultyListMap.get(difficulty).size()));
  }

  /**
   * gets a random word from all category with no repeated word (no history word)
   *
   * @param words word to be drew
   * @param history history words
   * @param currentUsername current user name
   * @return a random word from all category
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If file does not exist
   */
  public String getRandomCategory(int words, String[] history, String currentUsername)
      throws IOException, CsvException {
    ArrayList<Object> randomWords = new ArrayList<>();
    switch (words) {
      case 2 -> {
        randomWords.addAll(difficultyListMap.get(Difficulty.E));
        randomWords.addAll(difficultyListMap.get(Difficulty.M));
      }
      case 3 -> {
        randomWords.addAll(difficultyListMap.get(Difficulty.E));
        randomWords.addAll(difficultyListMap.get(Difficulty.M));
        randomWords.addAll(difficultyListMap.get(Difficulty.H));
      }
      case 4 -> randomWords.addAll(difficultyListMap.get(Difficulty.H));

      default -> randomWords.addAll(difficultyListMap.get(Difficulty.E));
    }
    for (String pastWord : history) {
      randomWords.remove(pastWord);
    }
    if (randomWords.size() == 0) {
      SpreadSheetReaderWriter spreadSheetReaderWriter = new SpreadSheetReaderWriter();
      spreadSheetReaderWriter.resetUserHistory(currentUsername);
    }
    return (String) randomWords.get(new Random().nextInt(randomWords.size()));
  }

  /**
   * get the current category
   *
   * @param difficulty category E, M, H
   * @return category; E, M, H
   */
  public List<String> getCategory(Difficulty difficulty) {
    return difficultyListMap.get(difficulty);
  }

  /**
   * Picks a random word from the easy category using category selector
   *
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If file does not exist
   * @throws URISyntaxException If URI does not exist
   */
  public String setWordToDraw(String currentUsername, int words)
      throws IOException, URISyntaxException, CsvException {
    String currentWord;
    if (currentUsername == null) { // if guest
      CategorySelector categorySelector = new CategorySelector(); // picks random word
      ArrayList<Object> randomWords = new ArrayList<>();
      switch (words) {
        case 1 -> // easy
        randomWords.add(categorySelector.getRandomCategory(CategorySelector.Difficulty.E));
        case 2 -> { // easy and medium
          randomWords.add(categorySelector.getRandomCategory(CategorySelector.Difficulty.E));
          randomWords.add(categorySelector.getRandomCategory(CategorySelector.Difficulty.M));
        }
        case 3 -> { // easy medium and hard
          randomWords.add(categorySelector.getRandomCategory(CategorySelector.Difficulty.E));
          randomWords.add(categorySelector.getRandomCategory(CategorySelector.Difficulty.M));
          randomWords.add(categorySelector.getRandomCategory(CategorySelector.Difficulty.H));
        }
        case 4 -> // just hard
        randomWords.add(categorySelector.getRandomCategory(CategorySelector.Difficulty.H));
      }
      currentWord = (String) randomWords.get(new Random().nextInt(randomWords.size()));
    } else { // if user chosen from their pool of words left
      SpreadSheetReaderWriter spreadSheetReaderWriter = new SpreadSheetReaderWriter();
      String[] historyArray = spreadSheetReaderWriter.getHistory(currentUsername).split(",");

      CategorySelector categorySelector = new CategorySelector(); // picks random word
      currentWord = categorySelector.getRandomCategory(words, historyArray, currentUsername);
    }
    SpreadSheetReaderWriter spreadSheetReaderWriter = new SpreadSheetReaderWriter();
    spreadSheetReaderWriter.updateWords(currentWord, currentUsername);
    return currentWord;
  }
}
