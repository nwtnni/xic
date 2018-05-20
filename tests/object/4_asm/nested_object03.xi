use io
use conv

class A {
  b: B
}

class B {
  a: int
}

class C extends A {
  a: A
}

main(args:int[][]) {
  
  a: A = new A
  b: A = new C
  c: C = new C
  
  a.b.a = 1
  b.b.a = 2 
  c.a.b.a = 3
  c.b.a = 4

  print(unparseInt(a.b.a + b.b.a + c.a.b.a + c.b.a))
}
