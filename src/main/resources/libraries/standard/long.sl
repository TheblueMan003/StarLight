package standard.long

"""
Long implementation using 4 integers
"""
struct long{
    int n0, n1, n2, n3

    void __init__(int n0, int n1, int n2, int n3){
        this.n0 = n0
        this.n1 = n1
        this.n2 = n2
        this.n3 = n3
    }
    void __init__(int n0, int n1, int n2){
        this.n0 = n0
        this.n1 = n1
        this.n2 = n2
        this.n3 = 0
    }
    void __init__(int n0, int n1){
        this.n0 = n0
        this.n1 = n1
        this.n2 = 0
        this.n3 = 0
    }
    void __init__(int n0){
        this.n0 = n0
        this.n1 = 0
        this.n2 = 0
        this.n3 = 0
    }

    long __add__(long other){
        int d0 = this.n0 + other.n0
        int u0 = d0 >> 16
        int l0 = d0 & ((1 << 16) - 1)

        int d1 = this.n1 + other.n1 + u0
        int u1 = d1 >> 16
        int l1 = d1 & ((1 << 16) - 1)

        int d2 = this.n2 + other.n2 + u1
        int u2 = d2 >> 16
        int l2 = d2 & ((1 << 16) - 1)

        int d3 = this.n3 + other.n3 + u2
        int l3 = d3 & ((1 << 16) - 1)
        
        return new long(l0, l1, l2, l3)
    }

    long __add__(int other){
        int d0 = this.n0 + other
        int u0 = d0 >> 16
        int l0 = d0 & ((1 << 16) - 1)

        int d1 = this.n1 + u0
        int u1 = d1 >> 16
        int l1 = d1 & ((1 << 16) - 1)

        int d2 = this.n2 + u1
        int u2 = d2 >> 16
        int l2 = d2 & ((1 << 16) - 1)

        int d3 = this.n3 + u2
        int l3 = d3 & ((1 << 16) - 1)
        
        return new long(l0, l1, l2, l3)
    }    

    long __mult__(int other){
        int d0 = this.n0 * other
        int u0 = d0 >> 16
        int l0 = d0 & ((1 << 16) - 1)

        int d1 = this.n1 * other + u0
        int u1 = d1 >> 16
        int l1 = d1 & ((1 << 16) - 1)

        int d2 = this.n2 * other + u1
        int u2 = d2 >> 16
        int l2 = d2 & ((1 << 16) - 1)

        int d3 = this.n3 * other + u2
        int l3 = d3 & ((1 << 16) - 1)

        return new long(l0, l1, l2, l3)
    }
}