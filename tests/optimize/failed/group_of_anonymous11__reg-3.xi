use io
use conv

main(args: int[][]) {
    i:int=0;
    r:int=0;

    while(i!=1000000){
        m0:int=1234567890123456785;
        m1:int=m0%(m0/1);
        m2:int=m1%(m0/2);
        m3:int=m2%(m0/3);
        m4:int=m3%(m0/4);
        m5:int=m4%(m0/5);
        m6:int=m5%(m0/6);
        m7:int=m6%(m0/7);
        m8:int=m7%(m0/8);
        m9:int=m8%(m0/9);
        m10:int=m9%(m0/10);
        m11:int=m10%(m0/11);
        m12:int=m11%(m0/12);
        m13:int=m12%(m0/13);
        m14:int=m13%(m0/14);
        m15:int=m14%(m0/15);
        m16:int=m15%(m0/16);
        m17:int=m16%(m0/17);
        m18:int=m17%(m0/18);
        m19:int=m18%(m0/19);
        m20:int=m19%(m0/20);
        if(m1<=m0 & 
            m2<=m1 & 
            m3<=m2 & 
            m4<=m3 & 
            m5<=m4 & 
            m6<=m5 & 
            m7<=m6 & 
            m8<=m7 & 
            m9<=m8 & 
            m10<=m9 & 
            m11<=m10 & 
            m12<=m11 & 
            m13<=m12 & 
            m14<=m13 & 
            m15<=m14 & 
            m16<=m15 & 
            m17<=m16 & 
            m18<=m17 & 
            m19<=m18 & 
            m20<=m19)
        {
            r=r+1;
        }
        i=i+1
    }
    print(unparseInt(r));
}