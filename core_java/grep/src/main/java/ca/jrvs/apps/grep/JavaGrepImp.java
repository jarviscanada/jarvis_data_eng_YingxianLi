package ca.jrvs.apps.grep;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaGrepImp implements JavaGrep{

  final Logger logger = LoggerFactory.getLogger(JavaGrep.class);

  private String regex;
  private String rootPath;
  private String outFile;

  public static void main(String[] args) {
    if (args.length != 3) {
      throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
    }

    // configure default logger
    BasicConfigurator.configure();

    JavaGrepImp javaGrepImp = new JavaGrepImp();
    javaGrepImp.setRegex(args[0]);
    javaGrepImp.setRootPath(args[1]);
    javaGrepImp.setOutFile(args[2]);

    try {
      javaGrepImp.process();
    } catch (Exception ex) {
      javaGrepImp.logger.error("Error: Unable to process", ex);
    }
  }


  @Override
  public void process() throws IOException {

    List<String> matchedLines = new ArrayList<>();

    List<File> files = listFiles(rootPath);

    for (File file : files) {
      List<String> lines = readLines(file);

      for (String line : lines) {
        if (containsPattern(line)) {
          matchedLines.add(line);
        }
      }
    }

    writeToFile(matchedLines);
  }

  @Override
  public List<File> listFiles(String rootDir) {

    List<File> files = new ArrayList<>();
    File root = new File(rootDir);

    if (!root.exists()) {
      logger.error("Root directory does not exist: {}", rootDir);
      return files;
    }

    File[] fileList = root.listFiles();

    if (fileList == null) {
      return files;
    }

    for (File file : fileList) {
      if (file.isDirectory()) {
        files.addAll(listFiles(file.getAbsolutePath()));
      } else if (file.isFile()) {
        files.add(file);
      }
    }

    return files;
  }

  @Override
  public List<String> readLines(File inputFile) {

    if (!inputFile.isFile()) {
      throw new IllegalArgumentException("Input is not a vaild  file");
    }

    List<String> lines = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(
            new FileInputStream(inputFile), "UTF-8"))) {

      String line;
      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }

    } catch (IOException e) {
      logger.error("Error reading file: {}", inputFile.getName(), e);
    }

    return lines;
  }

  @Override
  public boolean containsPattern(String line) {
    return Pattern.compile(regex).matcher(line).find();
  }

  @Override
  public void writeToFile(List<String> lines) throws IOException {

    File output = new File(outFile);

    try (BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(
            new FileOutputStream(output), "UTF-8"))) {

      for (String line : lines) {
        writer.write(line);
        writer.newLine();
      }

    } catch (IOException e) {
      logger.error("Error writing to file: {}", outFile, e);
      throw e;
    }
  }

  @Override
  public String getRootPath() {
    return rootPath;
  }

  @Override
  public void setRootPath(String rootPath) {
    this.rootPath = rootPath;
  }

  @Override
  public String getRegex() {
    return regex;
  }

  @Override
  public void setRegex(String regex) {
    this.regex = regex;
  }

  @Override
  public String getOutFile() {
    return outFile;
  }

  @Override
  public void setOutFile(String outFile) {
    this.outFile = outFile;
  }
}
