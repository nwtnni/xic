.text
.globl _Imain_paai
.align 4
FUNC(_Imain_paai):
pushq %rbp
movq %rsp, %rbp
subq $42, %rsp
movq ARG1(), %rax
movq %rax, -8(%rbp)
movq $1, %rax
movq %rax, -16(%rbp)
movq -16(%rbp), ARG1()
callq FUNC(_IunparseInt_aii)
movq %rax, %rax
movq %rax, -24(%rbp)
movq -24(%rbp), %rax
movq %rax, -32(%rbp)
movq -32(%rbp), ARG1()
callq FUNC(_Iprintln_pai)
movq %rax, %rax
movq %rax, -40(%rbp)
addq $%d, %rsp
popq %rbp
retq
