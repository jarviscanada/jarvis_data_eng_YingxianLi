package ca.jrvs.apps.grep;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * JavaGrep defines the blueprint for grep application
 */
public interface JavaGrep  {

  /**
   * Top level search workflow
   *
   * @throws IOException if file processing fails
   */
  void process() throws IOException;

  /**
   * Traverse a given directory and return all files recursively
   *
   * @param rootDir input directory path
   * @return list of all files under rootDir
   */
  List<File> listFiles(String rootDir);

  /**
   * Read a file and return all lines as a List of Strings
   *
   * Commonly uses:
   * - FileReader for reading the characters in file)
   * - BufferedReader for reading faster and easier
   *
   * @param inputFile file to be read
   * @return list of lines from the file
   * @throws IllegalArgumentException if inputFile is not a valid file
   */
  List<String> readLines(File inputFile);

  /**
   * Check if a line matches the regex pattern
   *
   * @param line input string
   * @return true if there is a match; false otherwise
   */
  boolean containsPattern(String line);

  /**
   * Write matched lines to a file
   *
   * @param lines matched lines
   * @throws IOException if writing fails
   */
  void writeToFile(List<String> lines) throws IOException;

  /**
   * Getter for root path
   */
  String getRootPath();

  /**
   * Setter for root path
   */
  void setRootPath(String rootPath);

  /**
   * Getter for regex pattern
   */
  String getRegex();

  /**
   * Setter for regex pattern
   */
  void setRegex(String regex);

  /**
   * Getter for output file path
   */
  String getOutFile();

  /**
   * Setter for output file path
   */
  void setOutFile(String outFile);
}


