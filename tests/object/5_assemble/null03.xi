use io
use conv

main(args:int[][]) {
    x:B = new A
    if x != null {
        print("not null")
    }
}

class A {}

class B extends A {}

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