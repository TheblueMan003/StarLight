# ==================================================
# void default.zzz_sl_block.80()
# a.k.a default.int.not_neq_float.onStop-0._0.5
# ==================================================

tellraw @a [{"text": "[FAILLED] ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"red"},{"translate":"default.int.not_neq_float"}]

scoreboard players add default.test.__fail__ tbms.var 1
