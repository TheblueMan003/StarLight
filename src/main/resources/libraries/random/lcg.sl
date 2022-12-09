package random.lcg

"""
Struct for Linear Congruent Generator
Use the value from glibc (https://en.wikipedia.org/wiki/Glibc)
"""
struct LCG{
    int s
    """
    Constructor
    """
    def lazy __init__(int init_seed = 0){
        this.s = init_seed
    }

    """
    Return a new random number
    """
    int next(){
        s *= 1103515245
        s += 12345
        s %= 2147483648
        return s
    }

    """
    Return a new random number
    """
    int nextInt(){
        s *= 1103515245
        s += 12345
        s %= 2147483648
        return s
    }

    """
    Return a new random number between min and max
    """
    int range(int min, int max){
        int n = next()
        n -= min
        n %= (max-min)
        n += min
        return n
    }

    """
    Return a new random float number
    """
    float nextFloat(){
        s *= 1103515245
        s += 12345
        s %= 2147483648
        float t
        t = s
        return t
    }

    """
    Return the current seed
    """
    int seed(){
        int a = s
        a /= 65536
        a %= 16384
        return a
    }
}

package .
random.lgc.LCG lcg = new random.lgc.LCG(0)