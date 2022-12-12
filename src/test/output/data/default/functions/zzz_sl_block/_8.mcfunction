scoreboard players operation default.math.root.hi tbms.var *= 100 tbms.const
scoreboard players operation default.math.root.hi tbms.var /= 1000 tbms.const
scoreboard players set default.math.root._2 tbms.var 100000
scoreboard players operation default.math.root._2 tbms.var *= default.math.root.hi tbms.var
scoreboard players operation default.math.root._2 tbms.var /= 1000 tbms.const
scoreboard players operation default.math.root._2 tbms.var *= default.math.root.hi tbms.var
scoreboard players operation default.math.root._2 tbms.var /= 1000 tbms.const
execute if score default.math.root._2 tbms.var > default.math.root.n tbms.var run function default/zzz_sl_block/_8
