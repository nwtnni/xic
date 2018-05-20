use io
use conv

main(args:int[][]) {
    x:int[]
    if x == null {
        print("null")
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