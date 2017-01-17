/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.transform.impl;

import org.powermock.api.mockito.repackaged.asm.Type;
import org.powermock.api.mockito.repackaged.cglib.core.CodeEmitter;
import org.powermock.api.mockito.repackaged.cglib.core.CodeGenerationException;
import org.powermock.api.mockito.repackaged.cglib.core.Constants;
import org.powermock.api.mockito.repackaged.cglib.core.ReflectUtils;
import org.powermock.api.mockito.repackaged.cglib.core.Signature;
import org.powermock.api.mockito.repackaged.cglib.core.TypeUtils;
import org.powermock.api.mockito.repackaged.cglib.transform.ClassEmitterTransformer;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author Juozas Baliuka
 */
public class AddDelegateTransformer extends ClassEmitterTransformer {
    private static final String DELEGATE = "$CGLIB_DELEGATE";
    private static final Signature CSTRUCT_OBJECT =
      TypeUtils.parseSignature("void <init>(Object)");
    
    private Class[] delegateIf;
    private Class delegateImpl;
    private Type delegateType;
    
    /** Creates a new instance of AddDelegateTransformer */
    public AddDelegateTransformer(Class delegateIf[], Class delegateImpl) {
        try {
            delegateImpl.getConstructor(Object.class);
            this.delegateIf = delegateIf;
            this.delegateImpl = delegateImpl;
            delegateType = Type.getType(delegateImpl);
        } catch (NoSuchMethodException e) {
            throw new CodeGenerationException(e);
        }
    }
    
    public void begin_class(int version, int access, String className, Type superType, Type[] interfaces, String sourceFile) {
        
        if(!TypeUtils.isInterface(access)){
            
        Type[] all = TypeUtils.add(interfaces, TypeUtils.getTypes(delegateIf));
        super.begin_class(version, access, className, superType, all, sourceFile);
        
        declare_field(Constants.ACC_PRIVATE | Constants.ACC_TRANSIENT,
                      DELEGATE,
                      delegateType,
                      null);
        for (int i = 0; i < delegateIf.length; i++) {
            Method[] methods = delegateIf[i].getMethods();
            for (int j = 0; j < methods.length; j++) {
                if (Modifier.isAbstract(methods[j].getModifiers())) {
                    addDelegate(methods[j]);
                }
            }
        }
        }else{
           super.begin_class(version, access, className, superType, interfaces, sourceFile);
        }
    }

    public CodeEmitter begin_method(int access, Signature sig, Type[] exceptions) {
        final CodeEmitter e = super.begin_method(access, sig, exceptions);
        if (sig.getName().equals(Constants.CONSTRUCTOR_NAME)) {
            return new CodeEmitter(e) {
                private boolean transformInit = true;
                public void visitMethodInsn(int opcode, String owner, String name, String desc) {
                    super.visitMethodInsn(opcode, owner, name, desc);
                    if (transformInit && opcode == Constants.INVOKESPECIAL) {
                        load_this();
                        new_instance(delegateType);
                        dup();
                        load_this();
                        invoke_constructor(delegateType, CSTRUCT_OBJECT);
                        putfield(DELEGATE);
                        transformInit = false;
                    }
                }
            };
        }
        return e;
    }

    private void addDelegate(Method m) {
        Method delegate;
        try {
            delegate = delegateImpl.getMethod(m.getName(), m.getParameterTypes());
            if (!delegate.getReturnType().getName().equals(m.getReturnType().getName())){
                throw new IllegalArgumentException("Invalid delegate signature " + delegate);
            }
        } catch (NoSuchMethodException e) {
            throw new CodeGenerationException(e);
        }

        final Signature sig = ReflectUtils.getSignature(m);
        Type[] exceptions = TypeUtils.getTypes(m.getExceptionTypes());
        CodeEmitter e = super.begin_method(Constants.ACC_PUBLIC, sig, exceptions);
        e.load_this();
        e.getfield(DELEGATE);
        e.load_args();
        e.invoke_virtual(delegateType, sig);
        e.return_value();
        e.end_method();
    }
}



