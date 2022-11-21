package test

lazy void fct(json test){
    jsonfile test{
        "test" : test
    }
}

def ticking main(){
    with(@a[c=1]){
        int a
    }
    lazy json a = {"test":"lol"}
    a += {"bruh":"lol"}
    fct({"value":a})
}

