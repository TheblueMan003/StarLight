# ==================================================
# float default.cmd.tp.getX()
# ==================================================
# ==================================================
# Get the x position of the current entity
# ==================================================

scoreboard players set default.cmd.tp.getX.x tbms.var 0
summon minecraft:marker ~ ~ ~ {Tags:["default.mc.pointer.4.a"]}
execute as @e[tag=default.mc.pointer.4.a] at @s run function default:zzz_sl_block/221
