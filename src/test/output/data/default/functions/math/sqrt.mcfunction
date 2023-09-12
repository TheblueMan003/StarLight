# ==================================================
# float default.math.sqrt(float value)
# ==================================================
# ==================================================
# return squart root of value
# ==================================================

scoreboard players set default.math.sqrt.__hasFunctionReturned__ tbms.var 0
execute if score default.math.sqrt.value tbms.var matches ..-1 run function default:zzz_sl_block/97
execute if score default.math.sqrt.__hasFunctionReturned__ tbms.var matches 0 run function default:zzz_sl_block/99
