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

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Remark:
 * <p/>
 * Author: Tim
 * Date: 12/15/13 13:25
 */
public class DynamicClassLoader extends URLClassLoader {
    public DynamicClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
    }

    public Class findClassByClassName(String className) throws ClassNotFoundException {
        return this.findClass(className);
    }

    public Class loadClass(String fullName, JavaClassObject jco) {
        byte[] classData = jco.getBytes();
        return this.defineClass(fullName, classData, 0, classData.length);
    }
}