# ==================================================
# void default.zzz_sl_block.87()
# a.k.a default.int.not_eq_float.onStop-0._0.3
# ==================================================

scoreboard players set default.int.not_eq_float.onStop-0._0._1 tbms.var 1
tellraw @a [{"text": "[PASSED] ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"green"},{"translate":"default.int.not_eq_float"}]
scoreboard players add default.test.__pass__ tbms.var 1