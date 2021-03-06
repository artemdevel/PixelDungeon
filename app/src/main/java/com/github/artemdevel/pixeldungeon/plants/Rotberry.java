package com.github.artemdevel.pixeldungeon.plants;

import com.github.artemdevel.pixeldungeon.game.common.Game;
import com.github.artemdevel.pixeldungeon.Assets;
import com.github.artemdevel.pixeldungeon.Dungeon;
import com.github.artemdevel.pixeldungeon.actors.Char;
import com.github.artemdevel.pixeldungeon.actors.blobs.Blob;
import com.github.artemdevel.pixeldungeon.actors.blobs.ToxicGas;
import com.github.artemdevel.pixeldungeon.actors.buffs.Buff;
import com.github.artemdevel.pixeldungeon.actors.buffs.Roots;
import com.github.artemdevel.pixeldungeon.actors.mobs.Mob;
import com.github.artemdevel.pixeldungeon.effects.CellEmitter;
import com.github.artemdevel.pixeldungeon.effects.Speck;
import com.github.artemdevel.pixeldungeon.items.bags.Bag;
import com.github.artemdevel.pixeldungeon.items.potions.PotionOfStrength;
import com.github.artemdevel.pixeldungeon.scenes.GameScene;
import com.github.artemdevel.pixeldungeon.sprites.ItemSpriteSheet;
import com.github.artemdevel.pixeldungeon.utils.GLog;

public class Rotberry extends Plant {

    private static final String TXT_DESC = "Berries of this shrub taste like sweet, sweet death.";

    {
        image = 7;
        plantName = "Rotberry";
    }

    @Override
    public void activate(Char ch) {
        super.activate(ch);

        GameScene.add(Blob.seed(pos, 100, ToxicGas.class));

        Dungeon.level.drop(new Seed(), pos).sprite.drop();

        if (ch != null) {
            Buff.prolong(ch, Roots.class, Roots.TICK * 3);
        }
    }

    @Override
    public String desc() {
        return TXT_DESC;
    }

    public static class Seed extends Plant.Seed {
        {
            plantName = "Rotberry";

            name = "seed of " + plantName;
            image = ItemSpriteSheet.SEED_ROTBERRY;

            plantClass = Rotberry.class;
            alchemyClass = PotionOfStrength.class;
        }

        @Override
        public boolean collect(Bag container) {
            if (super.collect(container)) {
                if (Dungeon.level != null) {
                    for (Mob mob : Dungeon.level.mobs) {
                        mob.beckon(Dungeon.hero.pos);
                    }

                    GLog.logWarning("The seed emits a roar that echoes throughout the dungeon!");
                    CellEmitter.center(Dungeon.hero.pos).start(Speck.factory(Speck.SCREAM), 0.3f, 3);
                    Game.sound.play(Assets.SND_CHALLENGE);
                }

                return true;
            } else {
                return false;
            }
        }

        @Override
        public String desc() {
            return TXT_DESC;
        }
    }
}