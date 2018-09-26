package de.codemakers.mcfp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.codemakers.mcfp.entities.Overrides;

import java.io.File;
import java.io.FileReader;

public class GSONTest {
    
    public static final void main(String[] args) throws Exception {
        System.out.println("This is a Test class");
        final GsonBuilder gsonBuilder = new GsonBuilder();
        final Gson gson = gsonBuilder.create();
        final Overrides overrides = gson.fromJson(new FileReader(new File("src/test/resources/test_overrides.json")), Overrides.class);
        System.out.println(overrides);
    }
    
}
