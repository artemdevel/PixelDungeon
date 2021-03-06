/*
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

package com.github.artemdevel.pixeldungeon.game.glscripts;

import java.util.HashMap;

import com.github.artemdevel.pixeldungeon.game.glwrap.Program;
import com.github.artemdevel.pixeldungeon.game.glwrap.Shader;

public class Script extends Program {

    private static Script curScript = null;
    private static Class<? extends Script> curScriptClass = null;
    private static final HashMap<Class<? extends Script>, Script> all = new HashMap<>();

    public static <T extends Script> T use(Class<T> newScriptClass) {
        if (newScriptClass != curScriptClass) {
            Script script = all.get(newScriptClass);
            if (script == null) {
                try {
                    script = newScriptClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                all.put(newScriptClass, script);
            }

            curScript = script;
            curScriptClass = newScriptClass;
            curScript.use();
        }

        return (T) curScript;
    }

    public static void reset() {
        for (Script script : all.values()) {
            script.delete();
        }
        all.clear();
        curScript = null;
        curScriptClass = null;
    }

    public void compile(String src) {
        String[] srcShaders = src.split("//\n");
        attach(Shader.createCompiled(Shader.VERTEX, srcShaders[0]));
        attach(Shader.createCompiled(Shader.FRAGMENT, srcShaders[1]));
        link();
    }
}
