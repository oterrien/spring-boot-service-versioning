package com.ote.test;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VersionTest {

    @Test
    public void release_version(){
        VersionFinder.Version version = new VersionFinder.Version("1.2.3");

        Assertions.assertThat(version.getMajor()).isEqualTo(1);
        Assertions.assertThat(version.getMinor()).isEqualTo(2);
        Assertions.assertThat(version.getPatch()).isEqualTo(3);
    }

    @Test
    public void when_searching_release_version_the_release_version_should_be_found_when_exist() {

        String version = "1.2.3";

        List<String> allVersion = new ArrayList<>();
        allVersion.add("1.1.19");
        allVersion.add("1.2.3");

        Optional<String> foundVersion = new VersionFinder(allVersion).find(version);

        Assertions.assertThat(foundVersion).isPresent();
        Assertions.assertThat(foundVersion.orElse(null)).isEqualTo("1.2.3");
    }

    @Test
    public void when_searching_release_version_nothing_should_not_be_found_when_not_exist() {

        String version = "1.2.3";

        List<String> allVersion = new ArrayList<>();
        allVersion.add("1.1.19");
        allVersion.add("2.3.3");

        Optional<String> foundVersion = new VersionFinder(allVersion).find(version);

        Assertions.assertThat(foundVersion.isPresent()).isFalse();
    }

    @Test
    public void when_searching_greater_version_the_greatest_version_should_be_found_when_exist(){

        String version = "^1.2.0";

        List<String> allVersion = new ArrayList<>();
        allVersion.add("1.1.19");
        allVersion.add("1.2.3");

        Optional<String> foundVersion = new VersionFinder(allVersion).find(version);

        Assertions.assertThat(foundVersion).isPresent();
        Assertions.assertThat(foundVersion.orElse(null)).isEqualTo("1.2.3");
    }

    @Test
    public void when_searching_greater_version_the_greatest_version_should_be_found_when_many_are_eligible(){

        String version = "^1.2.0";

        List<String> allVersion = new ArrayList<>();
        allVersion.add("1.1.19");
        allVersion.add("1.2.3");
        allVersion.add("1.2.4");
        allVersion.add("2.0.0");

        Optional<String> foundVersion = new VersionFinder(allVersion).find(version);

        Assertions.assertThat(foundVersion).isPresent();
        Assertions.assertThat(foundVersion.orElse(null)).isEqualTo("2.0.0");
    }

}
