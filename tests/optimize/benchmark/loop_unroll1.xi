use io
use conv

main (args:int[][]) {
    i:int = 0
    j:int = 0
    while i<100000000 {
        j = 0
        while j<6 {
            j = j+1
        }
        i = i+1
    }
    print(unparseInt(j))
}