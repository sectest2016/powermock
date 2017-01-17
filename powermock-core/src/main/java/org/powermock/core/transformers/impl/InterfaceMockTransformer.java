/*
 *   Copyright 2016 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.powermock.core.transformers.impl;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import org.powermock.core.transformers.TransformStrategy;

import static org.powermock.core.transformers.TransformStrategy.CLASSLOADER;
import static org.powermock.core.transformers.TransformStrategy.INST_TRANSFORM;

/**
 *
 */
public class InterfaceMockTransformer extends AbstractMainMockTransformer {

    public InterfaceMockTransformer() {
        this(CLASSLOADER);
    }

    public InterfaceMockTransformer(TransformStrategy strategy) {
        super(strategy);
    }

    @Override
    protected CtClass transformMockClass(CtClass clazz) throws CannotCompileException, NotFoundException {
        if (!clazz.isInterface()) {
            return clazz;
        }

        /*
        * Set class modifier to public to allow for mocking of package private
        * classes. This is needed because we've changed to CgLib naming policy
        * to allow for mocking of signed classes.
        */

        final String name = allowMockingOfPackagePrivateClasses(clazz);

        suppressStaticInitializerIfRequested(clazz, name);

        allowMockingOfStaticAndFinalAndNativeMethods(clazz);

        if (strategy != INST_TRANSFORM) {
            clazz.instrument(new PowerMockExpressionEditor(clazz));
        }

        ensureJvmMethodSizeLimit(clazz);

        return clazz;
    }
}
