def intComparaison(){
    int a
    int b

    if (a == b){
        /say hi
    }
    if (a != b){
        /say hi
    }
    if (a >= b){
        /say hi
    }


    if (a == 0){
        /say hi
    }
    if (a != 0){
        /say hi
    }
    if (a >= 0){
        /say hi
    }


    if (0 == b){
        /say hi
    }
    if (0 != b){
        /say hi
    }
    if (0 >= b){
        /say hi
    }
}

def floatComparaison(){
    float a
    float b

    if (a == b){
        /say hi
    }
    if (a != b){
        /say hi
    }
    if (a >= b){
        /say hi
    }


    if (a == 0){
        /say hi
    }
    if (a != 0){
        /say hi
    }
    if (a >= 0){
        /say hi
    }


    if (0 == b){
        /say hi
    }
    if (0 != b){
        /say hi
    }
    if (0 >= b){
        /say hi
    }
}

def boolComparaison(){
    bool a
    bool b

    if (a == b){
        /say hi
    }
    if (a != b){
        /say hi
    }
    if (a >= b){
        /say hi
    }


    if (a == true){
        /say hi
    }
    if (a != false){
        /say hi
    }
    if (a >= true){
        /say hi
    }


    if (true == b){
        /say hi
    }
    if (true != b){
        /say hi
    }
    if (false >= b){
        /say hi
    }
}

def enumComparaison(){
    enum e{
    }
    e a,b
    if (a == b){
        /say hi
    }
    if (a != b){
        /say hi
    }
    if (a >= b){
        /say hi
    }


    if (a == 0){
        /say hi
    }
    if (a != 0){
        /say hi
    }
    if (a >= 0){
        /say hi
    }
}

def classComparaison(){
    class e{
        bool __lt__(int a){

        }
        bool __le__(int a){
            
        }
        bool __eq__(int a){
            
        }
        bool __gt__(int a){
            
        }
        bool __ge__(int a){
            
        }
        bool __ne__(int a){
            
        }
    }
    e a
    if (a == 0){
        /say hi
    }
    if (a != 0){
        /say hi
    }
    if (a >= 0){
        /say hi
    }
    if (a > 0){
        /say hi
    }
    if (a < 0){
        /say hi
    }
    if (a <= 0){
        /say hi
    }
}

intComparaison()
floatComparaison()
boolComparaison()
enumComparaison()
classComparaison()