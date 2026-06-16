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
        boolean hasVite = hasConfig("vite.config");
        boolean hasWebPack = hasConfig("webpack.config");
        boolean hasMultipleBundlers = hasMultipleFilesOfType("vite.config", "webpack.config");
        boolean hasEnv = hasFile(".env");

        // TODO: fill out descriptions / prompts for root signals
        return new RepoRootRecord(
                new Signal<>(
                        RootSignals.HAS_PACKAGE_JSON, hasPackageJson, hasPackageJson ? Severity.NULL : Severity.CRITICAL,
                        ""
                ),
                new Signal<>(
                        RootSignals.HAS_README, hasReadMe, hasReadMe ? Severity.NULL : Severity.HIGH,
                        "Readmes communicate project content and intent."
                ),
                new Signal<>(
                        RootSignals.HAS_ROOT_SRC, hasRootSrc, hasRootSrc && !hasNext ? Severity.NULL : Severity.HIGH,
                        ""
                ),
                new Signal<>(
                        RootSignals.HAS_GIT_IGNORE, hasGitIgnore, hasGitIgnore ? Severity.NULL : Severity.HIGH,
                        ""
                ),
                new Signal<>(
                        RootSignals.HAS_NODE_MODULES, hasNodeModules, hasNodeModules ? Severity.HIGH : Severity.NULL,
                        "Node modules should be added to .gitignore"
                ),
                new Signal<>(
                        RootSignals.HAS_YARN_LOCKFILE, hasYarnLockFile, Severity.NULL,
                        "Understand tradeoffs between the yarn and the other package managers"
                ),
                new Signal<>(
                        RootSignals.HAS_NPM_LOCKFILE, hasNpmLockFile, Severity.NULL,
                        "Understand tradeoffs between the NPM and the other package managers"
                ),
                new Signal<>(
                        RootSignals.HAS_PNPM_LOCKFILE, hasPNPMLockFile, Severity.NULL,
                        "Understand tradeoffs between the PNPM and the other package managers"
                ),
                new Signal<>(
                        RootSignals.HAS_MULTIPLE_LOCKFILES, hasMultipleLockFiles, hasMultipleLockFiles ? Severity.HIGH : Severity.NULL,
                        ""
                ),
                new Signal<>(
                        RootSignals.HAS_LINTER, hasEsLint, hasEsLint ? Severity.NULL : Severity.MEDIUM,
                        ""
                ),
                new Signal<>(
                        RootSignals.HAS_FORMATTER, hasPrettier, hasPrettier ? Severity.NULL : Severity.LOW,
                        ""
                ),
                new Signal<>(
                        RootSignals.HAS_TYPESCRIPT_CONFIG, hasTypeScript, Severity.NULL,
                        ""
                ),
                new Signal<>(
                        RootSignals.HAS_ASTRO_CONFIG, hasAstro, Severity.NULL,
                        ""
                ),
                new Signal<>(
                        RootSignals.HAS_NEXT_CONFIG, hasNext, Severity.NULL,
                        ""
                ),
                new Signal<>(
                        RootSignals.HAS_VITE, hasVite, Severity.NULL,
                        ""
                ),
                new Signal<>(
                        RootSignals.HAS_WEBPACK, hasWebPack, Severity.NULL,
                        ""
                ),
                new Signal<>(
                        RootSignals.HAS_MULTIPLE_BUNDLERS, hasMultipleBundlers, hasMultipleBundlers ? Severity.CRITICAL : Severity.NULL,
                        ""
                ),
                new Signal<>(
                        RootSignals.HAS_ENV_FILE, hasEnv, hasEnv ? Severity.CRITICAL : Severity.NULL,
                        ""
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
