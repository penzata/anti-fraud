package antifraud.exceptionhandler;

public class ExceptionConstants {
    public static final String EXISTING_USERNAME = "The provided username already exists! " +
            "Please choose another one.";
    public static final String EXISTING_IP = "The provided IP already exists in the database!";
    public static final String EXISTING_CARD = "The provided card is already stored in the database!";
    public static final String EXISTING_FEEDBACK = "There is already existing feedback for this transaction in the database!";
    public static final String FAILED_AUTH = "Authentication failed at controller advice";
    public static final String USERNAME_NOT_FOUND = " - this user is not found!";
    public static final String IP_NOT_FOUND = " - this IP is not found!";
    public static final String CARD_NOT_FOUND = " - this card number is not found in the database!";
    public static final String TRANSACTIONS_NOT_FOUND = "There are no transactions found!";
    public static final String ADMIN = "There is already existent admin!";
    public static final String SAME_ROLE = "This role has already been provided!";
    public static final String CANNOT_BE_BLOCKED = "This role cannot be manipulated!";
    public static final String FEEDBACK_COLLISION = "The provided feedback is the same as the result(type) of the transaction!";

    private ExceptionConstants() {
    }
}