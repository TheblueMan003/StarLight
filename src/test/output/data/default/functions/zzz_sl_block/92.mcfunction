# ==================================================
# void default.zzz_sl_block.92()
# a.k.a default.math.root.0
# ==================================================

scoreboard players operation default.math.root.lo tbms.var *= c10 tbms.const
scoreboard players set default.math.root._1 tbms.var 100
scoreboard players operation default.math.root._2 tbms.var = default.math.root.lo tbms.var
scoreboard players operation default.math.root._2 tbms.var /= c1000 tbms.const
scoreboard players operation default.standard.int.pow.x tbms.var = default.math.root._2 tbms.var
scoreboard players set default.standard.int.pow.n tbms.var 2
scoreboard players set default.standard.int.pow.m tbms.var 1
function default:standard/int/pow
scoreboard players operation default.math.root._2 tbms.var = default.standard.int.pow._ret tbms.var
scoreboard players operation default.math.root._1 tbms.var *= default.math.root._2 tbms.var
scoreboard players operation default.math.root._3 tbms.var = default.math.root._1 tbms.var
scoreboard players operation default.math.root._3 tbms.var *= c1000 tbms.const
execute if score default.math.root.n tbms.var < default.math.root._3 tbms.var run function default:zzz_sl_block/92
