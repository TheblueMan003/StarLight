# ==================================================
# void default.test.A.test()
# ==================================================

scoreboard players operation default.zzz_sl_mux._0 tbms.var = @s default.test.A.b
execute as @e[tag=--class.default.test.B] if score default.zzz_sl_mux._0 tbms.var = @s default.object.__ref run function default:object/__rem-ref
scoreboard players operation @s default.test.A.b = default.zzz_sl_mux.void___to___default.test.B._ret tbms.var
scoreboard players operation default.zzz_sl_mux._2 tbms.var = @s default.test.A.b
execute as @e[tag=--class.default.test.B] if score default.zzz_sl_mux._2 tbms.var = @s default.object.__ref if score @s default.object.__refCount matches 0.. run scoreboard players add @s default.object.__refCount 1
