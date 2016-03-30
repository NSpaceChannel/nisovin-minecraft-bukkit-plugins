# Lightning #

Instant spell. This spell calls down lightning at the targeted location. Can only be used outside.


## Configuration ##

| **Option** | **Description** | **Default** |
|:-----------|:----------------|:------------|
| no-damage  | Whether the lightning should just be for show. | false       |
| require-entity-target | Whether this plugin should target an entity or just a block. | false       |
| obey-los   | Whether to obey line-of-sight. Only used when require-entity-target is true. | 20          |
| target-players | Whether players can be targeted. Only used if require-entity-target is true. | false       |
| check-plugins | Whether to check other plugins for allowed damage. Only used if require-entity-target and target-players are both true. |
| str-cast-fail | Message that appears when the spell fails. | (empty)     |
| str-no-target | Message that appears when there is no target. Only used if require-entity-target is true. | Unable to find target. |