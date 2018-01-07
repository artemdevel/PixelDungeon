package com.github.artemdevel.pixeldungeon.actors.mobs;


import com.github.artemdevel.pixeldungeon.Assets;
import com.github.artemdevel.pixeldungeon.Dungeon;
import com.github.artemdevel.pixeldungeon.actors.Char;
import com.github.artemdevel.pixeldungeon.actors.blobs.ToxicGas;
import com.github.artemdevel.pixeldungeon.actors.buffs.Buff;
import com.github.artemdevel.pixeldungeon.actors.buffs.Paralysis;
import com.github.artemdevel.pixeldungeon.game.common.audio.GameSound;
import com.github.artemdevel.pixeldungeon.game.utils.Random;
import com.github.artemdevel.pixeldungeon.items.weapon.enchantments.Death;
import com.github.artemdevel.pixeldungeon.sprites.UndeadSprite;

import java.util.HashSet;

public class Undead extends Mob {

    // This constant is required by the King mob.
    private static final int MAX_ARMY_SIZE = 5;

    public static int count = 0;

    private static final HashSet<Class<?>> IMMUNITIES = new HashSet<>();

    static {
        IMMUNITIES.add(Death.class);
        IMMUNITIES.add(Paralysis.class);
    }

    {
        name = "undead dwarf";
        spriteClass = UndeadSprite.class;

        HP = HT = 28;
        defenseSkill = 15;

        EXP = 0;

        state = WANDERING;
    }

    @Override
    protected void onAdd() {
        count++;
        super.onAdd();
    }

    @Override
    protected void onRemove() {
        count--;
        super.onRemove();
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(12, 16);
    }

    @Override
    public int attackSkill(Char target) {
        return 16;
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        // TODO: Probably this must be implemented on King's side.
        if (Random.Int(MAX_ARMY_SIZE) == 0) {
            Buff.prolong(enemy, Paralysis.class, 1);
        }

        return damage;
    }

    @Override
    public void damage(int dmg, Object src) {
        super.damage(dmg, src);
        if (src instanceof ToxicGas) {
            ((ToxicGas) src).clear(pos);
        }
    }

    @Override
    public void die(Object cause) {
        super.die(cause);

        if (Dungeon.visible[pos]) {
            GameSound.INSTANCE.play(Assets.SND_BONES);
        }
    }

    @Override
    public int dr() {
        return 5;
    }

    @Override
    public String defenseVerb() {
        return "blocked";
    }

    @Override
    public String description() {
        return "These undead dwarves, risen by the will of the King of Dwarves, were members of his court. " +
            "They appear as skeletons with a stunning amount of facial hair.";
    }

    @Override
    public HashSet<Class<?>> immunities() {
        return IMMUNITIES;
    }
}
