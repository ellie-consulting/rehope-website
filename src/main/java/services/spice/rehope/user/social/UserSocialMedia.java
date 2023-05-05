package services.spice.rehope.user.social;

/**
 * Represents a social media connection for a user.
 *
 * @param userId foreign key
 */
public record UserSocialMedia(long id, long userId, SocialMediaType socialMediaType, String socialMediaId) {

}
