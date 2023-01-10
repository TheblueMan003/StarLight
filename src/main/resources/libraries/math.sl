package math

val pi = 3.1415
val e = 2.718

"""
Return the absolute value of x
"""
T abs<T>(T x){
	if(x < 0){
		x *= -1
	}
    return x
}

"""
Return the max between the a and b
"""
T max<T>(T a, T b){
	if(a > b){
		b = a
	}
	return b
}

"""
Return the min between the a and b
"""
T min<T>(T a, T b){
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
int pow(int x, int n){
    return x ^ n
}

"""
Return sign of the argument
-1 if value smaller than 0
1 otherwise
"""
T sign<T>(T value){
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
T clamp<T>(T value, T a, T b){
    return(math.max(math.min(value, a),b))
}

"""
Return (a,b) if a <= b else return (b,a)
"""
(T,T) sorted<T>(T a, T b){
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
lazy float lerp(float a0, float a1, float w){
    linearLerp(a0, a1, w)
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

"""
Compute the sinus of the angle in degrees with a taylor series
"""
float sin(float angle){
    float angleRad = angle * pi / 180
    float res = 0
    float sign = 1
    float fact = 1
    float pow = 1
    for(int i = 0; i < 10; i++){
        res += sign * pow / fact
        sign *= -1
        fact *= (2*i+2) * (2*i+3)
        pow *= angleRad
        pow *= angleRad
    }
    return res
}

"""
Compute the cossinus of the angle in degrees with a taylor series
"""
float cos(float angle){
    float angleRad = angle * pi / 180
    float res = 0
    float sign = 1
    float fact = 1
    float pow = 1
    for(int i = 0; i < 10; i++){
        res += sign * pow / fact
        sign *= -1
        fact *= (2*i+1) * (2*i+2)
        pow *= angleRad
        pow *= angleRad
    }
    return res
}

"""
Compute the tangente of the angle in degrees with a taylor series
"""
float tan(float angle){
    return(sin(angle)/cos(angle))
}

"""
Compute the arcsinus of the angle in degrees with a taylor series
"""
float asin(float angle){
    float res = 0
    float sign = 1
    float fact = 1
    float pow = 1
    for(int i = 0; i < 10; i++){
        res += sign * pow / fact
        sign *= -1
        fact *= (2*i+1) * (2*i+2)
        pow *= angle
        pow *= angle
    }
    return res
}

"""
Compute the arccossinus of the angle in degrees with a taylor series
"""
float acos(float angle){
    return(90 - asin(angle))
}

"""
Compute the arctangente of the angle in degrees with a taylor series
"""
float atan(float angle){
    return(90 - acos(angle))
}

"""
Compute the arctangente of the angle in degrees with a taylor series
"""
float atan2(float y, float x){
    return(atan(y/x))
}

"""
Compute the square root of the value with a taylor series
"""
float sqrt2(float value){
    float res = 0
    float sign = 1
    float fact = 1
    float pow = 1
    for(int i = 0; i < 10; i++){
        res += sign * pow / fact
        sign *= -1
        fact *= (2*i+1) * (2*i+2)
        pow *= value
    }
    return res
}

"""
Compute the log of x with a taylor series
"""
float log(float x){
    float res = 0
    float sign = 1
    float fact = 1
    float pow = 1
    for(int i = 0; i < 10; i++){
        res += sign * pow / fact
        sign *= -1
        fact *= (2*i+1) * (2*i+2)
        pow *= x
    }
    return res
}

"""
Compute the exp of x with a taylor series
"""
float exp(float x){
    float res = 0
    float sign = 1
    float fact = 1
    float pow = 1
    for(int i = 0; i < 10; i++){
        res += sign * pow / fact
        sign *= -1
        fact *= (2*i+1) * (2*i+2)
        pow *= x
    }
    return res
}

"""
Compute the pow of x with a taylor series
"""
float pow(float x, float y){
    return(exp(y * log(x)))
}