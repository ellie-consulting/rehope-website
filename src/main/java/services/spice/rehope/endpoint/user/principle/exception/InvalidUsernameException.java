package services.spice.rehope.endpoint.user.principle.exception;

import io.javalin.http.BadRequestResponse;

public class InvalidUsernameException extends BadRequestResponse {

    public InvalidUsernameException(Reason reason) {
        super(reason.name());
    }

    public enum Reason {
        INVALID,
        IN_USE,
        TOO_LONG,
        EMPTY
    }

}
