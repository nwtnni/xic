use io
use conv

main (args:int[][]) {
    x:int = 0
    i:int = 0
    while i<300000000 {
        y:int = x
        z:int = y+1
        i = i+1
    }
    println(unparseInt(x))
}