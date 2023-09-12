# ==================================================
# void default.zzz_sl_block.83()
# a.k.a default.math.pow.6
# ==================================================

scoreboard players set default.math.pow._0 tbms.var 1
scoreboard players operation default.math.pow._5.ret tbms.var = default.math.pow.x tbms.var
scoreboard players operation default.math.pow._5.ret tbms.var *= default.math.pow.m tbms.var
scoreboard players operation default.math.pow._5.ret tbms.var /= c1000 tbms.const
scoreboard players operation default.math.pow._ret tbms.var = default.math.pow._5.ret tbms.var
