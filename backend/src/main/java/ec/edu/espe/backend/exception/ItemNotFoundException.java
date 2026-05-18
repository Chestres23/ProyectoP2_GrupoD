package ec.edu.espe.backend.exception;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String message) { super(message); }
}