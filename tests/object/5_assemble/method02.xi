use io
use conv

main(args:int[][]) {
    new A.foo(1)
}

class A {
    foo(i:int) {
        printInt(i)
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