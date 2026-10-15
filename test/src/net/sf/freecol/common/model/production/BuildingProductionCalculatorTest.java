/**
 *  Copyright (C) 2002-2026  The FreeCol Team
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

package net.sf.freecol.common.model.production;

import java.util.Collections;
import java.util.List;

import net.sf.freecol.common.model.BuildingType;
import net.sf.freecol.common.model.FeatureContainer;
import net.sf.freecol.common.model.ProductionInfo;
import net.sf.freecol.common.model.Turn;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.util.test.FreeColTestCase;


/**
 * A worker can briefly have a null production type while being moved
 * between work locations (e.g. when ejectUnits runs after a disaster
 * damages a building).  That must not throw.
 */
public class BuildingProductionCalculatorTest extends FreeColTestCase {

    public void testNullProductionTypeDoesNotThrow() {
        BuildingType carpenterHouse
            = spec().getBuildingType("model.building.carpenterHouse");
        UnitType colonist = spec().getUnitType("model.unit.freeColonist");
        BuildingProductionCalculator calc
            = new BuildingProductionCalculator(null, new FeatureContainer(), 0);
        List<WorkerAssignment> workers = List.of(
            new WorkerAssignment(colonist, null));

        ProductionInfo info = calc.getAdjustedProductionInfo(
            carpenterHouse, new Turn(1), workers,
            Collections.emptyList(), Collections.emptyList(), 100);
        assertNotNull(info);
    }
}
