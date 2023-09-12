# ==================================================
# float default.math.root(float n)
# ==================================================

scoreboard players set default.math.min---444556297.impl.a tbms.var 1000
scoreboard players operation default.math.min---444556297.impl.b tbms.var = default.math.root.n tbms.var
function default:zzz_sl_block/90
scoreboard players operation default.math.root.lo tbms.var = default.math.min---444556297.impl._ret tbms.var
scoreboard players set default.math.max---444556297.impl.a tbms.var 1000
scoreboard players operation default.math.max---444556297.impl.b tbms.var = default.math.root.n tbms.var
function default:zzz_sl_block/91
scoreboard players operation default.math.root.hi tbms.var = default.math.max---444556297.impl._ret tbms.var
execute unless score default.math.root.mid tbms.var = default.math.root.mid tbms.var run scoreboard players set default.math.root.mid tbms.var 0
scoreboard players set default.math.root._4 tbms.var 100
scoreboard players operation default.math.root._5 tbms.var = default.math.root.lo tbms.var
scoreboard players operation default.math.root._5 tbms.var /= c1000 tbms.const
scoreboard players operation default.standard.int.pow.x tbms.var = default.math.root._5 tbms.var
scoreboard players set default.standard.int.pow.n tbms.var 2
scoreboard players set default.standard.int.pow.m tbms.var 1
function default:standard/int/pow
scoreboard players operation default.math.root._5 tbms.var = default.standard.int.pow._ret tbms.var
scoreboard players operation default.math.root._4 tbms.var *= default.math.root._5 tbms.var
scoreboard players operation default.math.root._6 tbms.var = default.math.root._4 tbms.var
scoreboard players operation default.math.root._6 tbms.var *= c1000 tbms.const
execute if score default.math.root.n tbms.var < default.math.root._6 tbms.var run function default:zzz_sl_block/92
scoreboard players set default.math.root._11 tbms.var 100
scoreboard players operation default.math.root._12 tbms.var = default.math.root.hi tbms.var
scoreboard players operation default.math.root._12 tbms.var /= c1000 tbms.const
scoreboard players operation default.standard.int.pow.x tbms.var = default.math.root._12 tbms.var
scoreboard players set default.standard.int.pow.n tbms.var 2
scoreboard players set default.standard.int.pow.m tbms.var 1
function default:standard/int/pow
scoreboard players operation default.math.root._12 tbms.var = default.standard.int.pow._ret tbms.var
scoreboard players operation default.math.root._11 tbms.var *= default.math.root._12 tbms.var
scoreboard players operation default.math.root._13 tbms.var = default.math.root._11 tbms.var
scoreboard players operation default.math.root._13 tbms.var *= c1000 tbms.const
execute if score default.math.root.n tbms.var > default.math.root._13 tbms.var run function default:zzz_sl_block/93
scoreboard players set default.math.root.ret tbms.var 0
scoreboard players set default.math.root._14.i tbms.var 0
execute if score default.math.root.ret tbms.var matches 0 if score default.math.root._14.i tbms.var matches ..99 run function default:zzz_sl_block/95
scoreboard players operation default.math.root._ret tbms.var = default.math.root.mid tbms.var
