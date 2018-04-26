use io
use conv

main (args:int[][]) {
    x:int = 0
    i:int = x
    while i<300000000 {
        y:int = x
        if y >= 0 {
            x = y+1
        }
        i = i+1
    }
    println(unparseInt(x))
}