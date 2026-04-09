package ca.jrvs.apps.practice;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Test;

public class LambdaStreamImpTest {

  private final LambdaStreamExc lse = new LambdaStreamImp();

  @Test
  public void testCreateStrStream_ToUpperCase_Filter() {
    List<String> upper = lse.toList(lse.toUpperCase("a", "b", "Hello"));
    assertEquals(Arrays.asList("A", "B", "HELLO"), upper);

    Stream<String> s = lse.createStrStream("cat", "car", "dog");
    List<String> filtered = lse.toList(lse.filter(s, "ca"));
    assertEquals(Arrays.asList("cat", "car"), filtered);
  }

  @Test
  public void testIntStreams_ToList_GetOdd() {
    assertEquals(
        Arrays.asList(1, 2, 3),
        lse.toList(lse.createIntStream(new int[]{1, 2, 3}))
    );

    assertEquals(
        Arrays.asList(0, 1, 2, 3),
        lse.toList(lse.createIntStream(0, 3))
    );

    List<Integer> odds = lse.toList(lse.getOdd(IntStream.of(1, 2, 3, 4, 5)));
    assertEquals(Arrays.asList(1, 3, 5), odds);
  }

  @Test
  public void testSquareRootIntStream() {
    double[] out = lse.squareRootIntStream(IntStream.of(1, 4, 9)).toArray();
    assertArrayEquals(new double[]{1.0, 2.0, 3.0}, out, 1e-9);
  }

  @Test
  public void testGetLambdaPrinter_PrintMessages() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream old = System.out;
    System.setOut(new PrintStream(baos));

    try {
      Consumer<String> printer = lse.getLambdaPrinter("msg:", "!");
      lse.printMessages(new String[]{"a", "b", "c"}, printer);

      String printed = baos.toString();
      assertTrue(printed.contains("msg:a!"));
      assertTrue(printed.contains("msg:b!"));
      assertTrue(printed.contains("msg:c!"));
    } finally {
      System.setOut(old);
    }
  }

  @Test
  public void testPrintOdd_FlatNestedInt() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream old = System.out;
    System.setOut(new PrintStream(baos));

    try {
      lse.printOdd(lse.createIntStream(0, 5), lse.getLambdaPrinter("odd number:", "!"));
      String printed = baos.toString();
      assertTrue(printed.contains("odd number:1!"));
      assertTrue(printed.contains("odd number:3!"));
      assertTrue(printed.contains("odd number:5!"));
    } finally {
      System.setOut(old);
    }

    Stream<List<Integer>> nested = Stream.of(
        Arrays.asList(1, 2),
        Arrays.asList(3),
        Arrays.asList(4, 5)
    );

    List<Integer> squared = lse.toList(lse.flatNestedInt(nested));
    assertEquals(Arrays.asList(1, 4, 9, 16, 25), squared);
  }
}
