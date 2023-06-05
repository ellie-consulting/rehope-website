package live.rehope.site.endpoint.user.social.model;

/**
 * Represents a social media connection for a user.
 *
 * @param userId foreign key
 */
public record UserSocial(int id, int userId, UserSocialPlatform platform, String socialMediaId) {

}
