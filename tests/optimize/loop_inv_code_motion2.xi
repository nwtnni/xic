use io
use conv

main (args:int[][]) {
    i:int = 0
    x:int[1]
    y:int[1]
    z:int[1]
    while i<70000000 {
        x[0] = 1
        y[0] = 1
        z[0] = 1
        i = i+1
    }
    print(unparseInt(x[0]))
}