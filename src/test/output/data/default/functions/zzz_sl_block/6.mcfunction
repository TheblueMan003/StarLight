# ==================================================
# void default.zzz_sl_block.6()
# a.k.a default.math.sin._0.1
# ==================================================

scoreboard players operation default.math.sin._0._0._0 tbms.var = default.math.sin.sign tbms.var
scoreboard players operation default.math.sin._0._0._0 tbms.var *= default.math.sin.pow tbms.var
scoreboard players operation default.math.sin._0._0._0 tbms.var /= c1000 tbms.const
scoreboard players operation default.math.sin._0._0._0 tbms.var /= default.math.sin.fact tbms.var
scoreboard players operation default.math.sin._0._0._0 tbms.var *= c1000 tbms.const
scoreboard players operation default.math.sin.res tbms.var += default.math.sin._0._0._0 tbms.var
scoreboard players operation default.math.sin.sign tbms.var *= c-1 tbms.const
scoreboard players set default.math.sin._0._0._1 tbms.var 2000
scoreboard players operation default.math.sin._0._0._1 tbms.var *= default.math.sin._0.i tbms.var
scoreboard players add default.math.sin._0._0._1 tbms.var 2000
scoreboard players operation default.math.sin.fact tbms.var *= default.math.sin._0._0._1 tbms.var
scoreboard players operation default.math.sin.fact tbms.var /= c1000 tbms.const
scoreboard players set default.math.sin._0._0._2 tbms.var 2000
scoreboard players operation default.math.sin._0._0._2 tbms.var *= default.math.sin._0.i tbms.var
scoreboard players add default.math.sin._0._0._2 tbms.var 3000
scoreboard players operation default.math.sin.fact tbms.var *= default.math.sin._0._0._2 tbms.var
scoreboard players operation default.math.sin.fact tbms.var /= c1000 tbms.const
scoreboard players operation default.math.sin.pow tbms.var *= default.math.sin.angleRad tbms.var
scoreboard players operation default.math.sin.pow tbms.var /= c1000 tbms.const
scoreboard players operation default.math.sin.pow tbms.var *= default.math.sin.angleRad tbms.var
scoreboard players operation default.math.sin.pow tbms.var /= c1000 tbms.const
scoreboard players add default.math.sin._0.i tbms.var 1
execute if score default.math.sin._0.i tbms.var matches ..9 run function default:zzz_sl_block/6
