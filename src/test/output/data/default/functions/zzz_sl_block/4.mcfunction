# ==================================================
# void default.zzz_sl_block.4()
# a.k.a default.math.asin._0.1
# ==================================================

scoreboard players operation default.math.asin._0._0._0 tbms.var = default.math.asin.sign tbms.var
scoreboard players operation default.math.asin._0._0._0 tbms.var *= default.math.asin.pow tbms.var
scoreboard players operation default.math.asin._0._0._0 tbms.var /= c1000 tbms.const
scoreboard players operation default.math.asin._0._0._0 tbms.var /= default.math.asin.fact tbms.var
scoreboard players operation default.math.asin._0._0._0 tbms.var *= c1000 tbms.const
scoreboard players operation default.math.asin.res tbms.var += default.math.asin._0._0._0 tbms.var
scoreboard players operation default.math.asin.sign tbms.var *= c-1 tbms.const
scoreboard players set default.math.asin._0._0._1 tbms.var 2000
scoreboard players operation default.math.asin._0._0._1 tbms.var *= default.math.asin._0.i tbms.var
scoreboard players add default.math.asin._0._0._1 tbms.var 1000
scoreboard players operation default.math.asin.fact tbms.var *= default.math.asin._0._0._1 tbms.var
scoreboard players operation default.math.asin.fact tbms.var /= c1000 tbms.const
scoreboard players set default.math.asin._0._0._2 tbms.var 2000
scoreboard players operation default.math.asin._0._0._2 tbms.var *= default.math.asin._0.i tbms.var
scoreboard players add default.math.asin._0._0._2 tbms.var 2000
scoreboard players operation default.math.asin.fact tbms.var *= default.math.asin._0._0._2 tbms.var
scoreboard players operation default.math.asin.fact tbms.var /= c1000 tbms.const
scoreboard players operation default.math.asin.pow tbms.var *= default.math.asin.angle tbms.var
scoreboard players operation default.math.asin.pow tbms.var /= c1000 tbms.const
scoreboard players operation default.math.asin.pow tbms.var *= default.math.asin.angle tbms.var
scoreboard players operation default.math.asin.pow tbms.var /= c1000 tbms.const
scoreboard players add default.math.asin._0.i tbms.var 1
execute if score default.math.asin._0.i tbms.var matches ..9 run function default:zzz_sl_block/4
