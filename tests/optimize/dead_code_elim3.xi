use io
use conv

main (args:int[][]) {
    i:int = 0
    while i<300000000 {
        x:int = 0
        if true {
            y:int = 1
        }
        else {
            print(unparseInt(x))
        }
        i = i+1
    }
}