# ==================================================
# void default.zzz_sl_block.383()
# a.k.a default.standard.int.pow.6
# ==================================================

scoreboard players operation default.standard.int.pow._5._0 tbms.var = default.standard.int.pow.n tbms.var
scoreboard players operation default.standard.int.pow._5._0 tbms.var %= c2 tbms.const
scoreboard players set default.standard.int.pow._5._1 tbms.var 1
execute if score default.standard.int.pow._5._0 tbms.var = default.standard.int.pow._5._1 tbms.var run function default:zzz_sl_block/382
scoreboard players operation default.standard.int.pow.n tbms.var /= c2 tbms.const
scoreboard players operation default.standard.int.pow.x tbms.var = default.standard.int.pow.x tbms.var
scoreboard players operation default.standard.int.pow.n tbms.var = default.standard.int.pow.n tbms.var
scoreboard players operation default.standard.int.pow.m tbms.var = default.standard.int.pow.m tbms.var
function default:standard/int/pow
scoreboard players operation default.standard.int.pow._ret tbms.var = default.standard.int.pow._ret tbms.var
