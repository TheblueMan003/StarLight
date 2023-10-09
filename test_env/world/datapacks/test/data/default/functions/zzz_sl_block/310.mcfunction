# ==================================================
# void default.zzz_sl_block.310()
# a.k.a default.int.sub_value.onStop-0._0.3
# ==================================================

scoreboard players set default.int.sub_value.onStop-0._0._1 tbms.var 1
tellraw @a [{"text": "[PASSED] ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"green"},{"translate":"default.int.sub_value"}]
scoreboard players add default.test.__pass__ tbms.var 1
