# Taverns

Rum does not have to go to Europe. Build a **tavern** and your colonists will
drink it while they talk politics.

| | |
|--|--|
| Building | **Tavern** (40 hammers) |
| Effect | 2 rum → 1 liberty bell per turn |
| Rebel bonus | +1 bell at 50% rebels, +2 at 100%, with no extra rum |
| Workers | None; it works on its own, like the chapel |

## Sell or drink?

Every turn the tavern takes rum from the warehouse. If you would rather sell
it, load it onto a wagon or ship before the end of the turn.

Pairs well with the **Cacao** mod (creole salon: chocolate → bells) and the
**Livestock** mod (brotherhood hall: cheese → crosses).

The rebel bonus needs the `model.ability.rebelBonusUnattended` support in
this repository's `FreeCol.jar`; on a stock FreeCol build the tavern still
works, just without the bonus.

## Enable

Preferences → Mods → enable **Taverns**, then start a **new game**.

Or use `./bin/run-mods.sh`.
