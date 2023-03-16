package cmd.bedrock.dialog

"""
Open a dialogue of an NPC to a player
"""
def lazy open(entity $npc, entity $player = @s){
    /dialogue open $npc $player
}

"""
Open a dialogue of scene `sceneName` of an NPC to a player
"""
def lazy open(entity $npc, entity $player, string $sceneName){
    /dialogue open $npc $player $sceneName
}

"""
Change the dialogue of an NPC to a scene
"""
def lazy change(entity $npc, string $sceneName){
    /dialogue change $npc $sceneName
}

