use io
use conv

main(args:int[][]) {
    println("main")
    println(unparseInt(1))
    foo()
}

bar() {
    a:int[3]

    a[0] = 'a'
    a[1] = 'b'
    a[2] = 'c'
}

foo() {
    println("foo");
}