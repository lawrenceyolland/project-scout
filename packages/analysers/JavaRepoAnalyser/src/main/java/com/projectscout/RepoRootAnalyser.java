package com.projectscout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class RepoRootAnalyser {
    private final File directory;
    private final List<String> fileNames = new ArrayList<>();

    private boolean hasFile(String root) {
        return fileNames.contains(root);
    }

    private boolean hasConfig(String prefix) {
        List<String> extensions = List.of(".js", ".ts" , ".cjs", ".mjs");

        return extensions.stream().anyMatch(ext -> fileNames.contains(prefix + ext));
    }

    private boolean hasAnyConfig(String... prefixes) {
        for (String prefix : prefixes) {
            if (hasConfig(prefix) || hasFile(prefix) ) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAnyFile(String... files) {
        for (String file : files) {
            if (hasFile(file)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasMultipleFilesOfType(String... files) {
        int counter = 0;

        for (String file : files) {
            if (hasFile(file)) {
                counter += 1;
            }
        }
        return counter > 1;
    }

    public String primaryLockFile (RepoRootRecord root) {
        if (root.hasPNPMLockFile().present()) {
            return "pnpm-lock.yaml";
        } else if (root.hasYarnLockFile().present()) {
            return "yarn.lock";
        } else if (root.hasNpmLockFile().present()) {
            return "package-lock.json";
        };

        return null;
    }

    public RepoRootRecord analyse() {
        File[] rootFiles = directory.listFiles();

        if (rootFiles != null) {
            for (File file : rootFiles) {
                this.fileNames.add(file.getName());
            }
        }
        boolean hasPackageJson = hasFile("package.json");
        boolean hasReadMe = hasFile("README.md");
        boolean hasRootSrc = hasFile("src");
        boolean hasGitIgnore = hasFile(".gitignore");
        boolean hasNodeModules = hasFile("node_modules");
        boolean hasYarnLockFile = hasFile("yarn.lock");
        boolean hasNpmLockFile = hasFile("package-lock.json");
        boolean hasPNPMLockFile = hasFile("pnpm-lock.yaml");
        boolean hasMultipleLockFiles = hasMultipleFilesOfType("yarn.lock", "package-lock.json", "pnpm-lock.yaml");
        boolean hasEsLint = hasAnyConfig("eslint.config", ".eslintrc");
        boolean hasPrettier = hasConfig(".prettierrc");
        boolean hasTypeScript = hasConfig("tsconfig");
        boolean hasAstro = hasConfig("astro");
        boolean hasNext = hasConfig("next.config");
        boolean hasNuxt = hasConfig("nuxt.config");
        boolean hasVite = hasConfig("vite.config");
        boolean hasWebPack = hasConfig("webpack.config");
        boolean hasMultipleBundlers = hasMultipleFilesOfType("vite.config", "webpack.config");
        boolean hasEnv = hasFile(".env");

        // TODO: fill out descriptions / prompts for root signals
        return new RepoRootRecord(
                new Signal<>(
                        RootSignals.HAS_PACKAGE_JSON, hasPackageJson, hasPackageJson ? Severity.NULL : Severity.CRITICAL,
                        hasPackageJson ? "The entry point for your project's dependencies, scripts, and metadata." : "A package.json is required to manage dependencies and scripts for your project."
                ),
                new Signal<>(
                        RootSignals.HAS_README, hasReadMe, hasReadMe ? Severity.NULL : Severity.HIGH,
                        hasReadMe ? "A Readme communicates project content and intent, and can be a place to demonstrate how someone can get started with your repo." : "Add a README to communicate project content and intent, and to help others get started with your repo."
                ),
                new Signal<>(
                        RootSignals.HAS_ROOT_SRC, hasRootSrc, hasRootSrc && (!hasNext || !hasNuxt) ? Severity.NULL : Severity.HIGH,
                        hasRootSrc ? "Source files are organised in a dedicated src/ directory, separating application code from config and tooling." : "Add a src/ directory to separate application code from config and tooling at the project root."
                ),
                new Signal<>(
                        RootSignals.HAS_GIT_IGNORE, hasGitIgnore, hasGitIgnore ? Severity.NULL : Severity.HIGH,
                        hasGitIgnore ? "Prevents unwanted files from being committed, reducing bloat and protecting access to your services." : "Add a .gitignore to prevent unwanted files from being committed, reducing bloat and protecting access to your services."
                ),
                new Signal<>(
                        RootSignals.HAS_NODE_MODULES, hasNodeModules, hasNodeModules ? Severity.HIGH : Severity.NULL,
                        hasNodeModules
                                ? (hasGitIgnore
                                        ? "node_modules/ should be added to .gitignore."
                                        : "Create a .gitignore file and add node_modules/.")
                                : "node_modules/ is not committed to the repository."
                ),
                new Signal<>(
                        RootSignals.HAS_YARN_LOCKFILE, hasYarnLockFile, Severity.NULL,
                        hasYarnLockFile ? "Yarn lockfile detected. Ensures deterministic installs across environments." : "Understand the tradeoffs between Yarn and other package managers."
                ),
                new Signal<>(
                        RootSignals.HAS_NPM_LOCKFILE, hasNpmLockFile, Severity.NULL,
                        hasNpmLockFile ? "NPM lockfile detected. Ensures deterministic installs across environments." : "Understand the tradeoffs between NPM and other package managers."
                ),
                new Signal<>(
                        RootSignals.HAS_PNPM_LOCKFILE, hasPNPMLockFile, Severity.NULL,
                        hasPNPMLockFile ? "PNPM lockfile detected. Ensures deterministic installs across environments." : "Understand the tradeoffs between PNPM and other package managers."
                ),
                new Signal<>(
                        RootSignals.HAS_MULTIPLE_LOCKFILES, hasMultipleLockFiles, hasMultipleLockFiles ? Severity.HIGH : Severity.NULL,
                       "Different lockfiles will fall out of sync, resulting in other devs or CI environments installing different dependency versions. Can cause bugs that are hard to trace."
                ),
                new Signal<>(
                        RootSignals.HAS_LINTER, hasEsLint, hasEsLint ? Severity.NULL : Severity.LOW,
                        hasEsLint ? "Linters help maintain consistent standards across your codebase." : "Add a linter to maintain consistent standards across your codebase."
                ),
                new Signal<>(
                        RootSignals.HAS_FORMATTER, hasPrettier, hasPrettier ? Severity.NULL : Severity.LOW,
                        hasPrettier ? "Formatters help maintain consistent code style across your codebase." + (hasEsLint ? " You typically don't need both a linter and a formatter." : "") : "Add a formatter to maintain consistent code style across your codebase."
                ),
                new Signal<>(
                        RootSignals.HAS_TYPESCRIPT_CONFIG, hasTypeScript, Severity.NULL,
                        hasTypeScript ? "TypeScript is configured, enabling static type checking across your project." : "Consider adding TypeScript for static type checking and improved developer experience."
                ),
                new Signal<>(
                        RootSignals.HAS_ASTRO_CONFIG, hasAstro, Severity.NULL,
                        hasAstro ? "Astro config detected." : ""
                ),
                new Signal<>(
                        RootSignals.HAS_NEXT_CONFIG, hasNext, Severity.NULL,
                        hasNext ? "Next.js config detected." : ""
                ),
                new Signal<>(
                        RootSignals.HAS_NUXT_CONFIG, hasNuxt, Severity.NULL,
                        hasNuxt ? "Nuxt.js config detected." : ""
                ),
                new Signal<>(
                        RootSignals.HAS_VITE, hasVite, Severity.NULL,
                        hasVite ? "Vite config detected." : ""
                ),
                new Signal<>(
                        RootSignals.HAS_WEBPACK, hasWebPack, Severity.NULL,
                        hasWebPack ? "Webpack config detected." : ""
                ),
                new Signal<>(
                        RootSignals.HAS_MULTIPLE_BUNDLERS, hasMultipleBundlers, hasMultipleBundlers ? Severity.CRITICAL : Severity.NULL,
                        "Having multiple bundler configs present may indicate leftover configuration from a migration. Ensure only one bundler is active in your build pipeline."
                ),
                new Signal<>(
                        RootSignals.HAS_ENV_FILE, hasEnv, hasEnv ? Severity.CRITICAL : Severity.NULL,
                        (
                                hasGitIgnore
                                        ? "env files should be added to .gitignore. "
                                        : "Create a .gitignore file and add any env files. "
                        ) + "env files have high potential for risk as they can carry credentials you do not want being published into your git history."
                )
        );
    }

    public RepoRootAnalyser(String filePath) {
        this.directory = new File(filePath);
        if (!directory.exists() || !directory.isDirectory()){
            throw new IllegalArgumentException("Repository root does not exist");
        }
    }
}
