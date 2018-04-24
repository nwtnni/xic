use io
use conv

main (args:int[][]) {
    i:int = 0
    while i<200000000 {
        x:int = 0
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