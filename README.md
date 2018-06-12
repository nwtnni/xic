# xic

Compiles `Xi` and `oXi` source files down to `x86_64` assembly.

The compiler is divided into the following phases:

- Lexing
- Parsing
- Type checking
- Intermediate representation (IR) generation
- Abstract assembly generation
- Optimization
- Register allocation

### Usage

```
-------------------------------------------------------------------------------------
Usage: xic <OPTION>* <OPERATION>* <FILE>+                                            
-------------------------------------------------------------------------------------
Where <OPTION> is zero or more of:                                                   
  --help                  : Print synopsis of options                                
  --report-opts           : Print synopsis of optimizations available                
  -D          <DIRECTORY> : Output diagnostic files to <DIRECTORY>                   
  -d          <DIRECTORY> : Output assembly files to <DIRECTORY>                     
  -libpath    <DIRECTORY> : Search for interface files in <DIRECTORY>                
  -sourcepath <DIRECTORY> : Search for source files in <DIRECTORY>                   
  -target     <OS>        : Specify the OS for which to generate code                
  --optir     <PHASE>     : Generate .ir file for phase <PHASE>                      
  --optcfg    <PHASE>     : Generate .dot file for phase <PHASE>                     
  -O<OPT>                 : Enable optimization <OPT>                                
  -O-no-<OPT>             : Disable optimization <OPT>                               
  -O                      : Disable optimizations, redundant if -O<OPT> passed       
-------------------------------------------------------------------------------------
Where <OPERATION> is one or more of:                                                 
  --lex                   : For each f.(i)xi, generate lex diagnostic file f.lexed   
  --parse                 : For each f.(i)xi, generate parse diagnostic f.(i)parsed  
  --typecheck             : For each f.xi, generate type check diagnostic f.typed    
  --irgen                 : For each f.xi, generate intermediate representation f.ir 
  --irrun                 : For each f.xi, generate and run IR f.ir                  
-------------------------------------------------------------------------------------
Where <FILE> is one or more source files to operate on                               
-------------------------------------------------------------------------------------
Where <PHASE> is exactly one of:                                                     
  initial : Before any optimizations                                                 
  cf      : Constant folding                                                         
  cp      : Constant propagation                                                     
  cse     : Common subexpression elimination                                         
  reg     : Register allocation                                                      
  mc      : Move coalescing                                                          
  final   : After all optimizations                                                  
-------------------------------------------------------------------------------------
Where <OPT> is exactly one of:                                                       
  cf      : Constant folding                                                         
  cp      : Constant propagation                                                     
  cse     : Common subexpression elimination                                         
  reg     : Register allocation                                                      
  mc      : Move coalescing                                                          
-------------------------------------------------------------------------------------
```

### Building

We use the [Gradle][1] build system to automate things like lexer and parser generation, compilation, and
executable creation. Run `./xic-build` to generate the `xic` executable.

### Example

```
// hello_world.xi

use io

main(args: int[][]) {

  println("Hello, world!")

}
```

Running `./xic -libpath tests/libs hello_world.xi` generates:


```asm
# hello_world.s

.globl _Imain_paai
.align 4
_Imain_paai:
        pushq %rbp
        movq %rsp, %rbp
        subq $32, %rsp
        movq $112, %rdi
        callq _xi_alloc
        movq %rax, %rcx
        movq $13, (%rcx)
        movq $72, 8(%rax)
        movq $101, 16(%rax)
        movq $108, 24(%rax)
        movq $108, 32(%rax)
        movq $111, 40(%rax)
        movq $44, 48(%rax)
        movq $32, 56(%rax)
        movq $119, 64(%rax)
        movq $111, 72(%rax)
        movq $114, 80(%rax)
        movq $108, 88(%rax)
        movq $100, 96(%rax)
        movq $33, 104(%rax)
        addq $8, %rax
        movq %rax, %rdi
        callq _Iprintln_pai
        jmp _RET_Imain_paai
_RET_Imain_paai:
        addq $32, %rsp
        popq %rbp
        retq
```

(and some library functions).

On a Unix system, we can link this assembly against the provided runtime with

`gcc -Lruntime -lxi -lpthread -o hello hello_world.s`

And running `./hello` prints the expected `Hello, world!`.

### References

- [Xi Language Specification][2]
- [oXi Language Specification][3]

[1]: https://gradle.org/
[2]: http://www.cs.cornell.edu/courses/cs4120/2018sp/project/language.pdf?1525283120
[3]: http://www.cs.cornell.edu/courses/cs4120/2018sp/project/oolang.pdf?1526606464
