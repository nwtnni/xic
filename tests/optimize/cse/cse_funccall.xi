use io
use conv

main (args:int[][]) {
    a:int = 1
    b:int = 3
    c:int
    f:int = 2

    g:bool = a < 100

    if ( a < 100 ) {
        c = a + 1
    }
    
    c = foo(a)

    if (a < 100 ) {
        c = 5
    }
}

foo(x:int) : int {
    return x;
}

