# ==================================================
# float default.math.root(float n)
# ==================================================

scoreboard players set default.math.min---444556297.impl.a tbms.var 1000
scoreboard players operation default.math.min---444556297.impl.b tbms.var = default.math.root.n tbms.var
scoreboard players operation default.math.min---444556297.impl.b tbms.var < default.math.min---444556297.impl.a tbms.var
scoreboard players operation default.math.min---444556297.impl._ret tbms.var = default.math.min---444556297.impl.b tbms.var
scoreboard players operation default.math.root.lo tbms.var = default.math.min---444556297.impl._ret tbms.var
scoreboard players set default.math.max---444556297.impl.a tbms.var 1000
scoreboard players operation default.math.max---444556297.impl.b tbms.var = default.math.root.n tbms.var
scoreboard players operation default.math.max---444556297.impl.b tbms.var > default.math.max---444556297.impl.a tbms.var
scoreboard players operation default.math.max---444556297.impl._ret tbms.var = default.math.max---444556297.impl.b tbms.var
scoreboard players operation default.math.root.hi tbms.var = default.math.max---444556297.impl._ret tbms.var
scoreboard players set default.math.root.mid tbms.var 0
scoreboard players set default.math.root._2 tbms.var 100000
scoreboard players operation default.math.root._2 tbms.var *= default.math.root.lo tbms.var
scoreboard players operation default.math.root._2 tbms.var /= c1000 tbms.const
scoreboard players operation default.math.root._2 tbms.var *= default.math.root.lo tbms.var
scoreboard players operation default.math.root._2 tbms.var /= c1000 tbms.const
execute if score default.math.root.n tbms.var < default.math.root._2 tbms.var run function default:zzz_sl_block/462
scoreboard players set default.math.root._5 tbms.var 100000
scoreboard players operation default.math.root._5 tbms.var *= default.math.root.hi tbms.var
scoreboard players operation default.math.root._5 tbms.var /= c1000 tbms.const
scoreboard players operation default.math.root._5 tbms.var *= default.math.root.hi tbms.var
scoreboard players operation default.math.root._5 tbms.var /= c1000 tbms.const
execute if score default.math.root.n tbms.var > default.math.root._5 tbms.var run function default:zzz_sl_block/463
scoreboard players set default.math.root.ret tbms.var 0
scoreboard players set default.math.root._6.i tbms.var 0
execute if score default.math.root.ret tbms.var matches 0 if score default.math.root._6.i tbms.var matches ..99 run function default:zzz_sl_block/465
