use io
use conv

main (args:int[][]) {
    i:int = 0
    c:int = 0
    while i<200000000 {
        c = foo(i)
        i = i+1
    }
    print(unparseInt(c))
}

foo(x:int):int {
    a:int = 0
    b:int = a+1
    if x > 1000 {
        return a+1
    }
    a = 1
    return a+1
}