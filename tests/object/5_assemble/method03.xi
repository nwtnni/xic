use io
use conv

main(args:int[][]) {
    printInt(new A.foo(1))
}

class A {
    foo(i:int):int {
        return i
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