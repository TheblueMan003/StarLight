## predicate isAdventure


## predicate isSurvival


## predicate isCreative


## predicate isSpectator


## lazy bool isAdventure()
return true if the gamemode of the current entity is `adventure`

## lazy bool isSurvival()
return true if the gamemode of the current entity is `survival`

## lazy bool isCreative()
return true if the gamemode of the current entity is `creative`

## lazy bool isSpectator()
return true if the gamemode of the current entity is `spectator`

## Gamemode? get()


## lazy void adventure(entity $e)
- entity $e

Set the gamemode to `adventure` for entity `e`

## lazy void survival(entity $e)
- entity $e

Set the gamemode to `survival` for entity `e`

## lazy void creative(entity $e)
- entity $e

Set the gamemode to `creative` for entity `e`

## lazy void spectator(entity $e)
- entity $e

Set the gamemode to `spectator` for entity `e`

## lazy void set(mcobject $gamemode, entity $e)
- mcobject $gamemode
- entity $e

Set the gamemode to `gamemode` for entity `e`

## lazy void set(int gamemode, entity $e)
- int gamemode
- entity $e

Set the gamemode to `gamemode` for entity `e`


