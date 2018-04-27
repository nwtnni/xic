use io
use conv

main (args:int[][]) {
    i:int = 0
    x:int = 1
    a:int = 2
    b:int = a+x
    c:int = a+x
    while i<500000000 {
        b = a+x
        c = a+x
        i = i+1
    }
    println(unparseInt(b))
    println(unparseInt(c))
}