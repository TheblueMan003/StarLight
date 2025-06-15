# ==================================================
# void default.zzz_sl_block.241()
# a.k.a default.cmd.tp.tpAxisMinus._19.1
# ==================================================

execute if score default.cmd.tp.tpAxisMinus.axis tbms.var matches 0 at @s run tp @s ~-4.096 ~ ~
execute if score default.cmd.tp.tpAxisMinus.axis tbms.var matches 1 at @s run tp @s ~ ~-4.096 ~
execute if score default.cmd.tp.tpAxisMinus.axis tbms.var matches 2 at @s run tp @s ~ ~ ~-4.096
execute if score default.cmd.tp.tpAxisMinus.axis tbms.var matches 3 at @s run tp @s ~ ~ ~ ~-4.096 ~
execute if score default.cmd.tp.tpAxisMinus.axis tbms.var matches 4 at @s run tp @s ~ ~ ~ ~ ~-4.096
scoreboard players remove default.cmd.tp.tpAxisMinus.x tbms.var 4096
