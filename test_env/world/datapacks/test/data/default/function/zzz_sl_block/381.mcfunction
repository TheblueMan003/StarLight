# ==================================================
# void default.zzz_sl_block.381()
# a.k.a default.standard.int.pow.4
# ==================================================

scoreboard players set default.standard.int.pow._0 tbms.var 1
scoreboard players operation default.standard.int.pow._3.ret tbms.var = default.standard.int.pow.x tbms.var
scoreboard players operation default.standard.int.pow._3.ret tbms.var *= default.standard.int.pow.m tbms.var
scoreboard players operation default.standard.int.pow._ret tbms.var = default.standard.int.pow._3.ret tbms.var
