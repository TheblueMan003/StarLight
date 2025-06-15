# ==================================================
# void default.zzz_sl_block.370()
# a.k.a default.standard.string.charAt.1
# ==================================================

data modify storage default.standard.string.length.value json set string storage default.standard.string.char-at.source json
execute store result score default.standard.string.length._ret tbms.var run data get storage default.standard.string.length.value json
scoreboard players operation default.standard.string.charAt._0._0 tbms.var = default.standard.string.length._ret tbms.var
scoreboard players operation default.standard.string.charAt._0._0 tbms.var += default.standard.string.charAt.index tbms.var
scoreboard players operation default.standard.string.charAt.index tbms.var = default.standard.string.charAt._0._0 tbms.var
