import {Hono} from "hono";
import { HTTPException } from 'hono/http-exception'
import * as fs from "node:fs";
import { simpleGit } from 'simple-git';
import PackageJsonAnalyser from "../../../../packages/analysers/src/repo/packageJsonAnalyser.js";
import RepoMetrics from "../../../../packages/analysers/src/repo/repoMetrics.js";
import FrameworkAnalyser from "../../../../packages/analysers/src/repo/frameworkAnalyser.js";
import {exec, spawn} from "node:child_process";
import {promisify} from "node:util";
import path from "node:path"

const router = new Hono();
const BASE_PATH = '/tmp/project-scout'
const execAsync = promisify(exec)

router.post("", async (c) => {
    const body = await c.req.json()

    if (!body?.repoUrl) {
      throw new HTTPException(500)
    }

    const jobId = `job_${c.get("requestId")}`
    let files = [];
    let stdout;
    let stderr;
    try {
        const jobPath = `${BASE_PATH}/jobs/${jobId}`

        if (!fs.existsSync(jobPath)) {
            fs.mkdirSync(jobPath, { recursive: true })
        }

        const dir =  fs.readdirSync(jobPath)
        console.log(`--- Job dir for ${jobId} has been created ---`)

        await simpleGit().clone(body.repoUrl, jobPath)

        const jarPath = path.resolve(
            process.cwd(),
            '../../packages/analysers/JavaRepoAnalyser/target/JavaRepoAnalyser-1.0-jar-with-dependencies.jar',
        )

        const command = `java -jar ${jarPath} ${jobPath}`;

        ({ stdout, stderr } = await execAsync(command));

        if (stderr) {
            return c.json({ success: false, error: stderr})
        }

        const repoAnalysis = JSON.parse(stdout);

        console.log({repoAnalysis})

        // TODO: incrementally move the below to the java analyser
        // 1. easy root level checks [/]
        // 2. package.json analysis [/]
        // 3. get the framework used in the repo [/]
        // 4. repo metrics (counts, sizes, depth)
            // const repoMetrics = await RepoMetrics.init(jobPath, pkg);
            // const filesInRepo = repoMetrics.getFileTypes() || {};
            // const numFiles = repoMetrics.getNumTotalFiles()
            // const numCodeFiles = repoMetrics.getNumCodeFiles()
            // const maxDepth = repoMetrics.getMaxDepth();
            // const linesPerCodeFile = await repoMetrics.getLinesPerCodeFile()
            // const linePerFileType = RepoMetrics.getLinesPerFileType(linesPerCodeFile || {});
            // const avgPerFileType = RepoMetrics.getAvgLinesPerFileType(linesPerCodeFile || {}, filesInRepo || {})
            // const medianPerFileType = RepoMetrics.getMedianLinesPerFileType(linesPerCodeFile || {}, filesInRepo || {})

        // 5. TODO: deeper file discovery (nested)
        //    given the framework (and version) are there any structural outliers - app router vs pages/

        // if no src then dont do the below!
        // let srcStructure: Record<string, boolean> | null = null;
        //
        // if (easyChecksResult.hasRootSrc) {
        //     srcStructure = repoMetrics.checkStructure();
        // }

        // 6. TODO: run audit-ci (stays here in .ts)
        // 7. TODO: run lint (stays here in .ts)

        fs.rm(jobPath, {recursive: true}, err => {
            if (err) {
                throw err;
            }

            console.log(`--- Job dir for ${jobId} has been removed ---`)
        })
        return c.json({ success: true, data: repoAnalysis})
    } catch (e: any) {
        console.error(e)
        throw new HTTPException(500, {message: e?.message ?? 'Server Error'});
    }
})

export default router