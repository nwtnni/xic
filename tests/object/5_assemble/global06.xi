use io
use conv

x:int[6]

main(args:int[][]) {
    foo()
    print(x)
}

foo() {
    x = "Hello!"
}

printBool(b:bool) {
    if b {
        print("true")
    }
    else {
        print("false")
    }
}