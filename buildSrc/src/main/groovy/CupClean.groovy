import java_cup.Main;

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class CupClean extends DefaultTask {

    String parser = ''
    String symbol = ''
    String sink = ''

    @TaskAction
    void clean() {
        project.delete(project.file(sink + parser + '.java'))
        project.delete(project.file(sink + symbol + '.java'))
    }
}
