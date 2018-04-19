import java_cup.Main;

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*;

class CupIXi extends DefaultTask {

    static final String source = 'src/main/cup/'
    static final String sink = 'src/main/java/parse/'
    static final String pkg = 'parse'
    static final String parser = 'IXiParser'
    static final String symbol = 'IXiSymbol'

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
