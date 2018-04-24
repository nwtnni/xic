use io
use conv

main (args:int[][]) {
    len:int = 200
    a:int[len][len][len]
    i:int = 0
    j:int = 0
    k:int = 0
    while i < len {
        while j < len {
            while k < len {
                a[i][j][k] = 100*i + 10*j + k
                k = k+1
            }
            j = j+1
        } 
        i = i+1
    }
}