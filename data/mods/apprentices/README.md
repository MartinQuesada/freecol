# Apprentices

Colonists learn trades as **apprentices** before becoming experts. Apprentices are formed in the New World only (not recruited in Europe).

## Progression

| Path | Steps |
|------|--------|
| European trades | Free colonist → Apprentice → Expert/Master |
| Native guild crafts | Indian convert → Native apprentice (ceiling) |
| Native field work | Unchanged: use **Conversos** (Las Casas) for native field experts |

Scout, pioneer, veteran soldier and missionary teaching stay direct (no apprentice step).

## Bonuses

- Apprentice farmer: **+1** grain
- Other apprentices: **+2** (additive) to their goods
- Native guild apprentices keep convert limits (no founding colonies, cannot teach, no European master upgrade)

## School timing

| Step | Easy trades | Planters / guild | Preacher / statesman |
|------|-------------|------------------|----------------------|
| Colonist → apprentice | 2 turns | 3 | 4 |
| Apprentice → expert | **3** turns | **4** | **5** |
| Total | 5 | 7 | 9 |

(Experience upgrades are random; apprentice→expert is a bit slower than colonist→apprentice.)

## Graphics

Each European and native guild apprentice has its own portrait (plus `.size2` /
`.size4` variants). Enable **HD Graphics** last so the painted versions load.

## Compatibility

- Enable **Conversos** for native outdoor experts; enable this mod for the apprentice ladder and native guild apprentices.
- Prefer loading **Apprentices** before mods that append education/experience with `preserve="true"` (e.g. Livestock, expandExperience), or those mods may reintroduce direct colonist→expert shortcuts.
- Disable **expandExperience** while using this mod (indoor experience is already covered here).

## Engine note

`UnitType.getTeachingType` allows teaching up to an intermediate type when there is no path to the teacher's full specialty (so converts can be schooled to guild apprentice without unlocking European experts).
