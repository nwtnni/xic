use io
use conv

class A {
  i: int
}

class B {
  a: A
}

main(args:int[][]) {
  b: B = new B
  b.a = new A
  print(unparseInt(b.a.i))
}
