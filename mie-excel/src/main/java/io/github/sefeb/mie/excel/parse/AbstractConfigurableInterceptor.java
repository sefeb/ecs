package io.github.sefeb.mie.excel.parse;

import com.google.gson.Gson;

public abstract class AbstractConfigurableInterceptor<C>{

    private C config;

    private AbstractConfigurableInterceptor(){
        // forbid no arg constructor
    }

    public AbstractConfigurableInterceptor(C config){
        this.config = config;
    }

    public AbstractConfigurableInterceptor(String json, Class<C> clazz){
        this.config = new Gson().fromJson(json, clazz);
    }

    protected C getConfig(){
        return config;
    }

}
