use io
use conv

main (args:int[][]) {
    i:int = 0
    while i < 5 {
        j:int, _ = parseInt(readln())
        println(unparseInt(j + 1))
        i = i + 1
    }
    println(unparseInt(-1))
}