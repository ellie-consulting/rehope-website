package live.rehope.site.datasource.exception;

import io.javalin.http.InternalServerErrorResponse;

public class DatabaseError extends InternalServerErrorResponse {

    public DatabaseError() {
        super("database error");
    }

}
