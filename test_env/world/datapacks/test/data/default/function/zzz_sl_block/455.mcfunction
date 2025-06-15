# ==================================================
# void default.zzz_sl_block.455()
# a.k.a default.math.pow.8
# ==================================================

scoreboard players operation default.math.pow._7.parity tbms.var = default.math.pow.n tbms.var
scoreboard players operation default.math.pow._7.parity tbms.var %= c2 tbms.const
execute if score default.math.pow._7.parity tbms.var matches 1 run function default:zzz_sl_block/454
scoreboard players operation default.math.pow.n tbms.var /= c2 tbms.const
scoreboard players operation default.math.pow.x tbms.var = default.math.pow.x tbms.var
scoreboard players operation default.math.pow.n tbms.var = default.math.pow.n tbms.var
scoreboard players operation default.math.pow.m tbms.var = default.math.pow.m tbms.var
scoreboard players set default.math.pow._0 tbms.var 0
execute if score default.math.pow.n tbms.var matches ..-1 run function default:zzz_sl_block/451
execute if score default.math.pow.n tbms.var matches 0 if score default.math.pow._0 tbms.var matches 0 run function default:zzz_sl_block/452
execute if score default.math.pow.n tbms.var matches 1 if score default.math.pow._0 tbms.var matches 0 run function default:zzz_sl_block/453
execute if score default.math.pow._0 tbms.var matches 0 run function default:zzz_sl_block/455
scoreboard players operation default.math.pow._ret tbms.var = default.math.pow._ret tbms.var
