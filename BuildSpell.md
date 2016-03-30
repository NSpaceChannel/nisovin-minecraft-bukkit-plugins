# Build #

Instant spell. Allows a player to build blocks from a distance, using blocks from a configurable location in their inventory (the first hotbar slot by default).


## Configuration ##

| **Option** | **Description** | **Default** |
|:-----------|:----------------|:------------|
| slot       | The slot to use to build blocks. | 0           |
| consume-block | Whether to use up the blocks in the defined slot. | true        |
| show-effect | Whether to use the block breaking effect as a spell animation. | true        |
| allowed-types | The block types that can be built with this spell, as a comma-separated list of item IDs. | 1,2,3,4,5,12,13,17,20,22,24,35,41,42,43,44,45,47,48,49,50,53,57,65,67,80,85,87,88,89,91,92 |
| check-plugins | Whether to check plugins for block build permissions. | true        |
| str-invalid-block | Error that appears when a player tries to build a block that isn't in the allowed list. | You can't build that block. |
| str-cant-build | Error that appears when a player can't build, either because a plugin blocks it or because there is no target. | You can't build there. |