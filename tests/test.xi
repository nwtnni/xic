use io
use conv

n:int = 5
a:int[2]

main (args:int[][]) {
    // a[0] = 'h'
    // a[1] = 'i'
    // println(unparseInt(n))
    // println(a)

    a1:A = new A
    b1:B = new B

    _ = a1.foo()
    c1:A = b1.foo()

    println(unparseInt(a1.x))
    println(unparseInt(b1.x))
    println(unparseInt(c1.x))

}

class A {
    x:int
    foo():A {
        x = 1
        println(unparseInt(x))
        return this
    }
}

class B extends A {
    foo():A {
        x = 2
        println(unparseInt(x))
        return this
    }
}