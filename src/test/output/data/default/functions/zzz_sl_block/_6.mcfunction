scoreboard players operation default.math.factorial.res tbms.var *= default.math.factorial.i tbms.var
scoreboard players add default.math.factorial.i tbms.var 1
execute if score default.math.factorial.i tbms.var <= default.math.factorial.x tbms.var run function default/zzz_sl_block/_6
