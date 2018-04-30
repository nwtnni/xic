use io
use conv

main (args:int[][]) {
    x:int
    i:int = 0
    while i<100000000 {
        x = x*1*1*1*1
        i = i+1
    }
    println(unparseInt(x))
}