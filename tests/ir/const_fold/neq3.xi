use io
use conv

main(args:int[][]) {
    printBool({} != {})
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