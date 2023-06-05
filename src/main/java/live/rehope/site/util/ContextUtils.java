package live.rehope.site.util;

import live.rehope.site.endpoint.user.principle.PrincipleUser;
import live.rehope.site.endpoint.user.principle.UserRole;
import io.javalin.http.Context;

import java.util.Optional;

public final class ContextUtils {
    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";

    public static void setupSessionAttributes(PrincipleUser user, Context context) {
        context.sessionAttribute(KEY_ID, user.id());
        context.sessionAttribute(KEY_USERNAME, user.username());
        context.sessionAttribute(KEY_ROLE, user.role());
    }

    public static Optional<Integer> userId(Context context) {
        return value(context, KEY_ID);
    }

    public static Optional<String> username(Context context) {
        return value(context, KEY_USERNAME);
    }

    public static void updateUsername(Context context, String name) {
        context.sessionAttribute(KEY_USERNAME, name);
    }

    public static Optional<UserRole> role(Context context) {
        return value(context, KEY_ROLE);
    }

    public static <T> Optional<T> value(Context context, String key) {
        return Optional.ofNullable(context.sessionAttribute(key));
    }


}
