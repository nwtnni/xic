use io
use conv

main(args:int[][]) {
    printInt((1-1)/0)
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