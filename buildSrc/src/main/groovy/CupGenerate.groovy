import java_cup.Main;

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.StopActionException
import org.gradle.api.tasks.TaskAction

class CupGenerate extends DefaultTask {

    String parser = ''
    String symbol = ''
    String source = ''
    String sink = ''


    @TaskAction
    void generate() {
        try {
            File input = project.file(source + parser + '.cup')
            def args = ['-destdir', sink, '-parser', parser, '-symbols', symbol, input] as String[]
            Main.main(args)
        } catch (Exception e) {
            throw new StopActionException("Error during CUP code generation.")
        }
    }
}
