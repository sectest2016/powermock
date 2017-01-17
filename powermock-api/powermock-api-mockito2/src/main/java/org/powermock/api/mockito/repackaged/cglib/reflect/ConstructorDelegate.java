/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.reflect;

import org.powermock.api.mockito.repackaged.asm.ClassVisitor;
import org.powermock.api.mockito.repackaged.asm.Type;
import org.powermock.api.mockito.repackaged.cglib.core.AbstractClassGenerator;
import org.powermock.api.mockito.repackaged.cglib.core.ClassEmitter;
import org.powermock.api.mockito.repackaged.cglib.core.CodeEmitter;
import org.powermock.api.mockito.repackaged.cglib.core.Constants;
import org.powermock.api.mockito.repackaged.cglib.core.EmitUtils;
import org.powermock.api.mockito.repackaged.cglib.core.KeyFactory;
import org.powermock.api.mockito.repackaged.cglib.core.ReflectUtils;
import org.powermock.api.mockito.repackaged.cglib.core.TypeUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author Chris Nokleberg
 * @version $Id: ConstructorDelegate.java,v 1.20 2006/03/05 02:43:19 herbyderby Exp $
 */
abstract public class ConstructorDelegate {
    private static final ConstructorKey KEY_FACTORY =
      (ConstructorKey) KeyFactory.create(ConstructorKey.class, KeyFactory.CLASS_BY_NAME);
    
    protected ConstructorDelegate() {
    }

    public static ConstructorDelegate create(Class targetClass, Class iface) {
        Generator gen = new Generator();
        gen.setTargetClass(targetClass);
        gen.setInterface(iface);
        return gen.create();
    }

    interface ConstructorKey {
        public Object newInstance(String declaring, String iface);
    }

    public static class Generator extends AbstractClassGenerator {
        private static final Source SOURCE = new Source(ConstructorDelegate.class.getName());
        private static final Type CONSTRUCTOR_DELEGATE =
          TypeUtils.parseType("org.powermock.api.mockito.repackaged.cglib.reflect.ConstructorDelegate");

        private Class iface;
        private Class targetClass;

        public Generator() {
            super(SOURCE);
        }

        public void setInterface(Class iface) {
            this.iface = iface;
        }

        public void setTargetClass(Class targetClass) {
            this.targetClass = targetClass;
        }

        public ConstructorDelegate create() {
            setNamePrefix(targetClass.getName());
            Object key = KEY_FACTORY.newInstance(iface.getName(), targetClass.getName());
            return (ConstructorDelegate)super.create(key);
        }

        protected ClassLoader getDefaultClassLoader() {
            return targetClass.getClassLoader();
        }

        public void generateClass(ClassVisitor v) {
            setNamePrefix(targetClass.getName());

            final Method newInstance = ReflectUtils.findNewInstance(iface);
            if (!newInstance.getReturnType().isAssignableFrom(targetClass)) {
                throw new IllegalArgumentException("incompatible return type");
            }
            final Constructor constructor;
            try {
                constructor = targetClass.getDeclaredConstructor(newInstance.getParameterTypes());
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("interface does not match any known constructor");
            }

            ClassEmitter ce = new ClassEmitter(v);
            ce.begin_class(Constants.V1_2,
                           Constants.ACC_PUBLIC,
                           getClassName(),
                           CONSTRUCTOR_DELEGATE,
                           new Type[]{ Type.getType(iface) },
                           Constants.SOURCE_FILE);
            Type declaring = Type.getType(constructor.getDeclaringClass());
            EmitUtils.null_constructor(ce);
            CodeEmitter e = ce.begin_method(Constants.ACC_PUBLIC,
                                            ReflectUtils.getSignature(newInstance),
                                            ReflectUtils.getExceptionTypes(newInstance));
            e.new_instance(declaring);
            e.dup();
            e.load_args();
            e.invoke_constructor(declaring, ReflectUtils.getSignature(constructor));
            e.return_value();
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
