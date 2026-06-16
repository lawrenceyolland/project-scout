package com.projectscout;

public record RepoRootRecord(
        Signal<RootSignals> hasPackageJson,
        Signal<RootSignals> hasReadMe,
        Signal<RootSignals> hasRootSrc,
        Signal<RootSignals> hasGitIgnore,
        Signal<RootSignals> hasNodeModules,
        Signal<RootSignals> hasYarnLockFile,
        Signal<RootSignals> hasNpmLockFile,
        Signal<RootSignals> hasPNPMLockFile,
        Signal<RootSignals> hasMultipleLockFiles,
        Signal<RootSignals> hasEsLint,
        Signal<RootSignals> hasPrettier,
        Signal<RootSignals> hasTypeScript,
        Signal<RootSignals> hasAstro,
        Signal<RootSignals> hasNext,
        Signal<RootSignals> hasVite,
        Signal<RootSignals> hasWebPack,
        Signal<RootSignals> hasMultipleBundlers,
        Signal<RootSignals> hasEnv
){};
