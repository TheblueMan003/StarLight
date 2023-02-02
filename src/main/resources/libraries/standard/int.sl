package standard.int

lazy int minValue = -2147483648
lazy int maxValue = 2147483647

"""
Returns a bitwise and with b. Computation is done using modulo 2 arithmetic.
"""
int bitwiseAnd(int a, int b){
    int result = 0
    foreach(i in 0..31){
        lazy int p = Compiler.powInt(2, 31-i)
        if(a >= p && b >= p){
            result += p
            a -= p
            b -= p
        }
    }
    return result
}

"""
Returns a bitwise or with b. Computation is done using modulo 2 arithmetic.
"""
int bitwiseOr(int a, int b){
    int result = 0
    foreach(i in 0..32){
        lazy int p = Compiler.powInt(2, 31-i)
        if(a >= p || b >= p){
            result += p
        }
    }
    return result
}

"""
Returns a bitwise xor with b. Computation is done using modulo 2 arithmetic.
"""
int bitwiseXor(int a, int b){
    int result = 0
    foreach(i in 0..32){
        lazy int p = Compiler.powInt(2, 31-i)
        if((a >= p && b < p) || (a < p && b >= p)){
            result += p
        }
    }
    return result
}

"""
return x^n
"""
int pow(int x, int n, int m = 1){
    if (n == 0){
        return(1)
    }
    else if (n == 1){
        int ret = x * m
        return(ret)
    }
    else{
        int parity = x % 2
        
        if (parity == 1){
            m *= x
            n -= 1
        }
        n /= 2
        x*=x
        return(pow(x, n, m))
    }
}

"""
Returns a shifted to the left by b bits.
"""
int shiftLeft(int a, int b){
    int c = pow(2, b)
    return a * c
}

"""
Returns a shifted to the right by b bits.
"""
int shiftRight(int a, int b){
    int c = pow(2, b)
    return a / c
}
