# ==================================================
# int default.standard.int.bitwiseXor(int a, int b)
# ==================================================
# ==================================================
# Returns a bitwise xor with b. Computation is done using modulo 2 arithmetic.
# ==================================================

scoreboard players set default.standard.int.bitwiseXor.result tbms.var 0
scoreboard players set default.standard.int.bitwiseXor._0._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..2147483646 if score default.standard.int.bitwiseXor.a tbms.var matches 2147483647.. run function default:zzz_sl_block/14
execute if score default.standard.int.bitwiseXor.b tbms.var matches 2147483647.. if score default.standard.int.bitwiseXor.a tbms.var matches ..2147483646 if score default.standard.int.bitwiseXor._0._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 2147483647
scoreboard players set default.standard.int.bitwiseXor._1._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..1073741823 if score default.standard.int.bitwiseXor.a tbms.var matches 1073741824.. run function default:zzz_sl_block/15
execute if score default.standard.int.bitwiseXor.b tbms.var matches 1073741824.. if score default.standard.int.bitwiseXor.a tbms.var matches ..1073741823 if score default.standard.int.bitwiseXor._1._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 1073741824
scoreboard players set default.standard.int.bitwiseXor._2._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..536870911 if score default.standard.int.bitwiseXor.a tbms.var matches 536870912.. run function default:zzz_sl_block/16
execute if score default.standard.int.bitwiseXor.b tbms.var matches 536870912.. if score default.standard.int.bitwiseXor.a tbms.var matches ..536870911 if score default.standard.int.bitwiseXor._2._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 536870912
scoreboard players set default.standard.int.bitwiseXor._3._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..268435455 if score default.standard.int.bitwiseXor.a tbms.var matches 268435456.. run function default:zzz_sl_block/17
execute if score default.standard.int.bitwiseXor.b tbms.var matches 268435456.. if score default.standard.int.bitwiseXor.a tbms.var matches ..268435455 if score default.standard.int.bitwiseXor._3._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 268435456
scoreboard players set default.standard.int.bitwiseXor._4._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..134217727 if score default.standard.int.bitwiseXor.a tbms.var matches 134217728.. run function default:zzz_sl_block/18
execute if score default.standard.int.bitwiseXor.b tbms.var matches 134217728.. if score default.standard.int.bitwiseXor.a tbms.var matches ..134217727 if score default.standard.int.bitwiseXor._4._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 134217728
scoreboard players set default.standard.int.bitwiseXor._5._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..67108863 if score default.standard.int.bitwiseXor.a tbms.var matches 67108864.. run function default:zzz_sl_block/19
execute if score default.standard.int.bitwiseXor.b tbms.var matches 67108864.. if score default.standard.int.bitwiseXor.a tbms.var matches ..67108863 if score default.standard.int.bitwiseXor._5._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 67108864
scoreboard players set default.standard.int.bitwiseXor._6._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..33554431 if score default.standard.int.bitwiseXor.a tbms.var matches 33554432.. run function default:zzz_sl_block/20
execute if score default.standard.int.bitwiseXor.b tbms.var matches 33554432.. if score default.standard.int.bitwiseXor.a tbms.var matches ..33554431 if score default.standard.int.bitwiseXor._6._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 33554432
scoreboard players set default.standard.int.bitwiseXor._7._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..16777215 if score default.standard.int.bitwiseXor.a tbms.var matches 16777216.. run function default:zzz_sl_block/21
execute if score default.standard.int.bitwiseXor.b tbms.var matches 16777216.. if score default.standard.int.bitwiseXor.a tbms.var matches ..16777215 if score default.standard.int.bitwiseXor._7._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 16777216
scoreboard players set default.standard.int.bitwiseXor._8._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..8388607 if score default.standard.int.bitwiseXor.a tbms.var matches 8388608.. run function default:zzz_sl_block/22
execute if score default.standard.int.bitwiseXor.b tbms.var matches 8388608.. if score default.standard.int.bitwiseXor.a tbms.var matches ..8388607 if score default.standard.int.bitwiseXor._8._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 8388608
scoreboard players set default.standard.int.bitwiseXor._9._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..4194303 if score default.standard.int.bitwiseXor.a tbms.var matches 4194304.. run function default:zzz_sl_block/23
execute if score default.standard.int.bitwiseXor.b tbms.var matches 4194304.. if score default.standard.int.bitwiseXor.a tbms.var matches ..4194303 if score default.standard.int.bitwiseXor._9._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 4194304
scoreboard players set default.standard.int.bitwiseXor._10._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..2097151 if score default.standard.int.bitwiseXor.a tbms.var matches 2097152.. run function default:zzz_sl_block/24
execute if score default.standard.int.bitwiseXor.b tbms.var matches 2097152.. if score default.standard.int.bitwiseXor.a tbms.var matches ..2097151 if score default.standard.int.bitwiseXor._10._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 2097152
scoreboard players set default.standard.int.bitwiseXor._11._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..1048575 if score default.standard.int.bitwiseXor.a tbms.var matches 1048576.. run function default:zzz_sl_block/25
execute if score default.standard.int.bitwiseXor.b tbms.var matches 1048576.. if score default.standard.int.bitwiseXor.a tbms.var matches ..1048575 if score default.standard.int.bitwiseXor._11._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 1048576
scoreboard players set default.standard.int.bitwiseXor._12._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..524287 if score default.standard.int.bitwiseXor.a tbms.var matches 524288.. run function default:zzz_sl_block/26
execute if score default.standard.int.bitwiseXor.b tbms.var matches 524288.. if score default.standard.int.bitwiseXor.a tbms.var matches ..524287 if score default.standard.int.bitwiseXor._12._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 524288
scoreboard players set default.standard.int.bitwiseXor._13._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..262143 if score default.standard.int.bitwiseXor.a tbms.var matches 262144.. run function default:zzz_sl_block/27
execute if score default.standard.int.bitwiseXor.b tbms.var matches 262144.. if score default.standard.int.bitwiseXor.a tbms.var matches ..262143 if score default.standard.int.bitwiseXor._13._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 262144
scoreboard players set default.standard.int.bitwiseXor._14._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..131071 if score default.standard.int.bitwiseXor.a tbms.var matches 131072.. run function default:zzz_sl_block/28
execute if score default.standard.int.bitwiseXor.b tbms.var matches 131072.. if score default.standard.int.bitwiseXor.a tbms.var matches ..131071 if score default.standard.int.bitwiseXor._14._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 131072
scoreboard players set default.standard.int.bitwiseXor._15._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..65535 if score default.standard.int.bitwiseXor.a tbms.var matches 65536.. run function default:zzz_sl_block/29
execute if score default.standard.int.bitwiseXor.b tbms.var matches 65536.. if score default.standard.int.bitwiseXor.a tbms.var matches ..65535 if score default.standard.int.bitwiseXor._15._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 65536
scoreboard players set default.standard.int.bitwiseXor._16._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..32767 if score default.standard.int.bitwiseXor.a tbms.var matches 32768.. run function default:zzz_sl_block/30
execute if score default.standard.int.bitwiseXor.b tbms.var matches 32768.. if score default.standard.int.bitwiseXor.a tbms.var matches ..32767 if score default.standard.int.bitwiseXor._16._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 32768
scoreboard players set default.standard.int.bitwiseXor._17._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..16383 if score default.standard.int.bitwiseXor.a tbms.var matches 16384.. run function default:zzz_sl_block/31
execute if score default.standard.int.bitwiseXor.b tbms.var matches 16384.. if score default.standard.int.bitwiseXor.a tbms.var matches ..16383 if score default.standard.int.bitwiseXor._17._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 16384
scoreboard players set default.standard.int.bitwiseXor._18._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..8191 if score default.standard.int.bitwiseXor.a tbms.var matches 8192.. run function default:zzz_sl_block/32
execute if score default.standard.int.bitwiseXor.b tbms.var matches 8192.. if score default.standard.int.bitwiseXor.a tbms.var matches ..8191 if score default.standard.int.bitwiseXor._18._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 8192
scoreboard players set default.standard.int.bitwiseXor._19._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..4095 if score default.standard.int.bitwiseXor.a tbms.var matches 4096.. run function default:zzz_sl_block/33
execute if score default.standard.int.bitwiseXor.b tbms.var matches 4096.. if score default.standard.int.bitwiseXor.a tbms.var matches ..4095 if score default.standard.int.bitwiseXor._19._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 4096
scoreboard players set default.standard.int.bitwiseXor._20._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..2047 if score default.standard.int.bitwiseXor.a tbms.var matches 2048.. run function default:zzz_sl_block/34
execute if score default.standard.int.bitwiseXor.b tbms.var matches 2048.. if score default.standard.int.bitwiseXor.a tbms.var matches ..2047 if score default.standard.int.bitwiseXor._20._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 2048
scoreboard players set default.standard.int.bitwiseXor._21._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..1023 if score default.standard.int.bitwiseXor.a tbms.var matches 1024.. run function default:zzz_sl_block/35
execute if score default.standard.int.bitwiseXor.b tbms.var matches 1024.. if score default.standard.int.bitwiseXor.a tbms.var matches ..1023 if score default.standard.int.bitwiseXor._21._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 1024
scoreboard players set default.standard.int.bitwiseXor._22._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..511 if score default.standard.int.bitwiseXor.a tbms.var matches 512.. run function default:zzz_sl_block/36
execute if score default.standard.int.bitwiseXor.b tbms.var matches 512.. if score default.standard.int.bitwiseXor.a tbms.var matches ..511 if score default.standard.int.bitwiseXor._22._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 512
scoreboard players set default.standard.int.bitwiseXor._23._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..255 if score default.standard.int.bitwiseXor.a tbms.var matches 256.. run function default:zzz_sl_block/37
execute if score default.standard.int.bitwiseXor.b tbms.var matches 256.. if score default.standard.int.bitwiseXor.a tbms.var matches ..255 if score default.standard.int.bitwiseXor._23._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 256
scoreboard players set default.standard.int.bitwiseXor._24._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..127 if score default.standard.int.bitwiseXor.a tbms.var matches 128.. run function default:zzz_sl_block/38
execute if score default.standard.int.bitwiseXor.b tbms.var matches 128.. if score default.standard.int.bitwiseXor.a tbms.var matches ..127 if score default.standard.int.bitwiseXor._24._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 128
scoreboard players set default.standard.int.bitwiseXor._25._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..63 if score default.standard.int.bitwiseXor.a tbms.var matches 64.. run function default:zzz_sl_block/39
execute if score default.standard.int.bitwiseXor.b tbms.var matches 64.. if score default.standard.int.bitwiseXor.a tbms.var matches ..63 if score default.standard.int.bitwiseXor._25._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 64
scoreboard players set default.standard.int.bitwiseXor._26._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..31 if score default.standard.int.bitwiseXor.a tbms.var matches 32.. run function default:zzz_sl_block/40
execute if score default.standard.int.bitwiseXor.b tbms.var matches 32.. if score default.standard.int.bitwiseXor.a tbms.var matches ..31 if score default.standard.int.bitwiseXor._26._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 32
scoreboard players set default.standard.int.bitwiseXor._27._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..15 if score default.standard.int.bitwiseXor.a tbms.var matches 16.. run function default:zzz_sl_block/41
execute if score default.standard.int.bitwiseXor.b tbms.var matches 16.. if score default.standard.int.bitwiseXor.a tbms.var matches ..15 if score default.standard.int.bitwiseXor._27._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 16
scoreboard players set default.standard.int.bitwiseXor._28._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..7 if score default.standard.int.bitwiseXor.a tbms.var matches 8.. run function default:zzz_sl_block/42
execute if score default.standard.int.bitwiseXor.b tbms.var matches 8.. if score default.standard.int.bitwiseXor.a tbms.var matches ..7 if score default.standard.int.bitwiseXor._28._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 8
scoreboard players set default.standard.int.bitwiseXor._29._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..3 if score default.standard.int.bitwiseXor.a tbms.var matches 4.. run function default:zzz_sl_block/43
execute if score default.standard.int.bitwiseXor.b tbms.var matches 4.. if score default.standard.int.bitwiseXor.a tbms.var matches ..3 if score default.standard.int.bitwiseXor._29._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 4
scoreboard players set default.standard.int.bitwiseXor._30._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..1 if score default.standard.int.bitwiseXor.a tbms.var matches 2.. run function default:zzz_sl_block/44
execute if score default.standard.int.bitwiseXor.b tbms.var matches 2.. if score default.standard.int.bitwiseXor.a tbms.var matches ..1 if score default.standard.int.bitwiseXor._30._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 2
scoreboard players set default.standard.int.bitwiseXor._31._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..0 if score default.standard.int.bitwiseXor.a tbms.var matches 1.. run function default:zzz_sl_block/45
execute if score default.standard.int.bitwiseXor.b tbms.var matches 1.. if score default.standard.int.bitwiseXor.a tbms.var matches ..0 if score default.standard.int.bitwiseXor._31._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 1
scoreboard players set default.standard.int.bitwiseXor._32._0 tbms.var 0
execute if score default.standard.int.bitwiseXor.b tbms.var matches ..-1 if score default.standard.int.bitwiseXor.a tbms.var matches 0.. run function default:zzz_sl_block/46
execute if score default.standard.int.bitwiseXor.b tbms.var matches 0.. if score default.standard.int.bitwiseXor.a tbms.var matches ..-1 if score default.standard.int.bitwiseXor._32._0 tbms.var matches 0 run scoreboard players add default.standard.int.bitwiseXor.result tbms.var 0
scoreboard players operation default.standard.int.bitwiseXor._ret tbms.var = default.standard.int.bitwiseXor.result tbms.var
