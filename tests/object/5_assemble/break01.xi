use io
use conv

main(args:int[][]) {
    x:int = 0
    while(true) {
        printInt(x)
        if x >= 3 {
            break
        }
        x = x+1
    }
}

printInt(i:int) {
    println(unparseInt(i))
}