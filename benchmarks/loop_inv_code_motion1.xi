use io
use conv

main (args:int[][]) {
    i:int = 0
    x:int = 0
    y:int = 0
    z:int = 0
    while i<400000000 {
        x = 1
        y = 1
        z = 1
        i = i+1
    }
    print(unparseInt(x))
}