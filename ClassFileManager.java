/*
 * The MIT License (MIT)
 *   Copyright (c) 2013 DONOPO Studio
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 */

package com.dnp.util.djc;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.IOException;

/**
 * Remark:
 * <p/>
 * Author: Tim
 * Date: 12/15/13 13:26
 */
@SuppressWarnings("unchecked")
public class ClassFileManager extends
        ForwardingJavaFileManager {
    public JavaClassObject getJavaClassObject() {
        return jclassObject;
    }

    private JavaClassObject jclassObject;

    public ClassFileManager(StandardJavaFileManager
                                    standardManager) {
        super(standardManager);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location,
                                               String className, JavaFileObject.Kind kind, FileObject sibling)
            throws IOException {
        jclassObject = new JavaClassObject(className, kind);
        return jclassObject;
    }
}