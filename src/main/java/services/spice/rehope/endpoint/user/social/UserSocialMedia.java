package services.spice.rehope.endpoint.user.social;

/**
 * Represents a social media connection for a user.
 *
 * @param userId foreign key
 */
public record UserSocialMedia(int id, int userId, SocialMediaType socialMediaType, String socialMediaId) {

}
