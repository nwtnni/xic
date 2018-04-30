use io
use conv

main (args:int[][]) {
    i:int = 0
    while i<50000000 {
        j:int = i*6
        k:int = j/3
        i = k - i + 1
    }
    print(unparseInt(i))
}