use io
use conv

main(args:int[][]) {
    new A.foo()
}

class A {
    x:int

    foo() {
        printInt(x)
    }
}

printInt(i:int) {
    print(unparseInt(i))
}

printBool(b:bool) {
    if b {
        print("true")
    }
    else {
        print("false")
    }
}