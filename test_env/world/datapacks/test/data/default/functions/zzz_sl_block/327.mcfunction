# ==================================================
# void default.zzz_sl_block.327()
# a.k.a default.utils.process_manager.show.2
# ==================================================

scoreboard players set default.utils.process_manager.show._0 tbms.var 1
tellraw @a [{"text": " [ON] default.test.TestRunner.__count__", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"green"}]
scoreboard players add default.utils.process_manager.show.running tbms.var 1