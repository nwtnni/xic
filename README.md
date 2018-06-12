# xic

Compiles `Xi` and `OXi` source files down to `x86_64` assembly.

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

###### hello\_world.xi

```
use io

main(args: int[][]) {

  println("Hello, world!")

}
```

Running `./xic -libpath tests/libs hello_world.xi` generates:

###### hello\_world.s

```asm
# main
.globl _Imain_paai
.align 4
_Imain_paai:
        pushq %rbp
        movq %rsp, %rbp
        subq $96, %rsp
        movq %r15, %r11
        movq %r11, -8(%rbp)
        movq %r14, %r11
        movq %r11, -16(%rbp)
        movq %r13, %r11
        movq %r11, -24(%rbp)
        movq %r12, %r11
        movq %r11, -32(%rbp)
        movq %rbx, %r11
        movq %r11, -40(%rbp)
        movq %rdi, %r11
        movq %r11, -48(%rbp)
        movq $112, %rdi
        movq %r11, -56(%rbp)
        callq _xi_alloc
        movq %rax, %r11
        movq %r11, -64(%rbp)
        movq -64(%rbp), %r10
        movq %r10, %r11
        movq %r11, -72(%rbp)
        movq -72(%rbp), %r10
        movq $13, (%r10)
        movq -64(%rbp), %r10
        movq $72, 8(%r10)
        movq -64(%rbp), %r10
        movq $101, 16(%r10)
        movq -64(%rbp), %r10
        movq $108, 24(%r10)
        movq -64(%rbp), %r10
        movq $108, 32(%r10)
        movq -64(%rbp), %r10
        movq $111, 40(%r10)
        movq -64(%rbp), %r10
        movq $44, 48(%r10)
        movq -64(%rbp), %r10
        movq $32, 56(%r10)
        movq -64(%rbp), %r10
        movq $119, 64(%r10)
        movq -64(%rbp), %r10
        movq $111, 72(%r10)
        movq -64(%rbp), %r10
        movq $114, 80(%r10)
        movq -64(%rbp), %r10
        movq $108, 88(%r10)
        movq -64(%rbp), %r10
        movq $100, 96(%r10)
        movq -64(%rbp), %r10
        movq $33, 104(%r10)
        movq -64(%rbp), %r10
        movq %r10, %r11
        movq %r11, -80(%rbp)
        movq -80(%rbp), %r11
        addq $8, %r11
        movq %r11, -80(%rbp)
        movq -80(%rbp), %r10
        movq -64(%rbp), %r11
        movq %r10, %r11
        movq %r11, -64(%rbp)
        movq -64(%rbp), %r10
        movq %r10, %rdi
        movq %r11, -56(%rbp)
        callq _Iprintln_pai
        movq -40(%rbp), %r10
        movq %r10, %rbx
        movq %r11, -56(%rbp)
        movq -32(%rbp), %r10
        movq %r10, %r12
        movq %r11, -56(%rbp)
        movq -24(%rbp), %r10
        movq %r10, %r13
        movq %r11, -56(%rbp)
        movq -16(%rbp), %r10
        movq %r10, %r14
        movq %r11, -56(%rbp)
        movq -8(%rbp), %r10
        movq %r10, %r15
        movq %r11, -56(%rbp)
        jmp _RET_Imain_paai
_RET_Imain_paai:
        addq $96, %rsp
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
