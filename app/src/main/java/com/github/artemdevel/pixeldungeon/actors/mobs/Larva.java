package com.github.artemdevel.pixeldungeon.actors.mobs;


import com.github.artemdevel.pixeldungeon.actors.Char;
import com.github.artemdevel.pixeldungeon.game.utils.Random;
import com.github.artemdevel.pixeldungeon.sprites.LarvaSprite;

public class Larva extends Mob {

    {
        name = "Yog's larva";
        spriteClass = LarvaSprite.class;
        HP = HT = 25;
        defenseSkill = 20;
        EXP = 0;
        state = HUNTING;
    }

    @Override
    public int attackSkill(Char target) {
        return 30;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(15, 20);
    }

    @Override
    public int dr() {
        return 8;
    }

    @Override
    public String description() {
        return "Yog's larva";

    }
}
