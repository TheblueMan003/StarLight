# ==================================================
# void default.object.__remRef()
# ==================================================

execute if score @s default.object.__refCount matches 1.. run scoreboard players remove @s default.object.__refCount 1
execute if score @s default.object.__refCount matches 0 run function default:object/__delete__
