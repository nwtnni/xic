use io
use conv

main (args:int[][]) {
    x:int = 1
    y:int
    if (x < 1) {
        y = 0
    }
    printInt(y)
    // a:int = 1 
    // b:int = 2 
    // c:int = 3
    // d:int = c
    // e:int = d
    // f:int = e
    // b = 2
    // printInt(a)
    // printInt(b)
    // printInt(c)
    // a = a + 1
}

// foo():int, int, int{
//     return 1,2,3
// }

printInt(i:int) {
    println(unparseInt(i))
}