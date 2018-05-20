# Edge Cases

**Problem**

Multiple classes within a single module can extend each othe, which means that we can't extend classes & check in one pass:

  ```
  class A extends B {
    foo() : int { return 1 }   
  }  

  class B {
    foo() : int { return 2 }
  }
  ```

Here we don't know enough about A at the first pass to extend immediately.

**Solution**

We can try:

- Two passes for classes; one to check top-level first (but then how do we deal with interfaces?)
  * Top-level means what--class name + methods + fields? Check extends at the end?

-------------------------------------------------------------------------------------------------------------------------

What does GlobalContext/Importer need to accomplish?

1. Prevent shadowing of top-level things (e.g. class names, global fields, function names)
2. Check extension semantics (e.g. subclasses with same method name as superclass needs to have same type)
3. Recursively visit use statements

For (2), we can only check extension after importing all top-levels

What about mutual dependencies between classes and functions?

Impossible to have classes depend on functions -> should type-check classes first, so they're in scope for
function arguments?

Or dirty hack: disable XiDeclr checking against the top-level
