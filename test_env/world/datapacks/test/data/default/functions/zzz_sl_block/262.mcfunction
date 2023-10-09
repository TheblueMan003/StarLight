# ==================================================
# void default.zzz_sl_block.262()
# a.k.a default.int.gte_int.onStop-0._0.3
# ==================================================

scoreboard players set default.int.gte_int.onStop-0._0._1 tbms.var 1
tellraw @a [{"text": "[PASSED] ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"green"},{"translate":"default.int.gte_int"}]
scoreboard players add default.test.__pass__ tbms.var 1
