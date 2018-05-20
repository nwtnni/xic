use io use conv
main(args:int[][]) {
    arr:int[] = {1, 2, 3, 4} z:int = 0
    while (z < 2000000) {
        a:int b:int c:int d:int e:int f:int g:int h:int
        i:int j:int k:int l:int m:int n:int o:int p:int
        // first row is live here
        a = arr[0] b = arr[1] c = arr[2] d = arr[3]
        e = arr[0] f = arr[1] g = arr[2] h = arr[3]
        arr[0] = a + b arr[1] = c + d arr[2] = e + f arr[3] = g + h // second row is live here
        i = arr[0] j = arr[1] k = arr[2] l = arr[3]
        m = arr[0] n = arr[1] o = arr[2] p = arr[3]
        arr[0] = i + j arr[1] = k + l arr[2] = m + n arr[3] = o + p z=z+ 1
    }
    println(unparseInt(arr[0])) println(unparseInt(arr[1]))
    println(unparseInt(arr[2])) println(unparseInt(arr[3]))
}