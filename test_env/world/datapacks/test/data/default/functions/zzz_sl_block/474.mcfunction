# ==================================================
# void default.zzz_sl_block.474()
# a.k.a default.utils.process_manager.show.198
# ==================================================

scoreboard players set default.utils.process_manager.show._196 tbms.var 1
tellraw @a [{"text": " [ON] ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"green"},{"translate":"default.int.neq_float_delta", "with":[{"text": "green", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"}]}]
scoreboard players add default.utils.process_manager.show.running tbms.var 1
