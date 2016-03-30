# Precision #

Some spells, such as [combust](CombustSpell.md) and [entomb](EntombSpell.md), can target entities (monsters, animals, players, etc). The plugin checks an arc in front of the casting player to find the target. The arc is defined by the range and precision options for the spell.

The range is simple, it is just the maximum distance from the casting player that the plugin will look for a target.

The precision is a measurement of the angle of the arc, in degrees. A smaller precision means that the caster must be more precise in their targeting, while a larger precision gives a larger margin of error.

A larger precision means that it will be easier to target entities, but it also means that it will be easier to target the wrong entity. A smaller precision means it will be possible to target specific entities in a group, but it will also require precise targeting, which may not be possible due to even slight server lag.