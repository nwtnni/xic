import java_cup.Main;

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*;

class CupGenerate extends DefaultTask {

    static final String source = 'src/main/cup/'
    static final String sink = 'src/main/java/parser/'
    static final String pkg = 'parser'
    static final String parser = 'XiParser'
    static final String symbol = 'XiSymbol'

    @PathSensitive(PathSensitivity.RELATIVE)
    @InputDirectory
    File input = project.file(source)

    @PathSensitive(PathSensitivity.RELATIVE)
    @OutputFile
    File output = project.file(source + parser + '.java')

    @TaskAction
    void generate() {
        try {
            project.delete(output)
            def args = ['-destdir', sink, '-parser', parser, '-symbols', symbol, '-package', pkg, project.file(source + parser + '.cup')] as String[]
            Main.main(args)
        } catch (Exception e) {
            throw new StopActionException("Error during CUP code generation.")
        }
    }
}
