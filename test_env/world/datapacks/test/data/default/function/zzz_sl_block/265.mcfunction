# ==================================================
# void default.zzz_sl_block.265()
# a.k.a default.cmd.tp.tpAxisPlus._11.1
# ==================================================

execute if score default.cmd.tp.tpAxisPlus.axis tbms.var matches 0 at @s run tp @s ~1048.576 ~ ~
execute if score default.cmd.tp.tpAxisPlus.axis tbms.var matches 1 at @s run tp @s ~ ~1048.576 ~
execute if score default.cmd.tp.tpAxisPlus.axis tbms.var matches 2 at @s run tp @s ~ ~ ~1048.576
execute if score default.cmd.tp.tpAxisPlus.axis tbms.var matches 3 at @s run tp @s ~ ~ ~ ~1048.576 ~
execute if score default.cmd.tp.tpAxisPlus.axis tbms.var matches 4 at @s run tp @s ~ ~ ~ ~ ~1048.576
scoreboard players remove default.cmd.tp.tpAxisPlus.x tbms.var 1048576
