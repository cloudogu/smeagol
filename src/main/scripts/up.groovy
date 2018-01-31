import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.TextProgressMonitor

import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.Files

String remote = "https://github.com/cloudogu/ecosystem"
String branch = "develop"

Path workspace = Paths.get(".workspace")
if (!Files.exists(workspace)) {
    Files.createDirectory(workspace)
}

Path ecosystem = workspace.resolve("ecosystem")
if (!Files.exists(workspace)) {
    println("clone ${remote}")

    Git.cloneRepository()
        .setURI(remote)
        .setDirectory(ecosystem.toFile())
        .setBranchesToClone(Collections.singleton("refs/head/" + branch))
        .setBranch(branch)
        .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
        .set
        .call()
        .close()
}

Path setupJson = ecosystem.resolve("setup.json")
if (!Files.exists(setupJson)) {
    Path source = Paths.get("src/main/scripts/setup.json")
    Files.copy(source, setupJson)
}

new ProcessBuilder("vagrant", "up")
        .directory(ecosystem.toFile())
        .start()
        .waitForProcessOutput(System.out, System.err)
