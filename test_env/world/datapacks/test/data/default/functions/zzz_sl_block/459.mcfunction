# ==================================================
# void default.zzz_sl_block.459()
# a.k.a default.utils.process_manager.show.163
# ==================================================

scoreboard players set default.utils.process_manager.show._161 tbms.var 1
tellraw @a [{"text": " [ON] ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"green"},{"translate":"default.int.not_addition_value", "with":[{"text": "green", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"}]}]
scoreboard players add default.utils.process_manager.show.running tbms.var 1
