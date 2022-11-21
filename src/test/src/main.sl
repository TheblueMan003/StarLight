package test

lazy void test(void=>void fct){
    fct()
}


def ticking main(){
    test(()=>{
        /say hi
    })
}

