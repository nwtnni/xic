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
    e:int = 5

    b = a + 1
    a = a + 1
    a = 5

    c = a + 1
    d = 3 / (a+1)

    if (a + 1 > 10) {
        a = c + d
    }
    else {
        b = c + d
    }

    e = a + 1



    return a, a    

}

