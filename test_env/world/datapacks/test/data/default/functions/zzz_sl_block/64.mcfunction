# ==================================================
# void default.zzz_sl_block.64()
# a.k.a default.int.not_gte_float.onStop-0._0.5
# ==================================================

tellraw @a [{"text": "[FAILLED] ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"red"},{"translate":"default.int.not_gte_float"}]

scoreboard players add default.test.__fail__ tbms.var 1