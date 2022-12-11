scoreboard players set default.math.min-0.a tbms.var 1000
scoreboard players operation default.math.min-0.b tbms.var = default.math.root.n tbms.var
function default:math/min-0
scoreboard players operation default.math.root.lo tbms.var = default.math.min-0._ret tbms.var
scoreboard players set default.math.max-0.a tbms.var 1000
scoreboard players operation default.math.max-0.b tbms.var = default.math.root.n tbms.var
function default:math/max-0
scoreboard players operation default.math.root.hi tbms.var = default.math.max-0._ret tbms.var
execute unless score default.math.root.mid tbms.var = default.math.root.mid tbms.var run scoreboard players set default.math.root.mid tbms.var 0
scoreboard players set default.math.root._1 tbms.var 100000
scoreboard players operation default.math.root._1 tbms.var *= default.math.root.lo tbms.var
scoreboard players operation default.math.root._1 tbms.var /= 1000 tbms.const
scoreboard players operation default.math.root._1 tbms.var *= default.math.root.lo tbms.var
scoreboard players operation default.math.root._1 tbms.var /= 1000 tbms.const
execute if score default.math.root._1 tbms.var < default.math.root.n tbms.var run function default/zzz_sl_block/_9
scoreboard players set default.math.root._3 tbms.var 100000
scoreboard players operation default.math.root._3 tbms.var *= default.math.root.hi tbms.var
scoreboard players operation default.math.root._3 tbms.var /= 1000 tbms.const
scoreboard players operation default.math.root._3 tbms.var *= default.math.root.hi tbms.var
scoreboard players operation default.math.root._3 tbms.var /= 1000 tbms.const
execute if score default.math.root._3 tbms.var > default.math.root.n tbms.var run function default/zzz_sl_block/_10
scoreboard players set default.math.root.ret tbms.var 0
scoreboard players set default.math.root.i tbms.var 0
execute if score default.math.root.i tbms.var matches ..99 if score default.math.root.ret tbms.var matches 0 run function default/zzz_sl_block/_12
scoreboard players operation default.math.root._ret tbms.var = default.math.root.mid tbms.var
