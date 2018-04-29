use io
use conv

main(args:int[][]) {
    a:int[5]
    printInt(length(a))
}

// foo():int {
//     return bar(5)[1]
// }

// bar(x:int):int[] {
//     y:int[] = {x,x}
//     return y
// }

printInt(x:int) {
    print(unparseInt(x))
}
