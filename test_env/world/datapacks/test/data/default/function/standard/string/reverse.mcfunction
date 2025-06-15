# ==================================================
# string default.standard.string.reverse(string source)
# ==================================================
# ==================================================
#     Reverse a string.    
# ==================================================

data modify storage default.standard.string.reverse.ret json set value ""
scoreboard players set default.standard.string.reverse.c tbms.var 0
execute unless data storage default.standard.string.reverse.source {json:""} run function default:zzz_sl_block/376
data modify storage default.standard.string.reverse._ret json set string storage default.standard.string.reverse.ret json
