use io
use conv

main(args:int[][]) {
    new A.foo()
}

class A {
    foo() {
        printInt(1)
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