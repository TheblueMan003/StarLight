# ==================================================
# string default.standard.string.multiply(string value, int count)
# ==================================================
# ==================================================
#     Repeat a string a number of times.    
# ==================================================

data modify storage default.standard.string.multiply.result json set value ""
scoreboard players set default.standard.string.multiply._0.i tbms.var 0
execute if score default.standard.string.multiply._0.i tbms.var < default.standard.string.multiply.count tbms.var run function default:zzz_sl_block/379
data modify storage default.standard.string.multiply._ret json set string storage default.standard.string.multiply.result json
