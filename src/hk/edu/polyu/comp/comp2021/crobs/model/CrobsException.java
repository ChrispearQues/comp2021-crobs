package hk.edu.polyu.comp.comp2021.crobs.model;

/** Base class for all business errors. The controller turns these into messages. */
public class CrobsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param message the text shown to the user
     */
    public CrobsException(String message) { super(message); }

    /** The user lacks the privilege required for the operation. */
    public static class PermissionDeniedException extends CrobsException {

        private static final long serialVersionUID = 1L;

        /**
         * Creates the exception.
         *
         * @param message the text shown to the user
         */
        public PermissionDeniedException(String message) { super(message); }
    }

    /** The referenced user / room / booking does not exist. */
    public static class NotFoundException extends CrobsException {

        private static final long serialVersionUID = 1L;

        /**
         * Creates the exception.
         *
         * @param message the text shown to the user
         */
        public NotFoundException(String message) { super(message); }
    }

    /** The requested time slot collides with an existing booking. */
    public static class ConflictException extends CrobsException {

        private static final long serialVersionUID = 1L;

        /**
         * Creates the exception.
         *
         * @param message the text shown to the user
         */
        public ConflictException(String message) { super(message); }
    }

    /** The command was ill-formed (wrong argument count, bad value, ...). */
    public static class MalformedCommandException extends CrobsException {

        private static final long serialVersionUID = 1L;

        /**
         * Creates the exception.
         *
         * @param message the text shown to the user
         */
        public MalformedCommandException(String message) { super(message); }
    }
}
