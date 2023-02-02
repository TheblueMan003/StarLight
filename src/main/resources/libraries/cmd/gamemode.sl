package cmd.gamemode

if (Compiler.isJava()){
    predicate isAdventure(){
        "condition": "minecraft:entity_properties",
        "entity": "this",
        "predicate": {
            "type_specific": {
                "type": "player",
                "gamemode": "adventure"
            }
        }
    }
    predicate isSurvival(){
        "condition": "minecraft:entity_properties",
        "entity": "this",
        "predicate": {
            "type_specific": {
                "type": "player",
                "gamemode": "survival"
            }
        }
    }
    predicate isCreative(){
        "condition": "minecraft:entity_properties",
        "entity": "this",
        "predicate": {
            "type_specific": {
                "type": "player",
                "gamemode": "creative"
            }
        }
    }
    predicate isSpectator(){
        "condition": "minecraft:entity_properties",
        "entity": "this",
        "predicate": {
            "type_specific": {
                "type": "player",
                "gamemode": "spectator"
            }
        }
    }
}
if (Compiler.isBedrock()){
    """
    return true if the gamemode of the current entity is `adventure`
    """
    def lazy bool isAdventure(){
        return @s[gamemode=adventure]
    }
    """
    return true if the gamemode of the current entity is `survival`
    """
    def lazy bool isSurvival(){
        return @s[gamemode=survival]
    }
    """
    return true if the gamemode of the current entity is `creative`
    """
    def lazy bool isCreative(){
        return @s[gamemode=creative]
    }
    """
    return true if the gamemode of the current entity is `spectator`
    """
    def lazy bool isSpectator(){
        return @s[gamemode=spectator]
    }
}


enum Gamemode{
    Survival,
    Creative,
    Adventure,
    Spectator
}
def [noReturnCheck=true] Gamemode get(){
    if (isSurvival()){
        return Gamemode.Survival
    }
    if (isCreative()){
        return Gamemode.Creative
    }
    if (isAdventure()){
        return Gamemode.Adventure
    }
    if (isSpectator()){
        return Gamemode.Spectator
    }
}

"""
Set the gamemode to `adventure` for entity `e`
"""
def lazy adventure(entity $e = @s){
    /gamemode adventure $e
}
"""
Set the gamemode to `survival` for entity `e`
"""
def lazy survival(entity $e = @s){
    /gamemode survival $e
}
"""
Set the gamemode to `creative` for entity `e`
"""
def lazy creative(entity $e = @s){
    /gamemode creative $e
}
"""
Set the gamemode to `spectator` for entity `e`
"""
def lazy spectator(entity $e = @s){
    /gamemode spectator $e
}

"""
Set the gamemode to `gamemode` for entity `e`
"""
def lazy set(mcobject $gamemode, entity $e = @s){
	/gamemode $gamemode $e
}

"""
Set the gamemode to `gamemode` for entity `e`
"""
def lazy set(int gamemode, entity $e = @s){
	if (gamemode == 0){
		/gamemode survival $e
	}
	else if (gamemode == 1){
		/gamemode creative $e
	}
	else if (gamemode == 2){
		/gamemode adventure $e
	}
	else if (gamemode == 3){
		/gamemode spectator $e
	}
}