# Gate #

Instant spell. Teleports the casting player to a specified location.


## Configuration ##

| **Option** | **Description** | **Default** |
|:-----------|:----------------|:------------|
| world      | The world to teleport to. Can specify a world name, or CURRENT to use the player's current world, or DEFAULT to use the main world. | CURRENT     |
| coordinates | The coordinates to teleport to. Can be in an x,y,z format, or can use SPAWN to teleport to the world's spawn location. | SPAWN       |
| useSpellEffect | Whether to use the spell effect, which causes portal blocks to temporarily appear in the player's current and teleport locations. | true        |
| str-gate-failed | Error that appears when unable to teleport. Can be caused by a misconfiguration, an unloaded world, or a blocked teleport location. | Unable to teleport. |