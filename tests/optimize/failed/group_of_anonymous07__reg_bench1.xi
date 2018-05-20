use io
use conv

main(args : int[][]) {
    a:int = 5;b:int;c:int;d:int;e:int;f:int;g:int;
    while(a < 50000000) {
        b=a;c=b;d=c;e=d;f=e;g=f;a=g+1
    }
    println(unparseInt(a) + unparseInt(b) + unparseInt(c) + unparseInt(d) + unparseInt(e) + unparseInt(f) + unparseInt(g))
} 
