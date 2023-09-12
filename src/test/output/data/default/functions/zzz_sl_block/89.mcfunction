# ==================================================
# void default.zzz_sl_block.89()
# a.k.a default.math.factorial._0.3
# ==================================================

scoreboard players set default.math.factorial._0.res tbms.var 1
scoreboard players set default.math.factorial._0._2.i tbms.var 1
execute if score default.math.factorial._0._2.i tbms.var <= default.math.factorial.x tbms.var run function default:zzz_sl_block/88
scoreboard players set default.math.factorial.__hasFunctionReturned__ tbms.var 1
scoreboard players operation default.math.factorial._ret tbms.var = default.math.factorial._0.res tbms.var
