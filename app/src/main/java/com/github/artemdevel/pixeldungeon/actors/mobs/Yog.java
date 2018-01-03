/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.github.artemdevel.pixeldungeon.actors.mobs;

import java.util.ArrayList;
import java.util.HashSet;

import com.github.artemdevel.pixeldungeon.Dungeon;
import com.github.artemdevel.pixeldungeon.ResultDescriptions;
import com.github.artemdevel.pixeldungeon.Statistics;
import com.github.artemdevel.pixeldungeon.actors.Actor;
import com.github.artemdevel.pixeldungeon.actors.Char;
import com.github.artemdevel.pixeldungeon.actors.blobs.Blob;
import com.github.artemdevel.pixeldungeon.actors.blobs.Fire;
import com.github.artemdevel.pixeldungeon.actors.blobs.ToxicGas;
import com.github.artemdevel.pixeldungeon.actors.buffs.Amok;
import com.github.artemdevel.pixeldungeon.actors.buffs.Buff;
import com.github.artemdevel.pixeldungeon.actors.buffs.Burning;
import com.github.artemdevel.pixeldungeon.actors.buffs.Charm;
import com.github.artemdevel.pixeldungeon.actors.buffs.Ooze;
import com.github.artemdevel.pixeldungeon.actors.buffs.Poison;
import com.github.artemdevel.pixeldungeon.actors.buffs.Sleep;
import com.github.artemdevel.pixeldungeon.actors.buffs.Terror;
import com.github.artemdevel.pixeldungeon.actors.buffs.Vertigo;
import com.github.artemdevel.pixeldungeon.effects.Pushing;
import com.github.artemdevel.pixeldungeon.effects.particles.ShadowParticle;
import com.github.artemdevel.pixeldungeon.items.keys.SkeletonKey;
import com.github.artemdevel.pixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.github.artemdevel.pixeldungeon.items.weapon.enchantments.Death;
import com.github.artemdevel.pixeldungeon.levels.Level;
import com.github.artemdevel.pixeldungeon.mechanics.Ballistica;
import com.github.artemdevel.pixeldungeon.scenes.GameScene;
import com.github.artemdevel.pixeldungeon.sprites.BurningFistSprite;
import com.github.artemdevel.pixeldungeon.sprites.CharSprite;
import com.github.artemdevel.pixeldungeon.sprites.LarvaSprite;
import com.github.artemdevel.pixeldungeon.sprites.RottingFistSprite;
import com.github.artemdevel.pixeldungeon.sprites.YogSprite;
import com.github.artemdevel.pixeldungeon.utils.GLog;
import com.github.artemdevel.pixeldungeon.utils.Utils;
import com.github.artemdevel.pixeldungeon.game.utils.Random;

public class Yog extends Mob {

    private static final HashSet<Class<?>> IMMUNITIES = new HashSet<>();

    static {
        IMMUNITIES.add(Death.class);
        IMMUNITIES.add(Terror.class);
        IMMUNITIES.add(Amok.class);
        IMMUNITIES.add(Charm.class);
        IMMUNITIES.add(Sleep.class);
        IMMUNITIES.add(Burning.class);
        IMMUNITIES.add(ToxicGas.class);
        IMMUNITIES.add(ScrollOfPsionicBlast.class);
    }

    {
        name = Dungeon.depth == Statistics.deepestFloor ? "Yog-Dzewa" : "echo of Yog-Dzewa";
        spriteClass = YogSprite.class;
        HP = HT = 300;
        EXP = 50;
        state = PASSIVE;
    }

    private static final String TXT_DESC =
        "Yog-Dzewa is an Old God, a powerful entity from the realms of chaos. A century ago, the ancient dwarves " +
        "barely won the war against its army of demons, but were unable to kill the god itself. Instead, they then " +
        "imprisoned it in the halls below their city, believing it to be too weak to rise ever again.";

    private static int fistsCount = 0;

    public Yog() {
        super();
    }

    public void spawnFists() {
        RottingFist fist1 = new RottingFist();
        BurningFist fist2 = new BurningFist();

        do {
            fist1.pos = pos + Level.NEIGHBOURS8[Random.Int(8)];
            fist2.pos = pos + Level.NEIGHBOURS8[Random.Int(8)];
        }
        while (!Level.passable[fist1.pos] || !Level.passable[fist2.pos] || fist1.pos == fist2.pos);

        GameScene.add(fist1);
        GameScene.add(fist2);
    }

    @Override
    public void damage(int dmg, Object src) {
        // TODO: Think how to re-implement this feature.
        fistsCount = 0;
        if (fistsCount > 0) {
            for (Mob mob : Dungeon.level.mobs) {
                if (mob instanceof BurningFist || mob instanceof RottingFist) {
                    mob.beckon(pos);
                }
            }

            dmg >>= fistsCount;
        }

        super.damage(dmg, src);
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        ArrayList<Integer> spawnPoints = new ArrayList<>();

        for (int i = 0; i < Level.NEIGHBOURS8.length; i++) {
            int p = pos + Level.NEIGHBOURS8[i];
            if (Actor.findChar(p) == null && (Level.passable[p] || Level.avoid[p])) {
                spawnPoints.add(p);
            }
        }

        if (spawnPoints.size() > 0) {
            Larva larva = new Larva();
            larva.pos = Random.element(spawnPoints);

            GameScene.add(larva);
            Actor.addDelayed(new Pushing(larva, pos, larva.pos), -1);
        }

        return super.defenseProc(enemy, damage);
    }

    @Override
    public void beckon(int cell) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void die(Object cause) {
        for (Mob mob : (Iterable<Mob>) Dungeon.level.mobs.clone()) {
            if (mob instanceof BurningFist || mob instanceof RottingFist) {
                mob.die(cause);
            }
        }

        GameScene.bossSlain();
        Dungeon.level.drop(new SkeletonKey(), pos).sprite.drop();
        super.die(cause);

        yell("...");
    }

    @Override
    public void notice() {
        super.notice();
        yell("Hope is an illusion...");
    }

    @Override
    public String description() {
        return TXT_DESC;
    }

    @Override
    public HashSet<Class<?>> immunities() {
        return IMMUNITIES;
    }

}
