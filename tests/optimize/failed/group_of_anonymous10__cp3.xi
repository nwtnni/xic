use io
use conv
main(args:int[][]) { 
    c:int = 50
    while (c < 400000000000000) {
        d:int = 50
        c = c + d * d * d * d + (4000000000000000000 *>> 400)
    }
    println(unparseInt(c)) 
}