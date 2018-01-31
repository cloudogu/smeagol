import java.nio.file.Path
import java.nio.file.Paths

Path ecosystem = Paths.get(".workspace", "ecosystem")
new ProcessBuilder("vagrant", "destroy", "-f")
        .directory(ecosystem.toFile())
        .start()
        .waitForProcessOutput(System.out, System.err)