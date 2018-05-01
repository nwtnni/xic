use io
use conv

main (args:int[][]) {
    i:int = 0
    a:int = 1
    b:int = 1
    c:int = a+b
    while i<300000000 {
        d:int
        if i<100 {
            a = 1
            d = a+b
        }
        else {
            d = a+b
        }
        i = i+1
    }
    print(unparseInt(c))
}