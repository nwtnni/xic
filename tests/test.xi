use io
use conv

main (args:int[][]) {
<<<<<<< HEAD
    i:int;

    i = i + 1
=======
    n:int = 3
    m:int = 5
    arr:int[n][m]

    i:int = 0
    while (i < length(arr)) {
        j:int = 0
        while (j < length(arr[i])) {
            arr[i][j] = (i + 1) * (j + 1)
            print(unparseInt(arr[i][j]))
            if (j != m - 1) print(", ")
            j = j + 1
        }
        println("")
        i = i + 1
    }
>>>>>>> 9d3488ae1749399c560890439721c412e66ba14f
}