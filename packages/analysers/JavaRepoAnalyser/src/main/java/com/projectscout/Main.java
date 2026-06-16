package com.projectscout;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Error: Failed to pass filepath to repo");
            System.exit(1);
        }

        String repoPath = args[0];

        try {
            RepoRootAnalyser repoRootAnalyser = new RepoRootAnalyser(repoPath);
            RepoRootRecord rootResult = repoRootAnalyser.analyse();

            PackageJsonRecord pkgResult = null;
            if (rootResult.hasPackageJson().present()) {
                PackageJsonAnalyser packageJsonAnalyser = new PackageJsonAnalyser(repoPath);
                pkgResult = packageJsonAnalyser.getPkgFields();
            }

            // TODO: if env file exists check contents result.hasEnv()
            FrameworkAnalyser frameworkAnalyser = new FrameworkAnalyser(repoPath);
            Map<String, Integer> scores = frameworkAnalyser.getSignalScores();
            FrameworkRecord estimatedFramework = frameworkAnalyser.estimateFramework();

            ObjectMapper mapper = new ObjectMapper();
            RepoAnalysisRecord combinedAnalysis = new RepoAnalysisRecord(
                    rootResult,
                    pkgResult,
                    scores,
                    estimatedFramework
            );
            String json = mapper.writeValueAsString(combinedAnalysis);
            System.out.println(json);

        } catch (Exception e) {
            System.err.println("JSON Error: " + e.getMessage());
        }
    }
}