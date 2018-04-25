use io
use conv

main (args:int[][]) {
    x:int
    i:int = 0
    while i<60000000 {
        x = 10000000/10/10/10
        i = i+1
    }
    println(unparseInt(x))
}