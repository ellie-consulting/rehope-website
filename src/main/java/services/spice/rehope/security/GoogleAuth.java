package services.spice.rehope.security;

import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.oauth.client.Google2Client;

import java.util.Optional;

public class GoogleAuth {

    public void doSomething() {
        Google2Client client = new Google2Client("932759867981-598hf7nui6q6o64dubnmnjlfipku7sau.apps.googleusercontent.com", "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");

        AuthorizationGenerator authGen = (ctx, session, profile) -> {
            String roles = (String) profile.getAttribute("roles");
            for (String role: roles.split(",")) {
                profile.addRole(role);
            }

            return Optional.of(profile);
        };
        client.addAuthorizationGenerator(authGen);
    }

}
