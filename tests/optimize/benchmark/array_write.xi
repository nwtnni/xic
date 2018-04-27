use io
use conv

main (args:int[][]) {
    a:int[100]
    num:int = 0
    i:int = 0
    while num < 200000000 {
        while i < 100 {
            a[i] = num
            i = i+1
        }
        num = num+1
    }
}