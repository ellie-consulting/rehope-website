package live.rehope.site.endpoint.user.preferences.model;

import org.jetbrains.annotations.NotNull;

/**
 * A site preference a user can have.
 */
public enum Preference {
    MAILING_LIST("mailingList", "mailing_list"),
    PRIVATE_PROFILE("privateProfile", "private_profile"),
    ANIMATED_BACKGROUND("animatedBackground", "animated_background"),
    ANIMATED_INTERFACES("animatedInterfaces", "animated_interfaces"),
    SITE_MUSIC("siteMusic", "site_music");

    public static Preference byFieldName(@NotNull String fieldName) {
        for (Preference value : values()) {
            if (value.getFieldName().equalsIgnoreCase(fieldName)) {
                return value;
            }
        }

        return null;
    }

    public static Preference bySqlKey(@NotNull String sqlKey) {
        for (Preference value : values()) {
            if (value.getSqlKey().equals(sqlKey)) {
                return value;
            }
        }

        return null;
    }

    private final String fieldName;
    private final String sqlKey;

    Preference(String fieldName, String sqlKey) {
        this.fieldName = fieldName;
        this.sqlKey = sqlKey;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getSqlKey() {
        return sqlKey;
    }


}
