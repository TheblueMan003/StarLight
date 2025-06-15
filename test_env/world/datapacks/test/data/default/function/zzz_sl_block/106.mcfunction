# ==================================================
# void default.zzz_sl_block.106()
# a.k.a default.array.sugar.onStop-0._0.5
# ==================================================

tellraw @a [{"text": "[FAILLED] ", "bold":false,"obfuscated":false,"strikethrough":false,"underlined":false,"italic":false, "color":"red"},{"translate":"default.array.sugar"}]

scoreboard players add default.test.__fail__ tbms.var 1
