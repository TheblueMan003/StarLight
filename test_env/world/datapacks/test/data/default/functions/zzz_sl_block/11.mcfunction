scoreboard players set default.utils.process_manager.show._1 tbms.var 1
tellraw @a [{"text": " [ON] default.test.TestRunner.__count__", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"green"}]
scoreboard players add default.utils.process_manager.show.running tbms.var 1
