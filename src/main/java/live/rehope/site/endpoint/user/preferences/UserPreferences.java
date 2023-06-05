package live.rehope.site.endpoint.user.preferences;

/**
 * User site settings and preferences.
 */
public record UserPreferences(int userId,
                              /* Preferences */
                              boolean mailingList, boolean privateProfile,
                              /* Site settings */
                              boolean animatedBackground, boolean animatedInterfaces, boolean siteMusic) {

}
