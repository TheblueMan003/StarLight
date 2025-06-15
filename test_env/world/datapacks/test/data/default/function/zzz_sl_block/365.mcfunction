# ==================================================
# void default.zzz_sl_block.365()
# a.k.a default.standard.string.trimLeft.7
# ==================================================

data modify storage default.standard.string.equals.source json set string storage default.standard.string.trim-left.source json 0 1
data modify storage default.standard.string.equals.value json set value " "
function default:standard/string/equals
execute if score default.standard.string.trimLeft.c tbms.var matches ..999 unless score default.standard.string.equals._ret tbms.var matches 0 run scoreboard players set default.standard.string.trimLeft._6 tbms.var 1
