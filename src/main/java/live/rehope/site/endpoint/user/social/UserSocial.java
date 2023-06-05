package live.rehope.site.endpoint.user.social;

/**
 * Represents a social media connection for a user.
 *
 * @param userId foreign key
 */
public record UserSocial(int id, int userId, UserSocialPlatform platform, String socialMediaId) {

}
