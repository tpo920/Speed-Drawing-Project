package nz.ac.auckland.se206;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SpreadSheetReaderWriter {

  /**
   * update the words for the current user
   *
   * @param word current word to be drew
   * @param currentUser current use name
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If file does not exist
   */
  public void updateWords(String word, String currentUser) throws IOException, CsvException {
    if (currentUser == null) {
      return;
    }
    int index = findUserName(currentUser);
    CSVReader csvReader = new CSVReader(new FileReader("userdata.csv")); // name of file
    List<String[]> allData = csvReader.readAll();
    allData.get(index)[5] = allData.get(index)[5] + word + ",";
    CSVWriter csvWriter = new CSVWriter(new FileWriter("userdata.csv")); // writes to file name
    csvWriter.writeAll(allData); // writes all the data to the same file
    csvWriter.flush();
  }

  /**
   * search the position of the current user
   *
   * @param currentUsername current user name
   * @return return an index
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If file does not exist
   */
  private int findUserName(String currentUsername) throws IOException, CsvException {
    CSVReader csvReader = new CSVReader(new FileReader("userdata.csv"));
    List<String[]> allData =
        csvReader.readAll(); // reads the entire csv file the name of file can be changed if
    // needed
    int i = 0;
    while (!allData.get(i)[0].equals(
        currentUsername)) { // cycles through all the users until there is a match
      i++;
    }
    return i;
  }

  /**
   * update the profile stats after a game ends
   *
   * @param win win or lose
   * @param currentUsername current user name
   * @param overallDif difficulty combination
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If file does not exist
   */
  public void updateResult(boolean win, String currentUsername, int overallDif)
      throws IOException, CsvException {
    if (currentUsername == null) {
      return;
    }
    int index = findUserName(currentUsername);
    CSVReader csvReader = new CSVReader(new FileReader("userdata.csv"));
    List<String[]> allData = csvReader.readAll();

    if (win) {
      allData.get(index)[2] =
          String.valueOf(Integer.parseInt(allData.get(index)[2]) + 1); // increment wins
      allData.get(index)[7] =
          String.valueOf(Integer.parseInt(allData.get(index)[7]) + 1); // increment streak
      if (Integer.parseInt(allData.get(index)[7])
          > Integer.parseInt(allData.get(index)[6])) { // checks streak
        allData.get(index)[6] = allData.get(index)[7]; // makes new highest streak
      }
      switch (overallDif) {
        case 2 -> // medium difficulty
        allData.get(index)[9] = String.valueOf(Integer.parseInt(allData.get(index)[9]) + 1);
        case 3 -> // hard difficulty
        allData.get(index)[10] = String.valueOf(Integer.parseInt(allData.get(index)[10]) + 1);
        case 4 -> // master difficulty
        allData.get(index)[11] = String.valueOf(Integer.parseInt(allData.get(index)[11]) + 1);
        default -> // easy difficulty
        allData.get(index)[8] = String.valueOf(Integer.parseInt(allData.get(index)[8]) + 1);
      }
    } else {
      allData.get(index)[3] =
          String.valueOf(Integer.parseInt(allData.get(index)[3]) + 1); // increment losses
      allData.get(index)[7] = "0"; // reset
    }

    CSVWriter csvWriter = new CSVWriter(new FileWriter("userdata.csv"));
    csvWriter.writeAll(allData); // writes all the data back
    csvWriter.flush();
  }

  /**
   * update time spent
   *
   * @param timeTaken time spent on drawing
   * @param currentUsername current use name
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If file does not exist
   */
  public void updateTime(int timeTaken, String currentUsername) throws IOException, CsvException {
    if (currentUsername == null) { // this is a guest user
      return;
    }
    int index = findUserName(currentUsername);
    CSVReader csvReader = new CSVReader(new FileReader("userdata.csv"));
    List<String[]> allData = csvReader.readAll();
    if (Integer.parseInt(allData.get(index)[4])
        > timeTaken) { // checks if the time is faster than the previous
      // record
      allData.get(index)[4] = String.valueOf(timeTaken);
    }

    CSVWriter csvWriter = new CSVWriter(new FileWriter("userdata.csv"));
    csvWriter.writeAll(allData); // writes all the data back
    csvWriter.flush();
  }

  /**
   * gets the number of wins for the current user
   *
   * @param username current user name
   * @return return the number of wins
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If file does not exist
   */
  public int getWins(String username) throws IOException, CsvException {
    int index = findUserName(username);
    CSVReader csvReader = new CSVReader(new FileReader("userdata.csv"));
    List<String[]> allData = csvReader.readAll();
    return Integer.parseInt(allData.get(index)[2]);
  }

  /**
   * gets the number of wins for the current user
   *
   * @param username current user name
   * @return return the number of losses
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If file does not exist
   */
  public int getLosses(String username) throws IOException, CsvException {
    int index = findUserName(username);
    CSVReader csvReader = new CSVReader(new FileReader("userdata.csv"));
    List<String[]> allData = csvReader.readAll();
    return Integer.parseInt(allData.get(index)[3]);
  }

  /**
   * get the fastest time spent
   *
   * @param currentUsername current user name
   * @return return the fastest time spent
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If file does not exist
   */
  public int getFastest(String currentUsername) throws IOException, CsvException {
    int index = findUserName(currentUsername);
    CSVReader csvReader = new CSVReader(new FileReader("userdata.csv"));
    List<String[]> allData = csvReader.readAll();
    return Integer.parseInt(allData.get(index)[4]);
  }

  /**
   * gets all the history words
   *
   * @param currentUsername current user name
   * @return return the list containing all history words encountered
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If file does not exist
   */
  public String getHistory(String currentUsername) throws IOException, CsvException {
    int index = findUserName(currentUsername);
    CSVReader csvReader = new CSVReader(new FileReader("userdata.csv"));
    List<String[]> allData = csvReader.readAll();
    return allData.get(index)[5];
  }

  /**
   * gets the streak for the current user
   *
   * @param currentUsername current user name
   * @return return the streak
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If file does not exist
   */
  public int getStreak(String currentUsername) throws IOException, CsvException {
    int index = findUserName(currentUsername);
    CSVReader csvReader = new CSVReader(new FileReader("userdata.csv"));
    List<String[]> allData = csvReader.readAll();
    return Integer.parseInt(allData.get(index)[6]);
  }

  /**
   * update time spent for the current user
   *
   * @param time time spent
   * @param currentUsername current user
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If file does not exist
   */
  public void updateUsersTime(int time, String currentUsername) throws IOException, CsvException {
    if (currentUsername == null) {
      return; // guest doesnt save difficulty
    }
    int index = findUserName(currentUsername);
    CSVReader csvReader = new CSVReader(new FileReader("userdata.csv"));
    List<String[]> allData = csvReader.readAll();
    allData.get(index)[12] = String.valueOf(time); // updates time difficulty value for next time

    CSVWriter csvWriter = new CSVWriter(new FileWriter("userdata.csv"));
    csvWriter.writeAll(allData); // writes all the data back
    csvWriter.flush();
  }

  /**
   * update current word category
   *
   * @param words word category
   * @param currentUsername current user name
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If file does not exist
   */
  public void updateUsersWords(int words, String currentUsername) throws IOException, CsvException {
    if (currentUsername == null) {
      return; // guest doesnt save difficulty
    }
    int index = findUserName(currentUsername);
    CSVReader csvReader = new CSVReader(new FileReader("userdata.csv"));
    List<String[]> allData = csvReader.readAll();
    allData.get(index)[13] = String.valueOf(words); // updates time difficulty value for next time

    CSVWriter csvWriter = new CSVWriter(new FileWriter("userdata.csv"));
    csvWriter.writeAll(allData); // writes all the data back
    csvWriter.flush();
  }

  /**
   * gets the confidence percentage for the current user
   *
   * @param confidence confidence percentage
   * @param currentUsername current user name
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If file does not exist
   */
  public void updateUsersConfidence(int confidence, String currentUsername)
      throws IOException, CsvException {
    if (currentUsername == null) {
      return; // guest doesnt save difficulty
    }
    int index = findUserName(currentUsername);
    CSVReader csvReader = new CSVReader(new FileReader("userdata.csv"));
    List<String[]> allData = csvReader.readAll();
    allData.get(index)[14] =
        String.valueOf(confidence); // updates time difficulty value for next time

    CSVWriter csvWriter = new CSVWriter(new FileWriter("userdata.csv"));
    csvWriter.writeAll(allData); // writes all the data back
    csvWriter.flush();
  }

  /**
   * update if the user wants the result is within top 1/2/3 prediction
   *
   * @param accuracy top 1/2/3 prediction
   * @param currentUsername current user name
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If file does not exist
   */
  public void updateUsersAccuracy(int accuracy, String currentUsername)
      throws IOException, CsvException {
    if (currentUsername == null) {
      return; // guest doesnt save difficulty
    }
    int index = findUserName(currentUsername);
    CSVReader csvReader = new CSVReader(new FileReader("userdata.csv"));
    List<String[]> allData = csvReader.readAll();
    allData.get(index)[15] =
        String.valueOf(accuracy); // updates time difficulty value for next time

    CSVWriter csvWriter = new CSVWriter(new FileWriter("userdata.csv"));
    csvWriter.writeAll(allData); // writes all the data back
    csvWriter.flush();
  }

  /**
   * get if the user wants the result is within top 1/2/3 prediction
   *
   * @param currentUsername current user name
   * @return accuracy top 1/2/3 prediction
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If file does not exist
   */
  public int getUsersAccuracy(String currentUsername) throws IOException, CsvException {
    int index = findUserName(currentUsername);
    CSVReader csvReader = new CSVReader(new FileReader("userdata.csv"));
    List<String[]> allData = csvReader.readAll();
    return Integer.parseInt(allData.get(index)[15]);
  }

  /**
   * get the time limit for the current user
   *
   * @param currentUsername current user name
   * @return time limit
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If file does not exist
   */
  public int getUsersTime(String currentUsername) throws IOException, CsvException {
    int index = findUserName(currentUsername);
    CSVReader csvReader = new CSVReader(new FileReader("userdata.csv"));
    List<String[]> allData = csvReader.readAll();
    return Integer.parseInt(allData.get(index)[12]);
  }

  /**
   * gets the word category for the current use
   *
   * @param currentUsername current user name
   * @return word category
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If file does not exist
   */
  public int getUsersWords(String currentUsername) throws IOException, CsvException {
    int index = findUserName(currentUsername);
    CSVReader csvReader = new CSVReader(new FileReader("userdata.csv"));
    List<String[]> allData = csvReader.readAll();
    return Integer.parseInt(allData.get(index)[13]);
  }

  /**
   * gets the confidence percentage for the current user
   *
   * @param currentUsername current user name
   * @return confidence percentage
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If file does not exist
   */
  public int getUsersConfidence(String currentUsername) throws IOException, CsvException {
    int index = findUserName(currentUsername);
    CSVReader csvReader = new CSVReader(new FileReader("userdata.csv"));
    List<String[]> allData = csvReader.readAll();
    return Integer.parseInt(allData.get(index)[14]);
  }

  /**
   * clear user history words
   *
   * @param currentUsername current user name
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If file does not exist
   */
  public void resetUserHistory(String currentUsername) throws IOException, CsvException {
    int index = findUserName(currentUsername);
    CSVReader csvReader = new CSVReader(new FileReader("userdata.csv"));
    List<String[]> allData = csvReader.readAll();
    allData.get(index)[5] = "none"; // updates time difficulty value for next time

    CSVWriter csvWriter = new CSVWriter(new FileWriter("userdata.csv"));
    csvWriter.writeAll(allData); // writes all the data back
    csvWriter.flush();
  }

  /**
   * gets the number of wins for the current difficulty
   *
   * @param currentUsername current user name
   * @return the number of wins for the current difficulty
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If file does not exist
   */
  public int[] getDifWins(String currentUsername) throws IOException, CsvException {
    int index = findUserName(currentUsername); // find username place in csv
    CSVReader csvReader = new CSVReader(new FileReader("userdata.csv"));
    List<String[]> allData = csvReader.readAll();

    int[] difWins = new int[4];
    difWins[0] = Integer.parseInt(allData.get(index)[8]); // easy wins
    difWins[1] = Integer.parseInt(allData.get(index)[9]); // medium wins
    difWins[2] = Integer.parseInt(allData.get(index)[10]); // hard wins
    difWins[3] = Integer.parseInt(allData.get(index)[11]); // master wins
    return difWins;
  }

  /**
   * gets the last lost word for the current user
   *
   * @param currentUsername current user name
   * @return lost words
   * @throws IOException If the model cannot be found on the file system.
   * @throws CsvException If file does not exist
   */
  public String[] getUsersLostWords(String currentUsername) throws IOException, CsvException {
    if (currentUsername == null) {
      return new String[] {"log in", "log in", "log in", "log in", "log in"};
    }
    int index = findUserName(currentUsername); // find username place in csv
    CSVReader csvReader = new CSVReader(new FileReader("userdata.csv"));
    List<String[]> allData = csvReader.readAll();

    String[] lastWords = new String[5];
    String[] words = allData.get(index)[5].split(",");
    if (words.length < 5) {
      lastWords[0] = "Play normal mode first";
      lastWords[1] = "Play normal mode first";
      lastWords[2] = "Play normal mode first";
      lastWords[3] = "Play normal mode first";
      lastWords[4] = "Play normal mode first";
      for (int i = 0; i < words.length; i++) {
        if (words[i].equals("")) {
          break;
        }
        lastWords[i] = words[i];
      }
    } else {
      for (int i = 1; i < 6; i++) {
        lastWords[i - 1] = words[words.length - i];
      }
    }

    return lastWords;
  }
}
