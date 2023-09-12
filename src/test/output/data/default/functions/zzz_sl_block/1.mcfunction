# ==================================================
# void default.zzz_sl_block.1()
# a.k.a default.math.exp._0.1
# ==================================================

scoreboard players operation default.math.exp._0._0._0 tbms.var = default.math.exp.sign tbms.var
scoreboard players operation default.math.exp._0._0._0 tbms.var *= default.math.exp.pow tbms.var
scoreboard players operation default.math.exp._0._0._0 tbms.var /= c1000 tbms.const
scoreboard players operation default.math.exp._0._0._0 tbms.var /= default.math.exp.fact tbms.var
scoreboard players operation default.math.exp._0._0._0 tbms.var *= c1000 tbms.const
scoreboard players operation default.math.exp.res tbms.var += default.math.exp._0._0._0 tbms.var
scoreboard players operation default.math.exp.sign tbms.var *= c-1 tbms.const
scoreboard players set default.math.exp._0._0._1 tbms.var 2000
scoreboard players operation default.math.exp._0._0._1 tbms.var *= default.math.exp._0.i tbms.var
scoreboard players add default.math.exp._0._0._1 tbms.var 1000
scoreboard players operation default.math.exp.fact tbms.var *= default.math.exp._0._0._1 tbms.var
scoreboard players operation default.math.exp.fact tbms.var /= c1000 tbms.const
scoreboard players set default.math.exp._0._0._2 tbms.var 2000
scoreboard players operation default.math.exp._0._0._2 tbms.var *= default.math.exp._0.i tbms.var
scoreboard players add default.math.exp._0._0._2 tbms.var 2000
scoreboard players operation default.math.exp.fact tbms.var *= default.math.exp._0._0._2 tbms.var
scoreboard players operation default.math.exp.fact tbms.var /= c1000 tbms.const
scoreboard players operation default.math.exp.pow tbms.var *= default.math.exp.x tbms.var
scoreboard players operation default.math.exp.pow tbms.var /= c1000 tbms.const
scoreboard players add default.math.exp._0.i tbms.var 1
execute if score default.math.exp._0.i tbms.var matches ..9 run function default:zzz_sl_block/1
