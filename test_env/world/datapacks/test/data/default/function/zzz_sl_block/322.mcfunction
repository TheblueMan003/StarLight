# ==================================================
# void default.zzz_sl_block.322()
# a.k.a default.standard.int.parse.4
# ==================================================

data modify storage default.standard.int.parse._3._10 json set string storage default.standard.int.parse.s json 0 1
data modify storage default.standard.int.parse.s json set string storage default.standard.int.parse.s json 1
execute unless data storage default.standard.int.parse.s {json:""} run function default:zzz_sl_block/322
