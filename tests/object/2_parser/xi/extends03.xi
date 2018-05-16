class A {
    x:int    
}

class B extends A {
    foo():A {
        return this
    }
}