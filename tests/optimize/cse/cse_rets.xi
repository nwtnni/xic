use io
use conv

main (args:int[][]) {
    a:int = 1

}

foo(x:int) : int, int {
    a:int = 1
    b:int = 2
    c:int = 3
    d:int = 4
    
    a = b + 1
    c = b + 1

    d = c * (b + 1)

    return b + 1, c * (b+1)
}

