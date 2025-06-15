# ==================================================
# float default.cmd.tp.getZ()
# ==================================================
# ==================================================
# Get the z position of the current entity
# ==================================================

scoreboard players set default.cmd.tp.getZ.z tbms.var 0
summon minecraft:marker ~ ~ ~ {Tags:["default.mc.pointer.0.a"]}
execute as @e[tag=default.mc.pointer.0.a] at @s run function default:zzz_sl_block/217
