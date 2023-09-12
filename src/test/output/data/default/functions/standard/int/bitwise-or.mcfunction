# ==================================================
# int default.standard.int.bitwiseOr(int a, int b)
# ==================================================
# ==================================================
# Returns a bitwise or with b. Computation is done using modulo 2 arithmetic.
# ==================================================

scoreboard players set default.standard.int.bitwiseOr.result tbms.var 0
scoreboard players set default.standard.int.bitwiseOr._0._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 2147483647.. run function default:zzz_sl_block/47
execute if score default.standard.int.bitwiseOr.b tbms.var matches 2147483647.. if score default.standard.int.bitwiseOr._0._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 2147483647
execute if score default.standard.int.bitwiseOr.a tbms.var matches 2147483647.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 2147483647
execute if score default.standard.int.bitwiseOr.b tbms.var matches 2147483647.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 2147483647
scoreboard players set default.standard.int.bitwiseOr._1._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 1073741824.. run function default:zzz_sl_block/48
execute if score default.standard.int.bitwiseOr.b tbms.var matches 1073741824.. if score default.standard.int.bitwiseOr._1._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 1073741824
execute if score default.standard.int.bitwiseOr.a tbms.var matches 1073741824.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 1073741824
execute if score default.standard.int.bitwiseOr.b tbms.var matches 1073741824.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 1073741824
scoreboard players set default.standard.int.bitwiseOr._2._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 536870912.. run function default:zzz_sl_block/49
execute if score default.standard.int.bitwiseOr.b tbms.var matches 536870912.. if score default.standard.int.bitwiseOr._2._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 536870912
execute if score default.standard.int.bitwiseOr.a tbms.var matches 536870912.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 536870912
execute if score default.standard.int.bitwiseOr.b tbms.var matches 536870912.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 536870912
scoreboard players set default.standard.int.bitwiseOr._3._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 268435456.. run function default:zzz_sl_block/50
execute if score default.standard.int.bitwiseOr.b tbms.var matches 268435456.. if score default.standard.int.bitwiseOr._3._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 268435456
execute if score default.standard.int.bitwiseOr.a tbms.var matches 268435456.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 268435456
execute if score default.standard.int.bitwiseOr.b tbms.var matches 268435456.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 268435456
scoreboard players set default.standard.int.bitwiseOr._4._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 134217728.. run function default:zzz_sl_block/51
execute if score default.standard.int.bitwiseOr.b tbms.var matches 134217728.. if score default.standard.int.bitwiseOr._4._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 134217728
execute if score default.standard.int.bitwiseOr.a tbms.var matches 134217728.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 134217728
execute if score default.standard.int.bitwiseOr.b tbms.var matches 134217728.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 134217728
scoreboard players set default.standard.int.bitwiseOr._5._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 67108864.. run function default:zzz_sl_block/52
execute if score default.standard.int.bitwiseOr.b tbms.var matches 67108864.. if score default.standard.int.bitwiseOr._5._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 67108864
execute if score default.standard.int.bitwiseOr.a tbms.var matches 67108864.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 67108864
execute if score default.standard.int.bitwiseOr.b tbms.var matches 67108864.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 67108864
scoreboard players set default.standard.int.bitwiseOr._6._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 33554432.. run function default:zzz_sl_block/53
execute if score default.standard.int.bitwiseOr.b tbms.var matches 33554432.. if score default.standard.int.bitwiseOr._6._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 33554432
execute if score default.standard.int.bitwiseOr.a tbms.var matches 33554432.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 33554432
execute if score default.standard.int.bitwiseOr.b tbms.var matches 33554432.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 33554432
scoreboard players set default.standard.int.bitwiseOr._7._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 16777216.. run function default:zzz_sl_block/54
execute if score default.standard.int.bitwiseOr.b tbms.var matches 16777216.. if score default.standard.int.bitwiseOr._7._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 16777216
execute if score default.standard.int.bitwiseOr.a tbms.var matches 16777216.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 16777216
execute if score default.standard.int.bitwiseOr.b tbms.var matches 16777216.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 16777216
scoreboard players set default.standard.int.bitwiseOr._8._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 8388608.. run function default:zzz_sl_block/55
execute if score default.standard.int.bitwiseOr.b tbms.var matches 8388608.. if score default.standard.int.bitwiseOr._8._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 8388608
execute if score default.standard.int.bitwiseOr.a tbms.var matches 8388608.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 8388608
execute if score default.standard.int.bitwiseOr.b tbms.var matches 8388608.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 8388608
scoreboard players set default.standard.int.bitwiseOr._9._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 4194304.. run function default:zzz_sl_block/56
execute if score default.standard.int.bitwiseOr.b tbms.var matches 4194304.. if score default.standard.int.bitwiseOr._9._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 4194304
execute if score default.standard.int.bitwiseOr.a tbms.var matches 4194304.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 4194304
execute if score default.standard.int.bitwiseOr.b tbms.var matches 4194304.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 4194304
scoreboard players set default.standard.int.bitwiseOr._10._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 2097152.. run function default:zzz_sl_block/57
execute if score default.standard.int.bitwiseOr.b tbms.var matches 2097152.. if score default.standard.int.bitwiseOr._10._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 2097152
execute if score default.standard.int.bitwiseOr.a tbms.var matches 2097152.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 2097152
execute if score default.standard.int.bitwiseOr.b tbms.var matches 2097152.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 2097152
scoreboard players set default.standard.int.bitwiseOr._11._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 1048576.. run function default:zzz_sl_block/58
execute if score default.standard.int.bitwiseOr.b tbms.var matches 1048576.. if score default.standard.int.bitwiseOr._11._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 1048576
execute if score default.standard.int.bitwiseOr.a tbms.var matches 1048576.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 1048576
execute if score default.standard.int.bitwiseOr.b tbms.var matches 1048576.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 1048576
scoreboard players set default.standard.int.bitwiseOr._12._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 524288.. run function default:zzz_sl_block/59
execute if score default.standard.int.bitwiseOr.b tbms.var matches 524288.. if score default.standard.int.bitwiseOr._12._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 524288
execute if score default.standard.int.bitwiseOr.a tbms.var matches 524288.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 524288
execute if score default.standard.int.bitwiseOr.b tbms.var matches 524288.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 524288
scoreboard players set default.standard.int.bitwiseOr._13._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 262144.. run function default:zzz_sl_block/60
execute if score default.standard.int.bitwiseOr.b tbms.var matches 262144.. if score default.standard.int.bitwiseOr._13._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 262144
execute if score default.standard.int.bitwiseOr.a tbms.var matches 262144.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 262144
execute if score default.standard.int.bitwiseOr.b tbms.var matches 262144.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 262144
scoreboard players set default.standard.int.bitwiseOr._14._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 131072.. run function default:zzz_sl_block/61
execute if score default.standard.int.bitwiseOr.b tbms.var matches 131072.. if score default.standard.int.bitwiseOr._14._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 131072
execute if score default.standard.int.bitwiseOr.a tbms.var matches 131072.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 131072
execute if score default.standard.int.bitwiseOr.b tbms.var matches 131072.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 131072
scoreboard players set default.standard.int.bitwiseOr._15._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 65536.. run function default:zzz_sl_block/62
execute if score default.standard.int.bitwiseOr.b tbms.var matches 65536.. if score default.standard.int.bitwiseOr._15._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 65536
execute if score default.standard.int.bitwiseOr.a tbms.var matches 65536.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 65536
execute if score default.standard.int.bitwiseOr.b tbms.var matches 65536.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 65536
scoreboard players set default.standard.int.bitwiseOr._16._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 32768.. run function default:zzz_sl_block/63
execute if score default.standard.int.bitwiseOr.b tbms.var matches 32768.. if score default.standard.int.bitwiseOr._16._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 32768
execute if score default.standard.int.bitwiseOr.a tbms.var matches 32768.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 32768
execute if score default.standard.int.bitwiseOr.b tbms.var matches 32768.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 32768
scoreboard players set default.standard.int.bitwiseOr._17._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 16384.. run function default:zzz_sl_block/64
execute if score default.standard.int.bitwiseOr.b tbms.var matches 16384.. if score default.standard.int.bitwiseOr._17._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 16384
execute if score default.standard.int.bitwiseOr.a tbms.var matches 16384.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 16384
execute if score default.standard.int.bitwiseOr.b tbms.var matches 16384.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 16384
scoreboard players set default.standard.int.bitwiseOr._18._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 8192.. run function default:zzz_sl_block/65
execute if score default.standard.int.bitwiseOr.b tbms.var matches 8192.. if score default.standard.int.bitwiseOr._18._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 8192
execute if score default.standard.int.bitwiseOr.a tbms.var matches 8192.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 8192
execute if score default.standard.int.bitwiseOr.b tbms.var matches 8192.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 8192
scoreboard players set default.standard.int.bitwiseOr._19._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 4096.. run function default:zzz_sl_block/66
execute if score default.standard.int.bitwiseOr.b tbms.var matches 4096.. if score default.standard.int.bitwiseOr._19._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 4096
execute if score default.standard.int.bitwiseOr.a tbms.var matches 4096.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 4096
execute if score default.standard.int.bitwiseOr.b tbms.var matches 4096.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 4096
scoreboard players set default.standard.int.bitwiseOr._20._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 2048.. run function default:zzz_sl_block/67
execute if score default.standard.int.bitwiseOr.b tbms.var matches 2048.. if score default.standard.int.bitwiseOr._20._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 2048
execute if score default.standard.int.bitwiseOr.a tbms.var matches 2048.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 2048
execute if score default.standard.int.bitwiseOr.b tbms.var matches 2048.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 2048
scoreboard players set default.standard.int.bitwiseOr._21._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 1024.. run function default:zzz_sl_block/68
execute if score default.standard.int.bitwiseOr.b tbms.var matches 1024.. if score default.standard.int.bitwiseOr._21._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 1024
execute if score default.standard.int.bitwiseOr.a tbms.var matches 1024.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 1024
execute if score default.standard.int.bitwiseOr.b tbms.var matches 1024.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 1024
scoreboard players set default.standard.int.bitwiseOr._22._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 512.. run function default:zzz_sl_block/69
execute if score default.standard.int.bitwiseOr.b tbms.var matches 512.. if score default.standard.int.bitwiseOr._22._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 512
execute if score default.standard.int.bitwiseOr.a tbms.var matches 512.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 512
execute if score default.standard.int.bitwiseOr.b tbms.var matches 512.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 512
scoreboard players set default.standard.int.bitwiseOr._23._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 256.. run function default:zzz_sl_block/70
execute if score default.standard.int.bitwiseOr.b tbms.var matches 256.. if score default.standard.int.bitwiseOr._23._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 256
execute if score default.standard.int.bitwiseOr.a tbms.var matches 256.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 256
execute if score default.standard.int.bitwiseOr.b tbms.var matches 256.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 256
scoreboard players set default.standard.int.bitwiseOr._24._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 128.. run function default:zzz_sl_block/71
execute if score default.standard.int.bitwiseOr.b tbms.var matches 128.. if score default.standard.int.bitwiseOr._24._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 128
execute if score default.standard.int.bitwiseOr.a tbms.var matches 128.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 128
execute if score default.standard.int.bitwiseOr.b tbms.var matches 128.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 128
scoreboard players set default.standard.int.bitwiseOr._25._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 64.. run function default:zzz_sl_block/72
execute if score default.standard.int.bitwiseOr.b tbms.var matches 64.. if score default.standard.int.bitwiseOr._25._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 64
execute if score default.standard.int.bitwiseOr.a tbms.var matches 64.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 64
execute if score default.standard.int.bitwiseOr.b tbms.var matches 64.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 64
scoreboard players set default.standard.int.bitwiseOr._26._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 32.. run function default:zzz_sl_block/73
execute if score default.standard.int.bitwiseOr.b tbms.var matches 32.. if score default.standard.int.bitwiseOr._26._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 32
execute if score default.standard.int.bitwiseOr.a tbms.var matches 32.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 32
execute if score default.standard.int.bitwiseOr.b tbms.var matches 32.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 32
scoreboard players set default.standard.int.bitwiseOr._27._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 16.. run function default:zzz_sl_block/74
execute if score default.standard.int.bitwiseOr.b tbms.var matches 16.. if score default.standard.int.bitwiseOr._27._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 16
execute if score default.standard.int.bitwiseOr.a tbms.var matches 16.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 16
execute if score default.standard.int.bitwiseOr.b tbms.var matches 16.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 16
scoreboard players set default.standard.int.bitwiseOr._28._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 8.. run function default:zzz_sl_block/75
execute if score default.standard.int.bitwiseOr.b tbms.var matches 8.. if score default.standard.int.bitwiseOr._28._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 8
execute if score default.standard.int.bitwiseOr.a tbms.var matches 8.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 8
execute if score default.standard.int.bitwiseOr.b tbms.var matches 8.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 8
scoreboard players set default.standard.int.bitwiseOr._29._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 4.. run function default:zzz_sl_block/76
execute if score default.standard.int.bitwiseOr.b tbms.var matches 4.. if score default.standard.int.bitwiseOr._29._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 4
execute if score default.standard.int.bitwiseOr.a tbms.var matches 4.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 4
execute if score default.standard.int.bitwiseOr.b tbms.var matches 4.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 4
scoreboard players set default.standard.int.bitwiseOr._30._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 2.. run function default:zzz_sl_block/77
execute if score default.standard.int.bitwiseOr.b tbms.var matches 2.. if score default.standard.int.bitwiseOr._30._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 2
execute if score default.standard.int.bitwiseOr.a tbms.var matches 2.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 2
execute if score default.standard.int.bitwiseOr.b tbms.var matches 2.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 2
scoreboard players set default.standard.int.bitwiseOr._31._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 1.. run function default:zzz_sl_block/78
execute if score default.standard.int.bitwiseOr.b tbms.var matches 1.. if score default.standard.int.bitwiseOr._31._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 1
execute if score default.standard.int.bitwiseOr.a tbms.var matches 1.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 1
execute if score default.standard.int.bitwiseOr.b tbms.var matches 1.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 1
scoreboard players set default.standard.int.bitwiseOr._32._0 tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 0.. run function default:zzz_sl_block/79
execute if score default.standard.int.bitwiseOr.b tbms.var matches 0.. if score default.standard.int.bitwiseOr._32._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseOr.result tbms.var 0
execute if score default.standard.int.bitwiseOr.a tbms.var matches 0.. run scoreboard players remove default.standard.int.bitwiseOr.a tbms.var 0
execute if score default.standard.int.bitwiseOr.b tbms.var matches 0.. run scoreboard players remove default.standard.int.bitwiseOr.b tbms.var 0
scoreboard players operation default.standard.int.bitwiseOr._ret tbms.var = default.standard.int.bitwiseOr.result tbms.var
