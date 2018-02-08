import java_cup.Main;

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*;

@CacheableTask
class CupGenerate extends DefaultTask {

    static final String source = 'src/main/cup/'
    static final String sink = 'src/main/java/parser/'
    static final String parser = 'XiParser'
    static final String symbol = 'Symbol'

    @PathSensitive(PathSensitivity.RELATIVE)
    @Input
    File input = project.file(source + parser + '.cup')

    @PathSensitive(PathSensitivity.RELATIVE)
    @OutputFile
    File output = project.file(source + parser + '.java')

    @TaskAction
    void generate() {
        try {
            project.delete(output)
            def args = ['-destdir', sink, '-parser', parser, '-symbols', symbol, input] as String[]
            Main.main(args)
        } catch (Exception e) {
            throw new StopActionException("Error during CUP code generation.")
        }
    }
}
