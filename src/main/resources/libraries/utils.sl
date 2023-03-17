package utils

"""
Execute a function for each block in a cube of size sizeX*sizeY*sizeZ and provide the coordinates of the block (relative to start) to the function
"""
def lazy forCube(int sizeX, int sizeY, int sizeZ, (int,int,int)=>void fct){
    def traverseZ(int z){
        def traverseY(int y){
            def traverseX(int x){
                fct(sizeX - x, sizeY - y, sizeZ - z)
                if (x > 0)at(~1 ~ ~)traverseX(x-1)
            }
            if (y > 0)at(~ ~1 ~)traverseY(y-1)
        }
        if (z > 0)at(~ ~ ~1)traverseZ(z-1)        
    }
}

"""
Execute a function for each block in a cube of size sizeX*sizeY*sizeZ
"""
def lazy forArea(int sizeX, int sizeY, int sizeZ, void=>void fct){
    def traverseZ(int z){
        def traverseY(int y){
            def traverseX(int x){
                fct()
                if (x > 0)at(~1 ~ ~)traverseX(x-1)
            }
            if (y > 0)at(~ ~1 ~)traverseY(y-1)
        }
        if (z > 0)at(~ ~ ~1)traverseZ(z-1)
    }
}

"""
Lock Player in place
"""
def lazy lockPosition(mcposition pos=~~~){
    at(pos)as(@s[distance=0.1..,gamemode=!creative])./tp @s ~ ~ ~
}