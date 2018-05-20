use io
use conv

main(args:int[][]) {
    new B.foo(1)
}

class A {
    foo(i:int) {
        printInt(i)
    }
}

class B extends A {
    foo(i:int) {
        printInt(i+1)
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