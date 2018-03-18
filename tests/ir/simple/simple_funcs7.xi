use io
use conv

main(args:int[][]) {
    _, x:int = foo()
    printInt(x)
}

foo():int,int {
    return bar()
}

bar():int,int {
    return 1,1
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