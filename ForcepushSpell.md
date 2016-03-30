# Forcepush #

Instant spell. This spell pushes back all enemies near you. They will be pushed directly away from your current position. Standing below an enemy can send them flying!

## Configuration ##

| **Option** | **Description** | **Default** |
|:-----------|:----------------|:------------|
| target-players | Whether this spell can target players. | false       |
| force      | How forceful the pushback effect is (really the initial velocity of the entities). | 30          |
| additional-vertical-force | An additional amount of force in the vertical direction. Fun to send enemies flying! | 15          |
| max-vertical-force | The maximum vertical force, to prevent targets from being flung too high into the air. | 20          |