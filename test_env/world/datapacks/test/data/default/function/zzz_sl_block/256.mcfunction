# ==================================================
# void default.zzz_sl_block.256()
# a.k.a default.cmd.tp.tpAxisPlus._2.1
# ==================================================

execute if score default.cmd.tp.tpAxisPlus.axis tbms.var matches 0 at @s run tp @s ~536870.912 ~ ~
execute if score default.cmd.tp.tpAxisPlus.axis tbms.var matches 1 at @s run tp @s ~ ~536870.912 ~
execute if score default.cmd.tp.tpAxisPlus.axis tbms.var matches 2 at @s run tp @s ~ ~ ~536870.912
execute if score default.cmd.tp.tpAxisPlus.axis tbms.var matches 3 at @s run tp @s ~ ~ ~ ~536870.912 ~
execute if score default.cmd.tp.tpAxisPlus.axis tbms.var matches 4 at @s run tp @s ~ ~ ~ ~ ~536870.912
scoreboard players remove default.cmd.tp.tpAxisPlus.x tbms.var 536870912
