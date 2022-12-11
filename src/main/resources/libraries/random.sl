package random

if (Compiler.isBedrock){
    lazy int range(int x, int y){
        int r
        Compiler.random(r, x, y)
        return r
    }
}