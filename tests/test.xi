use io
use conv

main(args:int[][]) {
    // x:int = 10

    // a:int[x]

    // x = x - 1
    // while (x >= 0) {
    //     a[x] = x
    //     print(unparseInt(a[x]))
    //     x = x - 1
    // }

    x:int = 1
    y:int = 2
    z:int = 3
    x = x + y + 1 * z / (2 + x + 3)

    // printInt(x)

    // y:int[] = {1,2,3,4,5}

    // x:int[] = "H" + "ii"
    // println(x)

    // print(unparseInt(x) + " + " + unparseInt(y) + " = " + unparseInt(x+y))
}


printInt(x:int) {
    println(unparseInt(x))
}
