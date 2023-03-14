scoreboard objectives add tbms.tmp dummy
scoreboard objectives add tbms.value dummy
scoreboard objectives add tbms.const dummy
scoreboard objectives add tbms.var dummy
scoreboard players set default.__totalRefCount tbms.var 0
scoreboard players set default.cell.main.applyTo tbms.var 0
scoreboard players set default.cell.main.applyTo.isPlayer tbms.var 0
function default/__load__
