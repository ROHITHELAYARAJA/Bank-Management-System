package banking;

public class DuplicateUsernameException extends Exception {

    public DuplicateUsernameException(String username) {
        super("Account with username "+username+" already exists");
    }

}
