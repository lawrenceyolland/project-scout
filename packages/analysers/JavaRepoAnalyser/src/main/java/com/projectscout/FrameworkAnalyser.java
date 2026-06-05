package com.projectscout;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrameworkAnalyser {
    private final PackageJson pkg;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Map<String, List<String>> signals;
    private static final Map<String, Integer> signalScores;
    private static final Map<String,String> metaMap;
    static {
        signals = new HashMap<String, List<String>>();
        signalScores = new HashMap<String, Integer>();
        metaMap = new HashMap<>();
        signalScores.put("react", 0);
        signalScores.put("vue", 0);
        signalScores.put("astro", 0);
        signalScores.put("angular", 0);
        signalScores.put("next", 0);
        signalScores.put("nuxt", 0);
        signalScores.put("analog", 0);
        signalScores.put("solid", 0);
        signalScores.put("svelte", 0);
        signalScores.put("sveltekit", 0);

        signals.put("react", List.of("@types/react", "@types/react-dom",
                "react", "react-dom",
                "@vitejs/plugin-react", "eslint-plugin-react-hooks",
                "eslint-plugin-react-refresh", "react-redux"));
        signals.put("vue", List.of("vue", "@vue/", "eslint-plugin-vue", "@vitejs/plugin-vue", "pinia"));
        signals.put("astro", List.of("astro", "eslint-plugin-astro", "@astrojs/"));
        signals.put("angular", List.of("angular", "@angular-eslint/eslint-plugin"));
        signals.put("solid", List.of("solid-js", "eslint-plugin-solid"));
        signals.put("svelte", List.of("svelte", "eslint-plugin-svelte", "@sveltejs/adapter-vercel", "@sveltejs/vite-plugin-svelte"));

        signals.put("next", List.of("next", "eslint-config-next", "next-auth"));
        signals.put("nuxt", List.of("nuxt", "@nuxt/", "vue-router"));
        signals.put("analog", List.of("@analogjs/vite-plugin-angular"));
        signals.put("sveltekit", List.of("@sveltejs/kit"));

        metaMap.put("next", "react");
        metaMap.put("nuxt", "vue");
        metaMap.put("analog", "angular");
        metaMap.put("sveltekit", "svelte");

    }
    private boolean matchesReactExtraCheck(String key) {
        String lowerKey = key.toLowerCase();

        if (lowerKey.equals("preact") || lowerKey.startsWith("preact-")) {
            return false;
        }

        if (lowerKey.contains("reactiv")) {
            return false;
        }

        return lowerKey.contains("react");
    }

    private boolean isFramework(String signalKey, String depKey) {
        List<String> signalPrefixes = signals.get(signalKey);

        return signalPrefixes.stream().anyMatch(x -> x.equals(depKey));
    }

    public Map<String, Integer> getSignalScores() {
        if (pkg.hasDependencies()) {
            for (Map.Entry<String,String> dep : pkg.dependencies.entrySet()) {
                frameworkCheck(dep);
            }
        }

        if (pkg.hasDevDependencies()) {
            for (Map.Entry<String,String> dep : pkg.devDependencies.entrySet()) {
                frameworkCheck(dep);
            }
        }

        if (pkg.hasPeerDependencies()) {
            for (Map.Entry<String,String> dep : pkg.peerDependencies.entrySet()) {
                frameworkCheck(dep);
            }
        }

        if (pkg.hasScripts()) {
            for (Map.Entry<String, String> script : pkg.scripts.entrySet()) {
                String currentScriptValue = script.getValue();
                for (Map.Entry<String,Integer> fw : signalScores.entrySet()) {
                    String fwName = fw.getKey();

                    if (currentScriptValue.contains(fwName)) {
                        signalScores.put(fwName, signalScores.get(fwName) + 1);
                        if (metaMap.containsKey(fwName)) {
                            String mappedFramework = metaMap.get(fwName);
                            signalScores.put(mappedFramework, signalScores.get(mappedFramework) + 1);
                        }
                    }
                }
            }
        }

        return signalScores;
    }

    private void frameworkCheck(Map.Entry<String, String> dep) {
        String currentDependencyKey = dep.getKey();
        for (Map.Entry<String, List<String>> signal: signals.entrySet()) {
            String currentSignalKey = signal.getKey();
            if (
                    isFramework(currentSignalKey, currentDependencyKey) ||
                            (currentSignalKey.equals("react")
                                    ? matchesReactExtraCheck(currentDependencyKey)
                                    : currentDependencyKey.contains(currentSignalKey))
            ) {
                if (signalScores.containsKey(currentSignalKey)) {
                    signalScores.put(currentSignalKey, signalScores.get(currentSignalKey) + 1);
                }
                if (metaMap.containsKey(currentSignalKey)) {
                    String mappedFramework = metaMap.get(currentSignalKey);
                    signalScores.put(mappedFramework, signalScores.get(mappedFramework) + 1);
                }
            }
        }
    }

    public FrameworkRecord estimateFramework() {
        Map.Entry<String, Integer> maxFrameworkEntry = null;
        Map.Entry<String, Integer> maxMetaFrameworkEntry = null;

        for (Map.Entry<String, Integer> entry : signalScores.entrySet()) {
            if (entry.getValue().equals(0)) continue;

            if (metaMap.containsKey(entry.getKey())) {
                if (maxMetaFrameworkEntry == null || entry.getValue().compareTo(maxMetaFrameworkEntry.getValue()) > 0) {
                    maxMetaFrameworkEntry = entry;
                }
            } else if (maxFrameworkEntry == null || entry.getValue().compareTo(maxFrameworkEntry.getValue()) > 0) {
                    maxFrameworkEntry = entry;
            }
        }

        if (maxFrameworkEntry == null) {
            return null;
        }

        return new FrameworkRecord(
             maxFrameworkEntry.getKey(),
                maxMetaFrameworkEntry != null ? maxMetaFrameworkEntry.getKey() : null
        );
    }

    public FrameworkAnalyser(String filePath) {
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
