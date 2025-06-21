package dev.pretsa.scm.version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Compute project version by getting current version from scm via {@link ScmTagProvider} then computing final version based on given configurations
 */
public class VersionProvider {

    private static final Logger logger = LoggerFactory.getLogger(VersionProvider.class);

    private final ScmTagProvider scm;

    /**
     * Construct {@link VersionProvider} passing in {@link ScmTagProvider} that will be used to get current scm version
     * @param scmTagProvider provider to get current scm version
     */
    public VersionProvider(ScmTagProvider scmTagProvider) {
        this.scm = scmTagProvider;
    }

    /**
     * Compute version based on given configurations
     * @param configuration configurations
     * @return computed version
     */
    public String getVersion(Configuration configuration) {
        String currentVersionTag = scm.getLatestTag(configuration.topDirectory());
        Version currentVersion = Version.parse(currentVersionTag);
        Version finalVersion =
                switch (configuration.versionType()) {
                    case CURRENT -> currentVersion;
                    case NEXT -> currentVersion.next(configuration.nextVersionComponent());
                };

        String finalVersionString =
                finalVersion.formatted(configuration.versionQualifier().suffix());
        logger.atInfo()
                .addArgument(Configuration.KEY_VERSION)
                .addArgument(finalVersionString)
                .addArgument(configuration.describe())
                .log("{} property computed to [{}] as the [{}]");

        return finalVersionString;
    }
}
