package cmd.score

"""
Sum up the value of the scoreboard of the entity in e
"""
lazy int sum(entity e, string score){
    int c = 0
    with(e){
        c += score
    }
    return c
}

"""
Get the min value of the scoreboard of the entity in e
"""
lazy int min(entity e, string score){
    int c = 0
    with(e){
        if(score < c){
            c = score
        }
    }
    return c
}

"""
Get the max value of the scoreboard of the entity in e
"""
lazy int max(entity e, string score){
    int c = 0
    with(e){
        if(score > c){
            c = score
        }
    }
    return c
}

"""
Get the average value of the scoreboard of the entity in e
"""
lazy int avg(entity e, string score){
    int c = 0
    int c2 = 0
    with(e){
        c += score
        c2 ++
    }
    return c / c2
}
