package com.projectscout;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class PackageJsonAnalyser {
    private final PackageJson pkg;
    private static final ObjectMapper mapper = new ObjectMapper();

    public PackageJsonRecord getPkgFields() {
        boolean hasDependencies = this.pkg.hasDependencies();
        boolean hasDevDependencies = this.pkg.hasDevDependencies();
        boolean hasPeerDependencies = this.pkg.hasPeerDependencies();
        boolean hasScripts = this.pkg.hasScripts();
        boolean hasName = this.pkg.hasName();
        boolean hasLicense = this.pkg.hasLicense();
        boolean hasType = this.pkg.hasType();
        boolean hasMain = this.pkg.hasMain();
        boolean hasDescription = this.pkg.hasDescription();
        boolean hasAuthor = this.pkg.hasAuthor();
        boolean hasHomepage = this.pkg.hasHomepage();
        boolean hasRepository = this.pkg.hasRepository();

        // TODO: fill out descriptions / prompts for pkg signals
        return new PackageJsonRecord(
                new Signal<>(
                        PackageJsonSignals.HAS_DEPENDENCIES, hasDependencies, hasDependencies ? Severity.NULL : Severity.CRITICAL,
                        ""
                ),
                new Signal<>(
                        PackageJsonSignals.HAS_DEV_DEPENDENCIES, hasDevDependencies, hasDevDependencies ? Severity.NULL : Severity.HIGH,
                        ""
                ),
                new Signal<>(
                        PackageJsonSignals.HAS_PEER_DEPENDENCIES, hasPeerDependencies, hasPeerDependencies ? Severity.NULL : Severity.LOW,
                        ""
                ),
                new Signal<>(
                        PackageJsonSignals.HAS_SCRIPTS, hasScripts, hasScripts ? Severity.NULL : Severity.CRITICAL,
                        ""
                ),
                new Signal<>(
                        PackageJsonSignals.HAS_NAME, hasName, hasName ? Severity.NULL : Severity.LOW,
                        ""
                ),
                new Signal<>(
                        PackageJsonSignals.HAS_LICENCE, hasLicense, hasLicense ? Severity.NULL : Severity.LOW,
                        ""
                ),
                new Signal<>(
                        PackageJsonSignals.HAS_TYPE, hasType, hasType ? Severity.NULL : Severity.MEDIUM,
                        ""
                ),
                new Signal<>(
                        PackageJsonSignals.HAS_MAIN, hasMain, hasMain ? Severity.NULL : Severity.LOW,
                        ""
                ),
                new Signal<>(
                        PackageJsonSignals.HAS_DESCRIPTION, hasDescription, hasDescription ? Severity.NULL : Severity.LOW,
                        ""
                ),
                new Signal<>(
                        PackageJsonSignals.HAS_AUTHOR, hasAuthor, hasAuthor ? Severity.NULL : Severity.LOW,
                        ""
                ),
                new Signal<>(
                        PackageJsonSignals.HAS_HOMEPAGE, hasHomepage, hasHomepage ? Severity.NULL : Severity.LOW,
                        ""
                ),
                new Signal<>(
                        PackageJsonSignals.HAS_REPOSITORY, hasRepository, hasRepository ? Severity.NULL : Severity.LOW,
                        ""
                )
        );
    }

    public PackageJson getPkg() {
        return this.pkg;
    }

    public PackageJsonAnalyser(String filePath) {
        File pkgFile = new File(filePath, "package.json");
        if (!pkgFile.exists() || !pkgFile.isFile()) {
            throw new IllegalArgumentException("package.json file does not exist " + filePath);
        }

        try {
            this.pkg = mapper.readValue(pkgFile, PackageJson.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
