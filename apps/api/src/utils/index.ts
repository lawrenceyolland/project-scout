import { ESLint } from "eslint";
import path from "node:path";
import { fileURLToPath } from "node:url";
import { exec } from "node:child_process";
import { promisify } from "node:util";

const execAsync = promisify(exec)

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

export const runESLint = async (jobPath: string) => {
    const eslint = new ESLint({
        cwd: jobPath,
        overrideConfigFile: path.resolve(__dirname, '../../eslint.config.js'),
    });

    const extPatterns = ['**/*.{js,cjs,mjs,jsx,ts,cts,mts,tsx}']
    const results = await eslint.lintFiles(extPatterns);
    const lintResult = { numErrors: 0, numWarnings: 0 };

    results.forEach((res) => {
        lintResult.numErrors += res.errorCount;
        lintResult.numWarnings += res.warningCount;
    })

    return lintResult;
}

type LockfileFlags =  {
    hasNpmLockFile: boolean,
    hasYarnLockFile: boolean,
    hasPNPMLockFile: boolean,
}

export const runVulnerabilityCheck = async (
    jobPath: string,
    {
        hasNpmLockFile,
        hasYarnLockFile,
        hasPNPMLockFile
    }: LockfileFlags
    ) => {
    const vulnerabilities: {critical: null | number, high: null | number } = { critical: null, high: null}

    if (hasNpmLockFile) {
        const { stdout } = await execAsync('npm audit --json', { cwd: jobPath }).catch((err) => err);
        try {
            const { metadata: { vulnerabilities: { critical, high} }, } = JSON.parse(stdout);
            vulnerabilities.critical = critical;
            vulnerabilities.high = high;
        } catch (e) {
            console.log('Error: failed to parse npm audit results', e)
            return vulnerabilities
        }

    } else if (hasYarnLockFile)  {
        const { stdout } = await execAsync('yarn audit --json', { cwd: jobPath }).catch((err) => err);
        try {
            const results = stdout
                .split("\n")
                .filter(Boolean)
                .map((line: string) => JSON.parse(line));
            const auditSummary = results.find((res: any) => res.type === "auditSummary");
            const { data: { vulnerabilities: { critical, high } }, } = auditSummary
            vulnerabilities.critical = critical;
            vulnerabilities.high = high;
        } catch (e) {
            console.log('Error: failed to parse yarn audit results', e)
            return vulnerabilities
        }

    } else if (hasPNPMLockFile) {
        const { stdout } = await execAsync('pnpm audit --json', { cwd: jobPath }).catch((err) => err);
       try {
           const { metadata: { vulnerabilities: { critical, high } }, } = JSON.parse(stdout)
           vulnerabilities.critical = critical;
           vulnerabilities.high = high;
       } catch (e) {
           console.log('Error: failed to parse pnpm audit results', e)
           return vulnerabilities
       }
    }
    return vulnerabilities
}