# Entomb #

Instant spell. This spell surrounds the target in glass, preventing them from moving until the barrier disappears (or until they break it, if it is a player).

## Configuration ##

| **Option** | **Description** | **Default** |
|:-----------|:----------------|:------------|
| target-players | Whether this spell can target players. | false       |
| obey-los   | Whether to obey line-of-sight. | true        |
| tomb-block-type | The block type to use to encase the target. | 20 (glass)  |
| tomb-duration | How long the barrier lasts, in seconds. | 20          |
| close-top-and-bottom | Whether to place blocks at the top and bottom of the tomb. | true        |
| str-no-target | The message that appears if there is no target to entomb. | (empty)     |