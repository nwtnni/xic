use io
use conv

main (args:int[][]) {
    i:int = 0
    x:int = 0
    while i<300000000 {
        x = 0
        x = 1
        x = 2
        x = 3
        i = i+1
    }
    print(unparseInt(x))
}