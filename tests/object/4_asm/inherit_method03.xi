use io

class A {
  foo() { print("A") }
}

class B extends A {}

class C extends B {
  foo() { print("C") }
}

class D extends C {}

class E extends D {
  foo() { print("E") }
}

main(args:int[][]) {
  a: A = new A
  b: A = new B
  c: A = new C
  d: A = new D
  e: A = new E
  a.foo()
  b.foo()
  c.foo()
  d.foo()
  e.foo()
}
