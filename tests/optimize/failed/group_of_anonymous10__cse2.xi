use io
use conv
main(args:int[][]) {
    c:int = 50
    d:int = 100
    x:int = c + d + c + d + c + d * c * d * c
    j:int = 0
    while (j < 1000000000000000) {
        j = j + d * c * d * c
    }
    println(unparseInt(j)) 
}