package io.github.sefeb.mie.excel.parse.processor;

import io.github.sefeb.mie.excel.parse.AbstractConfigurableInterceptor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PNumber extends AbstractConfigurableInterceptor<PNumberConfig> implements IProcessor<String, BigDecimal> {

    public PNumber(PNumberConfig config) {
        super(config);
    }

    @Override
    public BigDecimal process(String value) {
        Double result = null;
        if(StringUtils.isBlank(value)){
            if(null != getConfig().nullAsValue()){
                result = getConfig().nullAsValue();
            } else {
                return null;
            }
        } else {
            try {
                result = Double.valueOf(value);
            } catch(NumberFormatException e){
                throw new ProcessorException("fail to convert string to double", e);
            }
        }
        BigDecimal bd = BigDecimal.valueOf(result);
        RoundingMode roundingMode = (StringUtils.isBlank(getConfig().roundingMode()) ? RoundingMode.HALF_UP : RoundingMode.valueOf(getConfig().roundingMode()));
        if(null != getConfig().scale()){
            bd = bd.setScale(getConfig().scale(), roundingMode);
        }
        return bd;
    }
}
