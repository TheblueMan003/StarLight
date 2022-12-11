scoreboard players operation default.math.root.lo tbms.var *= 10 tbms.const
scoreboard players set default.math.root._0 tbms.var 100000
scoreboard players operation default.math.root._0 tbms.var *= default.math.root.lo tbms.var
scoreboard players operation default.math.root._0 tbms.var /= 1000 tbms.const
scoreboard players operation default.math.root._0 tbms.var *= default.math.root.lo tbms.var
scoreboard players operation default.math.root._0 tbms.var /= 1000 tbms.const
execute if score default.math.root._0 tbms.var < default.math.root.n tbms.var run function default/zzz_sl_block/_9
