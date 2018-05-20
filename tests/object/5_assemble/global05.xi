use io
use conv

x:bool

main(args:int[][]) {
    foo()
    printBool(x)
}

foo() {
    x = !x
}

printBool(b:bool) {
    if b {
        print("true")
    }
    else {
        print("false")
    }
}