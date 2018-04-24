use io
use conv

main (args:int[][]) {
    a:int = 0
    b:int = 1
    c:int = 2
    d:int = 3
    e:int = 4
    f:int = 5
    i:int = 0

    while i < 100000000 {
        m:int,n:int,o:int,p:int,q:int,r:int = foo(a,b,c,d,e,f)
        i = i+1
    }
}

foo (a:int, b:int, c:int, d:int, e:int, f:int):int,int,int,int,int,int {
    return a,b,c,d,e,f
}