foo() {
	a:int[] = {1, 2, 3}
	x:int = a[0]
    c:int[a[0]]
	x = bar()
}

bar () : int {
	return 12
}

barfoo() {
    a:int[] = {1,2}
    b:int[] = {1,2,3}
    if (a == b){

    }
}