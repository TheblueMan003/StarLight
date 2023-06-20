scoreboard players set default.game.Room.show._1 tbms.var 1
tellraw @a [{"text": " [ON] default.fruit.test.room.__count__", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"green"}]
scoreboard players add default.game.Room.show.running tbms.var 1
