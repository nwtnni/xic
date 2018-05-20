use io
use conv

len:int = 5
x:int[len]

main(args:int[][]) {
    x = "Hello"
    print(x)
}

printInt(i:int) {
    println(unparseInt(i))
}

printBool(b:bool) {
    if b {
        print("true")
    }
    else {
        print("false")
    }
}