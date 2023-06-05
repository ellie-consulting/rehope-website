package live.rehope.site.endpoint;

import live.rehope.site.endpoint.user.principle.model.UserRole;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(value={METHOD, TYPE})
@Retention(value=RUNTIME)
public @interface EndpointRoles {

    UserRole[] value() default {};

}
