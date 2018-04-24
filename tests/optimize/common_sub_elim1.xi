use io
use conv

main (args:int[][]) {
    i:int = 0
    while i<100000000 {
        x:int = foo()
        y:int = x+1
        if x+1 == 0 {
            y = (x+1)*2
        }
        else {
            y = (x+1)*3
        }
        i = i+1
    }
}

foo():int {
    return 1
}