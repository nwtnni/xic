use io
use conv

main (args:int[][]) {
    i:int = 0
    x:int = 0
    while i<200000000 {
        x = foo(x)
        i = i+1
    }
}

foo(x:int):int {
    return x
}