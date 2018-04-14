use io
use conv

main(args:int[][]) {
    // x:int = 10

    // a:int[x]

    // x = x - 1
    // while (x >= 0) {
    //     a[x] = x
    //     print(unparseInt(a[x]))
    //     x = x - 1
    // }

    x:int[] = "H" + "ii"
    println(x)

    // print(unparseInt(x) + " + " + unparseInt(y) + " = " + unparseInt(x+y))
}

// main (args:int[][]) {
//     // x:int = 1;
//     // y:int = 1 + x;

//     arr:int[] = {1, 2, 3}
//     i:int = 0;
//     while (i < 3) {
//         printInt(arr[i])
//         i = i + 1
//     }

//     printInt(foo(5))

// }

// foo (n:int) : int {
//     if n <= 0 {
//         return 1;
//     } else {
//         return foo (n - 1) * n;
//     }
// }

printInt(x:int) {
    println(unparseInt(x))
}

// printBool(x:bool) {
//     if x 
//         print("True")
//     else
//         print("False")
// }
    
//     a:int, b:int, c:int, d:int, e:int, f:int, g:int, h:int, i:int, j:int, k:int = fun(1,2,3,4,5,6,7,8,9,10,11)
//     println(unparseInt(a))
//     println(unparseInt(b))
//     println(unparseInt(c))
//     println(unparseInt(d))
//     println(unparseInt(e))
//     println(unparseInt(f))
//     println(unparseInt(g))
//     println(unparseInt(h))
//     println(unparseInt(i))
//     println(unparseInt(j))
//     println(unparseInt(k))
// }

// // fun (a:int, b:int) : int, int, int {
// //     return 1, 2, 3
// // }

// fun(a:int, b:int, c:int, d:int, e:int, f:int, g:int, h:int, i:int, j:int, k:int):int, int, int, int, int, int, int, int, int, int, int {
//     println(unparseInt(a))
//     println(unparseInt(b))
//     println(unparseInt(c))
//     println(unparseInt(d))
//     println(unparseInt(e))
//     println(unparseInt(f))
//     println(unparseInt(g))
//     println(unparseInt(h))
//     println(unparseInt(i))
//     println(unparseInt(j))
//     println(unparseInt(k))
//     println(unparseInt(a+b+c+d+e+f+g+h+i+j+k))
//     return 1,2,3,4,5,6,7,8,9,10,11
// }