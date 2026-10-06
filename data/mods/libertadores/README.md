# Libertadores

Adds founding fathers to FreeCol, most of them from Latin American history,
plus several from 17th-century North America and Europe. With the base
game's 25 the Congress has **48** candidates, which helps on games with
monthly turns.

## New fathers

| Father | Type | Effect |
|--------|------|--------|
| Alexander Hamilton | Trade | +50% tool production |
| Alexander von Humboldt | Exploration | +50% grain and fish production |
| José de San Martín | Military | +10% Sons of Liberty; +25% offence for soldiers and dragoons |
| Francisco de Miranda | Political | +50% liberty bell production |
| Sor Juana Inés de la Cruz | Religious | +25% crosses; schools/colleges/universities cost 50% less |
| Francisco de Orellana | Exploration | +1 line of sight and +1 movement for land units |
| Toussaint Louverture | Military | +15% Sons of Liberty; +25% defence for soldiers and dragoons |
| Vasco da Gama | Trade | Naval +1 movement; Europe voyages −1 turn (lighter than Magellan) |
| Eli Whitney | Trade | +50% cotton and musket production |
| Blas de Lezo | Military | +50% defence for all land units |
| Thomas Cochrane | Military | +50% offence for naval units |
| Miguel Hidalgo | Political | +10% Sons of Liberty; native tension grows 25% slower |
| António Vieira | Religious | +20% chance of native conversion |
| Roque González de Santa Cruz | Religious | Converts and native experts +1 grain, sugar, tobacco, cotton, furs, fish |
| Félix de Azara | Exploration | Horses and livestock breed 50% faster |
| Jorge Juan and Antonio de Ulloa | Exploration | Reveals the land around your colonies; naval units +1 line of sight |
| Roger Williams | Political | +25% crosses; native tension grows 25% slower |
| Jean-Baptiste Colbert | Trade | +25% rum, cigars, cloth and coats |
| John Rolfe | Trade | +50% tobacco |
| Samuel de Champlain | Exploration | Free seasoned scout; native tension grows 25% slower |
| Michiel de Ruyter | Military | +50% defence for naval units |
| Vauban | Military | Stockades, forts and fortresses cost 50% less to buy |
| Marie de l'Incarnation | Religious | Free schoolhouse in every colony that can have one |

Jacob Fugger and Henry Hudson from the base game are kept.

## Weights by game phase

Each father is weighted by era (before 1600, 1600–1700, after 1700)
according to when his effect is most useful, like the base game does (Peter
Minuit early, Adam Smith in the middle, Stuyvesant late):

- Early: Vasco da Gama, Orellana, Humboldt, Azara, Jorge Juan and Ulloa,
  Vieira, John Rolfe, Champlain.
- Middle: Hamilton, Cochrane, Sor Juana, Roque González, Colbert,
  De Ruyter, Vauban, Roger Williams, Marie de l'Incarnation.
- Late: the independence leaders (San Martín, Miranda, Toussaint, Hidalgo),
  Eli Whitney, Blas de Lezo.

Hernando de Soto from the base game is moved to the early game, while
there are still lost city rumours to explore.

With the "Founding Fathers by game phase" game option, a father is only
offered from the era in which his weight is highest.

## Requirements

Félix de Azara and Jorge Juan y Ulloa need this repository's `FreeCol.jar`
(owner breeding modifiers and the `exploreAroundColonies` event). On a stock
FreeCol build they can be elected but do nothing. Roque González also counts
native experts from the **Conversos** mod.

## Enable

Preferences → Mods → enable **Libertadores**, then start a new game (or load a game started with the mod).

Or use `./bin/run-mods.sh`.

## Portraits

Portraits are cropped from public-domain works on Wikimedia Commons and
set in the same gilded frame as the base game's portraits. See `CREDITS.md`
for sources and licenses. High-res variants use the `.size6.jpg` suffix
(400×473); standard cards are 200×237.
