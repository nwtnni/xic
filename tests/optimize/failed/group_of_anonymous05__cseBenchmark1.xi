use conv use io
main (args:int[][]) {
    a:int = 7; b:int = 8;
    d:int = test(a,b);
}

test(a:int, b:int) : int {
    x:int = a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*
    (b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b);
    i:int = 0;

    while (i < 5000000) {
        i = i + 1;
        y:int = b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b;
        z:int = a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a;
        w:int = a*a*a*a*a*a*a*a*a*a*b*b*a*a*b*b*a*a*b*b*a*a*b*b*a*a*b*b*a*a*b*b*
            a*a*b*b*a*a*b*b*a*a*b*b*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*
            a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a;
        x = w;
    } 
    return x;
}