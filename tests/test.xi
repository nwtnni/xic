x:int[5]

main(args: int[][]) {
    i:int[] = "hello"
    x = {}
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