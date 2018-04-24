use io
use conv

main(args:int[][]) {
    a:int, b:int, c:int, d:int, e:int = bar(1,2,3,4,5,6,7)
    println(unparseInt(a))
    println(unparseInt(b))
    println(unparseInt(c))
    println(unparseInt(d))
    println(unparseInt(e))
}

bar(a:int,b:int,c:int,d:int,e:int,f:int,g:int):int,int,int,int,int {
    return a,b,c,d,e
}