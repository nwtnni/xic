use io
use conv

main (args:int[][]) {
    a:int = 1 
    b:int = 2 
    c:int = 4

    // CSE1 here
    d:int = a + b
    printint(d) // 3

    e:int = a + b + c
    printint(e) // 7
    
    f:int = c + (a + b)
    printint(f) // 7

    // Should see CSE1 throughout both if and else
    if (a < b) {
        g:int = a + b
        printint(g) // 3

        // CSE2 here
        d = a + c
        printint(d) // 5

        e = a + c
        printint(e) // 5
    } else {
        d = a + b
        printint(d) // 3

        // Should not see CSE2
        e = a + c
        printint(e) // 5
    }

    // Should kill CSE
    a = 10  

    // Should not see CSE now

    e = a + b
    printint(e) // 12

    f = a + c
    printint(f) // 14
}

printint (i: int) {
    println(unparseInt(i))
}
