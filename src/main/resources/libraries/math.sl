package math


val pi = 3.1415
val e = 2.718

"""
Return the absolute value of x
"""
int abs(int x){
	if(x < 0){
		x *= -1
	}
    return x
}

"""
Return the absolute value of x
"""
float abs(float x){
	if(x < 0){
		x *= -1
	}
    return x
}

"""
Return the max between the a and b
"""
int max(int a, int b){
	if(a > b){
		b = a
	}
	return b
}

"""
Return the max between the a and b
"""
float max(float a, float b){
	if(a > b){
		b = a
	}
	return b
}

"""
Return the min between the a and b
"""
int min(int a, int b){
	if(a < b){
		b = a
	}
	return b
}

"""
Return the min between the a and b
"""
float min(float a, float b){
	if(a < b){
		b = a
	}
	return b
}

"""
return true if difference between x and y smaller than maxDiff
"""
bool isClose(float x, float y, float maxDiff = 0.01){
    float diff = abs(x - y)
    
    if (diff < maxDiff){
        return true
    }
    if (diff >= maxDiff){
        return false
    }
}

"""
return squart root of value
"""
float sqrt(float value){
    if (value < 0){
        //exception.invalidArgument("value in math.sqrt")
    }
    
    def float iterate(float guess){
        float next = ((value/guess) + guess)/2
        bool close = isClose(next, guess)
        
        if (isClose(next, guess)){
            return(next)
        }
        else{
            return(iterate(next))
        }
    }
    return(iterate(value))
}
float root(float n){
    // Max and min are used to take into account numbers less than 1
    float lo = min(1, n)
    float hi = max(1, n)
    float mid
    
    // Update the bounds to be off the target by a factor of 10
    while(100 * lo * lo < n) lo *= 10
    while(100 * hi * hi > n) hi *= 0.1
    bool ret = false
    for(int i = 0 ; i < 100 && !ret; i++){
        mid = (lo+hi)/2
        if(mid*mid == n){ret = true}
        if(mid*mid > n){hi = mid}
        else {lo = mid}
    }
    return mid
}


"""
return x!
"""
def int factorial(int x){
    if (x < 0){
        //exception.invalidArgument("X in math.factorial")
    }
    int res = 1

    for(int i = 1;i <= x;i++){
        res *= i
    }
    return(res)
}

"""
return x^n
"""
def float pow(float x, int n, float m = 1){
    if (n < 0){
        //exception.invalidArgument("n in math.pow")
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
return x^n
"""
def int pow(int x, int n, int m = 1){
    if (n < 0){
        //exception.invalidArgument("n in math.intPow")
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

"""
Return sign of the argument
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
Return sign of the argument
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
Clamp the value between a and b
"""
int clamp(int value, int a, int b){
    return(math.max(math.min(value, a),b))
}

"""
Clamp the value between a and b
"""
float clamp(float value, float a, float b){
    return(math.max(math.min(value, a),b))
}

"""
Return (a,b) if a <= b else return (b,a)
"""
(int,int) sorted(int a, int b){
    if (a > b){
        return(b, a)
    }
    if (a <= b){
        return(a, b)
    }
}

"""
Return (a,b) if a <= b else return (b,a)
"""
(float,float) sorted(float a, float b){
    if (a > b){
        return(b, a)
    }
    if (a <= b){
        return(a, b)
    }
}


"""
Return the linear interpolated value of a0 and a1 with coefficient w
"""
float linearLerp(float a0, float a1, float w){
    return((1.0-w)*a0 + a1*w)
}

"""
Return the 3rd degree interpolated value of a0 and a1 with coefficient w
"""
float smoothLerp(float a0, float a1, float w){
    w = w*w*(3-2*w)
    float value = (1.0 - w)*a0 + (w * a1)
    (float,float) s = sorted(a0, a1)
    if (value < s._0){
        value = s._0
    }
    if (value > s._1){
        value = s._1
    }
    return value
}