function default:a/adas/on-stop
scoreboard players set default.a.adas.enabled tbms.var 0
scoreboard players operation default.zzz_sl_mux.void___to___void.__fct__ tbms.var = default.a.adas.callback tbms.var
function default:zzz_sl_mux/void___to___void
scoreboard players set default.a.adas.callback tbms.var 0
