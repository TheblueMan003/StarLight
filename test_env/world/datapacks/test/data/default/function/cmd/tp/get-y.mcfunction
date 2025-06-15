# ==================================================
# float default.cmd.tp.getY()
# ==================================================
# ==================================================
# Get the y position of the current entity
# ==================================================

scoreboard players set default.cmd.tp.getY.y tbms.var 0
summon minecraft:marker ~ ~ ~ {Tags:["default.mc.pointer.2.a"]}
execute as @e[tag=default.mc.pointer.2.a] at @s run function default:zzz_sl_block/219
