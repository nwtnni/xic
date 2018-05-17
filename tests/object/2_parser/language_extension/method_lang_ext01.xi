class A {
    foo () {
    x:int, y:int = 1,2
    x:bool, y:bool = true, false
    x:int, y:int, z:int = 1*5%3, 2/2-5, A.x
    x:bool, y:bool, z:bool = {true}[0], {new A}.x.foo(2), true
    x:bool[][], y:A, z:B = {{}}, new A, new A.x.createB()
}
}