package random.perlin

from random.lcg import LCG
from math import lerp

struct Perlin2D{
    float[256*256] p
    LCG random

    def __init__(int seed){
        random = LCG(seed)
        for(int i = 0; i < 256*256; i++){
            p[i] = random.nextFloat()
        }
    }

    float dotGridGradient(int ix, int iy){
        return p[ix + iy * 256] % 8
    }

    float getPoint(float x, float y){
        int x0 = x % 256
        int y0 = y % 256
        int x1 = (x0 + 1) % 256
        int y1 = (y0 + 1) % 256

        float sx = x - x0
        float sy = y - y0

        float n0, n1, ix0, ix1, value

        n0 = dotGridGradient(x0, y0)
        n1 = dotGridGradient(x1, y0)
        ix0 = lerp(n0, n1, sx)

        n0 = dotGridGradient(x0, y1)
        n1 = dotGridGradient(x1, y1)
        ix1 = lerp(n0, n1, sx)

        value = lerp(ix0, ix1, sy)

        return value
    }
}

struct Perlin3D{
    float[256*256*256] p
    LCG random

    def __init__(int seed){
        random = LCG(seed)
        for(int i = 0; i < 256*256*256; i++){
            p[i] = random.nextFloat()
        }
    }

    float dotGridGradient(int ix, int iy, int iz){
        return p[ix + iy * 256 + iz * 256 * 256] % 8
    }

    float getPoint(float x, float y, float z){
        int x0 = x % 256
        int y0 = y % 256
        int z0 = z % 256
        int x1 = (x0 + 1) % 256
        int y1 = (y0 + 1) % 256
        int z1 = (z0 + 1) % 256

        float sx = x - x0
        float sy = y - y0
        float sz = z - z0

        float n0, n1, ix0, ix1, value

        n0 = dotGridGradient(x0, y0, z0)
        n1 = dotGridGradient(x1, y0, z0)
        ix0 = lerp(n0, n1, sx)

        n0 = dotGridGradient(x0, y1, z0)
        n1 = dotGridGradient(x1, y1, z0)
        ix1 = lerp(n0, n1, sx)

        value = lerp(ix0, ix1, sy)

        n0 = dotGridGradient(x0, y0, z1)
        n1 = dotGridGradient(x1, y0, z1)
        ix0 = lerp(n0, n1, sx)

        n0 = dotGridGradient(x0, y1, z1)
        n1 = dotGridGradient(x1, y1, z1)
        ix1 = lerp(n0, n1, sx)

        value = lerp(value, lerp(ix0, ix1, sy), sz)

        return value
    }
}