use io
use conv

main(args:int[][]) {
    x:A = new A
    printInt(x.x)
    printInt(x.foo().x)
    printInt(x.x)

    x = new B
    printInt(x.x)
    printInt(x.foo().x)
    printInt(x.x)
}

class A {
    x:int
    foo():A {
        this.x = 1
        return this
    }
}

class B extends A {
    foo():A {
        this.x = 2
        return this
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