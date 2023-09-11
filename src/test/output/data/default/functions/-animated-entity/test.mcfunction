execute as @e[tag=__class__] if score default.AnimatedEntity.test.a tbms.var = @s default.object.__ref run function default:object/__rem-ref
scoreboard players add default.__totalRefCount tbms.var 1
tag @e[tag=!object.__tagged] add object.__tagged
function animated_java:among_us/summon
execute as @e[tag=!object.__tagged,tag=aj.among_us.root] run function default:zzz_sl_block/0
scoreboard players operation default.AnimatedEntity.test.a tbms.var = default.__totalRefCount tbms.var
execute as @e[tag=__class__] if score default.AnimatedEntity.test.a tbms.var = @s default.object.__ref run function default:zzz_sl_block/2
