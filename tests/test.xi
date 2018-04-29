use io
use conv

main(args:int[][]) {
    x:int = foo()
    y:int = 0
    x = x * y

    z:int[5]
    println(unparseInt(length(z)))
}

foo() : int {
    return 1
}