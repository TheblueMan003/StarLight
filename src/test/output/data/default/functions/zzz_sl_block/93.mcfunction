# ==================================================
# void default.zzz_sl_block.93()
# a.k.a default.math.root.7
# ==================================================

scoreboard players operation default.math.root.hi tbms.var *= c100 tbms.const
scoreboard players operation default.math.root.hi tbms.var /= c1000 tbms.const
scoreboard players set default.math.root._8 tbms.var 100
scoreboard players operation default.math.root._9 tbms.var = default.math.root.hi tbms.var
scoreboard players operation default.math.root._9 tbms.var /= c1000 tbms.const
scoreboard players operation default.standard.int.pow.x tbms.var = default.math.root._9 tbms.var
scoreboard players set default.standard.int.pow.n tbms.var 2
scoreboard players set default.standard.int.pow.m tbms.var 1
function default:standard/int/pow
scoreboard players operation default.math.root._9 tbms.var = default.standard.int.pow._ret tbms.var
scoreboard players operation default.math.root._8 tbms.var *= default.math.root._9 tbms.var
scoreboard players operation default.math.root._10 tbms.var = default.math.root._8 tbms.var
scoreboard players operation default.math.root._10 tbms.var *= c1000 tbms.const
execute if score default.math.root.n tbms.var > default.math.root._10 tbms.var run function default:zzz_sl_block/93
