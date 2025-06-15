# ==================================================
# bool default.standard.string.contains(string source, string value)
# ==================================================
# ==================================================
#     Check if a string contains another string.    
# ==================================================

scoreboard players set default.standard.string.contains.ret tbms.var 0
scoreboard players set default.standard.string.contains.c tbms.var 0
execute if score default.standard.string.contains.ret tbms.var matches 0 unless data storage default.standard.string.contains.source {json:""} run function default:zzz_sl_block/378
