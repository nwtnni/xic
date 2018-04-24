use io
use conv

main (args:int[][]) {
    x:bool
    i:int = 0
    while i<300000000 {
        x = true & true & true | false
        i = i+1
    }
    
}