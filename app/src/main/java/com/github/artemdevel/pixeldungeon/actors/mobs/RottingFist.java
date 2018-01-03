package com.github.artemdevel.pixeldungeon.actors.mobs;


import com.github.artemdevel.pixeldungeon.actors.Char;
import com.github.artemdevel.pixeldungeon.actors.blobs.ToxicGas;
import com.github.artemdevel.pixeldungeon.actors.buffs.Amok;
import com.github.artemdevel.pixeldungeon.actors.buffs.Buff;
import com.github.artemdevel.pixeldungeon.actors.buffs.Ooze;
import com.github.artemdevel.pixeldungeon.actors.buffs.Poison;
import com.github.artemdevel.pixeldungeon.actors.buffs.Sleep;
import com.github.artemdevel.pixeldungeon.actors.buffs.Terror;
import com.github.artemdevel.pixeldungeon.actors.buffs.Vertigo;
import com.github.artemdevel.pixeldungeon.effects.particles.ShadowParticle;
import com.github.artemdevel.pixeldungeon.game.utils.Random;
import com.github.artemdevel.pixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.github.artemdevel.pixeldungeon.items.weapon.enchantments.Death;
import com.github.artemdevel.pixeldungeon.levels.Level;
import com.github.artemdevel.pixeldungeon.sprites.RottingFistSprite;

import java.util.HashSet;

public class RottingFist extends Mob {

    private static final int REGENERATION = 4;

    private static final HashSet<Class<?>> RESISTANCES = new HashSet<>();

    static {
        RESISTANCES.add(ToxicGas.class);
        RESISTANCES.add(Death.class);
        RESISTANCES.add(ScrollOfPsionicBlast.class);
    }

    private static final HashSet<Class<?>> IMMUNITIES = new HashSet<>();

    static {
        IMMUNITIES.add(Amok.class);
        IMMUNITIES.add(Sleep.class);
        IMMUNITIES.add(Terror.class);
        IMMUNITIES.add(Poison.class);
        IMMUNITIES.add(Vertigo.class);
    }

    {
        name = "rotting fist";
        spriteClass = RottingFistSprite.class;

        HP = HT = 300;
        defenseSkill = 25;

        EXP = 0;

        state = WANDERING;
    }

    public RottingFist() {
        super();
        // TODO: Think how to re-implement this feature.
//        fistsCount++;
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        // TODO: Think how to re-implement this feature.
//        fistsCount--;
    }

    @Override
    public int attackSkill(Char target) {
        return 36;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(24, 36);
    }

    @Override
    public int dr() {
        return 15;
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        if (Random.Int(3) == 0) {
            Buff.affect(enemy, Ooze.class);
            enemy.sprite.burst(0xFF000000, 5);
        }

        return damage;
    }

    @Override
    public boolean act() {
        if (Level.water[pos] && HP < HT) {
            sprite.emitter().burst(ShadowParticle.UP, 2);
            HP += REGENERATION;
        }

        return super.act();
    }

    @Override
    public String description() {
        return "Yog's rotten fist";
    }

    @Override
    public HashSet<Class<?>> resistances() {
        return RESISTANCES;
    }

    @Override
    public HashSet<Class<?>> immunities() {
        return IMMUNITIES;
    }
}
