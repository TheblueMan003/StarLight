# ==================================================
# string default.standard.string.toUpper(string source)
# ==================================================
# ==================================================
#     Make the string uppercase.    
# ==================================================

data modify storage default.standard.string.to-upper.ret json set value ""
scoreboard players set default.standard.string.toUpper.c tbms.var 0
execute unless data storage default.standard.string.to-upper.source {json:""} run function default:zzz_sl_block/367
data modify storage default.standard.string.to-upper._ret json set string storage default.standard.string.to-upper.ret json
