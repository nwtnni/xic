use io
use conv

main (args:int[][]) {
    n:int = 5
    m:int = 5
    // arr:int[n][m]
    arr:int[n]

    println(unparseInt(length(arr)))

    i:int = 0
    while (i < length(arr)) {
        arr[i] = i * 2
        println(unparseInt(arr[i]))
        // j:int = 0
        // while (j < length(arr[j])) {
        //     arr[i][j] = i * j
        //     println(unparseInt(i) + ", " + unparseInt(j) + " = " + unparseInt(arr[i][j]))
        //     j = j + 1
        // }
        i = i + 1
    }
}