use io use conv
main(args:int[][]) {
    i:int = 0
    while (i < 10000) {
        j:int = 0
        b:bool = (j + i + j + i + j + i) != (i + j + i + j + j + i) | i < 10 
                | i > 15 | i < 20 | i == j;
        while (j + i < 20000) {
            if (b & j + i - (j + i) - (i + j) + 3000 >= 0 & b | i == j | i == j) {
                println(unparseInt(j-i+i+i-j+i-j-i))
                b = !(b & j + i - (j + i) - (i + j) + 3000 >= 0 & b | i == j | i == j)
            } 
            j =j+ 1
        }
        i = i+ 1
    }
}
