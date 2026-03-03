package ca.jrvs.apps.grep;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaGrepLambdaImp extends JavaGrepImp{

  private static final Logger logger = LoggerFactory.getLogger(JavaGrepLambdaImp.class);

  public static void main(String[] args) {
    if (args.length != 3) {
      throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
    }

    BasicConfigurator.configure();

    JavaGrepLambdaImp javaGrep = new JavaGrepLambdaImp();
    javaGrep.setRegex(args[0]);
    javaGrep.setRootPath(args[1]);
    javaGrep.setOutFile(args[2]);

    try {
      javaGrep.process();
    } catch (Exception ex) {
      logger.error("Error: Unable to process", ex);
    }
  }

  @Override
  public void process() throws IOException {
    final java.util.regex.Pattern p = java.util.regex.Pattern.compile(getRegex());

    List<String> matchedLines = listFiles(getRootPath()).stream()
        .filter(Objects::nonNull)
        .flatMap(f -> {
          try {
            return readLines(f).stream();
          } catch (Exception e) {
            logger.error("Error reading file: {}", f.getAbsolutePath(), e);
            return Stream.empty();
          }
        })
        .filter(Objects::nonNull)
        .filter(line -> p.matcher(line).find())
        .collect(Collectors.toList());

    writeToFile(matchedLines);
  }

  @Override
  public List<File> listFiles(String rootDir) {
    Path root = Paths.get(rootDir);

    if (!Files.exists(root)) {
      logger.error("Root directory does not exist: {}", rootDir);
      return Collections.emptyList();
    }

    try (Stream<Path> paths = Files.walk(root)) {
      return paths
          .filter(Files::isRegularFile)
          .map(Path::toFile)
          .collect(Collectors.toList());
    } catch (IOException e) {
      logger.error("Error walking directory: {}", rootDir, e);
      return Collections.emptyList();
    }
  }

  @Override
  public List<String> readLines(File inputFile) {
    if (inputFile == null || !inputFile.isFile()) {
      throw new IllegalArgumentException("Input is not a file");
    }

    Path path = inputFile.toPath();

    try (Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
      return lines.collect(Collectors.toList());
    } catch (IOException e) {
      logger.error("Error reading file: {}", inputFile.getAbsolutePath(), e);
      return Collections.emptyList();
    }
  }

  @Override
  public void writeToFile(List<String> lines) throws IOException {
    Path outPath = Paths.get(getOutFile());
    Path parent = outPath.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }

    Files.write(outPath, lines, StandardCharsets.UTF_8,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING);
  }
}
