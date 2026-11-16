/**
 *  Copyright (C) 2002-2024   The FreeCol Team
 *
 *  This file is part of FreeCol.
 *
 *  FreeCol is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  FreeCol is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with FreeCol.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.freecol.server.ai;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;

import net.sf.freecol.common.io.FreeColXMLReader;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.CombatModel;
import net.sf.freecol.common.model.Constants.IndianDemandAction;
import net.sf.freecol.common.model.FeatureContainer;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Modifier;
import net.sf.freecol.common.model.NativeTrade;
import net.sf.freecol.common.model.NativeTrade.NativeTradeAction;
import net.sf.freecol.common.model.NativeTradeItem;
import net.sf.freecol.common.model.Ownable;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Stance;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.model.Tension;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Turn;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Role;
import net.sf.freecol.common.model.pathfinding.CostDecider;
import net.sf.freecol.common.model.pathfinding.CostDeciders;
import net.sf.freecol.common.option.GameOptions;
import static net.sf.freecol.common.util.CollectionUtils.*;
import net.sf.freecol.common.util.LogBuilder;
import net.sf.freecol.common.util.RandomChoice;
import static net.sf.freecol.common.util.RandomUtils.*;
import net.sf.freecol.server.ai.mission.DefendSettlementMission;
import net.sf.freecol.server.ai.mission.IndianBringGiftMission;
import net.sf.freecol.server.ai.mission.IndianDemandMission;
import net.sf.freecol.server.ai.mission.Mission;
import net.sf.freecol.server.ai.mission.UnitSeekAndDestroyMission;
import net.sf.freecol.server.ai.mission.UnitWanderHostileMission;


/**
 * Objects of this class contains AI-information for a single {@link
 * Player} and is used for controlling this player.
 *
 * The method {@link #startWorking} gets called by the
 * {@link AIInGameInputHandler} when it is this player's turn.
 */
public final class NativeAIPlayer extends MissionAIPlayer {

    private static final Logger logger = Logger.getLogger(NativeAIPlayer.class.getName());

    public static final int MAX_DISTANCE_TO_BRING_GIFTS = 5;

    public static final int MAX_NUMBER_OF_GIFTS_BEING_DELIVERED = 1;

    public static final int MAX_DISTANCE_TO_MAKE_DEMANDS = 5;

    public static final int MAX_NUMBER_OF_DEMANDS = 1;

    /**
     * Debug helper to keep track of why/what the units are doing.
     * Do not serialize.
     */
    private final java.util.Map<Unit, String> reasons = new HashMap<>();


    /**
     * Creates a new {@code AIPlayer}.
     *
     * @param aiMain The main AI-class.
     * @param player The player that should be associated with this
     *            {@code AIPlayer}.
     */
    public NativeAIPlayer(AIMain aiMain, Player player) {
        super(aiMain, player);

        this.initialized = getPlayer() != null;
    }

    /**
     * Creates a new {@code AIPlayer}.
     *
     * @param aiMain The main AI-object.
     * @param xr The input stream containing the XML.
     * @throws XMLStreamException if a problem was encountered during parsing.
     */
    public NativeAIPlayer(AIMain aiMain,
                          FreeColXMLReader xr) throws XMLStreamException {
        super(aiMain, xr);

        this.initialized = getPlayer() != null;
    }


    /**
     * Simple initialization of AI missions given that we know the starting
     * conditions.
     *
     * @param lb A {@code LogBuilder}  to log to.
     */
    private void initializeMissions(LogBuilder lb) {
        final Player player = getPlayer();
        lb.add("\n  Initialize");

        // Give defensive missions up to the minimum expected defence,
        // leave the rest with the default wander-hostile mission.
        for (IndianSettlement is : player.getIndianSettlementList()) {
            List<Unit> units = is.getAllUnitsList();
            while (units.size() > is.getRequiredDefenders()) {
                Unit u = units.remove(0);
                AIUnit aiu = getAIUnit(u);
                Mission m = getWanderHostileMission(aiu);
                if (m != null) lb.add(" ", m);
            }
            for (Unit u : units) {
                AIUnit aiu = getAIUnit(u);
                Mission m = getDefendSettlementMission(aiu, is);
                if (m != null) lb.add(" ", m);
            }
        }
    }

    /**
     * Determines the stances towards each player.
     * That is: should we declare war?
     *
     * @param lb A {@code LogBuilder} to log to.
     */
    private void determineStances(LogBuilder lb) {
        final Player player = getPlayer();
        lb.mark();

        for (Player p : getGame().getLivePlayerList(player)) {
            Stance newStance = determineStance(p);
            if (newStance != player.getStance(p)) {
                getAIMain().getFreeColServer().getInGameController()
                    .changeStance(player, newStance, p, true);
                lb.add(" ", p.getDebugName(), "->", newStance, ", ");
            }
        }
        if (lb.grew("\n  Stance changes:")) lb.shrink(", ");
    }

    /**
     * {@inheritDoc}
     *
     * Escalate to war when any settlement is at least ANGRY toward a
     * European, even if player-level tension has not yet crossed the
     * normal war threshold.
     */
    @Override
    protected Stance determineStance(Player other) {
        final Stance fromTension = super.determineStance(other);
        if (!other.isEuropean()) {
            return fromTension;
        }
        // Honour tension-based de-escalation out of WAR (gifts / peace tribute).
        final Stance current = getPlayer().getStance(other);
        if (current == Stance.WAR) {
            return fromTension;
        }
        if (fromTension == Stance.WAR) {
            return Stance.WAR;
        }
        // From peace/cease-fire, escalate if any settlement is angry.
        for (IndianSettlement is : getPlayer().getIndianSettlementList()) {
            final Tension alarm = is.getAlarm(other);
            if (alarm != null
                    && alarm.getLevel().compareTo(Tension.Level.ANGRY) >= 0) {
                return Stance.WAR;
            }
        }
        return fromTension;
    }

    /**
     * Takes the necessary actions to secure the settlements.
     * This is done by making new military units or to give existing
     * units new missions.
     *
     * @param lb A {@code LogBuilder} to log to.
     */
    private void secureSettlements(LogBuilder lb) {
        List<IndianSettlement> settlements
            = getPlayer().getIndianSettlementList();
        redistributeMilitaryGoods(settlements, lb);
        // Capital and most-threatened camps first so they keep units/arms.
        for (IndianSettlement is : settlementsByPriority(settlements)) {
            lb.mark();
            equipBraves(is, lb);
            secureIndianSettlement(is, lb);
            if (lb.grew("\n  At ", is.getName())) lb.shrink(", ");
        }
    }

    /**
     * Sort settlements so the capital and threatened camps are secured first.
     *
     * @param settlements The settlements to order.
     * @return A new ordered list.
     */
    private List<IndianSettlement> settlementsByPriority(
            List<IndianSettlement> settlements) {
        return sort(settlements, Comparator
            .comparing((IndianSettlement is) -> !is.isCapital())
            .thenComparingDouble(is -> -estimateThreatStrength(is)));
    }

    /**
     * Move surplus muskets/horses from quiet camps to threatened ones.
     *
     * @param settlements All settlements of this player.
     * @param lb A {@code LogBuilder} to log to.
     */
    private void redistributeMilitaryGoods(List<IndianSettlement> settlements,
                                           LogBuilder lb) {
        if (settlements.size() < 2) return;

        lb.mark();
        List<IndianSettlement> byNeed = sort(settlements,
            Comparator.comparingDouble(this::militaryGoodsNeed).reversed());
        for (IndianSettlement needy : byNeed) {
            if (militaryGoodsNeed(needy) <= 0) break;
            IndianSettlement donor = maximize(settlements,
                is -> is != needy && militaryGoodsSurplus(is) > 0,
                Comparator.comparingInt(this::militaryGoodsSurplus));
            if (donor == null) break;
            donor.tradeGoodsWithSettlement(needy);
            lb.add(donor.getName(), "->", needy.getName(), ", ");
        }
        if (lb.grew("\n  Arms redistribution: ")) lb.shrink(", ");
    }

    /**
     * How badly a settlement needs more military goods.
     *
     * @param is The settlement to score.
     * @return Higher means more need.
     */
    private double militaryGoodsNeed(IndianSettlement is) {
        final Specification spec = getSpecification();
        int stock = 0;
        for (GoodsType gt : transform(spec.getGoodsTypeList(),
                                      GoodsType::getMilitary)) {
            stock += is.getGoodsCount(gt);
        }
        int needEquip = 0;
        for (Unit u : is.getAllUnitsList()) {
            if (is.canImproveUnitMilitaryRole(u) != null) needEquip++;
        }
        return estimateThreatStrength(is) * 10 + needEquip * 5 - stock;
    }

    /**
     * How much military stock a settlement can spare.
     *
     * @param is The settlement to score.
     * @return Higher means more surplus.
     */
    private int militaryGoodsSurplus(IndianSettlement is) {
        final Specification spec = getSpecification();
        final int keep = 50;
        int surplus = 0;
        for (GoodsType gt : transform(spec.getGoodsTypeList(),
                                      GoodsType::getMilitary)) {
            surplus += Math.max(0, is.getGoodsCount(gt) - keep);
        }
        // Threatened camps should not donate.
        if (estimateThreatStrength(is) > 0) {
            surplus = Math.max(0, surplus / 4);
        }
        return surplus;
    }

    /**
     * Estimate local threat strength around a settlement.
     *
     * @param is The settlement to evaluate.
     * @return Aggregated offence power of alarming neighbours.
     */
    private double estimateThreatStrength(IndianSettlement is) {
        final Player player = getPlayer();
        final CombatModel cm = getGame().getCombatModel();
        double total = 0.0;
        for (Tile t : is.getTile().getSurroundingTiles(is.getRadius() + 1)) {
            if (t.getUnitCount() == 0) continue;
            final Unit first = t.getFirstUnit();
            if (first == null || first.getOwner() == player) continue;
            final Player enemy = first.getOwner();
            final Tension tension = is.getAlarm(enemy);
            if (tension == null
                    || tension.getLevel().compareTo(Tension.Level.CONTENT) <= 0) {
                continue;
            }
            final double bonus = Math.max(0,
                tension.getLevel().ordinal() - Tension.Level.CONTENT.ordinal());
            if (t.isLand()) {
                total += sumDouble(t.getUnits(),
                    u -> cm.getOffencePower(u, is) > 0,
                    u -> cm.getOffencePower(u, is) + bonus);
            } else {
                // Naval / embarked threats from adjacent water.
                for (Unit u : t.getUnitList()) {
                    if (u.getOwner() == player) continue;
                    if (u.isOffensiveUnit()) {
                        total += cm.getOffencePower(u, is) + bonus;
                    }
                    if (u.isNaval() && u.hasCargo()) {
                        total += sumDouble(u.getUnitList(),
                            Unit::isOffensiveUnit,
                            land -> cm.getOffencePower(land, is) + bonus);
                    }
                }
            }
        }
        return total;
    }

    /**
     * Greedily equips braves with horses and muskets.
     *
     * Public for the test suite.
     *
     * @param is The {@code IndianSettlement} where the equipping occurs.
     * @param lb A {@code LogBuilder} to log to.
     */
    public void equipBraves(IndianSettlement is, LogBuilder lb) {
        // Prioritize promoting partially equipped units to full dragoon
        final Comparator<Unit> comp = getGame().getCombatModel()
            .getMilitaryStrengthComparator();
        for (Unit u : sort(is.getAllUnitsList(), comp)) {
            Role r = is.canImproveUnitMilitaryRole(u);
            if (r != null) {
                Role old = u.getRole();
                if (getAIUnit(u).equipForRole(r) && u.getRole() != old) {
                    lb.add(u, " upgraded from ", old.getSuffix(), ", ");
                }
            }
        }
    }

    /**
     * Takes the necessary actions to secure an indian settlement
     *
     * Public for the test suite.
     *
     * @param is The {@code IndianSettlement} to secure.
     * @param lb A {@code LogBuilder} to log to.
     */
    public void secureIndianSettlement(final IndianSettlement is,
                                       LogBuilder lb) {
        final AIMain aiMain = getAIMain();
        final Player player = getPlayer();
        final CombatModel cm = getGame().getCombatModel();
        final int minimumDefence = is.getType().getMinimumSize() - 1;
        DefendSettlementMission dm;

        // Collect native units and defenders
        List<Unit> units = is.getAllUnitsList();
        List<Unit> defenders = new ArrayList<>();
        for (Unit u : is.getOwnedUnitList()) {
            if (!units.contains(u)) units.add(u);
        }

        // Collect the current defenders
        for (Unit u : new ArrayList<>(units)) {
            AIUnit aiu = aiMain.getAIUnit(u);
            if (aiu == null) {
                units.remove(u);
            } else if ((dm = aiu.getMission(DefendSettlementMission.class)) != null
                && dm.getTarget() == is) {
                defenders.add(u);
                units.remove(u);
            } else if (Mission.invalidNewMissionReason(aiu) != null) {
                units.remove(u);
            }
        }

        // Collect threats and other potential defenders
        final HashMap<Tile, Double> threats = new HashMap<>();
        Player enemy;
        Tension tension;
        for (Tile t : is.getTile().getSurroundingTiles(is.getRadius() + 1)) {
            if (t.getUnitCount() == 0) {
                ; // Do nothing
            } else if ((enemy = t.getFirstUnit().getOwner()) == player) {
                // Its one of ours!
                if (!t.isLand()) continue;
                for (Unit u : t.getUnitList()) {
                    AIUnit aiu;
                    if (defenders.contains(u) || units.contains(u)
                        || (aiu = aiMain.getAIUnit(u)) == null) {
                        ; // Do nothing
                    } else if ((dm = aiu.getMission(DefendSettlementMission.class)) != null
                        && dm.getTarget() == is) {
                        defenders.add(u);
                    } else if (Mission.invalidNewMissionReason(aiu) == null) {
                        units.add(u);
                    }
                }
            } else if ((tension = is.getAlarm(enemy)) == null
                || tension.getLevel().compareTo(Tension.Level.CONTENT) <= 0) {
                ; // Not regarded as a threat
            } else if (t.isLand()) {
                // Evaluate the land threat
                double threshold, bonus, value = 0.0;
                if (tension.getLevel().compareTo(Tension.Level.DISPLEASED) <= 0) {
                    threshold = 1.0;
                    bonus = 0.0f;
                } else {
                    threshold = 0.0;
                    bonus = (float)tension.getLevel().ordinal()
                        - Tension.Level.CONTENT.ordinal();
                }
                value += sumDouble(t.getUnits(),
                                   u -> cm.getOffencePower(u, is) > threshold,
                                   u -> cm.getOffencePower(u, is) + bonus);
                if (value > 0.0) threats.put(t, value);
            } else {
                // Naval / embarked threats from water tiles.
                double bonus = Math.max(0,
                    (float)tension.getLevel().ordinal()
                        - Tension.Level.CONTENT.ordinal());
                double value = 0.0;
                for (Unit u : t.getUnitList()) {
                    if (u.getOwner() == player) continue;
                    if (u.isOffensiveUnit()) {
                        value += cm.getOffencePower(u, is) + bonus;
                    }
                    if (u.isNaval() && u.hasCargo()) {
                        value += sumDouble(u.getUnitList(),
                            Unit::isOffensiveUnit,
                            land -> cm.getOffencePower(land, is) + bonus);
                    }
                }
                if (value > 0.0) threats.put(t, value);
            }
        }

        // Sort the available units by proximity to the settlement.
        // Simulates favouring the first warriors found by outgoing messengers.
        // Also favour units native to the settlement.
        final int homeBonus = 3;
        final Tile isTile = is.getTile();
        final Comparator<Unit> isComparator = cachingIntComparator(u -> {
                final Tile t = u.getTile();
                return t.getDistanceTo(isTile)
                    - ((u.getHomeIndianSettlement() == is) ? homeBonus : 0);
            });

        // Defender demand starts from threatened-tile count (so counter-attack
        // units remain free), then adds a little more for heavy stacks.
        // When besieged, keep a skeleton garrison and break out with the rest.
        final double totalThreat = sumDouble(threats.values(), v -> v);
        final boolean besieged = isBesieged(is, threats);
        int needed = minimumDefence + threats.size();
        if (totalThreat > threats.size() * 4.0) {
            needed += Math.min(2, (int)(totalThreat / 15.0));
        }
        if (besieged) {
            needed = Math.max(1, minimumDefence / 2);
            lb.add(" besieged, ");
        } else if (!threats.isEmpty()) {
            // Always leave at least one unit free to counter-attack.
            final int pool = defenders.size() + units.size();
            needed = Math.min(needed, Math.max(minimumDefence, pool - 1));
        }
        if (defenders.size() < needed) { // More needed, call some in.
            units.sort(isComparator);
            while (!units.isEmpty()) {
                Unit u = units.remove(0);
                AIUnit aiu = aiMain.getAIUnit(u);
                Mission m = getDefendSettlementMission(aiu, is);
                if (m != null) {
                    lb.add(m, ", ");
                    defenders.add(u);
                    if (defenders.size() >= needed) break;
                }
            }
        } else if (defenders.size() > needed) { // Less needed, release them
            defenders.sort(isComparator.reversed());
            while (defenders.size() > needed) {
                units.add(defenders.remove(0));
            }
        }

        // Sort threat tiles by threat value (strongest first).
        final Comparator<Tile> threatComp
            = Comparator.<Tile>comparingDouble(t -> threats.get(t)).reversed();
        List<Tile> threatTiles = sort(threats.keySet(), threatComp);

        if (!defenders.isEmpty()) {
            lb.add(" defend with:");
            for (Unit u : defenders) lb.add(" ", u);
            lb.add(" minimum=", minimumDefence,
                   " threatStrength=", (int)totalThreat, ", ");
        }

        // Assign units to attack the threats, greedily choosing closest unit.
        while (!threatTiles.isEmpty() && !units.isEmpty()) {
            Tile tile = threatTiles.remove(0);
            if (!tile.isLand()) continue; // Seek land targets; naval handled elsewhere.
            final ToIntFunction<Unit> score = cacheInt(u ->
                u.getTile().getDistanceTo(tile));
            final Predicate<Unit> validPred = u ->
                UnitSeekAndDestroyMission.invalidMissionReason(aiMain.getAIUnit(u),
                    tile.getDefendingUnit(u)) == null
                && score.applyAsInt(u) >= 0;
            final Comparator<Unit> scoreComp = Comparator.comparingInt(score);
            Unit unit = minimize(units, validPred, scoreComp);
            if (unit == null) continue; // Declined to attack.
            units.remove(unit);
            AIUnit aiUnit = aiMain.getAIUnit(unit);
            Unit target = tile.getDefendingUnit(unit);
            Mission m = getSeekAndDestroyMission(aiUnit, target);
            if (m != null) lb.add(m, ", ");
        }

        // Siege breakout: remaining braves push the nearest enemy colony.
        if (besieged && !units.isEmpty()) {
            assignSiegeBreakout(is, units, lb);
        }
    }

    /**
     * Is a settlement effectively besieged by hostile neighbours?
     *
     * @param is The settlement to check.
     * @param threats The evaluated threat tiles.
     * @return True if most adjacent land tiles are threatened.
     */
    private boolean isBesieged(IndianSettlement is,
                               java.util.Map<Tile, Double> threats) {
        int land = 0;
        int threatened = 0;
        for (Tile t : is.getTile().getSurroundingTiles(1)) {
            if (!t.isLand()) continue;
            land++;
            if (threats.containsKey(t)) threatened++;
        }
        return land > 0 && threatened * 2 >= land;
    }

    /**
     * Send leftover units at a besieged settlement against the nearest
     * enemy colony that can legally be attacked.
     *
     * @param is The besieged settlement.
     * @param units Available units (mutated).
     * @param lb A {@code LogBuilder} to log to.
     */
    private void assignSiegeBreakout(IndianSettlement is, List<Unit> units,
                                     LogBuilder lb) {
        final AIMain aiMain = getAIMain();
        final Tile home = is.getTile();
        while (!units.isEmpty()) {
            Unit unit = units.remove(0);
            AIUnit aiUnit = aiMain.getAIUnit(unit);
            if (aiUnit == null) continue;
            Location target = UnitSeekAndDestroyMission
                .findMissionTarget(aiUnit, 8, true, false);
            if (target == null) {
                // Prefer any nearby colony of an angry European.
                Colony best = null;
                int bestDist = Integer.MAX_VALUE;
                for (Tile t : home.getSurroundingTiles(8)) {
                    Colony c = t.getColony();
                    if (c == null) continue;
                    if (UnitSeekAndDestroyMission
                            .invalidMissionReason(aiUnit, c) != null) continue;
                    int dist = home.getDistanceTo(c.getTile());
                    if (dist >= 0 && dist < bestDist) {
                        bestDist = dist;
                        best = c;
                    }
                }
                target = best;
            }
            Mission m = (target == null) ? null
                : getSeekAndDestroyMission(aiUnit, target);
            if (m != null) {
                lb.add("breakout ", m, ", ");
            } else {
                m = getWanderHostileMission(aiUnit);
                if (m != null) lb.add(m, ", ");
            }
        }
    }

    /**
     * Gives a mission to all units.
     *
     * @param lb A {@code LogBuilder} to log to.
     */
    private void giveNormalMissions(LogBuilder lb) {
        final Player player = getPlayer();
        List<AIUnit> aiUnits = getAIUnits();

        lb.mark();
        List<AIUnit> done = new ArrayList<>(aiUnits.size());
        reasons.clear();
        for (AIUnit aiUnit : aiUnits) {
            final Unit unit = aiUnit.getUnit();
            Mission m = aiUnit.getMission();

            if (!unit.isInitialized() || unit.isDisposed()) {
                reasons.put(unit, "Invalid");

            } else if (m != null && m.isValid() && !m.isOneTime()) {
                reasons.put(unit, "Valid");

            } else { // Unit needs a mission
                continue;
            }
            done.add(aiUnit);
        }
        aiUnits.removeAll(done);
        done.clear();

        for (AIUnit aiUnit : aiUnits) {
            final Unit unit = aiUnit.getUnit();
            final Settlement settlement = unit.getSettlement();
            final IndianSettlement is = unit.getHomeIndianSettlement();
            Mission m = aiUnit.getMission();
            
            if (settlement != null && settlement.getUnitCount()
                + settlement.getTile().getUnitCount() <= 1) {
                // First see to local settlement defence
                if (!(m instanceof DefendSettlementMission)
                    || m.getTarget() != settlement) {
                    m = getDefendSettlementMission(aiUnit, settlement);
                    if (m == null) continue;
                    lb.add(m, ", ");
                }
                reasons.put(unit, "Defend-" + settlement.getName());

            } else if (is != null
                && is.canImproveUnitMilitaryRole(unit) != null) {
                // Go home for new equipment if the home settlement has it
                if (!(m instanceof DefendSettlementMission)
                    || m.getTarget() != is) {
                    m = getDefendSettlementMission(aiUnit, is);
                    if (m == null) continue;
                    lb.add(m, ", ");
                }
                reasons.put(unit, "Equip-" + is.getName());

            } else {
                // Prefer hunting a nearby valid target over aimless wandering.
                Mission hunt = getSeekAndDestroyMission(aiUnit, 8);
                if (hunt != null) {
                    m = hunt;
                    lb.add(m, ", ");
                    reasons.put(unit, "Hunt");
                } else if (!(m instanceof UnitWanderHostileMission)) {
                    m = getWanderHostileMission(aiUnit);
                    if (m == null) continue;
                    lb.add(m, ", ");
                    reasons.put(unit, "Patrol");
                } else {
                    reasons.put(unit, "Patrol");
                }
            }
            done.add(aiUnit);
        }
        aiUnits.removeAll(done);
        done.clear();

        // Log
        if (lb.grew("\n  Mission changes: ")) lb.shrink(", ");
        if (!aiUnits.isEmpty()) {
            lb.add("\n  Free Land Units:");
            for (AIUnit aiu : aiUnits) lb.add(" ", aiu.getUnit());
        }
        lb.add("\n  Missions(settlements=", player.getSettlementCount(), ")");
        logMissions(reasons, lb);
    }

    /**
     * Brings gifts to nice players with nearby colonies.
     *
     * @param randoms An array of random percentages.
     * @param lb A {@code LogBuilder} to log to.
     */
    private void bringGifts(int[] randoms, LogBuilder lb,
                            Set<Player> giftNations) {
        final Player player = getPlayer();
        final CostDecider cd = CostDeciders.numberOfLegalTiles();
        final int settlements = Math.max(1, player.getSettlementCount());
        // Larger tribes send gifts more often and can run more missions.
        final int giftPercent = Math.min(100,
            getSpecification().getPercentage(GameOptions.GIFT_PROBABILITY)
                + settlements * 2);
        final int maxGifts = Math.max(MAX_NUMBER_OF_GIFTS_BEING_DELIVERED,
                                      settlements / 2);
        int randomIdx = 0;
        lb.mark();

        for (IndianSettlement is : player.getIndianSettlementList()) {
            // Do not bring gifts all the time.
            if (randoms[randomIdx++] >= giftPercent) continue;

            // Check if the settlement has anything to give.
            Goods gift = is.getRandomGift(getAIRandom());
            if (gift == null) continue;

            // Check if there are available units, and if there are already
            // enough missions in operation.
            List<Unit> availableUnits = new ArrayList<>();
            int alreadyAssignedUnits = 0;
            for (Unit ou : is.getOwnedUnitList()) {
                AIUnit aiu = getAIUnit(ou);
                if (aiu == null) {
                    continue;
                } else if (aiu.hasMission(IndianBringGiftMission.class)) {
                    alreadyAssignedUnits++;
                } else if (Mission.invalidNewMissionReason(aiu) == null) {
                    availableUnits.add(ou);
                }
            }
            if (alreadyAssignedUnits > maxGifts) {
                lb.add(is.getName(), " has ", alreadyAssignedUnits, 
                       " already, ");
                continue;
            } else if (availableUnits.isEmpty()) {
                lb.add(is.getName(), " has no gift units, ");
                continue;
            }
            // Pick a random available capable unit.
            Unit unit = null;
            AIUnit aiUnit = null;
            Tile home = is.getTile();
            while (unit == null && !availableUnits.isEmpty()) {
                Unit u = availableUnits.get(randomInt(logger, "Gift unit",
                        getAIRandom(), availableUnits.size()));
                availableUnits.remove(u);
                aiUnit = getAIUnit(u);
                if (IndianBringGiftMission.invalidMissionReason(aiUnit) == null
                    && u.findPath(u.getTile(), home, null, cd, null) != null) {
                    unit = u;
                }
            }
            if (unit == null) {
                lb.add(is.getName(), " found no gift unit, ");
                continue;
            }

            // Collect nearby colonies.  Filter out ones which are uncontacted,
            // unreachable or otherwise unsuitable.  Score the rest on alarm
            // and distance.
            List<RandomChoice<Colony>> nearbyColonies = new ArrayList<>();
            for (Tile t : home.getSurroundingTiles(MAX_DISTANCE_TO_BRING_GIFTS)) {
                Colony c = t.getColony();
                PathNode path;
                if (c == null
                    || !is.hasContacted(c.getOwner())
                    || IndianBringGiftMission.invalidMissionReason(aiUnit, c) != null
                    || (path = unit.findPath(home, c.getTile(),
                                             null, cd, null)) == null) continue;
                int alarm = Math.max(1, is.getAlarm(c.getOwner()).getValue());
                nearbyColonies.add(new RandomChoice<>(c,
                        1000000 / alarm / (1 + path.getTotalTurns())));
            }

            // If there are any suitable colonies, pick a random one
            // to send a gift to.
            if (nearbyColonies.isEmpty()) {
                lb.add(is.getName(), " found no gift colonies, ");
                continue;
            }
            Colony target = RandomChoice.getWeightedRandom(logger,
                "Choose gift colony", nearbyColonies, getAIRandom());
            if (target == null) {
                throw new RuntimeException("No gift target: " + this);
            }

            // Send the unit.
            Mission m = new IndianBringGiftMission(getAIMain(), aiUnit, target);
            giftNations.add(target.getOwner());
            lb.add(m, " gift from ", is.getName(),
                   " to ", target.getName(), ", ");
        }
        if (lb.grew("\n  Gifts: ")) lb.shrink(", ");
    }

    /**
     * Demands tribute from nasty players with nearby colonies.
     *
     * @param randoms An array of random percentages.
     * @param lb A {@code LogBuilder} to log to.
     * @param giftNations Nations already receiving gifts this turn.
     */
    private void demandTribute(int[] randoms, LogBuilder lb,
                               Set<Player> giftNations) {
        final Player player = getPlayer();
        final CostDecider cd = CostDeciders.numberOfLegalTiles();
        final int settlements = Math.max(1, player.getSettlementCount());
        final int demandPercent = Math.min(100,
            getSpecification().getPercentage(GameOptions.DEMAND_PROBABILITY)
                + settlements * 2);
        final int maxDemands = Math.max(MAX_NUMBER_OF_DEMANDS,
                                        settlements / 2);
        int randomIdx = 0;
        lb.mark();

        for (IndianSettlement is : player.getIndianSettlementList()) {
            // Do not demand tribute all of the time.
            if (randoms[randomIdx++] >= demandPercent) continue;

            // Check if there are available units, and if there are already
            // enough missions in operation.
            List<Unit> availableUnits = new ArrayList<>();
            int alreadyAssignedUnits = 0;
            for (Unit ou : is.getOwnedUnitList()) {
                AIUnit aiu = getAIUnit(ou);
                if (Mission.invalidNewMissionReason(aiu) == null) {
                    if (aiu.hasMission(IndianDemandMission.class)) {
                        alreadyAssignedUnits++;
                    } else {
                        availableUnits.add(ou);
                    }
                }
            }
            if (alreadyAssignedUnits > maxDemands) {
                lb.add(is.getName(), " has ", alreadyAssignedUnits,
                       " already, ");
                continue;
            } else if (availableUnits.isEmpty()) {
                lb.add(is.getName(), " has no demand units, ");
                continue;
            }
            // Pick a random available capable unit.
            Tile home = is.getTile();
            Unit unit = null;
            AIUnit aiUnit = null;
            while (unit == null && !availableUnits.isEmpty()) {
                Unit u = availableUnits.get(randomInt(logger, "Demand unit",
                        getAIRandom(), availableUnits.size()));
                availableUnits.remove(u);
                aiUnit = getAIUnit(u);
                if (IndianDemandMission.invalidMissionReason(aiUnit) == null
                    && u.findPath(u.getTile(), home, null, cd, null) != null) {
                    unit = u;
                }
            }
            if (unit == null) {
                lb.add(is.getName(), " found no demand unit, ");
                continue;
            }

            // Collect nearby colonies.  Filter out ones which are unreachable,
            // already receiving gifts this turn, or on adequate terms.
            List<RandomChoice<Colony>> nearbyColonies = new ArrayList<>();
            for (Tile t : home.getSurroundingTiles(MAX_DISTANCE_TO_MAKE_DEMANDS)) {
                Colony c = t.getColony();
                PathNode path;
                if (c == null
                    || !is.hasContacted(c.getOwner())
                    || giftNations.contains(c.getOwner())
                    || IndianDemandMission.invalidMissionReason(aiUnit, c) != null
                    || (path = unit.findPath(home, c.getTile(),
                                             null, cd, null)) == null) continue;
                int alarm = is.getAlarm(c.getOwner()).getValue();
                int defence = c.getUnitCount() + ((c.getStockade() == null) ? 1
                    : (c.getStockade().getLevel() * 10));
                int weight = 1 + alarm * (1000000 / defence
                    / (1 + path.getTotalTurns()));
                nearbyColonies.add(new RandomChoice<>(c, weight));
            }
            // If there are any suitable colonies, pick one to demand from.
            // Sometimes a random one, sometimes the weakest, sometimes the
            // most annoying.
            if (nearbyColonies.isEmpty()) {
                lb.add(is.getName(), " found no demand colonies, ");
                continue;
            }
            Colony target = RandomChoice.getWeightedRandom(logger,
                "Choose demand colony", nearbyColonies, getAIRandom());
            if (target == null) {
                lb.add(is.getName(), " found no demand target, ");
                continue;
            }

            // Send the unit.
            Mission m = new IndianDemandMission(getAIMain(), aiUnit, target);
            lb.add("At ", is.getName(), " ", m,
                   " will demand of ", target, ", ");
        }
        if (lb.grew("\n  Tribute: ")) lb.shrink(", ");
    }

    /**
     * Gets the appropriate ship trade penalties.
     *
     * @param sense The sense to apply the modifiers.
     * @return The ship trade penalties.
     */
    private List<Modifier> getShipTradePenalties(boolean sense) {
        final Specification spec = getSpecification();
        final int penalty = ((sense) ? 1 : -1)
            * spec.getInteger(GameOptions.SHIP_TRADE_PENALTY);
        final Function<Modifier, Modifier> mapper
            = m -> Modifier.makeModifier(m).setValue(penalty);
        return transform(spec.getModifiers(Modifier.SHIP_TRADE_PENALTY),
                         alwaysTrue(), mapper);
    }

    /**
     * Aborts all the missions which are no longer valid.
     *
     * Public for the test suite.
     */
    public void abortInvalidMissions() {
        for (AIUnit au : getAIUnits()) {
            Mission mission = au.getMission();
            String reason = (mission == null) ? null : mission.invalidReason();
            if (reason != null) au.changeMission(null);
        }
    }


    // AIPlayer interface
    // Inherit:
    //   acceptDiplomaticTrade
    //   acceptTax
    //   acceptMercenaries
    //   selectFoundingFather

    /**
     * {@inheritDoc}
     */
    public IndianDemandAction indianDemand(Unit unit, Colony colony,
                                           GoodsType type, int amount,
                                           IndianDemandAction accept) {
        final Player player = getPlayer();
        AIUnit aiu;
        IndianDemandMission mission;
        if (unit.getOwner() == player) { // Its one of ours
            if ((aiu = getAIUnit(unit)) != null // and its valid and demanding
                && (mission = aiu.getMission(IndianDemandMission.class)) != null
                && accept != IndianDemandAction.INDIAN_DEMAND_DONE) {
                mission.setSucceeded(accept == IndianDemandAction.INDIAN_DEMAND_ACCEPT);
            }
        }
        // Once we get here, the demand is settled
        return IndianDemandAction.INDIAN_DEMAND_DONE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startWorking() {
        final Player player = getPlayer();
        final Turn turn = getGame().getTurn();
        final int nSettlements = player.getSettlementCount();
        final Random air = getAIRandom();

        LogBuilder lb = new LogBuilder(1024);
        lb.add(player.getDebugName(), " in ", turn, "/", turn.getNumber());

        clearAIUnits();

        determineStances(lb);
        List<AIUnit> more;
        if (turn.isFirstTurn()) {
            initializeMissions(lb);
            more = getAIUnits();
        } else {
            int[] randoms;
            abortInvalidMissions();
            if (nSettlements > 0) {
                secureSettlements(lb);
                final Set<Player> giftNations = new HashSet<>();
                randoms = randomInts(logger, "Gifts", air, 100, nSettlements);
                bringGifts(randoms, lb, giftNations);
                randoms = randomInts(logger, "Tribute", air, 100, nSettlements);
                demandTribute(randoms, lb, giftNations);
            }
            giveNormalMissions(lb);
            more = doMissions(getAIUnits(), lb);
        }

        if (!more.isEmpty()) {
            abortInvalidMissions();
            giveNormalMissions(lb);
            doMissions(more, lb);
        }
        clearAIUnits();
        lb.log(logger, Level.FINEST);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int adjustMission(AIUnit aiUnit, PathNode path, Class<?> type, int value) {
        if (type == DefendSettlementMission.class) {
            // Reduce value in proportion to the number of active defenders.
            Settlement settlement = (Settlement)DefendSettlementMission
                .extractTarget(aiUnit, path);
            value -= 75 * getSettlementDefenders(settlement);

        } else if (type == UnitSeekAndDestroyMission.class) {
            // Natives prefer to attack when DISPLEASED.
            Location target = UnitSeekAndDestroyMission
                .extractTarget(aiUnit, path);
            Player targetPlayer = (target instanceof Ownable)
                ? ((Ownable)target).getOwner()
                : null;
            IndianSettlement is = aiUnit.getUnit().getHomeIndianSettlement();
            if (targetPlayer != null
                && is != null && is.getAlarm(targetPlayer) != null) {
                value += is.getAlarm(targetPlayer).getValue()
                    - Tension.Level.DISPLEASED.getLimit();
            }
        }

        return value;
    }

    /**
     * Price the goods to buy.
     *
     * @param nt The {@code NativeTrade} to update.
     * @param anger A penalty to the native prices due to anger.
     */
    private void updateTrade(NativeTrade nt, int anger) {
        final Specification spec = getSpecification();
        final Turn turn = getGame().getTurn();
        final IndianSettlement is = nt.getIndianSettlement();
        final Unit unit = nt.getUnit();
        Set<Modifier> modifiers = new HashSet<>();

        if (is.hasMissionary(unit.getOwner())
            && spec.getBoolean(GameOptions.ENHANCED_MISSIONARIES)) {
            Unit u = is.getMissionary();
            modifiers.addAll(u.getMissionaryTradeModifiers(true));
        }
        if (unit.isNaval()) {
            modifiers.addAll(getShipTradePenalties(true));
        }
        for (NativeTradeItem nti : nt.getUnitToSettlement()) {
            int price = (int)FeatureContainer.applyModifiers(1.0f / anger
                * is.getPriceToBuy(nti.getGoods()), turn, modifiers);
            for (int h = nti.getHaggleCount(); h >= 0; h--) {
                price = NativeTrade.haggleUp(price);
            }
            if (price <= NativeTradeItem.PRICE_UNSET) {
                price = NativeTradeItem.PRICE_INVALID;
            }
            nti.setPrice(price);
        }

        modifiers.clear();
        if (is.hasMissionary(unit.getOwner())
            && spec.getBoolean(GameOptions.ENHANCED_MISSIONARIES)) {
            Unit u = is.getMissionary();
            modifiers.addAll(u.getMissionaryTradeModifiers(false));
        }
        if (unit.isNaval()) {
            modifiers.addAll(getShipTradePenalties(false));
        }
        for (NativeTradeItem nti : nt.getSettlementToUnit()) {
            int price = (int)FeatureContainer.applyModifiers((float)anger
                * is.getPriceToSell(nti.getGoods()), turn, modifiers);
            for (int h = nti.getHaggleCount(); h >= 0; h--) {
                price = NativeTrade.haggleDown(price);
            }
            if (price <= NativeTradeItem.PRICE_UNSET) {
                price = NativeTradeItem.PRICE_INVALID;
            }
            nti.setPrice(price);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NativeTradeAction handleTrade(NativeTradeAction action,
                                         NativeTrade nt) {
        final int HAGGLE_NUMBER = 3;
        if (nt == null || !this.getPlayer().owns(nt.getIndianSettlement())) {
            return NativeTradeAction.NAK_INVALID;
        }
        final IndianSettlement is = nt.getIndianSettlement();
        final Unit unit = nt.getUnit();
        final Player other = unit.getOwner();
        NativeTradeItem ours;
        int anger, haggle;
        Tension tension = is.getAlarm(other);
        if (tension == null) return NativeTradeAction.NAK_INVALID;

        switch (action) {
        case OPEN:
            switch (tension.getLevel()) {
            case HAPPY: case CONTENT:
                anger = 1;
                break;
            case DISPLEASED:
                anger = 2;
                break;
            case ANGRY: case HATEFUL: default:
                // At war / hostile: allow gifts only so Europeans can
                // offer peace goods.  Buy/sell stay blocked via atWar().
                anger = 3;
                nt.setBuy(false);
                nt.setSell(false);
                updateTrade(nt, anger);
                return (nt.canGift()) ? NativeTradeAction.ACK_OPEN
                    : NativeTradeAction.NAK_HOSTILE;
            }
            updateTrade(nt, anger);
            return (nt.getDone()) ? NativeTradeAction.NAK_NOSALE
                : NativeTradeAction.ACK_OPEN;

        case BUY:
            switch (tension.getLevel()) {
            case HAPPY: case CONTENT:
                anger = 1;
                break;
            case DISPLEASED:
                anger = 2;
                break;
            case ANGRY: default:
                anger = -1;
                break;
            }
            if (anger < 0) return NativeTradeAction.NAK_HOSTILE;
            updateTrade(nt, anger);
            ours = find(nt.getSettlementToUnit(), nt.getItem().goodsMatcher());
            if (ours == null) return NativeTradeAction.NAK_INVALID;
            if (nt.getItem().priceIsSet()
                && nt.getItem().getPrice() >= ours.getPrice()) {
                return NativeTradeAction.ACK_BUY;
            }
            haggle = ours.getHaggleCount() + 1;
            if (randomInt(logger, "Haggle-buy", getAIRandom(),
                          HAGGLE_NUMBER + haggle) >= HAGGLE_NUMBER) {
                return NativeTradeAction.NAK_HAGGLE;
            }
            ours.setHaggleCount(haggle);
            updateTrade(nt, anger);
            nt.setItem(ours);
            return NativeTradeAction.ACK_BUY_HAGGLE;

        case SELL:
            switch (tension.getLevel()) {
            case HAPPY: case CONTENT:
                anger = 1;
                break;
            case DISPLEASED:
                anger = 2;
                break;
            case ANGRY:
                anger = (any(nt.getUnitToSettlement(),
                             nti -> nti.getGoods().getType().getMilitary()))
                    ? 3 : -1;
                break;
            default:
                anger = -1;
                break;
            }
            if (anger < 0) return NativeTradeAction.NAK_HOSTILE;
            updateTrade(nt, anger);
            ours = find(nt.getUnitToSettlement(), nt.getItem().goodsMatcher());
            if (ours == null) return NativeTradeAction.NAK_INVALID;
            if (nt.getItem().priceIsSet()
                && nt.getItem().getPrice() <= ours.getPrice()) {
                return NativeTradeAction.ACK_SELL;
            }
            haggle = ours.getHaggleCount() + 1;
            if (randomInt(logger, "Haggle-sell", getAIRandom(),
                          HAGGLE_NUMBER + haggle) >= HAGGLE_NUMBER) {
                return NativeTradeAction.NAK_HAGGLE;
            }
            ours.setHaggleCount(haggle);
            updateTrade(nt, anger);
            nt.setItem(ours);
            return NativeTradeAction.ACK_SELL_HAGGLE;

        case GIFT:
            ours = find(nt.getUnitToSettlement(), nt.getItem().goodsMatcher());
            if (ours == null) return NativeTradeAction.NAK_INVALID;
            if (!is.canAdd(nt.getItem().getGoods())) {
                return NativeTradeAction.NAK_GOODS;
            }
            // During war only accept goods the tribe actually wants (or
            // standard trade/military goods) as a path to peace.
            if (getPlayer().atWarWith(other)
                    && !is.acceptsPeaceGoods(ours.getGoods().getType())) {
                return NativeTradeAction.NAK_GOODS;
            }
            return NativeTradeAction.ACK_GIFT;

        default: // Invalid
            return NativeTradeAction.NAK_INVALID;
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getNeededWagons(Tile tile) {
        throw new RuntimeException("Can not happen: " + this);
    }

    /**
     * {@inheritDoc}
     */
    public int pioneersNeeded() {
        throw new RuntimeException("Can not happen: " + this);
    }

    /**
     * {@inheritDoc}
     */
    public int scoutsNeeded() {
        throw new RuntimeException("Can not happen: " + this);
    }

    /**
     * {@inheritDoc}
     */
    public void completeWish(Wish w) {
        throw new RuntimeException("Can not happen: " + this);
    }
    

    // Serialization

    // getXMLTagName not needed, uses parent
}
