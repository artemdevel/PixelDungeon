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
package com.github.artemdevel.pixeldungeon.plants;

import java.util.ArrayList;

import com.github.artemdevel.pixeldungeon.game.common.audio.Sample;
import com.github.artemdevel.pixeldungeon.Assets;
import com.github.artemdevel.pixeldungeon.Dungeon;
import com.github.artemdevel.pixeldungeon.actors.Char;
import com.github.artemdevel.pixeldungeon.actors.buffs.Barkskin;
import com.github.artemdevel.pixeldungeon.actors.buffs.Buff;
import com.github.artemdevel.pixeldungeon.actors.hero.Hero;
import com.github.artemdevel.pixeldungeon.actors.hero.HeroSubClass;
import com.github.artemdevel.pixeldungeon.effects.CellEmitter;
import com.github.artemdevel.pixeldungeon.effects.particles.LeafParticle;
import com.github.artemdevel.pixeldungeon.items.Dewdrop;
import com.github.artemdevel.pixeldungeon.items.Generator;
import com.github.artemdevel.pixeldungeon.items.Item;
import com.github.artemdevel.pixeldungeon.levels.Level;
import com.github.artemdevel.pixeldungeon.levels.Terrain;
import com.github.artemdevel.pixeldungeon.sprites.PlantSprite;
import com.github.artemdevel.pixeldungeon.utils.Utils;
import com.github.artemdevel.pixeldungeon.game.utils.Bundlable;
import com.github.artemdevel.pixeldungeon.game.utils.Bundle;
import com.github.artemdevel.pixeldungeon.game.utils.Random;

public class Plant implements Bundlable {

    public String plantName;

    public int image;
    public int pos;

    public PlantSprite sprite;

    public void activate( Char ch ) {

        if (ch instanceof Hero && ((Hero)ch).subClass == HeroSubClass.WARDEN) {
            Buff.affect( ch, Barkskin.class ).level( ch.HT / 3 );
        }

        wither();
    }

    public void wither() {
        Dungeon.level.uproot( pos );

        sprite.kill();
        if (Dungeon.visible[pos]) {
            CellEmitter.get( pos ).burst( LeafParticle.GENERAL, 6 );
        }

        if (Dungeon.hero.subClass == HeroSubClass.WARDEN) {
            if (Random.Int( 5 ) == 0) {
                Dungeon.level.drop( Generator.random( Generator.Category.SEED ), pos ).sprite.drop();
            }
            if (Random.Int( 5 ) == 0) {
                Dungeon.level.drop( new Dewdrop(), pos ).sprite.drop();
            }
        }
    }

    private static final String POS    = "pos";

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        pos = bundle.getInt( POS );
    }

    @Override
    public void storeInBundle( Bundle bundle ) {
        bundle.put( POS, pos );
    }

    public String desc() {
        return null;
    }

    public static class Seed extends Item {

        public static final String AC_PLANT    = "PLANT";

        private static final String TXT_INFO = "Throw this seed to the place where you want to grow %s.\n\n%s";

        private static final float TIME_TO_PLANT = 1f;

        {
            stackable = true;
            defaultAction = AC_THROW;
        }

        protected Class<? extends Plant> plantClass;
        protected String plantName;

        public Class<? extends Item> alchemyClass;

        @Override
        public ArrayList<String> actions( Hero hero ) {
            ArrayList<String> actions = super.actions( hero );
            actions.add( AC_PLANT );
            return actions;
        }

        @Override
        protected void onThrow( int cell ) {
            if (Dungeon.level.map[cell] == Terrain.ALCHEMY || Level.pit[cell]) {
                super.onThrow( cell );
            } else {
                Dungeon.level.plant( this, cell );
            }
        }

        @Override
        public void execute( Hero hero, String action ) {
            if (action.equals( AC_PLANT )) {

                hero.spend( TIME_TO_PLANT );
                hero.busy();
                ((Seed)detach( hero.belongings.backpack )).onThrow( hero.pos );

                hero.sprite.operate( hero.pos );

            } else {

                super.execute (hero, action );

            }
        }

        public Plant couch( int pos ) {
            try {
                if (Dungeon.visible[pos]) {
                    Sample.INSTANCE.play( Assets.SND_PLANT );
                }
                Plant plant = plantClass.newInstance();
                plant.pos = pos;
                return plant;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public boolean isIdentified() {
            return true;
        }

        @Override
        public int price() {
            return 10 * quantity;
        }

        @Override
        public String info() {
            return String.format( TXT_INFO, Utils.indefinite( plantName ), desc() );
        }
    }
}