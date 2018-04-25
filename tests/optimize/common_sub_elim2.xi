use io
use conv

main (args:int[][]) {
    i:int = 0
    y:int
    while i<200000000 {
        x:int = 0
        if x+1 == 0 {
            y = x+1
        }
        else {
            x = 1
            y = x+1
        }
        y = x+1
        y = 2*(x+1)
        i = i+1
    }
    print(unparseInt(y))
}