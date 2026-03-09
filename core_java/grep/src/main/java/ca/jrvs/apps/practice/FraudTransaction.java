package ca.jrvs.apps.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FraudTransaction {

  public static List<Integer> detectFraud(List<Integer> transactions, int threshold) {
    if (transactions == null) throw new IllegalArgumentException("Transaction list cannot be null.");

    if (threshold < 0) throw new IllegalArgumentException("Threshold cannot be negative.");


    List<Integer> suspiciousTransactions = new ArrayList<>();

    for (Integer amount : transactions) {
      if (amount == null) {throw new IllegalArgumentException("Transaction amount cannot be null.");}
      if (amount > threshold) {suspiciousTransactions.add(amount);}
    }
    return suspiciousTransactions;
  }

  public static void main(String[] args) {
    List<Integer> transactions = Arrays.asList(20, 40, 5000, 30);
    int threshold = 1000;

    List<Integer> result = detectFraud(transactions, threshold);
    System.out.println(result);
  }
}
