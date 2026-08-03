import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.TextProgressMonitor

import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.Files

String remote = "https://git@github.com/cloudogu/ecosystem.git"
String branch = "develop"

Path workspace = Paths.get(".workspace")
if (!Files.exists(workspace)) {
    Files.createDirectory(workspace)
}

Path ecosystem = workspace.resolve("ecosystem")
if (!Files.exists(ecosystem)) {
    Files.createDirectory(ecosystem)

    println("clone ${remote}")

    Git.cloneRepository()
        .setURI(remote)
        .setDirectory(ecosystem.toFile())
        .setBranchesToClone(Collections.singleton("refs/heads/" + branch))
        .setBranch(branch)
        .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
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
    .inheritIO()
    .start()
    .waitFor()
