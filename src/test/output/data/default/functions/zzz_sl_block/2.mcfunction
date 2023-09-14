# ==================================================
# void default.zzz_sl_block.2()
# a.k.a default.test.TestRunner.stop.1
# ==================================================

function default:test/-test-runner/on-stop-0
scoreboard players set default.test.TestRunner.enabled tbms.var 0
scoreboard players operation default.zzz_sl_mux.void___to___void.__fct__ tbms.var = default.test.TestRunner.callback tbms.var
function default:zzz_sl_block/1
scoreboard players set default.test.TestRunner.callback tbms.var 0
