use io
use conv
main(args:int[][]) {
    q:int = 0
    while (q < 10000000000) {
        x:int = 0 y:int = 0 z:int = 0 d:int = 0 g:int = 1 + z f:int = d + g + z
        e:int = 10 h:int = 50 b:int = 900 m:int = 70 n:int = 50 l:int = 23
        b = x + y + z + d + g + f + e + h+b+m+n+l
        q=q+ b
    }
    println(unparseInt(q))
}