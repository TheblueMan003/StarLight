# ==================================================
# string default.standard.string.trimLeft(string source)
# ==================================================
# ==================================================
#     Trim whitespace from the left side of the string.    
# ==================================================

scoreboard players set default.standard.string.trimLeft.c tbms.var 0
scoreboard players set default.standard.string.trimLeft._6 tbms.var 0
execute unless data storage default.standard.string.trim-left.source {json:""} run function default:zzz_sl_block/365
execute unless score default.standard.string.trimLeft._6 tbms.var matches 0 run function default:zzz_sl_block/364
data modify storage default.standard.string.trim-left._ret json set string storage default.standard.string.trim-left.source json
