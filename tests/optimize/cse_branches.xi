use io
use conv

main (args:int[][]) {
    a:int = 1 
    b:int = 2 

    // Should see CSE1
    c:int = a + b
    d:int = a + b
    e:int = a + b + c
    f:int = c + (a + b)
    
    // Won't work because of associativity
    f = c + a + b

    if (a < b) {
        c = a + b

        // Should see CSE2
        d = a + c
        e = a + c
    } else {
        d = a + b

        // Should not see CSE2
        f = a + c
    }

    // Should see CSE1
    e = a + b

    // Should see CSE2
    f = a + c

}
