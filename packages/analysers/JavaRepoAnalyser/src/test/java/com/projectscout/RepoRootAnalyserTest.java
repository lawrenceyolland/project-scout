//package com.projectscout;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.io.TempDir;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*; // or org.junit.Assert.* in JUnit 4
//
//@Test
//void testHasFile(@TempDir Path tempDir) throws IOException {
//    Files.createFile(tempDir.resolve("package.json"));
//    Files.writeString(tempDir.resolve("package.json"), """
//                      {
//                      "name": "test-project",
//                      "dependencies": {
//                           "react": "^18.3.1",
//                           "react-dom": "^18.3.1"
//                      },
//                      "devDependencies": {
//                          "@types/react": "^18.3.3",
//                          "@vitejs/plugin-react": "^4.3.1"
//                      }
//                    }
//                    """
//    );
//
//    RepoRootAnalyser analyser = new RepoRootAnalyser(tempDir.toString());
//    assertTrue(analyser.hasFile("package.json"));
//}
//
