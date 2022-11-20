package test

lazy void fct(json test){
    jsonfile test{
        "test" : test
    }
}

def ticking main(){
    lazy string a = "test"
    a += a
    fct({"value":a})
}