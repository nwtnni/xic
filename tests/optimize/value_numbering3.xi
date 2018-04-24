use io
use conv

main (args:int[][]) {
    i:int = 0
    while i<40000000 {
        x:int[] = {0}
        y:int = x[0]
        if y >= 0 {
            x[0] = y+1
        }
        i = i+1
    }
}