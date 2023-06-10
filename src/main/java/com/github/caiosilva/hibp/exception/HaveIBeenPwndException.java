/* (C)2023 */
package com.github.caiosilva.hibp.exception;

public class HaveIBeenPwndException extends Exception {

    public HaveIBeenPwndException() {
        super();
    }

    public HaveIBeenPwndException(String message) {
        super(message);
    }

    public HaveIBeenPwndException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class BadRequestException extends HaveIBeenPwndException {
        private static final String DEFAULT_MESSAGE =
                "Bad request — the account does not comply with an acceptable format (i.e. it's an"
                        + " empty string)";

        public BadRequestException() {
            super(DEFAULT_MESSAGE);
        }
    }

    public static class IOException extends HaveIBeenPwndException {
        private static final String DEFAULT_MESSAGE =
                "Bad request — the account does not comply with an acceptable format (i.e. it's an"
                        + " empty string)";

        public IOException(String message, Throwable throwable) {
            super(message, throwable);
        }
    }

    public static class NoAPIKeyProvidedException extends HaveIBeenPwndException {
        private static final String DEFAULT_MESSAGE = "No API key — no API key provided";

        public NoAPIKeyProvidedException() {
            super(DEFAULT_MESSAGE);
        }
    }

    public static class UnauthorizedException extends HaveIBeenPwndException {
        private static final String DEFAULT_MESSAGE =
                "Unauthorised — the API key provided was not valid";

        public UnauthorizedException() {
            super(DEFAULT_MESSAGE);
        }
    }

    public static class ForbiddenException extends HaveIBeenPwndException {
        private static final String DEFAULT_MESSAGE =
                "Forbidden — no user agent has been specified in the request";

        public ForbiddenException() {
            super(DEFAULT_MESSAGE);
        }
    }

    public static class NotFoundException extends HaveIBeenPwndException {
        private static final String DEFAULT_MESSAGE = "Not found — content requested was not found";

        public NotFoundException() {
            super(DEFAULT_MESSAGE);
        }
    }

    public static class TooManyRequestsException extends HaveIBeenPwndException {
        private static final String DEFAULT_MESSAGE =
                "Too many requests — the rate limit has been exceeded";

        public TooManyRequestsException() {
            super(DEFAULT_MESSAGE);
        }
    }

    public static class ServiceUnavailableException extends HaveIBeenPwndException {
        private static final String DEFAULT_MESSAGE = "Service unavailable";

        public ServiceUnavailableException(String message) {
            super(DEFAULT_MESSAGE + " - " + message);
        }

        public ServiceUnavailableException(String message, Throwable throwable) {
            super(DEFAULT_MESSAGE + " - " + message, throwable);
        }
    }

    public static class UnknownErrorCodeException extends HaveIBeenPwndException {
        private static final String DEFAULT_MESSAGE = "Unknown error code";

        public UnknownErrorCodeException(int errorCode) {
            super(DEFAULT_MESSAGE + " " + errorCode);
        }
    }
}
