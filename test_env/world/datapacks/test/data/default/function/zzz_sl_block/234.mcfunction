# ==================================================
# void default.zzz_sl_block.234()
# a.k.a default.cmd.tp.tpAxisMinus._12.1
# ==================================================

execute if score default.cmd.tp.tpAxisMinus.axis tbms.var matches 0 at @s run tp @s ~-524.288 ~ ~
execute if score default.cmd.tp.tpAxisMinus.axis tbms.var matches 1 at @s run tp @s ~ ~-524.288 ~
execute if score default.cmd.tp.tpAxisMinus.axis tbms.var matches 2 at @s run tp @s ~ ~ ~-524.288
execute if score default.cmd.tp.tpAxisMinus.axis tbms.var matches 3 at @s run tp @s ~ ~ ~ ~-524.288 ~
execute if score default.cmd.tp.tpAxisMinus.axis tbms.var matches 4 at @s run tp @s ~ ~ ~ ~ ~-524.288
scoreboard players remove default.cmd.tp.tpAxisMinus.x tbms.var 524288
