# ==================================================
# void default.zzz_sl_block.462()
# a.k.a default.math.root.0
# ==================================================

scoreboard players operation default.math.root.lo tbms.var *= c10 tbms.const
scoreboard players set default.math.root._1 tbms.var 100000
scoreboard players operation default.math.root._1 tbms.var *= default.math.root.lo tbms.var
scoreboard players operation default.math.root._1 tbms.var /= c1000 tbms.const
scoreboard players operation default.math.root._1 tbms.var *= default.math.root.lo tbms.var
scoreboard players operation default.math.root._1 tbms.var /= c1000 tbms.const
execute if score default.math.root.n tbms.var < default.math.root._1 tbms.var run function default:zzz_sl_block/462
