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
        return s
    }

    """
    Return a new random number
    """
    int nextInt(){
        s *= 1103515245
        s += 12345
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
        float t
        t = s
        return t
    }

    """
    Return the current seed
    """
    int seed(){
        return s
    }
}

package _
random.lcg.LCG lcg = new random.lcg.LCG(0)