# ==================================================
# void default.zzz_sl_block.222()
# a.k.a default.cmd.tp.tpAxisMinus._0.1
# ==================================================

execute if score default.cmd.tp.tpAxisMinus.axis tbms.var matches 0 at @s run tp @s ~-2147483.648 ~ ~
execute if score default.cmd.tp.tpAxisMinus.axis tbms.var matches 1 at @s run tp @s ~ ~-2147483.648 ~
execute if score default.cmd.tp.tpAxisMinus.axis tbms.var matches 2 at @s run tp @s ~ ~ ~-2147483.648
execute if score default.cmd.tp.tpAxisMinus.axis tbms.var matches 3 at @s run tp @s ~ ~ ~ ~-2147483.648 ~
execute if score default.cmd.tp.tpAxisMinus.axis tbms.var matches 4 at @s run tp @s ~ ~ ~ ~ ~-2147483.648
scoreboard players remove default.cmd.tp.tpAxisMinus.x tbms.var 2147483647
