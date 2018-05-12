class A {
    x:int

    foo():A {
        x = 5
        return this
    }

    y:int

    bar():A,A {
        x = 4
        y = 5
        return this, this
    }
}