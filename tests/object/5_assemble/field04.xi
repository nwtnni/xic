use io
use conv

main(args:int[][]) {
    x:B = new B
    x.bar()
    x.foo()
}

class A {
    x:int

    foo() {
        printInt(x)
    }
}

class B extends A {
    bar() {
        x = x+1
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