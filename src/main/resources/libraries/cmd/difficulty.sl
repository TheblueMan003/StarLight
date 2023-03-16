package cmd.difficulty

enum Difficulty{
    Peaceful,
    Easy,
    Normal,
    Hard
}

"""
Set the difficulty to `peaceful`
"""
def lazy peaceful(){
    /difficulty peaceful
}

"""
Set the difficulty to `easy`
"""
def lazy easy(){
    /difficulty easy
}

"""
Set the difficulty to `normal`
"""
def lazy normal(){
    /difficulty normal
}

"""
Set the difficulty to `hard`
"""
def lazy hard(){
    /difficulty hard
}

"""
Set the difficulty to `difficulty`
"""
def lazy set(Difficulty gamemode){
    if (gamemode == Difficulty.Peaceful){
        /difficulty peaceful
    }
    else if (gamemode == Difficulty.Easy){
        /difficulty easy
    }
    else if (gamemode == Difficulty.Normal){
        /difficulty normal
    }
    else if (gamemode == Difficulty.Hard){
        /difficulty hard
    }
}

"""
Set the difficulty to `difficulty`
"""
def lazy set(int difficulty){
    if (difficulty == 0){
        /difficulty peaceful
    }
    else if (difficulty == 1){
        /difficulty easy
    }
    else if (difficulty == 2){
        /difficulty normal
    }
    else if (difficulty == 3){
        /difficulty hard
    }
}