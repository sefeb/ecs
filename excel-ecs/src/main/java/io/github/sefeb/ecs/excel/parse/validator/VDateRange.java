/*
 * Copyright [2024] [Hao Yong]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.sefeb.ecs.excel.parse.validator;

import io.github.sefeb.ecs.excel.parse.AbstractConfigurableInterceptor;
import io.github.sefeb.ecs.excel.parse.CellInfo;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class VDateRange extends AbstractConfigurableInterceptor<VDateRange.VDateRangeConfig> implements IValidator<LocalDateTime> {

    public static final String EC_DR_OVER_MIN = "EC_DR_OVER_MIN";
    public static final String EC_DR_OVER_MAX = "EC_DR_OVER_MAX";

    @Getter
    @Builder
    public static class VDateRangeConfig{
        private String start;
        private String end;
        private String dateTimeFormat;
        private Integer backwardOffset;
        private Integer forwardOffset;
        private String offsetUnit;
    }

    public VDateRange(VDateRangeConfig config) {
        super(config);
    }

    private Pair<LocalDateTime, LocalDateTime> getDateScope(){
        LocalDateTime curr = LocalDateTime.now();
        VDateRangeConfig drc = getConfig();
        ChronoUnit offsetUnit = StringUtils.isBlank(drc.getOffsetUnit()) ? ChronoUnit.DAYS : ChronoUnit.valueOf(drc.getOffsetUnit());
        DateTimeFormatter dateTimeFormatter = StringUtils.isBlank(drc.getDateTimeFormat()) ? DateTimeFormatter.ISO_LOCAL_DATE : DateTimeFormatter.ofPattern(drc.getDateTimeFormat().trim().toUpperCase());

        LocalDateTime start = null;
        if(!StringUtils.isBlank(drc.getStart())){
            start = LocalDateTime.parse(drc.getStart(), dateTimeFormatter);
        }
        if(null != drc.getBackwardOffset()){
            LocalDateTime ldt = curr.minus(drc.getBackwardOffset(), offsetUnit);
            if(null == start || ldt.isAfter(start))
                start = ldt;
        }

        LocalDateTime end = null;
        if(!StringUtils.isBlank(drc.getEnd())){
            end = LocalDateTime.parse(drc.getEnd(), dateTimeFormatter);
        }
        if(null != drc.getForwardOffset()){
            LocalDateTime ldt = curr.plus(drc.getForwardOffset(), offsetUnit);
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
