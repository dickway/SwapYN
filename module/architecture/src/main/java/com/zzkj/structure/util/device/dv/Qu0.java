package com.zzkj.structure.util.device.dv;

import java.io.File;
import java.io.FileFilter;

public final class Qu0 implements FileFilter {
    public final Qt0 A00;

    public Qu0(Qt0 r1) {
        this.A00 = r1;
    }

    public final boolean accept(File file) {
        return file.getName().matches("cpu[0-9]+");
    }
}