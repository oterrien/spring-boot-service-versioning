package com.ote.test;

import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VersionFinder {

    private final List<Version> allVersions;

    public VersionFinder(List<String> versions) {
        allVersions = versions.stream().map(Version::new).collect(Collectors.toList());
    }

    public Optional<String> find(String version) {

        ExtendedVersion extendedVersion = new ExtendedVersion(version);

        return allVersions.
                stream().
                filter(extendedVersion::isEligible).
                max(Version::compareTo).
                map(p -> p.version);
    }

    @Data
    public static class Version implements Comparable<Version> {

        protected int major;
        protected int minor;
        protected int patch;
        protected String version;

        public Version(String version) {

            this.version = version;

            String[] versionPart = version.split("\\.");

            for (int i = 0; i < versionPart.length; i++) {
                int value = Integer.parseInt(versionPart[i]);
                switch (i) {
                    case 0:
                        major = value;
                        break;
                    case 1:
                        minor = value;
                        break;
                    case 2:
                        patch = value;
                        break;
                }
            }
        }

        @Override
        public int compareTo(Version otherVersion) {

            if (major == otherVersion.major) {
                if (minor == otherVersion.minor) {
                    if (patch == otherVersion.patch) {
                        return 0;
                    }
                    return Integer.compare(patch, otherVersion.patch);
                }
                return Integer.compare(minor, otherVersion.minor);
            }
            return Integer.compare(major, otherVersion.major);
        }
    }

    @Data
    public static class ExtendedVersion extends Version {

        private boolean withGreater;

        public ExtendedVersion(String version) {
            super(version.replace("^", ""));

            withGreater = version.startsWith("^");
        }

        public boolean isEligible(Version version) {

            return withGreater ? version.compareTo(this) >= 0 : version.compareTo(this) == 0;
        }

    }
}
