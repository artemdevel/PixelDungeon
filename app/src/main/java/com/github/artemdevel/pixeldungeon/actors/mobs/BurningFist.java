package com.github.artemdevel.pixeldungeon.actors.mobs;


import com.github.artemdevel.pixeldungeon.Dungeon;
import com.github.artemdevel.pixeldungeon.ResultDescriptions;
import com.github.artemdevel.pixeldungeon.actors.Char;
import com.github.artemdevel.pixeldungeon.actors.blobs.Blob;
import com.github.artemdevel.pixeldungeon.actors.blobs.Fire;
import com.github.artemdevel.pixeldungeon.actors.blobs.ToxicGas;
import com.github.artemdevel.pixeldungeon.actors.buffs.Amok;
import com.github.artemdevel.pixeldungeon.actors.buffs.Burning;
import com.github.artemdevel.pixeldungeon.actors.buffs.Sleep;
import com.github.artemdevel.pixeldungeon.actors.buffs.Terror;
import com.github.artemdevel.pixeldungeon.game.utils.Random;
import com.github.artemdevel.pixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.github.artemdevel.pixeldungeon.items.weapon.enchantments.Death;
import com.github.artemdevel.pixeldungeon.levels.Level;
import com.github.artemdevel.pixeldungeon.mechanics.Ballistica;
import com.github.artemdevel.pixeldungeon.scenes.GameScene;
import com.github.artemdevel.pixeldungeon.sprites.BurningFistSprite;
import com.github.artemdevel.pixeldungeon.sprites.CharSprite;
import com.github.artemdevel.pixeldungeon.utils.GLog;
import com.github.artemdevel.pixeldungeon.utils.Utils;

import java.util.HashSet;

public class BurningFist extends Mob {

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
        IMMUNITIES.add(Burning.class);
    }

    {
        name = "burning fist";
        spriteClass = BurningFistSprite.class;

        HP = HT = 200;
        defenseSkill = 25;

        EXP = 0;

        state = WANDERING;
    }

    public BurningFist() {
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
        return Random.NormalIntRange(20, 32);
    }

    @Override
    public int dr() {
        return 15;
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return Ballistica.cast(pos, enemy.pos, false, true) == enemy.pos;
    }

    @Override
    public boolean attack(Char enemy) {
        if (!Level.adjacent(pos, enemy.pos)) {
            spend(attackDelay());

            if (hit(this, enemy, true)) {

                int dmg = damageRoll();
                enemy.damage(dmg, this);

                enemy.sprite.bloodBurstA(sprite.center(), dmg);
                enemy.sprite.flash();

                if (!enemy.isAlive() && enemy == Dungeon.hero) {
                    Dungeon.fail(Utils.format(ResultDescriptions.BOSS, name, Dungeon.depth));
                    GLog.logNegative(TXT_KILL, name);
                }
                return true;

            } else {

                enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
                return false;
            }
        } else {
            return super.attack(enemy);
        }
    }

    @Override
    public boolean act() {
        for (int i = 0; i < Level.NEIGHBOURS9.length; i++) {
            GameScene.add(Blob.seed(pos + Level.NEIGHBOURS9[i], 2, Fire.class));
        }

        return super.act();
    }

    @Override
    public String description() {
        return "Yog's burning fist";
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
