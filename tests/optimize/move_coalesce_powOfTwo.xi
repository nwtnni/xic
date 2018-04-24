use io
use conv

main (args:int[][]) {
    print(unparseInt(powOfTwo(26)))
}

powOfTwo(i:int):int {
    if i <= 0 {
        return 1
    }
    return powOfTwo(i-1) + powOfTwo(i-1)
}