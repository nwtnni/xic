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