use io
use conv

main(args:int[][]) {
    x:A = new B
    x.foo()
}

class A {
    foo() {
        printInt(1)
    }
}

class B extends A {
    foo() {
        printInt(2)
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