package com.reincarnation.cache.util;

import java.util.Arrays;

/**
 * <p>
 * Description: HashFunction
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 * 
 * @author Denom
 */
public class HashFunction {
    private int result = 1;
    
    public HashFunction() {
    }
    
    public HashFunction(int primary) {
        result = 31 * result + primary;
    }
    
    public HashFunction hash(boolean field) {
        result = 31 * result + (field ? 1 : 0);
        return this;
    }
    
    public HashFunction hash(byte field) {
        result = 31 * result + field;
        return this;
    }
    
    public HashFunction hash(char field) {
        result = 31 * result + field;
        return this;
    }
    
    public HashFunction hash(short field) {
        result = 31 * result + field;
        return this;
    }
    
    public HashFunction hash(int field) {
        result = 31 * result + field;
        return this;
    }
    
    public HashFunction hash(long field) {
        result = 31 * result + (int) (field ^ (field >>> 32));
        return this;
    }
    
    public HashFunction hash(float field) {
        result = 31 * result + Float.floatToIntBits(field);
        return this;
    }
    
    public HashFunction hash(double field) {
        long doubleFieldBits = Double.doubleToLongBits(field);
        result = 31 * result + (int) (doubleFieldBits ^ (doubleFieldBits >>> 32));
        return this;
    }
    
    public HashFunction hash(Object field) {
        result = 31 * result + (field == null ? 0 : field.hashCode());
        return this;
    }
    
    public HashFunction hash(Object[] field) {
        result = 31 * result + Arrays.hashCode(field);
        return this;
    }
    
    public int getResult() {
        return result;
    }
}
