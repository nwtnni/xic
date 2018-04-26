use io
use conv

main (args:int[][]) {
    i:int = 0
    x:int = 0
    while i<50000000 {
        j:int = 0
        while j<6 {
            x = foo(j)
            j = j+1
        }
        i = i+1
    }
    print(unparseInt(x))
}

foo(j:int):int {
    return j
}