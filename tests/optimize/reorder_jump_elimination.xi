use io
use conv

main (args:int[][]) {
    x:int = 0
    if (true) {
        // Should eliminate this bit of unreachable code
        if (false) {
            x = 1
        }
        x = 2
    }
    x = 3
}