use io
use conv

main(args:int[][]) {
    x:A = new A
    x.foo()
    x.x = 1
    x.y = 2
    x.z = 3
    x.foo()
}

class A {
    x,y,z:int

    foo() {
        println(unparseInt(x))
        println(unparseInt(y))
        println(unparseInt(z))
    }
}