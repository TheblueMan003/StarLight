## _set
- mcposition $position
- mcobject $block
- int $id

## _fill
- mcposition $start
- mcposition $end
- mcobject $block
- int $id

## _fill
- mcposition $start
- mcposition $end
- mcobject $block
- int $id
- mcobject $fro
- int $id2

## _fill
- mcposition $start
- mcposition $end
- mcobject $block
- int $id
- string $mod

## _clone
- mcposition $start
- mcposition $end
- mcposition $target
- mcobject $block
- int $id
- string $mod2
List()
## _set
- mcposition $position
- mcobject $block

## _fill
- mcposition $start
- mcposition $end
- mcobject $block

## _fill
- mcposition $start
- mcposition $end
- mcobject $block
- mcobject $fro

## _fill
- mcposition $start
- mcposition $end
- mcobject $block
- string $mod

## _clone
- mcposition $start
- mcposition $end
- mcposition $target
- mcobject $block
- string $mod2
List()
## _clone
- mcposition $start
- mcposition $end
- mcposition $target
- string $mod1
- string $mod2

## set
- mcposition position
- mcobject block
Set a `block` at `position`
## set
- mcobject block
Set a `block` here
## fill
- mcposition start
- mcposition end
- mcobject block
Fill from `start` to `end` with `block`
## fill
- mcposition start
- mcposition end
- mcobject block
- mcobject fro
Replace `fro` to `block` from `start` to `end`
## replaceNear
- int $radius
- mcobject from_
- mcobject to_
Replace `from_` to `to_` in a radius.
## fillDestroy
- mcposition start
- mcposition end
- mcobject block
Fill and Destroy from `start` to `end` with `block`
## fillHollow
- mcposition start
- mcposition end
- mcobject block
Hollow Fill from `start` to `end` with `block`
## fillKeep
- mcposition start
- mcposition end
- mcobject block
Fill Keep from `start` to `end` with `block`
## fillOutline
- mcposition start
- mcposition end
- mcobject block
Fill Outline from `start` to `end` with `block`
## clone
- mcposition start
- mcposition end
- mcposition target
Clone Area from `start` to `end`, to new area `target`
## cloneMask
- mcposition start
- mcposition end
- mcposition target
Mask Clone Area from `start` to `end`, to new area `target`
## clone
- mcposition start
- mcposition end
- mcposition target
- mcobject block
Clone Filtered by `block` in Area from `start` to `end`, to new area `target`
## move
- mcposition start
- mcposition end
- mcposition target
Move Area from `start` to `end`, to new area `target`
## moveMask
- mcposition start
- mcposition end
- mcposition target
Mask Move Area from `start` to `end`, to new area `target`
## move
- mcposition start
- mcposition end
- mcposition target
- mcobject block
Move Filtered by `block` in Area from `start` to `end`, to new area `target`
