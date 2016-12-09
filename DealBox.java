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

package com.dnp.util;


import com.dnp.core.pool.PoolNamedObjectAble;
import com.dnp.core.pool.PoolService;


/**
 * Remark:
 * <p/>
 * Author: Tim
 * Date: 12/18/13 20:20
 */
public class DealBox implements PoolNamedObjectAble {

    public DealBox() {
    }

    private int seed[];
    private int current;

    protected DealBox setMax(int max) {
        this.current = max;
        for (int i = 0; i < max; i++) {
            seed[i] = i;
        }
        return this;
    }

    public int next() {
        int pos = (int) (Math.random() * (current + 1));
        int result = seed[pos];
        seed[pos] = seed[current];
        current--;
        return result;
    }

    public static DealBox get(int number) {
        return get("" + number);
    }

    public static DealBox get(String name) {
        return (DealBox) PoolService.get().getObjByName(name, DealBox.class);
    }

    private String name;

    @Override
    public String name() {
        return name;
    }

    @Override
    public void reset(String name) {
        int max = Integer.parseInt(name);
        if (null == seed) {
            seed = new int[max];
        }
        setMax(max);
    }

    @Override
    public void remand() {
        PoolService.get().returnNameObj(this);
    }


}
