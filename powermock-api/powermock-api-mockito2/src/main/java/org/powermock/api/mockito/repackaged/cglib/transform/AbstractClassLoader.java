/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.transform;

import org.powermock.api.mockito.repackaged.asm.Attribute;
import org.powermock.api.mockito.repackaged.asm.ClassReader;
import org.powermock.api.mockito.repackaged.asm.ClassWriter;
import org.powermock.api.mockito.repackaged.cglib.core.ClassGenerator;
import org.powermock.api.mockito.repackaged.cglib.core.CodeGenerationException;
import org.powermock.api.mockito.repackaged.cglib.core.DebuggingClassWriter;

import java.io.IOException;

abstract public class AbstractClassLoader extends ClassLoader {
    private static java.security.ProtectionDomain DOMAIN ;

    static{

        DOMAIN = (java.security.ProtectionDomain)
        java.security.AccessController.doPrivileged(
          new java.security.PrivilegedAction() {
            public Object run() {
               return AbstractClassLoader.class.getProtectionDomain();
            }
        });
     }

    private ClassFilter filter;
    private ClassLoader classPath;
    
    protected AbstractClassLoader(ClassLoader parent, ClassLoader classPath, ClassFilter filter) {
        super(parent);
        this.filter = filter;
        this.classPath = classPath;
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        
        Class loaded = findLoadedClass(name);
        
        if( loaded != null ){
            if( loaded.getClassLoader() == this ){
               return loaded;
            }//else reload with this class loader
        }
        
        if (!filter.accept(name)) {
            return super.loadClass(name);
        }
        ClassReader r;
        try {
            
           java.io.InputStream is = classPath.getResourceAsStream( 
                       name.replace('.','/') + ".class"
                  ); 
           
           if (is == null) {
               
              throw new ClassNotFoundException(name);
              
           }
           try { 
               
              r = new ClassReader(is);
            
           } finally {
               
              is.close();
             
           }
        } catch (IOException e) {
            throw new ClassNotFoundException(name + ":" + e.getMessage());
        }

        try {
            ClassWriter w =  new DebuggingClassWriter(ClassWriter.COMPUTE_MAXS);
            getGenerator(r).generateClass(w);
            byte[] b = w.toByteArray();
            Class c = super.defineClass(name, b, 0, b.length, DOMAIN);
            postProcess(c);
            return c;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new CodeGenerationException(e);
        }
    }

    protected ClassGenerator getGenerator(ClassReader r) {
        return new ClassReaderGenerator(r, attributes(), getFlags());
    }

    protected int getFlags() {
        return 0;
    }
    
    protected Attribute[] attributes() {
        return null;
    }

    protected void postProcess(Class c) {
    }
}
