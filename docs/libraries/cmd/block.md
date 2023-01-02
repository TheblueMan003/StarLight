## private lazy void cmd.block._set(mcposition $position, mcobject $block, int $id)
- mcposition $position
- mcobject $block
- int $id



## private lazy void cmd.block._fill(mcposition $start, mcposition $end, mcobject $block, int $id)
- mcposition $start
- mcposition $end
- mcobject $block
- int $id



## private lazy void cmd.block._fill(mcposition $start, mcposition $end, mcobject $block, int $id, mcobject $fro, int $id2)
- mcposition $start
- mcposition $end
- mcobject $block
- int $id
- mcobject $fro
- int $id2



## private lazy void cmd.block._fill(mcposition $start, mcposition $end, mcobject $block, int $id, string $mod)
- mcposition $start
- mcposition $end
- mcobject $block
- int $id
- string $mod



## private lazy void cmd.block._clone(mcposition $start, mcposition $end, mcposition $target, mcobject $block, int $id, string $mod2)
- mcposition $start
- mcposition $end
- mcposition $target
- mcobject $block
- int $id
- string $mod2



## private lazy void cmd.block._set(mcposition $position, mcobject $block)
- mcposition $position
- mcobject $block



## private lazy void cmd.block._fill(mcposition $start, mcposition $end, mcobject $block)
- mcposition $start
- mcposition $end
- mcobject $block



## private lazy void cmd.block._fill(mcposition $start, mcposition $end, mcobject $block, mcobject $fro)
- mcposition $start
- mcposition $end
- mcobject $block
- mcobject $fro



## private lazy void cmd.block._fill(mcposition $start, mcposition $end, mcobject $block, string $mod)
- mcposition $start
- mcposition $end
- mcobject $block
- string $mod



## private lazy void cmd.block._clone(mcposition $start, mcposition $end, mcposition $target, mcobject $block, string $mod2)
- mcposition $start
- mcposition $end
- mcposition $target
- mcobject $block
- string $mod2



## private lazy void cmd.block._clone(mcposition $start, mcposition $end, mcposition $target, string $mod1, string $mod2)
- mcposition $start
- mcposition $end
- mcposition $target
- string $mod1
- string $mod2



## lazy void cmd.block.set(mcposition position, mcobject block)
- mcposition position
- mcobject block

Set a `block` at `position`

## lazy void cmd.block.set(mcobject block)
- mcobject block

Set a `block` here

## lazy void cmd.block.fill(mcposition start, mcposition end, mcobject block)
- mcposition start
- mcposition end
- mcobject block

Fill from `start` to `end` with `block`

## lazy void cmd.block.fill(mcposition start, mcposition end, mcobject block, mcobject fro)
- mcposition start
- mcposition end
- mcobject block
- mcobject fro

Replace `fro` to `block` from `start` to `end`

## lazy void cmd.block.replaceNear(int $radius, mcobject from_, mcobject to_)
- int $radius
- mcobject from_
- mcobject to_

Replace `from_` to `to_` in a radius.

## lazy void cmd.block.fillDestroy(mcposition start, mcposition end, mcobject block)
- mcposition start
- mcposition end
- mcobject block

Fill and Destroy from `start` to `end` with `block`

## lazy void cmd.block.fillHollow(mcposition start, mcposition end, mcobject block)
- mcposition start
- mcposition end
- mcobject block

Hollow Fill from `start` to `end` with `block`

## lazy void cmd.block.fillKeep(mcposition start, mcposition end, mcobject block)
- mcposition start
- mcposition end
- mcobject block

Fill Keep from `start` to `end` with `block`

## lazy void cmd.block.fillOutline(mcposition start, mcposition end, mcobject block)
- mcposition start
- mcposition end
- mcobject block

Fill Outline from `start` to `end` with `block`

## lazy void cmd.block.clone(mcposition start, mcposition end, mcposition target)
- mcposition start
- mcposition end
- mcposition target

Clone Area from `start` to `end`, to new area `target`

## lazy void cmd.block.cloneMask(mcposition start, mcposition end, mcposition target)
- mcposition start
- mcposition end
- mcposition target

Mask Clone Area from `start` to `end`, to new area `target`

## lazy void cmd.block.clone(mcposition start, mcposition end, mcposition target, mcobject block)
- mcposition start
- mcposition end
- mcposition target
- mcobject block

Clone Filtered by `block` in Area from `start` to `end`, to new area `target`

## lazy void cmd.block.move(mcposition start, mcposition end, mcposition target)
- mcposition start
- mcposition end
- mcposition target

Move Area from `start` to `end`, to new area `target`

## lazy void cmd.block.moveMask(mcposition start, mcposition end, mcposition target)
- mcposition start
- mcposition end
- mcposition target

Mask Move Area from `start` to `end`, to new area `target`

## lazy void cmd.block.move(mcposition start, mcposition end, mcposition target, mcobject block)
- mcposition start
- mcposition end
- mcposition target
- mcobject block

Move Filtered by `block` in Area from `start` to `end`, to new area `target`


