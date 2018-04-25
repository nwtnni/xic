use io
use conv

main (args:int[][]) {
    i:int = 0
    while i<100000000 {
        i = i*2-i+1
    }
    print(unparseInt(i))
}