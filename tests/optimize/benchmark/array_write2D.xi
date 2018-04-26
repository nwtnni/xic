use io
use conv

main (args:int[][]) {
    a:int[100][100]
    num:int = 0
    i:int = 0
    j:int = 0
    while num < 300000000 {
        while i < 100 {
            while j < 100 {
                a[i][j] = num
                j = j+1
            }
            i = i+1
        }
        num = num+1
    }
}