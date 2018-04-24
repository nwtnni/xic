use io
use conv

main (args:int[][]) {
    i:int = 0
    a:int = 1
    b:int = a+1
    while i<300000000 {
        c:int
        if i<100 {
            a = 3
            c = a+1
        }
        else {
            c = a+b
        }
        i = i+1
    }
}