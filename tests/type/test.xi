foo() {
	x:int, y:int[1] = bar()
}

bar() : int, int[] {
	return 1, {2, 3}
}