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
                        hasDependencies ? "Runtime dependencies your application needs to function." : "Runtime dependencies are required for your application to function."
                ),
                new Signal<>(
                        PackageJsonSignals.HAS_DEV_DEPENDENCIES, hasDevDependencies, hasDevDependencies ? Severity.NULL : Severity.HIGH,
                        hasDevDependencies ? "Dependencies only used during development, such as bundlers, test runners, and linters." : "Dev dependencies are required for local development, building, and testing."
                ),
                new Signal<>(
                        PackageJsonSignals.HAS_PEER_DEPENDENCIES, hasPeerDependencies, hasPeerDependencies ? Severity.NULL : Severity.LOW,
                        hasPeerDependencies ? "Packages your code expects the consumer to provide, e.g. a plugin expecting a specific version of React to already be installed." : "Peer dependencies define packages your code expects the consumer to provide."
                ),
                new Signal<>(
                        PackageJsonSignals.HAS_SCRIPTS, hasScripts, hasScripts ? Severity.NULL : Severity.CRITICAL,
                        hasScripts ? "Scripts for running local development, building, testing, linting, and auditing." : "Scripts are required to run local development, building, tests, linting, auditing, and anything else."
                ),
                new Signal<>(
                        PackageJsonSignals.HAS_NAME, hasName, hasName ? Severity.NULL : Severity.LOW,
                        hasName ? "The name of your package." : "Add a name to your package.json to identify your project."
                ),
                new Signal<>(
                        PackageJsonSignals.HAS_LICENCE, hasLicense, hasLicense ? Severity.NULL : Severity.LOW,
                        hasLicense ? "Defines how others may use, modify, or distribute your code." : "Add a license to clarify how others may use, modify, or distribute your code."
                ),
                new Signal<>(
                        PackageJsonSignals.HAS_TYPE, hasType, hasType ? Severity.NULL : Severity.MEDIUM,
                        hasType ? "Defines whether your package uses ES modules (\"module\") or CommonJS (\"commonjs\")." : "Set \"type\" in your package.json to declare whether you are using ES modules or CommonJS."
                ),
                new Signal<>(
                        PackageJsonSignals.HAS_MAIN, hasMain, hasMain ? Severity.NULL : Severity.LOW,
                        hasMain ? "The entry point of your package." : "Add a main field to define the entry point of your package."
                ),
                new Signal<>(
                        PackageJsonSignals.HAS_DESCRIPTION, hasDescription, hasDescription ? Severity.NULL : Severity.LOW,
                        hasDescription ? "A brief description of your project." : "Add a description to summarise what your project does."
                ),
                new Signal<>(
                        PackageJsonSignals.HAS_AUTHOR, hasAuthor, hasAuthor ? Severity.NULL : Severity.LOW,
                        hasAuthor ? "Identifies the author of the package." : "Add an author field to identify who owns or maintains this package."
                ),
                new Signal<>(
                        PackageJsonSignals.HAS_HOMEPAGE, hasHomepage, hasHomepage ? Severity.NULL : Severity.LOW,
                        hasHomepage ? "A URL pointing to the project's homepage or documentation." : "Add a homepage URL to point to your project's site or documentation."
                ),
                new Signal<>(
                        PackageJsonSignals.HAS_REPOSITORY, hasRepository, hasRepository ? Severity.NULL : Severity.LOW,
                        hasRepository ? "Links to the source repository for this package." : "Add a repository field to link to your project's source repository."
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
