foo():int {
    return new A.foo()
}

class A {
    x:int

    foo():int {
        return x
    }
}