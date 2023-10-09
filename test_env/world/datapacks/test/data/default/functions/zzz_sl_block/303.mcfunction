# ==================================================
# void default.zzz_sl_block.303()
# a.k.a default.int.lt_int.onStop-0._0.5
# ==================================================

tellraw @a [{"text": "[FAILLED] ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"red"},{"translate":"default.int.lt_int"}]

scoreboard players add default.test.__fail__ tbms.var 1
