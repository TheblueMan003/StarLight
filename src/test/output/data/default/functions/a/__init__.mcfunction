execute as @e[tag=__class__] at @s if score default.a.c tbms.var = @s s1644059425 run function default:object/__rem-ref
scoreboard players add default.__totalRefCount tbms.var 1
tag @e[tag=!object.__tagged] add object.__tagged
function dr_snake:summon/default
execute as @e[tag=!object.__tagged,type=marker] run function default:zzz_sl_block/0
scoreboard players operation default.a.c tbms.var = default.__totalRefCount tbms.var
execute as @e[tag=__class__] at @s if score default.a.c tbms.var = @s s1644059425 run function default:a/-a/__init__
execute unless score default.a.adas.enabled tbms.var = default.a.adas.enabled tbms.var run scoreboard players set default.a.adas.enabled tbms.var 0
execute unless score default.a.adas.crashCount tbms.var = default.a.adas.crashCount tbms.var run scoreboard players set default.a.adas.crashCount tbms.var 0
execute unless score default.a.adas.callback tbms.var = default.a.adas.callback tbms.var run scoreboard players set default.a.adas.callback tbms.var 0
