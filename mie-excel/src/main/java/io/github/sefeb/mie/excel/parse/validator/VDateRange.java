package io.github.sefeb.mie.excel.parse.validator;

import com.sefeb.mie.excel.parse.*;
import io.github.sefeb.mie.excel.parse.AbstractConfigurableInterceptor;
import io.github.sefeb.mie.excel.parse.CellInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class VDateRange extends AbstractConfigurableInterceptor<VDateRangeConfig> implements IValidator<LocalDateTime> {

    public static final String EC_DR_OVER_MIN = "EC_DR_OVER_MIN";
    public static final String EC_DR_OVER_MAX = "EC_DR_OVER_MAX";


    public VDateRange(VDateRangeConfig config) {
        super(config);
    }

    private Pair<LocalDateTime, LocalDateTime> getDateScope(){
        LocalDateTime curr = LocalDateTime.now();
        VDateRangeConfig drc = getConfig();
        ChronoUnit offsetUnit = StringUtils.isBlank(drc.offsetUnit()) ? ChronoUnit.DAYS : ChronoUnit.valueOf(drc.offsetUnit());
        DateTimeFormatter dateTimeFormatter = StringUtils.isBlank(drc.dateTimeFormat()) ? DateTimeFormatter.ISO_LOCAL_DATE : DateTimeFormatter.ofPattern(drc.dateTimeFormat().trim().toUpperCase());

        LocalDateTime start = null;
        if(!StringUtils.isBlank(drc.start())){
            start = LocalDateTime.parse(drc.start(), dateTimeFormatter);
        }
        if(null != drc.backwardOffset()){
            LocalDateTime ldt = curr.minus(drc.backwardOffset(), offsetUnit);
            if(null == start || ldt.isAfter(start))
                start = ldt;
        }

        LocalDateTime end = null;
        if(!StringUtils.isBlank(drc.end())){
            end = LocalDateTime.parse(drc.end(), dateTimeFormatter);
        }
        if(null != drc.forwardOffset()){
            LocalDateTime ldt = curr.plus(drc.forwardOffset(), offsetUnit);
            if(null == end || ldt.isBefore(end))
                end = ldt;
        }

        return Pair.of(start, end);
    }

    @Override
    public ValidateResult validate(CellInfo cell, LocalDateTime value) {
        Pair<LocalDateTime, LocalDateTime> scope = getDateScope();
        if(null != scope.getLeft() && value.isBefore(scope.getLeft())) {
            return new ValidateResult(false, cell, EC_DR_OVER_MIN, getErrorMessage(EC_DR_OVER_MIN));
        }
        if(null != scope.getRight() && value.isAfter(scope.getRight())) {
            return new ValidateResult(false, cell, EC_DR_OVER_MAX, getErrorMessage(EC_DR_OVER_MAX));
        }
        return new ValidateResult(true, cell,null, null);
    }

    @Override
    public String getErrorMessage(String errorCode) {
        String msg = null;
        switch(errorCode){
            case EC_DR_OVER_MIN -> {
                msg = "less than min value.";
            }
            case EC_DR_OVER_MAX -> {   // cell
                msg = "greater than max value.";
            }
            default -> {
                // do nothing
            }
        }
        return msg;
    }

}
