# Recall #

This spell teleports you to your marked location, created with the [mark](MarkSpell.md) spell.

## Configuration ##

| **Option** | **Description** | **Default** |
|:-----------|:----------------|:------------|
| allow-cross-world | Whether to allow a player to recall to a mark on another world. | true        |
| max-range  | The maximum teleport range. | 0 (infinite) |
| str-no-mark | The message that appears when a player tries to recall without a mark. | You have no mark to recall to. |
| str-other-world | The message that appears when a player's mark is on another world and the allow-cross-world option is false. | Your mark is in another world. |
| str-too-far | The message that appears when a player's mark is to far to recall to, if max-range is greater than 0. | Your mark is too far away. |