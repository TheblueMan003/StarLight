# ==================================================
# void default.zzz_sl_block.203()
# a.k.a default.utils.process_manager.show.4
# ==================================================

scoreboard players set default.utils.process_manager.show._0 tbms.var 1
tellraw @a [{"text": " [OFF] ", "bold":false,"obfuscated":false,"strikethrough":false,"underlined":false,"italic":false, "color":"red"},{"translate":"default.array.sugar", "with":[{"text": "red", "bold":false,"obfuscated":false,"strikethrough":false,"underlined":false,"italic":false, "color":"white"}]}]
scoreboard players add default.utils.process_manager.show.off tbms.var 1
