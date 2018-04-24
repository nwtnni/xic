use io
use conv

main (args:int[][]) {
    print(unparseInt(fib(38)))
}

fib(i:int):int {
    if i < 0 {
        return 0
    }
    if i == 0 {
        return 0
    }
    if i == 1 {
        return 1
    }

    return fib(i-2) + fib(i-1)
}