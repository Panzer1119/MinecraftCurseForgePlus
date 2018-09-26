package de.codemakers.mcfp;

import de.codemakers.io.file.AdvancedFile;

public class Main {
    
    public static AdvancedFile MINECRAFT_INSTANCE = null;
    
    public static final void main(String[] args) {
        if (args != null && args.length > 0) {
            MINECRAFT_INSTANCE = new AdvancedFile(args[0]);
        }
        System.out.println("This is the Main class");
    }
    
}
