# ==================================================
# string default.standard.string.trimRight(string source)
# ==================================================
# ==================================================
#     Trim whitespace from the right side of the string.    
# ==================================================

scoreboard players set default.standard.string.trimRight.c tbms.var 0
scoreboard players set default.standard.string.trimRight._6 tbms.var 0
execute unless data storage default.standard.string.trim-right.source {json:""} run function default:zzz_sl_block/362
execute unless score default.standard.string.trimRight._6 tbms.var matches 0 run function default:zzz_sl_block/361
data modify storage default.standard.string.trim-right._ret json set string storage default.standard.string.trim-right.source json
