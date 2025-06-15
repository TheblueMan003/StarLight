# ==================================================
# string default.standard.string.trim(string source)
# ==================================================
# ==================================================
#     Trim whitespace from both sides of the string.    
# ==================================================

data modify storage default.standard.string.trim-right.source json set string storage default.standard.string.trim.source json
scoreboard players set default.standard.string.trimRight.c tbms.var 0
scoreboard players set default.standard.string.trimRight._6 tbms.var 0
execute unless data storage default.standard.string.trim-right.source {json:""} run function default:zzz_sl_block/362
execute unless score default.standard.string.trimRight._6 tbms.var matches 0 run function default:zzz_sl_block/361
data modify storage default.standard.string.trim-right._ret json set string storage default.standard.string.trim-right.source json
data modify storage default.standard.string.trim-left.source json set string storage default.standard.string.trim-right._ret json
scoreboard players set default.standard.string.trimLeft.c tbms.var 0
scoreboard players set default.standard.string.trimLeft._6 tbms.var 0
execute unless data storage default.standard.string.trim-left.source {json:""} run function default:zzz_sl_block/365
execute unless score default.standard.string.trimLeft._6 tbms.var matches 0 run function default:zzz_sl_block/364
data modify storage default.standard.string.trim-left._ret json set string storage default.standard.string.trim-left.source json
data modify storage default.standard.string.trim._ret json set string storage default.standard.string.trim-left._ret json
