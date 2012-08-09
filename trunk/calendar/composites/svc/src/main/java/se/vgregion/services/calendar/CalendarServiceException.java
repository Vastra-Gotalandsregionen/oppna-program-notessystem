package se.vgregion.services.calendar;

/**
 * Exception class thrown when exceptions occur in association to {@link CalendarService}.
 *
 * @author Patrik Bergström
 */
public class CalendarServiceException extends Exception {

    /**
     * Constructor.
     *
     * @param cause the cause
     */
    public CalendarServiceException(Throwable cause) {
        super(cause);
    }

}
