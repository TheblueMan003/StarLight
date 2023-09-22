# ==================================================
# void default.zzz_sl_block.405()
# a.k.a default.utils.process_manager.show.37
# ==================================================

scoreboard players set default.utils.process_manager.show._35 tbms.var 1
tellraw @a [{"text": " [ON] ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"green"},{"translate":"default.int.not_lte_float", "with":[{"text": "green", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"}]}]
scoreboard players add default.utils.process_manager.show.running tbms.var 1
