## lazy void cmd.team.create(mcobject $name, rawjson $text)
- mcobject $name
- rawjson $text



## lazy void cmd.team.setPrefix(mcobject $name, rawjson $text)
- mcobject $name
- rawjson $text



## lazy void cmd.team.setColor(mcobject $name, mcobject $color)
- mcobject $name
- mcobject $color



## lazy void cmd.team.seeFriendlyInvisibles(mcobject $name, bool $value)
- mcobject $name
- bool $value



## lazy void cmd.team.hideFriendlyInvisibles(mcobject $name)
- mcobject $name



## lazy void cmd.team.disableCollision(mcobject $name)
- mcobject $name



## lazy void cmd.team.collisionOther(mcobject $name)
- mcobject $name



## lazy void cmd.team.collisionOwn(mcobject $name)
- mcobject $name



## lazy void cmd.team.enableCollision(mcobject $name)
- mcobject $name



## lazy void cmd.team.setName(mcobject $name, rawjson $text)
- mcobject $name
- rawjson $text



## lazy void cmd.team.friendlyFire(mcobject $name, bool $value)
- mcobject $name
- bool $value



## lazy void cmd.team.join(mcobject $name, entity $sel)
- mcobject $name
- entity $sel



## lazy void cmd.team.join(mcobject $name)
- mcobject $name



## lazy void cmd.team.leave(entity $sel)
- entity $sel



## lazy void cmd.team.empty(mcobject $name)
- mcobject $name



## lazy void cmd.team.delete(mcobject $name)
- mcobject $name



# struct team.Team


## lazy void team.__init__(rawjson text)
- rawjson text



## lazy void team.setPrefix(rawjson text)
- rawjson text



## lazy void team.setSuffix(rawjson text)
- rawjson text



## lazy void team.setColor(mcobject color)
- mcobject color



## lazy void team.seeFriendlyInvisibles(bool value)
- bool value



## lazy void team.hideFriendlyInvisibles()


## lazy void team.disableCollision()


## lazy void team.collisionOther()


## lazy void team.collisionOwn()


## lazy void team.enableCollision()


## lazy void team.setName(json text)
- json text



## lazy void team.friendlyFire(bool value)
- bool value



## lazy void team.join(entity sel)
- entity sel



## lazy void team.join()


## lazy void team.forcejoin()


## lazy void team.__add__(entity sel)
- entity sel



## lazy void team.leave(entity sel)
- entity sel



## lazy void team.leave()


## lazy void team.__sub__(entity sel)
- entity sel



## lazy void team.empty()


## lazy void team.delete()





