use io
use conv

main(args:int[][]) {
    shift(1,2,3,{4,5},{6,7});
}

shift(a:int, b:int, c:int, x:int[],y:int[]) {
    upd(x[0]);
}

upd(i:int) {
    printInt(i)
}

printInt(x:int) {
    print(unparseInt(x))
}