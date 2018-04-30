use io
use conv

main (args:int[][]) {
    a:int[101]
    i:int = 0
    while i<1000000 {
        j:int = 0
        while j<33 {
            a[j*3] = 0
            a[j*3+1] = 1
            a[j*3+2] = 2
            j = j+1
        }
        i = i+1
    }
    print(unparseInt(a[0]))
}