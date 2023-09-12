# ==================================================
# void default.zzz_sl_block.88()
# a.k.a default.math.factorial._0._2.1
# ==================================================

scoreboard players operation default.math.factorial._0.res tbms.var *= default.math.factorial._0._2.i tbms.var
scoreboard players add default.math.factorial._0._2.i tbms.var 1
execute if score default.math.factorial._0._2.i tbms.var <= default.math.factorial.x tbms.var run function default:zzz_sl_block/88
