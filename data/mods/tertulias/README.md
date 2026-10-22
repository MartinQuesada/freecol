# Tertulias

Cigars do not have to go to Europe. Build a **tertulia** and the colony's
politicians will smoke them while they plot.

The word is the same in English. A tertulia was the evening gathering where
criollo politicians met. It is not a tavern (that one drinks rum) and not
the creole salon (that one drinks chocolate).

| | |
|--|--|
| Building | **Tertulia** (48 hammers, population 3) |
| Effect | 2 cigars → 1 liberty bell per turn |
| Rebel bonus | +1 bell at 50% rebels, +2 at 100%, with no extra cigars |
| Workers | None; it works on its own, like the tavern |

Cigars and rum sell for the same price in Europe, so the bell rate matches
the tavern. The tertulia costs a few more hammers and waits until the
colony has 3 colonists.

## Sell or plot?

Every turn the tertulia takes cigars from the warehouse. If you would rather
sell them, load them onto a wagon or ship before the end of the turn.

Pairs with the **Taverns** mod (rum → bells).

The rebel bonus needs the `model.ability.rebelBonusUnattended` support in
this repository's `FreeCol.jar`; on a stock FreeCol build the tertulia still
works, just without the bonus.

## Enable

Preferences → Mods → enable **Tertulias**, then start a **new game**.

Or use `./bin/run-mods.sh`.
