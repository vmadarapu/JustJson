/*
 * Copyright (C) 2014 Kalin Maldzhanski
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

package org.djodjo.json.generator.generators;

import org.djodjo.json.JsonArray;
import org.djodjo.json.JsonElement;
import org.djodjo.json.generator.Generator;
import org.djodjo.json.schema.Schema;
import org.djodjo.json.schema.SchemaV4;
import org.hamcrest.Matcher;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ArrayGenerator extends Generator {

    public ArrayGenerator(SchemaV4 schema) {
        super(schema);
    }

    public JsonArray generate() {
        JsonArray res = new JsonArray();
        ArrayList<Schema> items = schema.getItems();
        JsonElement  newEl;
        int minItems = schema.getMinItems();
        int maxItems = minItems + rnd.nextInt(schema.getMaxItems()-minItems);

        //meant for JSON generator after all, not OutOfMemory generator :)
        if(minItems>500) minItems = 500;
        if(maxItems>500) maxItems = 500;

        if(items!=null && items.size()>0) {
            //if we have array with
            if (items.size() == 1) {
                for(int i =0;i<maxItems;i++) {
                    for (Map.Entry<Matcher<Schema>, Class> entry : commonPropertyMatchers.entrySet()) {
                        if (entry.getKey().matches(items.get(0))) {
                            try {
                                Generator gen = (Generator) entry.getValue().getDeclaredConstructor(SchemaV4.class).newInstance(items.get(0));
                                newEl = gen.generate();
                                if (newEl != null) {
                                    res.put(newEl);
                                    break;
                                }
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } else {
                int cnt = 0;
                for (Schema itemSchema : items) {
                    for (Map.Entry<Matcher<Schema>, Class> entry : commonPropertyMatchers.entrySet()) {
                        if (entry.getKey().matches(itemSchema)) {
                            try {
                                Generator gen = (Generator) entry.getValue().getDeclaredConstructor(SchemaV4.class).newInstance(itemSchema);
                                newEl = gen.generate();
                                if (newEl != null) {
                                    res.put(newEl);
                                    break;
                                }
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if(++cnt>maxItems) break;
                }
            }
        } else {
            //then items can be any
            throw new NotImplementedException();
        }

        return res;
    }
}
