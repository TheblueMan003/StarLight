# ==================================================
# void default.zzz_sl_block.327()
# a.k.a default.int.multi_variable.onStop-0._0.5
# ==================================================

tellraw @a [{"text": "[FAILLED] ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"red"},{"translate":"default.int.multi_variable"}]

scoreboard players add default.test.__fail__ tbms.var 1
