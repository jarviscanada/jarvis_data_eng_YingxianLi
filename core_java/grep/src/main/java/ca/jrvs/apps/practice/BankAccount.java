package ca.jrvs.apps.practice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BankAccount {
  private String accountNumber;
  private String ownerName;
  private double balance;
  private List<Double> transactions = new ArrayList<>();


  public BankAccount(String accountNumber, double balance, String ownerName) {
    if (balance < 0) throw new IllegalArgumentException("starting balance must be >= 0");

    this.accountNumber = accountNumber;
    this.balance = balance;
    this.ownerName = ownerName;
  }

  public double getBalance() {
    return balance;
  }

  public void deposit(double amount) {
    if (amount < 0) throw new IllegalArgumentException("deposit amount must be >= 0");

    balance += amount;
    transactions.add(amount);
  }

  public void withdraw(double amount) {
    if (amount < 0) throw new IllegalArgumentException("withdraw amount must be > 0");

    if (amount > balance) throw new IllegalArgumentException("cannot withdraw more than current balance");

    balance -= amount;
    transactions.add(-amount);
  }

  public String getAccountInfo() {
    return "Account:" + accountNumber + " \n" +
        "Owner:" + ownerName + "\n" +
        "Balance" + String.format("%.2f", balance);
  }

  public double getTotalDeposited() {
    return transactions.stream()
        .filter(t -> t > 0)
        .mapToDouble(Double::doubleValue)
        .sum();
  }

  public double getTotalWithdrawn() {
    return transactions.stream()
        .filter(t -> t < 0)
        .mapToDouble(Double::doubleValue)
        .sum();
  }

  public double getLargestTransaction() {
    return transactions.stream()
        .mapToDouble(Math::abs)
        .max()
        .orElse(0.0);
  }

  public List<Double> getAllDeposits() {
    return transactions.stream()
        .filter(t -> t > 0)
        .collect(Collectors.toList());
  }

  public static void main(String[] args) {
    BankAccount account = new BankAccount("12345", 100.0, "Alice");

    account.deposit(50.0);
    account.withdraw(30.0);

    System.out.println(account.getAccountInfo());
    System.out.println("Current balance: " + account.getBalance());
    System.out.println("Total deposited: " + account.getTotalDeposited());
    System.out.println("Total withdrawn: " + account.getTotalWithdrawn());
    System.out.println("Largest transaction: " + account.getLargestTransaction());
    System.out.println("All deposits: " + account.getAllDeposits());
  }
}
