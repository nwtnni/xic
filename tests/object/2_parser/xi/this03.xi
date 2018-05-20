class A {
    x:int

    foo():int,int {
        return this.bar()
    }

    bar():int {
        return x
    }
}