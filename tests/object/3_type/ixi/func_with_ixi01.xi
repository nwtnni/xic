use A_01

foo() {
    x:A = new A
    x.foo()
    x.foo(x)
    x = x.foo2(x)
}