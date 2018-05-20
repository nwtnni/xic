class A {
  foo() {}

  bar(): A {
    foo: A = new A
    return foo
  }
}
