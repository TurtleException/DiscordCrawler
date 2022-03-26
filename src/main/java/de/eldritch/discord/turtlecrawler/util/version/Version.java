package de.eldritch.discord.turtlecrawler.util.version;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Properties;

public record Version(byte MAJOR, byte MINOR, short BUILD, String EXTRA) {
    public static Version parse(String raw) throws IllegalVersionException {
        String[] parts = raw.split("[.\\-_]");

        byte major;
        byte minor;
        short build;
        try {
            major = Byte.parseByte(parts[0]);
            minor = Byte.parseByte(parts[1]);
            build = Short.parseShort(parts[2]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new IllegalVersionException(raw, e);
        }

        String extra = null;
        if (parts.length > 3)
            extra = parts[3];

        return new Version(major, minor, build, extra);
    }

    /**
     * Retrieves the version from <code>resources/version.properties</code>. If the file does not exist, does not
     * contain a valid version or could not be read <code>null</code> is returned and an {@link Exception} will be
     * printed to the console. It is recommended to check whether the version is <code>null</code> while constructing
     * the main class.
     * @return Version stored in resources.
     */
    public static Version retrieveFromResources() {
        try {
            Properties properties = new Properties();
            properties.load(Version.class.getClassLoader().getResourceAsStream("version.properties"));
            return parse(properties.getProperty("version"));
        } catch (NullPointerException | IOException | IllegalVersionException e) {
            System.out.println("Unable to retrieve version from resources.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Compares another {@link Version} object and returns whether
     * it represents a more recent version.
     * <p>
     *     This ignores version extras and only compares major,
     *     minor and build.
     * </p>
     * @param v The {@link Version} to compare.
     * @return true if v represents a more recent Version.
     */
    public boolean isMoreRecent(Version v) {
        if (this.MAJOR < v.MAJOR) return true;
        if (this.MINOR < v.MINOR) return true;
        return this.BUILD < v.BUILD;
    }

    /**
     * Returns a string representation of the version.
     * <p>
     *     Schema: (depending on whether <code>EXTRA</code> is defined)
     *     <li><code>MAJOR.MINOR-BUILD</code></li>
     *     <li><code>MAJOR.MINOR-BUILD_EXTRA</code></li>
     * </p>
     * @return a string representation of the object.
     */
    @Override
    public @NotNull String toString() {
        return new DecimalFormat("00").format(MAJOR) + "." + new DecimalFormat("00").format(MINOR) + "-" + new DecimalFormat("000").format(BUILD) +
                ((EXTRA != null && !EXTRA.equals("")) ? "_" + EXTRA : "");
    }
}