package test

private lazy int give(int item = stone){
    %
    give @s item
    %
}

int a = 0
int b
int c
int d
int e

int abs(int x){
    if (x > 0){
        return x
    }
    else{
        return -x
    }
}

float abs(float x){
    if (x > 0){
        return x
    }
    else{
        return -x
    }
}
entity int score
jsonfile advencments.events.test{
    "key": {
        "key1":[0, 0.1, "test"]
    }
}

def test(){
    score = 0
    int=>int test2
    float=>float test3

    test2 = abs
    test3 = abs
    int a = test2(5)

    while(a > 0){
        a -=1
    }

    do{
        a -=1
    }while(a > 0)

    for(int a = 0;a < 10; a+=1){
        int b = a
    }
}