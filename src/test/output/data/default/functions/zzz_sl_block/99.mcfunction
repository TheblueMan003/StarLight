# ==================================================
# void default.zzz_sl_block.99()
# a.k.a default.math.sqrt._0.2
# ==================================================

scoreboard players set default.math.sqrt.__hasFunctionReturned__ tbms.var 1
scoreboard players operation default.math.sqrt._0.iterate.guess tbms.var = default.math.sqrt.value tbms.var
function default:zzz_sl_block/98
scoreboard players operation default.math.sqrt._ret tbms.var = default.math.sqrt._0.iterate._ret tbms.var
