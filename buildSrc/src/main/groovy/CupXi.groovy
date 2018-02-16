import java_cup.Main;

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*;

class CupXi extends DefaultTask {

    static final String source = 'src/main/cup/'
    static final String sink = 'src/main/java/parser/'
    static final String pkg = 'parser'
    static final String parser = 'XiParser'
    static final String symbol = 'XiSymbol'

    @PathSensitive(PathSensitivity.RELATIVE)
    @InputDirectory
    File input = project.file(source)

    @PathSensitive(PathSensitivity.RELATIVE)
    @OutputDirectory
    File output = project.file(sink)

    @TaskAction
    void generate() {
        try {
            project.delete(project.file(sink + parser + '.java'))
            project.delete(project.file(sink + symbol + '.java'))
            def args = [
                '-locations',
                '-destdir', sink,
                '-parser', parser,
                '-symbols', symbol,
                '-package', pkg,
                project.file(source + parser + '.cup')
            ] as String[]
            Main.main(args)
        } catch (Exception e) {
            throw new StopActionException("Error during CUP code generation.")
        }
    }
}
