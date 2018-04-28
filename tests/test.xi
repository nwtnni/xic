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
    
    i:int = foo()
    a = i + 1
    b = i + 1
    c = i + 1
}

foo() : int {
    return 1
}

printint (i: int) {
    println(unparseInt(i))
}
