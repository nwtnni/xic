use io
use conv

main (args:int[][]) {
    i:int = 0
    x:int = 0
    while i<300000000 {
        x = foo()
        i = i+1
    }
    print(unparseInt(x))
}

foo():int {
    return 5
}