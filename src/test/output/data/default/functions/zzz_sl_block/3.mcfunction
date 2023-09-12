# ==================================================
# void default.zzz_sl_block.3()
# a.k.a default.math.sqrt2._0.1
# ==================================================

scoreboard players operation default.math.sqrt2._0._0._0 tbms.var = default.math.sqrt2.sign tbms.var
scoreboard players operation default.math.sqrt2._0._0._0 tbms.var *= default.math.sqrt2.pow tbms.var
scoreboard players operation default.math.sqrt2._0._0._0 tbms.var /= c1000 tbms.const
scoreboard players operation default.math.sqrt2._0._0._0 tbms.var /= default.math.sqrt2.fact tbms.var
scoreboard players operation default.math.sqrt2._0._0._0 tbms.var *= c1000 tbms.const
scoreboard players operation default.math.sqrt2.res tbms.var += default.math.sqrt2._0._0._0 tbms.var
scoreboard players operation default.math.sqrt2.sign tbms.var *= c-1 tbms.const
scoreboard players set default.math.sqrt2._0._0._1 tbms.var 2000
scoreboard players operation default.math.sqrt2._0._0._1 tbms.var *= default.math.sqrt2._0.i tbms.var
scoreboard players add default.math.sqrt2._0._0._1 tbms.var 1000
scoreboard players operation default.math.sqrt2.fact tbms.var *= default.math.sqrt2._0._0._1 tbms.var
scoreboard players operation default.math.sqrt2.fact tbms.var /= c1000 tbms.const
scoreboard players set default.math.sqrt2._0._0._2 tbms.var 2000
scoreboard players operation default.math.sqrt2._0._0._2 tbms.var *= default.math.sqrt2._0.i tbms.var
scoreboard players add default.math.sqrt2._0._0._2 tbms.var 2000
scoreboard players operation default.math.sqrt2.fact tbms.var *= default.math.sqrt2._0._0._2 tbms.var
scoreboard players operation default.math.sqrt2.fact tbms.var /= c1000 tbms.const
scoreboard players operation default.math.sqrt2.pow tbms.var *= default.math.sqrt2.value tbms.var
scoreboard players operation default.math.sqrt2.pow tbms.var /= c1000 tbms.const
scoreboard players add default.math.sqrt2._0.i tbms.var 1
execute if score default.math.sqrt2._0.i tbms.var matches ..9 run function default:zzz_sl_block/3
