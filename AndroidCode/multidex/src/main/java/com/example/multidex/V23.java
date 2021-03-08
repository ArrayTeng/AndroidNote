package com.example.multidex;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

/**
 *  multidex  热修复的方式就是将我们写的修复用的dex包打到有问题的dex包之前，核心在于如何修改 Element[] 数组
 */
public class V23 {

    /**
     *
     * @param classLoader 类加载器
     * @param dexFiles 加载dex的文件
     * @param optFile 优化后的dex保存目录
     */
    public static void install(ClassLoader classLoader, List<File> dexFiles,File optFile){
        try {
            //找到DexPathList这个属性
            Field pathList = ShareReflectUtil.findField(classLoader,"pathList");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
