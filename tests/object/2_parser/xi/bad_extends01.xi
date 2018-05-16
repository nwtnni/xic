class A {
    x:int
    foo():int{
        return 5
    }
}

class B extends A extends A {
    foo():int {
        return this.x
    }
}