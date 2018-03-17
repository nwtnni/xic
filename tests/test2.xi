use io
use conv

main(args: int[][]) {
    // fix bugs with empty array

    arr:int[][] = {{0, 1}, {2, 3}}

    println(unparseInt(arr[0][0]))
    // arr[0][0] = 2
    // println(unparseInt(arr[0][0]))
    // arr[0][1][0] = 'W'
    // println(arr[0][1])
    // arr[1] = {"oops"}
    // println(arr[1][0])

    // println("ab" + "cd")

    // println(unparseInt(length("a" + "b")))
    // println(unparseInt(length("a")))
    // println(unparseInt(length("b")))

    // arr:int[][] = {"hello", "world"} + {"foo"}

    // i:int = 0
    // while (i < length(arr)) {
    //     println(arr[i])
    //     i = i + 1
    // }

    // arr:int[] = "12345"
    // arr:int[][][] = {{"hello", "world"}, {"foo", "bar", "baz"}}

    // arr:int[][] = {"abc"}

    // i:int = arr[0][1]

    // x:int = 0
    // i:int = length(arr)
    // println(unparseInt(i))
    // while (x < i) {
    //     y:int = 0
    //     j: int = length(arr[x])
    //     while (y < j) {
    //         println(arr[x][y])
    //         y = y + 1
    //     }
    //     x = x + 1
    // }
    
    // arr:int[][] = {}
    // println(arr[1]) // should be caught as out of bounds
}
