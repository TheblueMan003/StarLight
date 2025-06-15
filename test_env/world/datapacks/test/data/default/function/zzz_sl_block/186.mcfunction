# ==================================================
# void default.zzz_sl_block.186()
# a.k.a default.array.initer.onStop-0._0.3
# ==================================================

scoreboard players set default.array.initer.onStop-0._0._1 tbms.var 1
tellraw @a [{"text": "[PASSED] ", "bold":false,"obfuscated":false,"strikethrough":false,"underlined":false,"italic":false, "color":"green"},{"translate":"default.array.initer"}]
scoreboard players add default.test.__pass__ tbms.var 1
