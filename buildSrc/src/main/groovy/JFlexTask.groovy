import jflex.GeneratorException
import jflex.Main

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.tasks.StopActionException
import org.gradle.api.tasks.TaskAction

class JFlexTask extends DefaultTask {

    File source = project.file('src/main/jflex/XiLexer.jflex')
    File sink = project.file('src/main/java/lexer/')
    File clean = project.file('src/main/java/lexer/XiLexer.java')

    @TaskAction
    void generate() {
        try {
            project.delete(clean)
            def args = ['-q', '-d', sink, source] as String[]
            Main.generate(args)
        } catch (GeneratorException e) {
            throw new StopActionException("Error during JFlex code generation.")
        }
    }
}
