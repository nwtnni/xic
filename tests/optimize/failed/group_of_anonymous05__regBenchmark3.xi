use io use conv
main(args:int[][]){
    i : int = 0; j:int = 0; k:int = 1024;l:int = 0;
    while (i < 1000000) {
        x : int = pow(19,19);y : int = pow(19,19);z : int = pow(19,19);
        x1 : int = pow(19,19);x2 : int = pow(19,19);x3 : int = pow(19,19);
        x0 : int = pow(19,19);x9 : int = pow(19,19);x4 : int = pow(19,19);
        x00 : int = pow(19,19);x8 : int = pow(19,19);x5 : int = pow(19,19);
        x01 : int = pow(19,19);x7 : int = pow(19,19);j = pow(2,10);
        sum:int = x+y+z+x1+x2+x3+x4+x0+x9+x4+x00+x8+x5+x01+x7;
        i = i + 1;
    } // if pow works, won't fail:
    if (k/j == 1) {} else {j = 1/(id(l))};
}

id(a:int) : int { return a }

pow(number:int, exponent:int):int{
    if (exponent==0) { return 1 }
    else { return number * pow(number,exponent-1) }
}