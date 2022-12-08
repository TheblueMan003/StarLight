package math
 /**
     * ifs.
     *
     * @author	Unknown
     * @since	v0.0.1
     * @version	v1.0.0	Wednesday, November 30th, 2022.
     * @param	mixed	if	
     * @return	void
     */

"""
Return the absolute value of x
"""
def int abs(int x){
	if(x < 0){
		return -x
	}
	if(x >= 0){
		return x
	}
}

"""
Return the absolute value of x
"""
def float abs(float x){
	if(x < 0){
		return -x
	}
	if(x >= 0){
		return x
	}
}

"""
Return the max between the a and b
"""
def int max(int a, int b){
	if(a > b){
		return a
	}
	if(b >= a){
		return b
	}
}

"""
Return the max between the a and b
"""
def float max(float a, float b){
	if(a > b){
		return a
	}
	if(b >= a){
		return b
	}
}

"""
Return the min between the a and b
"""
def int min(int a, int b){
	if(a < b){
		return a
	}
	if(b <= a){
		return b
	}
}

"""
Return the min between the a and b
"""
def float min(float a, float b){
	if(a < b){
		return a
	}
	if(b <= a){
		return b
	}
}

"""
return true if difference between x and y smaller than maxDiff
"""
def bool isClose(float x, float y, float maxDiff = 0.01){
    float diff = x - y
    diff = abs(diff)
    
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
def float sqrt(float value){
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