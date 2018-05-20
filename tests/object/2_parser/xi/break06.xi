x:int

foo():int  {
    while(true) {
        x = x+1
        {{
            x = 5
            break
        }}
    }
    return x
}