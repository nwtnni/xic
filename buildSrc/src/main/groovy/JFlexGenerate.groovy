import jflex.GeneratorException
import jflex.Main

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.tasks.*;

class JFlexGenerate extends DefaultTask {

    String lexer = 'XiLexer'
    String source = 'src/main/jflex/'
    String sink = 'src/main/java/lexer/'

    @PathSensitive(PathSensitivity.RELATIVE)
    @InputDirectory
    File input = project.file(source)

    @PathSensitive(PathSensitivity.RELATIVE)
    @OutputFile
    File output = project.file(sink + lexer + '.java')

    @TaskAction
    void generate() {
        try {
            project.delete(output)
            def args = ['-d', sink, project.file(source + lexer + '.jflex')] as String[]
            Main.generate(args)
        } catch (GeneratorException e) {
            throw new StopActionException("Error during JFlex code generation.")
        }
    }
}
