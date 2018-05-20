use io
use conv

class B extends A {}

class A {
    foo(): int {
        return 1
    }
}

bar(b: B): int {
  return b.foo()
}

main(args:int[][]) {
    println(unparseInt(new A.foo()))
    println(unparseInt(bar(new B)))
}
