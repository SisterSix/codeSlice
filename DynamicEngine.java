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

import com.dnp.AmmsApplication;
import com.dnp.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Remark:
 * <p/>
 * Author: Tim
 * Date: 12/15/13 13:24
 */
public class DynamicEngine {

    private static final Logger logger = LoggerFactory.getLogger(AmmsApplication.class);
    private static DynamicEngine ourInstance = new DynamicEngine();

    public static DynamicEngine get() {
        return ourInstance;
    }

    private URLClassLoader parentClassLoader;
    private String classpath;

    private DynamicEngine() {
        this.parentClassLoader = (URLClassLoader) this.getClass().getClassLoader();
        this.buildClassPath();
    }

    private void buildClassPath() {
        this.classpath = null;
        StringBuilder sb = new StringBuilder();
        for (URL url : this.parentClassLoader.getURLs()) {
            String p = url.getFile();
            sb.append(p).append(File.pathSeparator);
        }
        this.classpath = sb.toString();
    }

    public Object javaFileToObject(String fullClassName, String fileName) throws IllegalAccessException, InstantiationException, IOException {
        return javaCodeToObject(fullClassName, FileUtil.readFile(fileName, Charset.forName("UTF-8")));
    }

    public Class javaFileToClass(String fullClassName, String fileName) throws IllegalAccessException, InstantiationException, IOException {
        return javaCodeToClass(fullClassName, FileUtil.readFile(fileName, Charset.forName("UTF-8")));
    }

    public Object javaCodeToObject(String fullClassName, String javaCode) throws IllegalAccessException, InstantiationException {
        Class clazz = javaCodeToClass(fullClassName, javaCode);
        if (null == clazz) return null;
        return clazz.newInstance();
    }

    public Class javaCodeToClass(String fullClassName, String javaCode) throws IllegalAccessException, InstantiationException {
        Class clazz = null;
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        ClassFileManager fileManager = new ClassFileManager(compiler.getStandardFileManager(diagnostics, null, null));

        List<JavaFileObject> jfiles = new ArrayList<JavaFileObject>();
        jfiles.add(new CharSequenceJavaFileObject(fullClassName, javaCode));

        List<String> options = new ArrayList<String>();
        options.add("-encoding");
        options.add("UTF-8");
        options.add("-classpath");
        options.add(this.classpath);


        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, jfiles);
        boolean success = task.call();

        if (success) {
            JavaClassObject jco = fileManager.getJavaClassObject();
            DynamicClassLoader dynamicClassLoader = new DynamicClassLoader(this.parentClassLoader);
//            DynamicClassLoader dynamicClassLoader = new DynamicClassLoader(org.springframework.boot.devtools.restart.classloader.RestartClassLoader.getSystemClassLoader().getParent());
            clazz = dynamicClassLoader.loadClass(fullClassName, jco);
        } else {
            String error = "";
            for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
                error = error + compilePrint(diagnostic);
            }
        }
        return clazz;
    }

    private String compilePrint(Diagnostic diagnostic) {
        logger.info("Compile error at " + diagnostic.getLineNumber());
        StringBuffer res = new StringBuffer();
        res.append("Code:[").append(diagnostic.getCode()).append("]\n");
        res.append("Kind:[").append(diagnostic.getKind()).append("]\n");
        res.append("Position:[").append(diagnostic.getPosition()).append("]\n");
        res.append("Start Position:[").append(diagnostic.getStartPosition()).append("]\n");
        res.append("End Position:[").append(diagnostic.getEndPosition()).append("]\n");
        res.append("Source:[").append(diagnostic.getSource()).append("]\n");
        res.append("Message:[").append(diagnostic.getMessage(null)).append("]\n");
        res.append("LineNumber:[").append(diagnostic.getLineNumber()).append("]\n");
        res.append("ColumnNumber:[").append(diagnostic.getColumnNumber()).append("]\n");
        logger.info("Compile error at " + res);
        return res.toString();
    }


}