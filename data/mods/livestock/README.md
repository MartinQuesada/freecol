# Livestock

Import **cows** and **sheep** cheaply from Europe, breed them in the colony, and process:

| Chain | Steps |
|-------|--------|
| Dairy | Buy/breed **cows** → **Dairy** (milk the herd) → **Cheese house** (milk → cheese) |
| Wool | Buy/breed **sheep** → **Wool shed** (shear the flock) → **Weaver** (wool → cloth) |
| Feasts | Cheese → **Brotherhood hall** (2 cheese → 1 cross per turn, no workers) |
| Experts | **Expert Dairy Worker**, **Expert Shepherd**, **Master Cheesemaker** |

Milk is perishable: it can not be traded in Europe or through the custom
house. Make it into cheese to export it.

Breeding buildings (**cattle pen**, **sheepfold**) cost **32 hammers** and then work like the horse pasture: you need **2** head, then surplus food grows the herd.

## Milking and shearing (no slaughter)

Animals are **not consumed** by dairy or wool work. Each cow yields
**3 milk** per turn and each sheep **3 wool** per turn. Workers produce
up to that herd limit. With enough animals they rotate and experts can
work at full rate; with too few animals, output is capped until the herd
grows.

Food works like **horses**: pens only use **surplus** food to breed.
There is no per-animal upkeep.

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
