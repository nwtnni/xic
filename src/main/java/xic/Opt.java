package xic;

public enum Opt {
    CF   ("Constant folding"),
    REG  ("Register allocation"),
    MC   ("Move coalescing"),
    CSE  ("Common subexpression elimination"),
    ALG  ("Algebraic optimizations (identities and reassociation)"),
    COPY ("Copy propagation"),
    DCE  ("Dead code elimination"),
    INL  ("Inlining"),
    SR   ("Strength reduction"),
    LU   ("Loop unrolling"),
    LICM ("Loopinvariant code motion"),
    PRE  ("Partial redundancy elimination"),
    CP   ("Constant propagation"),
    VN   ("Local value numbering");

    private String description;

    private Opt(String description) {
        this.description = description;
    }
}
