// ~~~~~~~~~~~~~~~~~~~~~~
// xic timeout test cases

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
// use io use conv
// main(args:int[][]) {
//     x:int[] = {1, 2, 3, 4, 5, 6}
//     i:int = 0
//     y:int = x[0] + x[1] + x[2] + x[3] + x[4] + x[5]
//     while (i < 3000000) {
//         z:int[3]
//         z[0] = x[0] + x[1] + x[2] + x[3] + x[4] + x[5] 
//         z[1] = x[0] + x[1] + x[2] + x[3] + x[4] + x[5] 
//         z[2] = x[0] + x[1] + x[2] + x[3] + x[4] + x[5] 
//         i = i + 1
//     }
//     println("finished")
// }

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


// ~~~~~~~~~~~~~~~~~~~~~~~~~~~
// Tiler nullpointer test cases

// group_of_anonymous10__cse2
// use io
// use conv
// main(args:int[][]) {
//     c:int = 50
//     d:int = 100
//     x:int = c + d + c + d + c + d * c * d * c
//     j:int = 0
//     while (j < 1000000000000000) {
//         j = j + d * c * d * c
//     }
//     println(unparseInt(j)) 
// }

// group_of_anonymous10__cf2
// use io
// use conv
// main(args:int[][]) {
//     q:int = 0
//     while (q < 100000000000) {
//         x:int = 0 y:int = 0 z:int = 0 d:int = 0 g:int = 1 + z f:int = d + g + z
//         e:int = 10 h:int = 50 b:int = 900 m:int = 70 n:int = 50 l:int = 23
//         b = 0 + 0 + 0 + 0 + 1 + 0 + 10 + 50 + 900 + 70 + 50 + 23
//         q=q+ b
//     }
//     println(unparseInt(q))
//  }

// group_of_anonymous10__cp2
// use io
// use conv
// main(args:int[][]) {
//     q:int = 0
//     while (q < 10000000000) {
//         x:int = 0 y:int = 0 z:int = 0 d:int = 0 g:int = 1 + z f:int = d + g + z
//         e:int = 10 h:int = 50 b:int = 900 m:int = 70 n:int = 50 l:int = 23
//         b = x + y + z + d + g + f + e + h+b+m+n+l
//         q=q+ b
//     }
//     println(unparseInt(q))
// }

// group_of_anonymous10__cp3
// use io
// use conv
// main(args:int[][]) { 
//     c:int = 50
//     while (c < 400000000000000) {
//         d:int = 50
//         c = c + d * d * d * d + (4000000000000000000 *>> 400)
//     }
//     println(unparseInt(c)) 
// }


// ~~~~~~~~~~~~~~~
// Stupid semicolon
// use io use conv
// main(args:int[][]){
//     i : int = 0; j:int = 0; k:int = 1024;l:int = 0;
//     while (i < 1000000) {
//         x : int = pow(19,19);y : int = pow(19,19);z : int = pow(19,19);
//         x1 : int = pow(19,19);x2 : int = pow(19,19);x3 : int = pow(19,19);
//         x0 : int = pow(19,19);x9 : int = pow(19,19);x4 : int = pow(19,19);
//         x00 : int = pow(19,19);x8 : int = pow(19,19);x5 : int = pow(19,19);
//         x01 : int = pow(19,19);x7 : int = pow(19,19);j = pow(2,10);
//         sum:int = x+y+z+x1+x2+x3+x4+x0+x9+x4+x00+x8+x5+x01+x7;
//         i = i + 1;
//     } // if pow works, won't fail:
//     if (k/j == 1) {} else {j = 1/(id(l))};
// }

// id(a:int) : int { return a }

// pow(number:int, exponent:int):int{
//     if (exponent==0) { return 1 }
//     else { return number * pow(number,exponent-1) }
// }

// ~~~~~~~~~
// Real bugs

// use io
// use conv

// main(args : int[][]) {
//     a:int = 5;b:int;c:int;d:int;e:int;f:int;g:int;
//     while(a < 50000000) {
//         b=a;c=b;d=c;e=d;f=e;g=f;a=g+1
//     }
//     println(unparseInt(a) + unparseInt(b) + unparseInt(c) + unparseInt(d) + unparseInt(e) + unparseInt(f) + unparseInt(g))
// } 

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

// use io
// use conv

// main(args: int[][]) {
//     i:int=0;
//     r:int=0;

//     while(i!=1000000){
//         m0:int=1234567890123456785;
//         m1:int=m0%(m0/1);
//         m2:int=m1%(m0/2);
//         m3:int=m2%(m0/3);
//         m4:int=m3%(m0/4);
//         m5:int=m4%(m0/5);
//         m6:int=m5%(m0/6);
//         m7:int=m6%(m0/7);
//         m8:int=m7%(m0/8);
//         m9:int=m8%(m0/9);
//         m10:int=m9%(m0/10);
//         m11:int=m10%(m0/11);
//         m12:int=m11%(m0/12);
//         m13:int=m12%(m0/13);
//         m14:int=m13%(m0/14);
//         m15:int=m14%(m0/15);
//         m16:int=m15%(m0/16);
//         m17:int=m16%(m0/17);
//         m18:int=m17%(m0/18);
//         m19:int=m18%(m0/19);
//         m20:int=m19%(m0/20);
//         if(m1<=m0 & 
//             m2<=m1 & 
//             m3<=m2 & 
//             m4<=m3 & 
//             m5<=m4 & 
//             m6<=m5 & 
//             m7<=m6 & 
//             m8<=m7 & 
//             m9<=m8 & 
//             m10<=m9 & 
//             m11<=m10 & 
//             m12<=m11 & 
//             m13<=m12 & 
//             m14<=m13 & 
//             m15<=m14 & 
//             m16<=m15 & 
//             m17<=m16 & 
//             m18<=m17 & 
//             m19<=m18 & 
//             m20<=m19)
//         {
//             r=r+1;
//         }
//         i=i+1
//     }
//     print(unparseInt(r));
// }