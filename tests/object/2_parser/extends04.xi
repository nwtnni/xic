class A {
    x:int
    
    foo():A {
        return this
    }
}

class B extends A {
    foo():A {
        return this
    }
}