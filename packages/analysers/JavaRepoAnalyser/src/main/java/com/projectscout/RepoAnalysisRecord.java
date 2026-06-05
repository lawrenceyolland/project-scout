package com.projectscout;

import java.util.Map;

public record RepoAnalysisRecord(
        RepoRootRecord rootResult,
        PackageJsonRecord pkgResult,
        Map<String, Integer> frameworkScores
) {}
