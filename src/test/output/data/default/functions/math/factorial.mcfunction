scoreboard players set default.math.factorial.res tbms.var 1
scoreboard players set default.math.factorial.i tbms.var 1
execute if score default.math.factorial.i tbms.var <= default.math.factorial.x tbms.var run function default/zzz_sl_block/8
scoreboard players operation default.math.factorial._ret tbms.var = default.math.factorial.res tbms.var
