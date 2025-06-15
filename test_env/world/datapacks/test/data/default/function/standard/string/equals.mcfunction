# ==================================================
# bool default.standard.string.equals(string source, string value)
# ==================================================
# ==================================================
#     Check if two strings are equal.    
# ==================================================

execute store success score default.standard.string.equals._ret tbms.var run data modify storage default.standard.string.equals.source json set string storage default.standard.string.equals.value json
scoreboard players set default.standard.string.equals._1 tbms.var 1
execute unless score default.standard.string.equals._ret tbms.var matches 0 run scoreboard players set default.standard.string.equals._1 tbms.var 0
scoreboard players operation default.standard.string.equals._ret tbms.var = default.standard.string.equals._1 tbms.var
