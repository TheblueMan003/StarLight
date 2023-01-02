## predicate cmd.gamemode.isAdventure


## predicate cmd.gamemode.isSurvival


## predicate cmd.gamemode.isCreative


## predicate cmd.gamemode.isSpectator


## lazy bool cmd.gamemode.isAdventure()
return true if the gamemode of the current entity is `adventure`

## lazy bool cmd.gamemode.isSurvival()
return true if the gamemode of the current entity is `survival`

## lazy bool cmd.gamemode.isCreative()
return true if the gamemode of the current entity is `creative`

## lazy bool cmd.gamemode.isSpectator()
return true if the gamemode of the current entity is `spectator`

## Gamemode? cmd.gamemode.get()


## lazy void cmd.gamemode.adventure(entity $e)
- entity $e

Set the gamemode to `adventure` for entity `e`

## lazy void cmd.gamemode.survival(entity $e)
- entity $e

Set the gamemode to `survival` for entity `e`

## lazy void cmd.gamemode.creative(entity $e)
- entity $e

Set the gamemode to `creative` for entity `e`

## lazy void cmd.gamemode.spectator(entity $e)
- entity $e

Set the gamemode to `spectator` for entity `e`

## lazy void cmd.gamemode.set(mcobject $gamemode, entity $e)
- mcobject $gamemode
- entity $e

Set the gamemode to `gamemode` for entity `e`

## lazy void cmd.gamemode.set(int gamemode, entity $e)
- int gamemode
- entity $e

Set the gamemode to `gamemode` for entity `e`


