// simple functions

afunc() : int
    return 1

afunc'() : int
    return 1;

bfunc() : bool {
    return true
}

b_func() : bool {
    return true;
}

cfunc(x: int) : bool
    return false

cfunc''(x: int) : bool
    return false;

dfunc(y: bool) : int {
    return -1
}

d__func(y: bool) : int {
    return -1;
}

efunc(x: int, y: int) : int, int
    return 4, 2

e_func'(x: int, y: int) : int, int
    return 4, 2;

ffunc(x: bool, y: int) : bool, bool {
    return true, false
}

f'func(x: bool, y: int) : bool, bool {
    return true, false;
}

gfunc(arr1: int[]) : int[]
    return {1, 2}

g_'func(arr1: int[]) : int[]
    return {1, 2};

hfunc(arr2: bool[][]) : bool[][] {
    return {{true, false}, {true, false}}
}

hfunc(arr2: bool[][]) : bool[][] {
    return {{true, false}, {true, false}};
}