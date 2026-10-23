# Changes from FreeCol 1.2.1

This document lists everything this fork adds on top of upstream
[FreeCol 1.2.1](https://github.com/FreeCol/freecol). The project is
**FreeCol Americas** (currently 0.3.4 in development): optional mods plus a
few engine changes that support them. **Libertadores** is the founding-fathers
mod within the pack.

Mod games need a **new game**. Savegames without these mods are unaffected.
Some father and goods effects need the `FreeCol.jar` built from this
repository; on a stock FreeCol build those mods still load, but the
affected effects do nothing.

---

## Mods

| Mod | Version | What it adds |
|-----|---------|--------------|
| [Libertadores](data/mods/libertadores/README.md) | 0.5 | 25 founding fathers; 50 candidates with the base game |
| [Deeper Buildings](data/mods/deeperBuildings/README.md) | 0.3 | Timber mill, City hall, Basilica and Citadel (star fort on the map) |
| [Tasajo](data/mods/tasajo/README.md) | 0.6 | Hunting meat, salt mines, salting house → salt meat (tasajo) |
| [Lumber Craft](data/mods/lumberCraft/README.md) | 0.1 | Tools and muskets also consume lumber |
| [Livestock](data/mods/livestock/README.md) | 0.5 | Cows and sheep: milk, cheese, wool; dairy/shepherd/cheesemaker experts; brotherhood hall |
| [Cacao](data/mods/cacao/README.md) | 0.3 | Cacao + milk → chocolate (needs Livestock); creole salon |
| [Vanilla](data/mods/vanilla/README.md) | 0.2 | Vanilla as a raw luxury export |
| [Taverns](data/mods/tabernas/README.md) | 0.2 | Tavern turns rum into liberty bells |
| [Tertulias](data/mods/tertulias/README.md) | 0.1 | Tertulia turns cigars into liberty bells |
| [Conversos](data/mods/conversos/README.md) | 0.1 | Las Casas: convert outdoor experts and guild apprentices |
| [Apprentices](data/mods/apprentices/README.md) | 0.1 | Free colonist → apprentice → expert (colonists only) |
| [HD Graphics](data/mods/hdGraphics/README.md) | 0.2 | High-res units, goods icons, buildings, settlements, father portraits, terrain blends |

Enable them under Preferences → Mods, or run:

```sh
./bin/run-mods.sh
```

---

## Libertadores founding fathers

With the base game's 25 fathers, the Congress has **50** candidates.

| Father | Type | Effect |
|--------|------|--------|
| Alexander Hamilton | Trade | +50% tool production |
| Alexander von Humboldt | Exploration | +50% grain and fish production |
| José de San Martín | Military | +10% Sons of Liberty; +25% offence for soldiers and dragoons |
| Francisco de Miranda | Political | +20% liberty bell production |
| Sor Juana Inés de la Cruz | Religious | +25% crosses; schools/colleges/universities cost 50% less hammers and tools |
| Francisco de Orellana | Exploration | +1 line of sight and +1 movement for land units |
| Toussaint Louverture | Military | +15% Sons of Liberty; +25% defence for soldiers and dragoons |
| Vasco da Gama | Trade | Naval +1 movement; Europe voyages −1 turn |
| Eli Whitney | Trade | +50% cotton and musket production |
| Blas de Lezo | Military | +50% defence for all land units |
| Thomas Cochrane | Military | +50% offence for naval units |
| Miguel Hidalgo | Political | +10% Sons of Liberty; native tension grows 25% slower |
| António Vieira | Religious | +20% chance of native conversion |
| Roque González de Santa Cruz | Religious | Converts and native experts +1 farmed goods and fish |
| Félix de Azara | Exploration | Horses and livestock breed 50% faster |
| Jorge Juan and Antonio de Ulloa | Exploration | Reveals land around colonies; naval +1 line of sight |
| Roger Williams | Political | +25% crosses; native tension grows 25% slower |
| Jean-Baptiste Colbert | Trade | +25% rum, cigars, cloth and coats |
| John Rolfe | Trade | +50% tobacco |
| Samuel de Champlain | Exploration | Free seasoned scout; native tension grows 25% slower |
| Michiel de Ruyter | Military | +50% defence for naval units |
| Vauban | Military | Stockades, forts and fortresses cost 50% less hammers and tools |
| Marie de l'Incarnation | Religious | Free schoolhouse in every colony that can have one |
| Joshua Humphreys | Trade | Ships repair twice as fast and cost 25% less hammers and tools |
| Henry Knox | Military | Artillery costs 25% less; damaged artillery repaired at half cost in colonies with an armory |

Jacob Fugger and Henry Hudson from the base game are kept. Hernando de Soto
is weighted almost only for the early game (`weight1=15`, `weight2=2`,
`weight3=0`) so he is offered while lost city rumours remain. Weights follow
game phase (early / middle / late) by usefulness of the effect, not by when
the father lived.

Portraits use the same gilded frame as the base game. Most are cropped
from public-domain works on Wikimedia Commons; Joshua Humphreys has an
imaginary likeness (no portrait of him is known). See
[CREDITS.md](data/mods/libertadores/CREDITS.md).

---

## Other mod highlights

- **Local consumption:** tavern (rum → bells), creole salon (chocolate →
  bells) and brotherhood hall (cheese → crosses) work with no colonists and
  give a bonus at ≥50% rebels.
- **Conversos:** with Las Casas, converts gain experience in outdoor jobs
  (native experts) and guild crafts (native apprentices, ceiling below
  European master). They stay converts (no colonies, poor in untrained
  buildings, no teaching). Las Casas no longer turns converts into free
  colonists.
- **Apprentices:** European trades go free colonist → apprentice → expert
  (apprentices are not recruited in Europe; school totals 5/7/9 turns).
  Convert progression is only in Conversos.
- **Cacao chocolate:** chocolatier buildings consume cacao and milk
  (Livestock). Without milk in the warehouse, no chocolate is made.
- **Native experts** in Cacao, Vanilla and Tasajo (cacao/vanilla planters,
  hunters, salt miners) for use with Conversos.
- **Livestock experts:** Expert Dairy Worker, Expert Shepherd and Master
  Cheesemaker (recruit in Europe, school or experience); each doubles milk,
  wool or cheese. Cloth still uses the base-game Master Weaver.
- **Perishable goods:** Livestock milk and Tasajo fresh meat cannot be
  traded in Europe or through the custom house. Export cheese or tasajo
  instead.
- **HD Graphics:** all 64 base unit images redrawn; sharper native
  settlements; oil portraits for all 50 fathers; wider terrain transitions.
  Units refreshed in a consistent painted style (civilians, soldiers,
  natives, ships, wagons/artillery, scout/pioneer/missionary roles);
  goods icons (base + Libertadores mod goods); colony building icons
  (base + Deeper Buildings, Cacao, Livestock, Taverns, Tertulias, Tasajo) and
  fortification outside backgrounds (stockade through citadel);
  Libertadores mod expert portraits (cacao, livestock, tasajo, vanilla,
  conversos, apprentices). The Petty Criminal is a transported convict
  (ragged, barefoot, sack), not in stocks. Distinct New World destination
  icon (not Lost City). Attack animations stay on the base FreeCol sprites
  for now. Graphics only.

---

## New game options

| Option | Default | Effect |
|--------|---------|--------|
| Founding Fathers by game phase | off | Offer each father from the era where his weight is highest |
| Founding Father cost by turns per year | on | With more than two turns a year, fathers cost proportionally more (×6 with monthly turns) |
| Boycotts block paying for buildings | off | Cannot pay to finish a building while missing goods are boycotted |
| Minimum colonies for independence | 0 | Minimum number of colonies to declare independence |
| Loyalist colonies | off | Colonies below a rebel threshold stay loyal and join the REF |
| Loyalist colonies threshold | 50% | Rebel % below which a colony stays loyal |
| Forests grow back | off | Cleared unused forest regrows after N years, losing plowing |
| Years for forests to grow back | 20 | Years until cleared forest regrows |
| Repair damaged artillery | off | Damaged artillery repaired in colonies with an armory (hammers then tools) |
| Repair damaged artillery hammers / tools | 40 / 40 | Cost of that repair |
| Ship repair costs lumber | off | Each colony repair turn costs lumber after production |
| Lumber per turn of ship repair | 20 | Lumber taken per repair turn |
| Regulars teach regulars | off | Colonial regulars train free colonists into colonial regulars (8 turns) |
| Building on hills costs more | off | Buildings cost more hammers/tools on elevated tiles |
| Extra building cost on hills | 25% | Extra percentage (rounded up) |
| REF amphibious assault | on | REF attacks from ships when the landmass has no free beach (−75% amphibious penalty) |

---

## Bug fixes and UI (fork)

- **REF:** no longer stays in Europe forever when every rebel colony is
  surrounded by units. It lands and attacks the guards. Land units left in
  Europe after a failed launch re-embark. With amphibious assault on, it
  can attack from ships when every beach is held.
- **Assign to teacher:** only offered to units working inside the colony
  (not fortified soldiers outside).
- **Independent nation name:** keeps accented letters and ñ.
- **Spanish:** Man of War is "Navío de línea" (not "Hombre de Guerra").
  Continental Army is "Ejército continental" (not "Armada continental").
- **Founding father recruitment:** no longer stalls when every remaining
  father of a type has no weight in the current era (common with monthly
  turns before 1600).
- **Warehouse:** perishable goods cannot be marked for export.
- **Warehouse display:** the goods bar always follows the client option
  "Hide goods in warehouse when fewer than" (0 = show every type,
  including empty). Debug mode no longer forces hiding empty goods.
- **Convert experience UI:** after Las Casas, converts show the same
  experience progress bar as colonists (menu falls back to work type;
  Apprentices keeps Conversos outdoor upgrade paths).
- **Trade routes:** the editor states that listed goods are loaded at a
  stop, that other goods on the carrier are unloaded, and that a stop
  with nothing listed unloads everything ("Unload all" on empty stops).
- **End of turn:** no longer hangs when a natural disaster damages a
  building and ejects its workers (null production type).

---

## Engine / mod-development API

These support the mods above. Features that need this fork's jar are noted
in each mod README.

- Experience targets the expert a unit can actually become (shared expert
  production for native experts).
- `UnitType.getTeachingType` may teach an intermediate specialty when there
  is no path to the teacher's full type (Apprentices: converts → guild
  apprentice without unlocking European masters).
- Ability `model.ability.rebelBonusUnattended`: unattended buildings get
  the colony rebel production bonus.
- Owner `model.modifier.breedingFactor` on breeding buildings (Azara).
- Father event `model.event.exploreAroundColonies` (Jorge Juan y Ulloa).
- `model.modifier.buildingPriceBonus` works at any percentage, not only −100%.
- Founding father `<unit>` accepts `role` and `number` in the schema
  (Champlain's scout).
- Goods attribute `perishable`: refused by `Player.canTrade()` for Europe
  and the custom house.
- Ability `model.ability.repairDemotedUnits` on armories (damaged artillery).
- Ability `model.ability.repairDamagedArtillery` on the player (repair even
  with the game option off; Knox).
- Modifiers `model.modifier.buildCost`, `model.modifier.repairRate`,
  `model.modifier.repairCost` (Humphreys, Knox, Sor Juana, Vauban).
- `Colony.getBuildCost()`: colony-aware build cost (hills bonus and father
  build-cost modifiers); build queue, panels, prices and AI use it.
- `Map.hasFreeBeach()`: used by REF amphibious assault.
- Launcher `bin/run-mods.sh` and Debian setup helper for JDK 11 + Ant.

---

## Version history (fork)

| Pack | Date | Highlights |
|------|------|------------|
| 0.3.0 | 5 Oct 2026 | Libertadores 0.3, Conversos, local consumption, native experts, father-by-phase option |
| 0.3.1 | 6 Oct 2026 | Boycott / independence / loyalist colony options |
| 0.3.2 | 6 Oct 2026 | Father weights by usefulness; framed portraits; option renamed |
| 0.3.3 | 7 Oct 2026 | HD Graphics; father cost scaled by turns per year; recruitment stall fix |
| 0.3.4 | in development | Forest regrowth; artillery/ship repair options; hills cost; regulars teach; REF surround + amphibious; perishable milk/meat; nation name / Spanish MoW; Humphreys & Knox (50 fathers); buildCost / repair modifiers; trade-route load/unload labels; warehouse goods display; disaster turn hang fix; Livestock dairy/shepherd/cheesemaker experts; full HD unit refresh (Petty Criminal without stocks); **Apprentices** mod (colonists only, 5/7/9 school turns); **Conversos** Las Casas gate for convert outdoor+guild paths; chocolate needs milk; De Soto early-only (`15/2/0`); New World icon; ship/tobacco transparency fixes; combat animation load no longer drops result messages |

Detailed per-release notes live in [CHANGELOG.md](CHANGELOG.md) under
"FreeCol Americas". Upstream FreeCol notes follow below those sections.
