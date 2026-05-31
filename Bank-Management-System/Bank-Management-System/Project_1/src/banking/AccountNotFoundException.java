package banking;

public class AccountNotFoundException extends Exception {
    public AccountNotFoundException(int acno) {
        super("Account not found "+acno);
    }

    public AccountNotFoundException(String username) {
        super("Account not found for username "+username);
    }

}
