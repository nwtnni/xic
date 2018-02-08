import jflex.GeneratorException
import jflex.Main

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.tasks.StopActionException
import org.gradle.api.tasks.TaskAction

class JFlexGenerate extends DefaultTask {

    String lexer = ''
    String source = ''
    String sink = ''

    @TaskAction
    void generate() {
        try {
            File input = project.file(source + lexer + '.jflex')
            def args = ['-d', sink, input] as String[]
            Main.generate(args)
        } catch (GeneratorException e) {
            throw new StopActionException("Error during JFlex code generation.")
        }
    }
}
