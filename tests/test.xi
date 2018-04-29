use io
use conv

main (args:int[][]) {
    a:int = 1 
    b:int = 2 
    c:int = 3
    d:int = c
    e:int = d
    f:int = foo()
    
    printint(a)
    printint(b)
    printint(c)
    printint(d)
    printint(e)
    printint(f)
}

foo() : int {
    return 1
}

printint (i: int) {
    println(unparseInt(i))
}
