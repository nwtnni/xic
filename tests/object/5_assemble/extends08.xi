use io
use conv

main(args:int[][]) {
    x:A = new D
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

class C extends A {
    foo() {
        printInt(3)
    }
}

class D extends A {
    foo() {
        printInt(4)
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