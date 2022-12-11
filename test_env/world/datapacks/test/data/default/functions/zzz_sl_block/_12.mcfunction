scoreboard players operation default.math.root.mid tbms.var = default.math.root.lo tbms.var
scoreboard players operation default.math.root.mid tbms.var += default.math.root.hi tbms.var
scoreboard players operation default.math.root.mid tbms.var /= 2 tbms.const
scoreboard players operation default.math.root._4 tbms.var = default.math.root.mid tbms.var
scoreboard players operation default.math.root._4 tbms.var *= default.math.root.mid tbms.var
scoreboard players operation default.math.root._4 tbms.var /= 1000 tbms.const
execute if score default.math.root._4 tbms.var = default.math.root.n tbms.var run scoreboard players set default.math.root.ret tbms.var 1
scoreboard players set default.math.root._6 tbms.var 0
scoreboard players operation default.math.root._5 tbms.var = default.math.root.mid tbms.var
scoreboard players operation default.math.root._5 tbms.var *= default.math.root.mid tbms.var
scoreboard players operation default.math.root._5 tbms.var /= 1000 tbms.const
execute if score default.math.root._5 tbms.var > default.math.root.n tbms.var run function default/zzz_sl_block/_11
execute if score default.math.root._6 tbms.var matches 0 run scoreboard players operation default.math.root.lo tbms.var = default.math.root.mid tbms.var
scoreboard players add default.math.root.i tbms.var 1
execute if score default.math.root.i tbms.var matches ..99 if score default.math.root.ret tbms.var matches 0 run function default/zzz_sl_block/_12
