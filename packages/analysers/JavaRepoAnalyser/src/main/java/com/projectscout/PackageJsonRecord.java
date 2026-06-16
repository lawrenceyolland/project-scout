package com.projectscout;

public record PackageJsonRecord(
        Signal<PackageJsonSignals> hasDependencies,
        Signal<PackageJsonSignals> hasDevDependencies,
        Signal<PackageJsonSignals> hasPeerDependencies,
        Signal<PackageJsonSignals> hasScripts,
        Signal<PackageJsonSignals> hasName,
        Signal<PackageJsonSignals> hasLicense,
        Signal<PackageJsonSignals> hasType,
        Signal<PackageJsonSignals> hasMain,
        Signal<PackageJsonSignals> hasDescription,
        Signal<PackageJsonSignals> hasAuthor,
        Signal<PackageJsonSignals> hasHomepage,
        Signal<PackageJsonSignals> hasRepository
) {}
