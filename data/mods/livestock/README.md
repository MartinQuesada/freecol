# Livestock

Import **cows** and **sheep** cheaply from Europe, breed them in the colony, and process:

| Chain | Steps |
|-------|--------|
| Dairy | Buy/breed **cows** → **Dairy** (1 cow → 3 milk) → **Cheese house** (milk → cheese export) |
| Wool | Buy/breed **sheep** → **Wool shed** (1 sheep → 3 wool) → **Weaver** (wool → cloth, or cotton as usual) |
| Feasts | Cheese → **Brotherhood hall** (2 cheese → 1 cross per turn, no workers) |

Breeding buildings (**cattle pen**, **sheepfold**) cost **32 hammers** and then work like the horse pasture: you need **2** head, then surplus food grows the herd.

## Herd size matters

Each dairy worker uses up 1 cow per turn and each shepherd 1 sheep. A pen
breeds 2 head per turn while the herd is under 51, and 4 per turn from 51 to
100 (if the colony has surplus food), so:

- 1 worker: the herd keeps growing.
- 2 workers: the herd stays the same while under 51.
- 3 workers: the herd shrinks to 2 and the dairy only makes 6 milk, unless
  the herd is over 50.
- **Without a pen** (or without surplus food) nothing replaces the animals,
  and the dairy eats the whole herd.

## Brotherhood hall

Works with no colonists: every turn it takes 2 cheese from the warehouse and
produces 1 cross (patron-saint feasts attract immigrants). With at least 50%
rebels in the colony it produces 1 extra cross (2 extra at 100%) without
using more cheese. If you would rather sell the cheese, load it onto a wagon
or ship before the end of the turn. Requires population 3; costs 48 hammers.

The rebel bonus needs the `model.ability.rebelBonusUnattended` support in
this repository's `FreeCol.jar`; on a stock FreeCol build the hall still
works, just without the bonus.

## Boycotts

Same rule as horses: if Europe boycotts cattle or sheep, you cannot buy more there.
**Local breeding still works.** Tips:

1. Buy a starter pair early (price is low).
2. Split animals to a second colony when you can.
3. **Alexander Hamilton** (Libertadores mod) lifts boycotts.

## Enable

Preferences → Mods → enable **Livestock**, then start a **new game**.

Or use `./bin/run-mods.sh`.
