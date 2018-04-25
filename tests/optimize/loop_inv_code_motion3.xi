use io
use conv

main (args:int[][]) {
    i:int = 0
    x:int
    while i<70000000 {
        x = foo()
        i = i+1
    }
    print(unparseInt(x))
}

foo():int {
    j:int = 0
    while j<6 {
        x:int = 1
        y:int = 1
        z:int = 1
        j = j+1
    }
    return j
}