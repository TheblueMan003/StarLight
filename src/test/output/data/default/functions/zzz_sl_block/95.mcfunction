# ==================================================
# void default.zzz_sl_block.95()
# a.k.a default.math.root._14.1
# ==================================================

scoreboard players operation default.math.root.mid tbms.var = default.math.root.lo tbms.var
scoreboard players operation default.math.root.mid tbms.var += default.math.root.hi tbms.var
scoreboard players operation default.math.root.mid tbms.var /= c2 tbms.const
scoreboard players operation default.math.root._14._0._0 tbms.var = default.math.root.mid tbms.var
scoreboard players operation default.math.root._14._0._0 tbms.var /= c1000 tbms.const
scoreboard players operation default.standard.int.pow.x tbms.var = default.math.root._14._0._0 tbms.var
scoreboard players set default.standard.int.pow.n tbms.var 2
scoreboard players set default.standard.int.pow.m tbms.var 1
function default:standard/int/pow
scoreboard players operation default.math.root._14._0._0 tbms.var = default.standard.int.pow._ret tbms.var
scoreboard players operation default.math.root._14._0._1 tbms.var = default.math.root._14._0._0 tbms.var
scoreboard players operation default.math.root._14._0._1 tbms.var *= c1000 tbms.const
execute if score default.math.root.n tbms.var = default.math.root._14._0._1 tbms.var run scoreboard players set default.math.root.ret tbms.var 1
scoreboard players set default.math.root._14._0._5 tbms.var 0
scoreboard players operation default.math.root._14._0._3 tbms.var = default.math.root.mid tbms.var
scoreboard players operation default.math.root._14._0._3 tbms.var /= c1000 tbms.const
scoreboard players operation default.standard.int.pow.x tbms.var = default.math.root._14._0._3 tbms.var
scoreboard players set default.standard.int.pow.n tbms.var 2
scoreboard players set default.standard.int.pow.m tbms.var 1
function default:standard/int/pow
scoreboard players operation default.math.root._14._0._3 tbms.var = default.standard.int.pow._ret tbms.var
scoreboard players operation default.math.root._14._0._4 tbms.var = default.math.root._14._0._3 tbms.var
scoreboard players operation default.math.root._14._0._4 tbms.var *= c1000 tbms.const
execute if score default.math.root.n tbms.var > default.math.root._14._0._4 tbms.var run function default:zzz_sl_block/94
execute if score default.math.root._14._0._5 tbms.var matches 0 run scoreboard players operation default.math.root.lo tbms.var = default.math.root.mid tbms.var
scoreboard players add default.math.root._14.i tbms.var 1
execute if score default.math.root.ret tbms.var matches 0 if score default.math.root._14.i tbms.var matches ..99 run function default:zzz_sl_block/95
