use io
use conv

main (args:int[][]) {
    a:int[] = {2, 3}
    arr:int[f(0)][f(1)]
    println(unparseInt(length(arr)))
    println(unparseInt(length(arr[0])))
}

f (i:int) : int {
    print("Index: ")
    println(unparseInt(i))
    return i
}