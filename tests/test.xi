use io
use conv

main (args:int[][]) {
    a:int = 1 
    b:int = 2 
    c:int = 3
    d:int = c
    e:int = d
    f:int = e

    d = a + 1
    e = a + 1
    f = a + 1 + b
    // b = 2
    // printInt(a)
    // printInt(b)
    // printInt(c)
    // a = a + 1
}

printint (i: int) {
    println(unparseInt(i))
}
