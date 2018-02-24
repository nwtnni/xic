foo () {
    _ = 1
    _,x:int = foo2()
    y:int,_ = foo2()
    a:int,b:int = foo2()
}

foo2():int, int {
    return 2,3
}