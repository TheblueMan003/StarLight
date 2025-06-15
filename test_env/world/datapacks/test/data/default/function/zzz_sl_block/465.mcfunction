# ==================================================
# void default.zzz_sl_block.465()
# a.k.a default.math.root._6.1
# ==================================================

scoreboard players operation default.math.root.mid tbms.var = default.math.root.lo tbms.var
scoreboard players operation default.math.root.mid tbms.var += default.math.root.hi tbms.var
scoreboard players operation default.math.root.mid tbms.var /= c2 tbms.const
scoreboard players operation default.math.root._6._0._0 tbms.var = default.math.root.mid tbms.var
scoreboard players operation default.math.root._6._0._0 tbms.var *= default.math.root.mid tbms.var
scoreboard players operation default.math.root._6._0._0 tbms.var /= c1000 tbms.const
execute if score default.math.root.n tbms.var = default.math.root._6._0._0 tbms.var run scoreboard players set default.math.root.ret tbms.var 1
scoreboard players set default.math.root._6._0._3 tbms.var 0
scoreboard players operation default.math.root._6._0._2 tbms.var = default.math.root.mid tbms.var
scoreboard players operation default.math.root._6._0._2 tbms.var *= default.math.root.mid tbms.var
scoreboard players operation default.math.root._6._0._2 tbms.var /= c1000 tbms.const
execute if score default.math.root.n tbms.var > default.math.root._6._0._2 tbms.var run function default:zzz_sl_block/464
execute if score default.math.root._6._0._3 tbms.var matches 0 run scoreboard players operation default.math.root.lo tbms.var = default.math.root.mid tbms.var
scoreboard players add default.math.root._6.i tbms.var 1
execute if score default.math.root.ret tbms.var matches 0 if score default.math.root._6.i tbms.var matches ..99 run function default:zzz_sl_block/465
