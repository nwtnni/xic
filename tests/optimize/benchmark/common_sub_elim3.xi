use io
use conv

main (args:int[][]) {
    i:int = 0
    y:int
    while i<20000000 {
        x:int[] = {0}
        y = x[0]+1
        if x[0]+1 == 0 {
            y = (x[0]+1)*2
        }
        else {
            y = (x[0]+1)*3
        }
        i = i+1
    }
    print(unparseInt(y))
}