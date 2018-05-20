use io

class A {
  foo() { print("A") }
}

class B extends A {
  foo() { print("B") }
}

main(args:int[][]) {
  a: A = new A
  b: A = new B
  a.foo()
  b.foo()
}
