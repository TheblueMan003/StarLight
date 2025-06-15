# ==================================================
# string default.object.--toString()
# ==================================================

scoreboard players operation default.zzz_sl_mux.void___to___string.__fct__ tbms.var = @s default.object.---toString
execute if score default.zzz_sl_mux.void___to___string.__fct__ tbms.var matches -838399586 run function default:zzz_sl_block/477
data modify storage default.object.--to-string._ret json set string storage default.zzz_sl_mux.void___to___string._ret json
