# ==================================================
# void default.zzz_sl_block.184()
# a.k.a default.int.not_addition_variable.onStop-0._0.5
# ==================================================

tellraw @a [{"text": "[FAILLED] ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"red"},{"translate":"default.int.not_addition_variable"}]

scoreboard players add default.test.__fail__ tbms.var 1
