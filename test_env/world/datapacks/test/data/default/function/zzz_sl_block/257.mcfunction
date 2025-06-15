# ==================================================
# void default.zzz_sl_block.257()
# a.k.a default.cmd.tp.tpAxisPlus._3.1
# ==================================================

execute if score default.cmd.tp.tpAxisPlus.axis tbms.var matches 0 at @s run tp @s ~268435.456 ~ ~
execute if score default.cmd.tp.tpAxisPlus.axis tbms.var matches 1 at @s run tp @s ~ ~268435.456 ~
execute if score default.cmd.tp.tpAxisPlus.axis tbms.var matches 2 at @s run tp @s ~ ~ ~268435.456
execute if score default.cmd.tp.tpAxisPlus.axis tbms.var matches 3 at @s run tp @s ~ ~ ~ ~268435.456 ~
execute if score default.cmd.tp.tpAxisPlus.axis tbms.var matches 4 at @s run tp @s ~ ~ ~ ~ ~268435.456
scoreboard players remove default.cmd.tp.tpAxisPlus.x tbms.var 268435456
