## lazy void cmd.particle.point(mcobject $part)
- mcobject $part

Spawn a single particle of type `part`

## lazy void cmd.particle.dust(float $r, float $g, float $b, float $a)
- float $r
- float $g
- float $b
- float $a

Spawn a single dust particle with color (`r`, `g`, `b`) and size `a`

## lazy void cmd.particle.dust(float $r, float $g, float $b)
- float $r
- float $g
- float $b

Spawn a single dust particle with color (`r`, `g`, `b`) and size 1

## lazy void cmd.particle.sphere(mcobject $part, float $radius, float $speed, float $amount)
- mcobject $part
- float $radius
- float $speed
- float $amount

Spawn a sphere of `radius` of particle of type `part` with `speed` containing `amount` of particles

## lazy void cmd.particle.disk(mcobject $part, float $radius, float $speed, float $amount)
- mcobject $part
- float $radius
- float $speed
- float $amount

Spawn a disk of `radius` of particle of type `part` with `speed` containing `amount` of particles

## void cmd.particle.explosion()
Spawn an explosion particle


