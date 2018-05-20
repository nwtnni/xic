use io
use conv

main(args:int[][]) {
    x:A[] = {new A, new B}
    x[0].foo()
    x[1].foo()
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
    println(unparseInt(i))
}

printBool(b:bool) {
    if b {
        println("true")
    }
    else {
        println("false")
    }
}