use io
use conv

main (args:int[][]) {
    i:int = 0
    x:int = 0
    while i<300000000 {
        x = 1
        if false {
            print(unparseInt(x))
        }
        i = i+1
    }
    print(unparseInt(x))
}