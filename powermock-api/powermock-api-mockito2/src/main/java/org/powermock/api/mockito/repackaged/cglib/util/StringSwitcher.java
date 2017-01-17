/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.util;

import org.powermock.api.mockito.repackaged.asm.ClassVisitor;
import org.powermock.api.mockito.repackaged.asm.Label;
import org.powermock.api.mockito.repackaged.asm.Type;
import org.powermock.api.mockito.repackaged.cglib.core.AbstractClassGenerator;
import org.powermock.api.mockito.repackaged.cglib.core.ClassEmitter;
import org.powermock.api.mockito.repackaged.cglib.core.CodeEmitter;
import org.powermock.api.mockito.repackaged.cglib.core.Constants;
import org.powermock.api.mockito.repackaged.cglib.core.EmitUtils;
import org.powermock.api.mockito.repackaged.cglib.core.KeyFactory;
import org.powermock.api.mockito.repackaged.cglib.core.ObjectSwitchCallback;
import org.powermock.api.mockito.repackaged.cglib.core.ReflectUtils;
import org.powermock.api.mockito.repackaged.cglib.core.Signature;
import org.powermock.api.mockito.repackaged.cglib.core.TypeUtils;

import java.util.Arrays;
import java.util.List;

/**
 * This class implements a simple String->int mapping for a fixed set of keys.
 */
abstract public class StringSwitcher {
    private static final Type STRING_SWITCHER =
      TypeUtils.parseType("org.powermock.api.mockito.repackaged.cglib.util.StringSwitcher");
    private static final Signature INT_VALUE =
      TypeUtils.parseSignature("int intValue(String)");
    private static final StringSwitcherKey KEY_FACTORY =
      (StringSwitcherKey) KeyFactory.create(StringSwitcherKey.class);

    protected StringSwitcher() {
    }

    /**
     * Helper method to create a StringSwitcher.
     * For finer control over the generated instance, use a new instance of StringSwitcher.Generator
     * instead of this static method.
     * @param strings the array of String keys; must be the same length as the value array
     * @param ints the array of integer results; must be the same length as the key array
     * @param fixedInput if false, an unknown key will be returned from {@link #intValue} as {@code -1}; if true,
     * the result will be undefined, and the resulting code will be faster
     */
    public static StringSwitcher create(String[] strings, int[] ints, boolean fixedInput) {
        Generator gen = new Generator();
        gen.setStrings(strings);
        gen.setInts(ints);
        gen.setFixedInput(fixedInput);
        return gen.create();
    }

    /**
     * Return the integer associated with the given key.
     * @param s the key
     * @return the associated integer value, or {@code -1} if the key is unknown (unless
     * {@code fixedInput} was specified when this {@code StringSwitcher} was created,
     * in which case the return value for an unknown key is undefined)
     */
    abstract public int intValue(String s);

    interface StringSwitcherKey {
        public Object newInstance(String[] strings, int[] ints, boolean fixedInput);
    }

    public static class Generator extends AbstractClassGenerator {
        private static final Source SOURCE = new Source(StringSwitcher.class.getName());

        private String[] strings;
        private int[] ints;
        private boolean fixedInput;
        
        public Generator() {
            super(SOURCE);
        }

        /**
         * Set the array of recognized Strings.
         * @param strings the array of String keys; must be the same length as the value array
         * @see #setInts
         */
        public void setStrings(String[] strings) {
            this.strings = strings;
        }

        /**
         * Set the array of integer results.
         * @param ints the array of integer results; must be the same length as the key array
         * @see #setStrings
         */
        public void setInts(int[] ints) {
            this.ints = ints;
        }

        /**
         * Configure how unknown String keys will be handled.
         * @param fixedInput if false, an unknown key will be returned from {@link #intValue} as {@code -1}; if true,
         * the result will be undefined, and the resulting code will be faster
         */
        public void setFixedInput(boolean fixedInput) {
            this.fixedInput = fixedInput;
        }

        protected ClassLoader getDefaultClassLoader() {
            return getClass().getClassLoader();
        }

        /**
         * Generate the {@code StringSwitcher}.
         */
        public StringSwitcher create() {
            setNamePrefix(StringSwitcher.class.getName());
            Object key = KEY_FACTORY.newInstance(strings, ints, fixedInput);
            return (StringSwitcher)super.create(key);
        }

        public void generateClass(ClassVisitor v) throws Exception {
            ClassEmitter ce = new ClassEmitter(v);
            ce.begin_class(Constants.V1_2,
                           Constants.ACC_PUBLIC,
                           getClassName(),
                           STRING_SWITCHER,
                           null,
                           Constants.SOURCE_FILE);
            EmitUtils.null_constructor(ce);
            final CodeEmitter e = ce.begin_method(Constants.ACC_PUBLIC, INT_VALUE, null);
            e.load_arg(0);
            final List stringList = Arrays.asList(strings);
            int style = fixedInput ? Constants.SWITCH_STYLE_HASHONLY : Constants.SWITCH_STYLE_HASH;
            EmitUtils.string_switch(e, strings, style, new ObjectSwitchCallback() {
                public void processCase(Object key, Label end) {
                    e.push(ints[stringList.indexOf(key)]);
                    e.return_value();
                }
                public void processDefault() {
                    e.push(-1);
                    e.return_value();
                }
            });
            e.end_method();
            ce.end_class();
        }

        protected Object firstInstance(Class type) {
            return ReflectUtils.newInstance(type);
        }

        protected Object nextInstance(Object instance) {
            return instance;
        }
    }
}
