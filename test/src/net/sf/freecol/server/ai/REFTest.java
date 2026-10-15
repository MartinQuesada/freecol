/**
 *  Copyright (C) 2002-2024  The FreeCol Team
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
import java.util.List;
import java.util.Random;

import net.sf.freecol.common.model.AbstractUnit;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Force;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Monarch;
import net.sf.freecol.common.model.Player.PlayerType;
import net.sf.freecol.common.model.Role;
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.model.Stance;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Turn;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.MoveType;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.option.BooleanOption;
import net.sf.freecol.common.option.GameOptions;
import net.sf.freecol.common.util.LogBuilder;
import static net.sf.freecol.common.util.CollectionUtils.*;
import net.sf.freecol.server.ServerTestHelper;
import net.sf.freecol.server.ai.mission.UnitSeekAndDestroyMission;
import net.sf.freecol.server.control.InGameController;
import net.sf.freecol.server.model.ServerPlayer;
import net.sf.freecol.server.model.ServerUnit;
import net.sf.freecol.util.test.FreeColTestCase;


public class REFTest extends FreeColTestCase {

    @Override
    public void tearDown() throws Exception {
        ServerTestHelper.stopServerGame();
        super.tearDown();
    }


    public void testCreateREFPlayer() {
        Game game = ServerTestHelper.startServerGame(getTestMap());
        final Specification spec = game.getSpecification();
        InGameController igc = ServerTestHelper.getInGameController();

        // Create player
        final ServerPlayer player1 = getServerPlayer(game, "model.nation.dutch");
        final Force exf = player1.getMonarch().getExpeditionaryForce();
        // Update to have full naval capacity
        exf.prepareToBoard(null);
        
        ServerPlayer refPlayer = igc.createREFPlayer(player1);

        assertNotNull("REF player is null", refPlayer);
        assertNotNull("Player ref is null", player1.getREFPlayer());
        assertEquals("REF player should be player1 ref", refPlayer,
            player1.getREFPlayer());

        Force newf = new Force(spec);
        for (Unit u : refPlayer.getUnitSet()) {
            newf.add(new AbstractUnit(u.getType(), u.getRole().getId(), 1));
        }
        assertTrue("REF player force != Player monarch Expeditionary force",
                   exf.matchAll(newf));
    }


    public void testREFLaunchAgainstRingedColony() {
        final Specification spec = spec();
        Game game = ServerTestHelper.startServerGame(
            getCoastTestMap(spec.getTileType("model.tile.plains"), true));
        InGameController igc = ServerTestHelper.getInGameController();
        ServerPlayer dutch = getServerPlayer(game, "model.nation.dutch");

        Colony colony = createStandardColony(3, 9, 8);
        assertTrue(colony.isConnectedPort());
        final UnitType soldierType = spec.getUnitType("model.unit.veteranSoldier");
        final Role soldierRole = spec.getRole("model.role.soldier");
        for (Tile t : transform(colony.getTile().getSurroundingTiles(1, 1),
                                Tile::isLand)) {
            new ServerUnit(game, t, dutch, soldierType, soldierRole);
        }

        dutch.getMonarch().getExpeditionaryForce().prepareToBoard(null);
        ServerPlayer refPlayer = igc.createREFPlayer(dutch);
        refPlayer.exploreMap(true);
        dutch.changePlayerType(PlayerType.REBEL);
        dutch.setIndependentNationName("Vrije Nederlands");
        REFAIPlayer refAI = (REFAIPlayer)ServerTestHelper.getServer()
            .getAIPlayer(refPlayer);

        Unit loose = find(refPlayer.getUnits(), u -> !u.isNaval());
        loose.setLocation(refPlayer.getEurope());
        assertNull(loose.getCarrier());

        assertTrue("REF launches against a colony ringed by units",
                   refAI.initialize(false));
        assertNotNull("Loose units are put back aboard", loose.getCarrier());
        AIUnit attacker = refAI.getAIMain().getAIUnit(loose);
        assertTrue("REF attacks the guards",
            attacker.getMission() instanceof UnitSeekAndDestroyMission
            && attacker.getMission().getTarget() instanceof Unit
            && dutch.owns((Unit)attacker.getMission().getTarget()));
    }

    /**
     * Put a rebel soldier on every free beach.
     */
    private List<Unit> guardBeaches(Game game, ServerPlayer rebel) {
        final Specification spec = game.getSpecification();
        final UnitType soldierType
            = spec.getUnitType("model.unit.veteranSoldier");
        final Role soldierRole = spec.getRole("model.role.soldier");
        List<Unit> guards = new ArrayList<>();
        for (Tile t : game.getMap().getTileSet(t -> t.isCoastland()
                && !t.hasSettlement())) {
            guards.add(new ServerUnit(game, t, rebel, soldierType,
                                      soldierRole));
        }
        return guards;
    }

    /**
     * Declare independence for a player, creating its REF.
     */
    private ServerPlayer rebel(ServerPlayer player) {
        InGameController igc = ServerTestHelper.getInGameController();
        player.getMonarch().getExpeditionaryForce().prepareToBoard(null);
        ServerPlayer refPlayer = igc.createREFPlayer(player);
        refPlayer.exploreMap(true);
        player.changePlayerType(PlayerType.REBEL);
        player.setIndependentNationName("Vrije Nederlands");
        refPlayer.setStance(player, Stance.WAR);
        player.setStance(refPlayer, Stance.WAR);
        return refPlayer;
    }

    /**
     * Put a REF soldier aboard a REF ship on a sea tile.
     */
    private Unit soldierAboard(ServerPlayer refPlayer, Tile sea) {
        Unit ship = find(refPlayer.getUnits(), u -> u.isNaval()
            && any(u.getUnitList(), Unit::isOffensiveUnit));
        Unit soldier = find(ship.getUnitList(), Unit::isOffensiveUnit);
        ship.setLocation(sea);
        soldier.setMovesLeft(soldier.getInitialMovesLeft());
        return soldier;
    }

    public void testREFAmphibiousAssaultRule() {
        BooleanOption option = spec().getOption(
            GameOptions.REF_AMPHIBIOUS_ASSAULT, BooleanOption.class);
        final boolean oldValue = option.getValue();
        try {
            Game game = ServerTestHelper.startServerGame(
                getCoastTestMap(spec().getTileType("model.tile.plains"), true));
            final Map map = game.getMap();
            ServerPlayer dutch = getServerPlayer(game, "model.nation.dutch");
            Colony colony = createStandardColony(3, 9, 8);
            List<Unit> guards = guardBeaches(game, dutch);
            ServerPlayer refPlayer = rebel(dutch);
            Tile sea = map.getTile(10, 8);
            Unit soldier = soldierAboard(refPlayer, sea);
            Unit guard = find(guards, g -> g.getTile().isAdjacent(sea));
            assertNotNull(guard);
            assertTrue(colony.getTile().isAdjacent(sea));

            option.setValue(true);
            assertEquals("Attack a colony from the ship",
                MoveType.ATTACK_SETTLEMENT,
                soldier.getMoveType(colony.getTile()));
            assertEquals("Attack a guard from the ship",
                MoveType.ATTACK_UNIT, soldier.getMoveType(guard.getTile()));

            option.setValue(false);
            assertEquals("No amphibious assault with the option off",
                MoveType.MOVE_NO_ATTACK_MARINE,
                soldier.getMoveType(colony.getTile()));

            option.setValue(true);
            Unit far = find(guards, g -> g.getTile().getY() == 0);
            Tile beach = far.getTile();
            far.setLocation(map.getTile(5, 0));
            assertTrue(map.hasFreeBeach(colony.getTile(), refPlayer));
            assertEquals("Land first while a beach is free",
                MoveType.MOVE_NO_ATTACK_MARINE,
                soldier.getMoveType(colony.getTile()));
            far.setLocation(beach);
            assertFalse(map.hasFreeBeach(colony.getTile(), refPlayer));
        } finally {
            option.setValue(oldValue);
        }
    }

    public void testREFLaunchAgainstGuardedLand() {
        Game game = ServerTestHelper.startServerGame(
            getCoastTestMap(spec().getTileType("model.tile.plains"), true));
        ServerPlayer dutch = getServerPlayer(game, "model.nation.dutch");
        createStandardColony(3, 9, 8);
        guardBeaches(game, dutch);
        ServerPlayer refPlayer = rebel(dutch);
        REFAIPlayer refAI = (REFAIPlayer)ServerTestHelper.getServer()
            .getAIPlayer(refPlayer);

        assertTrue("REF launches against land with no free beach",
                   refAI.initialize(false));
        assertTrue("REF land units have attack missions",
            any(refAI.getAIUnits(), aiu -> !aiu.getUnit().isNaval()
                && aiu.getMission() instanceof UnitSeekAndDestroyMission));
    }

    public void testREFAmphibiousAttack() {
        Game game = ServerTestHelper.startServerGame(
            getCoastTestMap(spec().getTileType("model.tile.plains"), true));
        final Map map = game.getMap();
        ServerPlayer dutch = getServerPlayer(game, "model.nation.dutch");
        createStandardColony(3, 9, 8);
        List<Unit> guards = guardBeaches(game, dutch);
        ServerPlayer refPlayer = rebel(dutch);
        REFAIPlayer refAI = (REFAIPlayer)ServerTestHelper.getServer()
            .getAIPlayer(refPlayer);
        Tile sea = map.getTile(10, 8);
        Unit soldier = soldierAboard(refPlayer, sea);
        Unit guard = find(guards, g -> g.getTile().isAdjacent(sea));
        final Role guardRole = guard.getRole();
        final Role soldierRole = soldier.getRole();
        final UnitType soldierType = soldier.getType();

        AIUnit aiu = refAI.getAIMain().getAIUnit(soldier);
        UnitSeekAndDestroyMission mission
            = new UnitSeekAndDestroyMission(refAI.getAIMain(), aiu, guard);
        aiu.setMission(mission);
        game.setCurrentPlayer(refPlayer);
        mission.doMission(new LogBuilder(0));

        assertTrue("The soldier attacked from the ship",
            soldier.isDisposed() || soldier.getMovesLeft() == 0);
        assertTrue("The combat had an effect",
            soldier.isDisposed() || guard.isDisposed()
            || guard.getRole() != guardRole
            || soldier.getRole() != soldierRole
            || soldier.getType() != soldierType);
    }

    public void testAddToREF() {
        Game game = ServerTestHelper.startServerGame(getTestMap());
        final Specification spec = game.getSpecification();
        Random random = ServerTestHelper.getServer().getServerRandom();

        // Create player
        final ServerPlayer player1 = getServerPlayer(game, "model.nation.dutch");
        final Monarch monarch = player1.getMonarch();
        game.setTurn(new Turn(200));
        assertTrue("REF addition",
                   monarch.actionIsValid(Monarch.MonarchAction.ADD_TO_REF));

        // If the REF has too many land units, it should add more naval,
        // if it has enough naval it should add more land.
        // If this does not happen, this test will fail by looping forever.
        int done = 0;
        while (done != 3) {
            boolean naval = monarch.shouldAddNavalUnit();
            AbstractUnit au = monarch.addToREF(random);
            assertNotNull("REF add", au);
            if (naval) {
                assertTrue("Naval unit required", au.getType(spec).isNaval());
            } else {
                assertFalse("Land unit required", au.getType(spec).isNaval());
            }
            if (au.getType(spec).isNaval()) done |= 2; else done |= 1;
        }
    }
}
