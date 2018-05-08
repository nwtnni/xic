// use io
// use conv

// main(args:int[][]) {
//     a:int[5]
//     b:int[] = "Hello"

//     x:int = 0
//     while (x < length(a)) {
//         a[x] = b[x]
//         printInt(a[x])
//         x = x + 1
//     }
//     println(a)
//     println(b)
// }

// printInt(x:int) {
//     println(unparseInt(x))
// }

// group_of_anonymous01__reg_loop
// use io use conv
// main(args:int[][]) {
//     arr:int[] = {1, 2, 3, 4} z:int = 0
//     while (z < 2000000) {
//         a:int b:int c:int d:int e:int f:int g:int h:int
//         i:int j:int k:int l:int m:int n:int o:int p:int
//         // first row is live here
//         a = arr[0] b = arr[1] c = arr[2] d = arr[3]
//         e = arr[0] f = arr[1] g = arr[2] h = arr[3]
//         arr[0] = a + b arr[1] = c + d arr[2] = e + f arr[3] = g + h // second row is live here
//         i = arr[0] j = arr[1] k = arr[2] l = arr[3]
//         m = arr[0] n = arr[1] o = arr[2] p = arr[3]
//         arr[0] = i + j arr[1] = k + l arr[2] = m + n arr[3] = o + p z=z+ 1
//     }
//     println(unparseInt(arr[0])) println(unparseInt(arr[1]))
//     println(unparseInt(arr[2])) println(unparseInt(arr[3]))
// }

// group_of_anonymous03__cse3
use io use conv
main(args:int[][]) {
    x:int[] = {1, 2, 3, 4, 5, 6}
    i:int = 0
    y:int = x[0] + x[1] + x[2] + x[3] + x[4] + x[5]
    while (i < 3000000) {
        z:int[3]
        z[0] = x[0] + x[1] + x[2] + x[3] + x[4] + x[5] 
        z[1] = x[0] + x[1] + x[2] + x[3] + x[4] + x[5] 
        z[2] = x[0] + x[1] + x[2] + x[3] + x[4] + x[5] 
        i = i + 1
    }
    println("finished")
}

// group_of_anonymous05__cseBenchmark1
// use conv use io
// main (args:int[][]) {
//     a:int = 7; b:int = 8;
//     d:int = test(a,b);
// }

// test(a:int, b:int) : int {
//     x:int = a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*
//     (b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b);
//     i:int = 0;

//     while (i < 5000000) {
//         i = i + 1;
//         y:int = b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b*b;
//         z:int = a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a;
//         w:int = a*a*a*a*a*a*a*a*a*a*b*b*a*a*b*b*a*a*b*b*a*a*b*b*a*a*b*b*a*a*b*b*
//             a*a*b*b*a*a*b*b*a*a*b*b*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*
//             a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*a;
//         x = w;
//     } 
//     return x;
// }

// group_of_anonymous15__cf1
// main(args: int[][]) {
//     i:int = 0
//     s:int = 0
//     while (i < 10000000) {
//         s = s + (1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1
//         +1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1
//         +1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1
//         +1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1
//         +1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1
//         +1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1)
//         i = i + 1
//     }
// }