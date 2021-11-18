package org.gestern.gringotts;

/**
 * The type Gringotts exception.
 */
public class GringottsException extends RuntimeException {
    private static final long serialVersionUID = 476895381491480536L;

    /**
     * Instantiates a new Gringotts exception.
     *
     * @param cause the cause
     */
    public GringottsException(Throwable cause) {
        super(cause);
    }
}
