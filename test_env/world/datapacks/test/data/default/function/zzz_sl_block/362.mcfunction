# ==================================================
# void default.zzz_sl_block.362()
# a.k.a default.standard.string.trimRight.7
# ==================================================

data modify storage default.standard.string.char-at.source json set string storage default.standard.string.trim-right.source json
scoreboard players set default.standard.string.charAt.index tbms.var -1
function default:standard/string/char-at
data modify storage default.standard.string.equals.source json set string storage default.standard.string.char-at._ret json
data modify storage default.standard.string.equals.value json set value " "
function default:standard/string/equals
execute if score default.standard.string.trimRight.c tbms.var matches ..999 unless score default.standard.string.equals._ret tbms.var matches 0 run scoreboard players set default.standard.string.trimRight._6 tbms.var 1
