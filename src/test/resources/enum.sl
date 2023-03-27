import test

def pass(){

}

enum Test(void=>void test){
    a(pass),b(pass),c(pass),d(pass)
}
def test(){
    int[10] array
    int b
    array[0] = 1
    lazy void=>void t = pass
    foreach(value in Test){
        array[b] = value
        value.test()
    }
}