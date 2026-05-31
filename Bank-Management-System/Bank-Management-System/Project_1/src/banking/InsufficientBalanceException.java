package banking;

import java.math.BigDecimal;

public class InsufficientBalanceException extends Exception {
    private  final BigDecimal balance;
    private final double amount;


    public InsufficientBalanceException(BigDecimal balance, double amount) {
        super("Insufficient balance. Available: " + balance + ", Requested: " + amount);
        this.balance = balance;
        this.amount = amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }
    public double getAmount() {
        return amount;
    }

}
