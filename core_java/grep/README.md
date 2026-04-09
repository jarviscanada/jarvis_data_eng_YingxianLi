# Introduction

This project implements a Java application that mimics the Linux `grep` command. It recursively scans a given root directory, reads each file line-by-line, applies a user-provided regular expression, and writes all matching lines to an output file. The app is designed with an interface + implementation pattern (`JavaGrep' + 'JavaGrepImp') and includes a Stream/Lambda-based variant for cleaner functional processing. 

Technologies used: 
- Core Java
- Maven (build/package)
- SLF4J and Log4j (logging) 
- Docker (containerized delivery)

# Quick Start

## Prerequisites
- Java 8+
- Maven 3+
- Docker

## Build
```bash
mvn clean package
```

## Run (Local JVM)
Arguments: `<regex> <rootPath> <outFile>`
```bash
java -cp target/grep-1.0-SNAPSHOT.jar ca.jrvs.apps.grep.JavaGrepImp "*Romeo.*Juliet.*" ./data ./out/grep.out
```

## Run (Docker)
```bash
docker build -t <docker_id>/grep .
docker run --rm -v "$PWD":/data <docker_id>/grep ."*Romeo.*Juliet.*" /data/data /data/out/grep.out
```

# Implemenation

## Pseudocode
```text
method process():
    assert regex != null AND rootPath != null AND outFile != null

    pattern <- compileRegex(regex)

    writer <- openBufferedWriter(outFile, CREATE, TRUNCATE_EXISTING)

    for each filePath in listFilesRecursively(rootPath):
        if isRegularFile(filePath) AND isReadable(filePath):
            for each line in readLinesStream(filePath):
                if pattern.matches(line):
                    writer.write(line)
                    writer.newLine()

    writer.close()
```

## Performance Issue
A memory issue occurs if the program loads entire files (or all matched lines) into memory before writing output. On large directories, this can trigger OOM. 

Fix by streaming: traverse with `Files.walk`, read with `Files.lines`/`BufferedReader` line-by-line, and write matches immediately using a `BufferedWriter` (try-with-resources).

# Test
- **Manual testing**
    - Prepare a `data/` folder with nested subfolders and text files.
    - Create known matching lines (e.g., containing `Romeo`, `Juliet`) and non-matching lines.
    - Run multiple cases: many matches, zero matches, invalid path, and special regex characters.
    - Compare the output file with expected results (spot-check + line counts).
- **IDE debugger**
    - Set breakpoints in file traversal and match logic.
    - Inspect current file path, current line, and match result during execution.

# Deployment

1. Package the app:
```bash
mvn clean package
```

2. Build Docker image:
```bash
docker build -t <docker_id>/grep .
```

3. Run with volume mount:
```bash
docker run --rm -v "$PWD":/data <docker_id>/grep ."<regex>" /data/data /data/out/grep.out
```

# Improvement
1. Add automated tests (JUnit) for traversal, matching, and edge cases (empty files, permissions, large files).
2. allow `--append` vs `--overwrite`, and optionally include `filePath:lineNumber:` like real `grep`
3. Detect binary files and skip them to avoid unreadable output.