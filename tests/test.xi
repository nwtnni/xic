use io
use conv

n:int = 5
a:int[2]

main (args:int[][]) {
    a[0] = 'h'
    a[1] = 'i'
    println(unparseInt(n))
    println(a)

    x:A = new A
    y:B = new B
}

class A {
    x:int
    foo():A {
        return null
    }
}

class B extends A {
    foo():A {
        return this
    }
}