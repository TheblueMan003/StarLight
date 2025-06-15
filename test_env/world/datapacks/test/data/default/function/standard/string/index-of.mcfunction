# ==================================================
# int default.standard.string.indexOf(string source, string value)
# ==================================================
# ==================================================
#     Return the index of `value` in `source`.    
# ==================================================

scoreboard players set default.standard.string.indexOf.ret tbms.var -1
scoreboard players set default.standard.string.indexOf.i tbms.var 0
execute if score default.standard.string.indexOf.ret tbms.var matches -1 unless data storage default.standard.string.index-of.source {json:""} run function default:zzz_sl_block/369
