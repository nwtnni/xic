use io
use conv

main(args:int[][]) {
    x:int = 1
    y:int = x-1
    printInt(x/y)
}

printInt(x:int) {
    print(unparseInt(x))
}

printBool(x:bool) {
    if x 
        print("True")
    else
        print("False")
}