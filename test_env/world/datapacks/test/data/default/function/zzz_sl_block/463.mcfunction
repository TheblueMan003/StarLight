# ==================================================
# void default.zzz_sl_block.463()
# a.k.a default.math.root.3
# ==================================================

scoreboard players operation default.math.root.hi tbms.var *= c100 tbms.const
scoreboard players operation default.math.root.hi tbms.var /= c1000 tbms.const
scoreboard players set default.math.root._4 tbms.var 100000
scoreboard players operation default.math.root._4 tbms.var *= default.math.root.hi tbms.var
scoreboard players operation default.math.root._4 tbms.var /= c1000 tbms.const
scoreboard players operation default.math.root._4 tbms.var *= default.math.root.hi tbms.var
scoreboard players operation default.math.root._4 tbms.var /= c1000 tbms.const
execute if score default.math.root.n tbms.var > default.math.root._4 tbms.var run function default:zzz_sl_block/463
