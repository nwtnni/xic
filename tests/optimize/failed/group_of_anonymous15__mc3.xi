use io
use conv

num():int { return 10 }

f1(): int {
    fx1 : int = num();
    fx2 : int = fx1;
    fx3 : int = fx2;
    fx4 : int = fx3;
    fx5 : int = fx4;
    fx6 : int = fx5;
    return fx6;
}

f2(): int, int, int {
    i : int = 1000;
    acc1 : int;
    acc2 : int;
    acc3 : int;
    while (i > 0 ){
        fx1 : int = f1();
        fx2 : int = fx1;
        fx3 : int = fx2;
        fx4 : int = fx3;
        fx5 : int = fx4;
        fx6 : int = fx5;
        acc1 = fx1;
        acc2 = fx3;
        acc3 = fx5;
        i = i - 1;
    }
    return acc1, acc2, acc3
}

main(args: int[][]) {
    i : int = 100000;
    acc:int = 0;
    while (i > 0 ){
        i = i - 1;
        x : int = f1();
        x2 : int, y2 : int, z2 : int = f2()
        acc = x + x2 + y2 + z2
    }
    println(unparseInt(acc))
}

