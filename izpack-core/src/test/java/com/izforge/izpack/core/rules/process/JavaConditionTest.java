/*
 * IzPack - Copyright 2001-2020 The IzPack project team.
 * All Rights Reserved.
 *
 * http://izpack.org/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.izforge.izpack.core.rules.process;

import com.izforge.izpack.api.adaptator.IXMLElement;
import com.izforge.izpack.api.adaptator.IXMLParser;
import com.izforge.izpack.api.adaptator.impl.XMLParser;
import com.izforge.izpack.api.data.AutomatedInstallData;
import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.rules.RulesEngine;
import com.izforge.izpack.core.container.DefaultContainer;
import com.izforge.izpack.core.data.DefaultVariables;
import com.izforge.izpack.core.rules.ConditionContainer;
import com.izforge.izpack.core.rules.RulesEngineImpl;
import com.izforge.izpack.util.Platforms;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JavaConditionTest {
    public static final boolean CONSTANT_VALUE = true;
    public static final Boolean CONSTANT_OBJECT_VALUE = Boolean.TRUE;

    public static void conditionMethodWithArgument(String someArgument) {
    }

    public static String conditionMethodWithNonBooleanResult() {
        return "true";
    }

    public static boolean conditionMethodFailing() {
        throw new RuntimeException();
    }

    public static boolean conditionMethodPrimitiveResult() {
        return true;
    }

    public static Boolean conditionMethod() {
        return Boolean.TRUE;
    }

    @Test
    public void testJavaConditions()
    {
        RulesEngine rules = createRulesEngine(new AutomatedInstallData(new DefaultVariables(), Platforms.UNIX));
        IXMLParser parser = new XMLParser();
        IXMLElement conditions = parser.parse(getClass().getResourceAsStream("javaconditions.xml"));
        rules.analyzeXml(conditions);

        assertFalse(rules.isConditionTrue("java0"));  // class does not exist
        assertFalse(rules.isConditionTrue("java1"));  // field does not exist
        assertTrue(rules.isConditionTrue("java2"));   // access to simple boolean field
        assertTrue(rules.isConditionTrue("java3"));   // access to boolean object field
        assertFalse(rules.isConditionTrue("java4"));  // method does not exist
        assertFalse(rules.isConditionTrue("java5"));  // method has arguments
        assertFalse(rules.isConditionTrue("java6"));  // method has non boolean return type
        assertFalse(rules.isConditionTrue("java7"));  // method invocation fails
        assertTrue(rules.isConditionTrue("java8"));   // simple boolean return value
        assertTrue(rules.isConditionTrue("java9"));   // boolean object return value
    }

    /**
     * Creates a new {@link RulesEngine}.
     *
     * @param installData the installation data
     * @return a new rules engine
     */
    private RulesEngine createRulesEngine(InstallData installData)
    {
        DefaultContainer parent = new DefaultContainer();
        RulesEngine rules = new RulesEngineImpl(installData, new ConditionContainer(parent), installData.getPlatform());
        parent.addComponent(RulesEngine.class, rules);
        return rules;
    }
}
