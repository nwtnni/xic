use io
use conv

main (args:int[][]) {
    i:int = 0
    x:int = 0
    while i<30000000 {
        x = foo(x,x,x,x,x,x,x,x,x,x,x)
        i = i+1
    }
    print(unparseInt(x))
}

foo(a:int,b:int,c:int,d:int,e:int,f:int,g:int,h:int,i:int,j:int,k:int):int {
    return a+b+c+d+e+f+g+h+i+j+k
}