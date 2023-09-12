# ==================================================
# void default.zzz_sl_block.2()
# a.k.a default.math.log._0.1
# ==================================================

scoreboard players operation default.math.log._0._0._0 tbms.var = default.math.log.sign tbms.var
scoreboard players operation default.math.log._0._0._0 tbms.var *= default.math.log.pow tbms.var
scoreboard players operation default.math.log._0._0._0 tbms.var /= c1000 tbms.const
scoreboard players operation default.math.log._0._0._0 tbms.var /= default.math.log.fact tbms.var
scoreboard players operation default.math.log._0._0._0 tbms.var *= c1000 tbms.const
scoreboard players operation default.math.log.res tbms.var += default.math.log._0._0._0 tbms.var
scoreboard players operation default.math.log.sign tbms.var *= c-1 tbms.const
scoreboard players set default.math.log._0._0._1 tbms.var 2000
scoreboard players operation default.math.log._0._0._1 tbms.var *= default.math.log._0.i tbms.var
scoreboard players add default.math.log._0._0._1 tbms.var 1000
scoreboard players operation default.math.log.fact tbms.var *= default.math.log._0._0._1 tbms.var
scoreboard players operation default.math.log.fact tbms.var /= c1000 tbms.const
scoreboard players set default.math.log._0._0._2 tbms.var 2000
scoreboard players operation default.math.log._0._0._2 tbms.var *= default.math.log._0.i tbms.var
scoreboard players add default.math.log._0._0._2 tbms.var 2000
scoreboard players operation default.math.log.fact tbms.var *= default.math.log._0._0._2 tbms.var
scoreboard players operation default.math.log.fact tbms.var /= c1000 tbms.const
scoreboard players operation default.math.log.pow tbms.var *= default.math.log.x tbms.var
scoreboard players operation default.math.log.pow tbms.var /= c1000 tbms.const
scoreboard players add default.math.log._0.i tbms.var 1
execute if score default.math.log._0.i tbms.var matches ..9 run function default:zzz_sl_block/2
