use io
use conv

main (args:int[][]) {
    x:int
    i:int = 0
    while i<300000000 {
        x = 5*5*5*5
        i = i+1
    }
    println(unparseInt(x))
}