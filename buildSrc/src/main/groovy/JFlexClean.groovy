import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class JFlexClean extends DefaultTask {

    String lexer = ''
    String sink = ''

    @TaskAction
    void clean() {
        project.delete(project.file(sink + lexer + '.java'))
    }
}
