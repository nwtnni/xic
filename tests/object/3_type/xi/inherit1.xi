class B extends A {}

class A {
  foo(): int
}

bar(b: B): int {
  return b.foo()
}
