use io
use conv

main (args:int[][]) {
    x:int = 0
    i:int = 0
    while i<200000000 {
        y:int = x
        z:int = y
        w:int = z
        i = i+1
    }
    println(unparseInt(x))
}