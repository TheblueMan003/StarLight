package math

lazy float pi(){
    return(3.1415)
}
lazy float e(){
    return(2.718)
}

"""
Give sign of argument
-1 if value smaller than 0 
    1 otherwise
"""
int sign(float value){
    if (value >= 0){
        return(1)
    }
    if (value < 0){
        return(-1)
    }
}

"""
Give sign of argument
-1 if value smaller than 0 
    1 otherwise
"""
int sign(int value){
    if (value >= 0){
        return(1)
    }
    if (value < 0){
        return(-1)
    }
}

"""
Give magnitude of argument
v if v > 0
    -v otherwise
"""
float abs(float value){
    if (value >= 0){
        return(value)
    }
    if (value < 0){
        return(-value)
    }
}

"""
Give magnitude of argument
v if v > 0
    -v otherwise
"""
int abs(int value){
    if (value >= 0){
        return(value)
    }
    if (value < 0){
        return(-value)
    }
}

"""
Round float to closest value
"""
float round(float value){
    value += 0.5
    value /= 1000
    value *= 1000
    return(value)
}

"""
Round float to lowest value
"""
float floor(float value){
    value /= 1000
    value *= 1000
    return(value)
}

"""
Round float to upper value
"""
float ceil(float value){
    value += 0.999
    value /= 1000
    value *= 1000
    return(value)
}

"""
return x!
"""
int factorial(int x){
    if (x < 0){
        exception.invalidArgument("X in math.factorial")
    }
    int res = 1

    for(int i = 1;i <= x;i++){
        res *= i
    }
    return(res)
}

"""
Give x^n
"""
float pow(float x, int n, float m = 1){
    if (n < 0){
        exception.invalidArgument("n in math.pow")
    }
    if (n == 0){
        return(1)
    }
    if (n == 1){
        float ret = x * m
        return(ret)
    }
    if (n > 1){
        int parity = n % 2
        
        if (parity == 1){
            m *= x
            n -= 1
        }
        n /= 2
        x *= x
        return(pow(x, n, m))
    }
}

"""
Give x^n
"""
int pow(int x, int n, int m = 1){
    if (n < 0){
        exception.invalidArgument("n in math.intPow")
    }
    if (n == 0){
        return(1)
    }
    if (n == 1){
        int ret = x * m
        return(ret)
    }
    if (n > 1){
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