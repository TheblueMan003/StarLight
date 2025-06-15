# ==================================================
# int default.standard.int.parse(string s)
# ==================================================
# ==================================================
#     Convert string to int, ignore any non-digit characters.    
# ==================================================

data modify storage default.standard.string.equals.source json set string storage default.standard.int.parse.s json 0 1
data modify storage default.standard.string.equals.value json set value "-"
function default:standard/string/equals
execute unless score default.standard.string.equals._ret tbms.var matches 0 run data modify storage default.standard.int.parse.s json set string storage default.standard.int.parse.s json 1
execute unless data storage default.standard.int.parse.s {json:""} run function default:zzz_sl_block/322
