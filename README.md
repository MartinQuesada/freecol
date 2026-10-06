# FreeCol - Colonization Strategy Game

<!-- [![Coverity Scan Build Status](https://img.shields.io/coverity/scan/13524.svg)](https://scan.coverity.com/projects/freecol-freecol) -->
[![Weekly Downloads](https://img.shields.io/sourceforge/dw/freecol.svg)](https://sourceforge.net/projects/freecol/)
[![Total Downloads](https://img.shields.io/sourceforge/dt/freecol.svg)](https://sourceforge.net/projects/freecol/)


[![Latest Release](https://img.shields.io/github/release/FreeCol/freecol/all.svg)](https://github.com/FreeCol/freecol/releases) [![Latest Release Downloads](https://img.shields.io/github/downloads/FreeCol/freecol/total.svg)](https://github.com/FreeCol/freecol/releases)

## This fork: Libertadores mod pack (v0.3.1)

This fork of FreeCol 1.2.1 adds a set of mods inspired by the
[FreeCol 2 ideas forum](https://sourceforge.net/p/freecol/discussion/719665/):
Latin American and 17th-century founding fathers, deeper building chains,
new colonial goods, local consumption of luxuries and native converts that
learn trades. A few small engine changes support them.

### Mods

| Mod | Version | What it adds |
|-----|---------|--------------|
| [Libertadores](data/mods/libertadores/README.md) | 0.3 | 23 founding fathers (San Martín, Miranda, Sor Juana, Humboldt, Hamilton, Colbert, Champlain, Vauban…); 48 candidates in total |
| [Deeper Buildings](data/mods/deeperBuildings/README.md) | 0.3 | Timber mill, City hall, Basilica and Citadel, with their own art (the citadel is a bastioned star fort on the map) |
| [Tasajo](data/mods/tasajo/README.md) | 0.5 | Hunting meat, salt mines and the salting house chain to export salt meat |
| [Lumber Craft](data/mods/lumberCraft/README.md) | 0.1 | Tools and muskets also consume lumber |
| [Livestock](data/mods/livestock/README.md) | 0.3 | Cows and sheep: milk, cheese, wool; brotherhood hall turns cheese into crosses |
| [Cacao](data/mods/cacao/README.md) | 0.3 | Cacao and chocolate; creole salon turns chocolate into liberty bells |
| [Vanilla](data/mods/vanilla/README.md) | 0.2 | Vanilla, a raw luxury export like silver |
| [Taverns](data/mods/tabernas/README.md) | 0.2 | Tavern turns rum into liberty bells |
| [Conversos](data/mods/conversos/README.md) | 0.1 | With Las Casas, converts gain experience in outdoor jobs and become native experts |

### What's new in v0.3.1

New game options, all off by default:

- **Boycotts block paying for buildings:** you can not pay to finish a
  building while goods it is missing, such as tools, are boycotted.
- **Minimum colonies for independence.**
- **Loyalist colonies:** when independence is declared, colonies with less
  than 50% rebels (configurable) stay loyal to the Crown and join the
  REF with every unit in them. You have to take them back to win.

### What's new in v0.3.0

- **Libertadores:** six new fathers from Latin American history (Hidalgo,
  Vieira, Roque González, Azara, Jorge Juan and Ulloa, Cochrane) and seven
  from the 17th century (Roger Williams, Colbert, John Rolfe, Champlain,
  De Ruyter, Vauban, Marie de l'Incarnation). Jacob Fugger and Henry Hudson
  are back; Hamilton now gives +50% tools.
- **Historical order of founding fathers:** new game option. Each father is
  only offered from the age in which he lived (before 1600, 1600–1700,
  after 1700).
- **Conversos** (new mod): Las Casas no longer turns converts into free
  colonists. Converts learn outdoor trades instead and become native
  experts, who are still converts.
- **Native experts** in Cacao, Vanilla and Tasajo (cacao and vanilla
  planters, hunters, salt miners) for use with Conversos.
- **Deeper Buildings:** the citadel now has its own settlement image
  instead of reusing the fortress.
- **Local consumption:** tavern (rum), creole salon (chocolate) and
  brotherhood hall (cheese) produce bells or crosses on their own, with a
  bonus when the colony has at least 50% rebels.

### Engine changes

Some mod features need the `FreeCol.jar` built from this fork. On a stock
FreeCol build those mods still load, but the affected effects do nothing.

- Experience looks for the expert a unit can actually become, so several
  unit types can share an expert production (native experts).
- `model.ability.rebelBonusUnattended`: buildings with no workers get the
  colony's rebel production bonus.
- Owner `breedingFactor` modifiers apply to breeding buildings (Félix de
  Azara).
- `model.event.exploreAroundColonies` father event (Jorge Juan and Ulloa).
- `buildingPriceBonus` works at any percentage, not only −100% (Sor Juana,
  Vauban).
- Founding father `<unit>` accepts `role` and `number` in the schema
  (Champlain's scout).
- Game option `model.option.historicalFoundingFathers` (off by default).
- Game option `model.option.boycottBlocksPayForBuilding` (off by default):
  you can not pay to finish a building while goods it is missing, such as
  tools, are boycotted.
- Game option `model.option.minimumColoniesForIndependence` (0 by default):
  the number of colonies needed to declare independence.
- Game options `model.option.loyalistColonies` (off by default) and
  `model.option.loyalistColoniesThreshold` (50%): when independence is
  declared, colonies below the threshold stay loyal to the Crown and join
  the REF, and have to be taken back to win.

### Playing with the mods

Run the game from this repository with all the mods enabled (macOS/Linux,
needs JDK 11 and Ant):

```sh
./bin/run-mods.sh
MODS=libertadores,conversos ./bin/run-mods.sh   # only some mods
```

The launcher keeps its settings and saves in `.freecol-dev/`.

To use the mods in a regular FreeCol install, copy the mod folders from
`data/mods/` into the user mods folder (`~/Library/Application
Support/freecol/mods/` on macOS), enable them in **Preferences → Mods** and
start a new game.

### Art

Founding father portraits are public-domain works from Wikimedia Commons
(see [credits](data/mods/libertadores/CREDITS.md)). Building, goods and
unit art is recolored from GPL FreeCol art or generated for these mods.

FreeCol is a turn-based strategy game based on the old game
Colonization, and similar to Civilization. The objective of the game is
to create an independent nation.

You start with only a few colonists defying the stormy seas in their
search for new land. Will you guide them on the Colonization of a New
World?

## About FreeCol

**Website: [FreeCol.org](http://www.freecol.org/)**

The FreeCol team aims to create an Open Source version of Colonization
(released under the GPL). At first we'll try to make an exact clone of
Colonization. The visuals will be brought up to date with more recent
standards but will remain clean, simple and functional. Certain new
'features' will be implemented but the gameplay and the rules will be
exactly the same as the original game. Examples of modern features are:
an isometric map and multiplayer support.

This clone will be developed incrementally and result in FreeCol 1.0.0
which will be an almost exact Colonization clone. Incremental
development basically means that we'll add features one at a time. This
allows us to have a running program at all times and also to release an
unfinished but working game once in a while.

Once FreeCol 1.0.0 is finished we'll start working towards FreeCol
2.0.0. FreeCol 2 will go beyond the original Colonization and will have
many new features, it will be an implementation of our (and our users')
image of what Colonization 2 would have been.

## Downloads

#### Supports Mac OS X, Windows, and Linux

The latest binary releases are created weekly and contain Mac OS X, Windows, and Linux installers.

* See: [Weekly Releases](https://github.com/FreeCol/freecol/releases)

## Contributing

Ways you can contribute:

* Download the [latest nightly release](https://github.com/FreeCol/freecol/releases) and play the game.
* Report any bugs you find to our [Bug Tracker.](https://sourceforge.net/p/freecol/bugs/)
* Suggest features or improvements in our [Improvement Requests Tracker.](https://sourceforge.net/p/freecol/improvement-requests/)
* Discuss FreeCol on our [Forums.](https://sourceforge.net/p/freecol/discussion/)
* Contribute to our [code base](https://github.com/FreeCol/freecol) by Forking and submitting a Pull Request. See [Creating a pull request from a fork.](https://help.github.com/articles/creating-a-pull-request-from-a-fork/)

See [doc/developer.tex](doc/developer.tex) for more details on contributing to the FreeCol project.


## Building

Build the latest version of the code by running:

```sh
ant
```

Requires Java 11, Ant, and Java SDK to build.


## License

The source code is licensed under the GPL v2. Most of the content, like artwork, music and sound effects, are also licensed under GPL v2. Some of the content is licensed using CC BY 4.0. Please refer to the README file in the same directory as the included content for more details.

