## lazy void cmd.effect.$name(entity $ent, int $duration, int $power, bool $particle)
- entity $ent
- int $duration
- int $power
- bool $particle

Give effect `$name` to `ent` for `duration` and `power`.    Show particles if `particle`

## lazy void cmd.effect.$name(int $duration, int $power, bool $particle)
- int $duration
- int $power
- bool $particle

Give effect `$name` to self for `duration` and `power`.    Show particles if `particle`

## lazy void cmd.effect.clear$name(entity $ent)
- entity $ent

Clear effect `$name` for `ent`

## lazy void cmd.effect.clear(entity $ent)
- entity $ent

Clear all effects for `ent`


