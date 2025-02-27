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

package io.github.sefeb.ecs.excel.parse;

import io.github.sefeb.ecs.excel.ann.ExcelColumn;
import io.github.sefeb.ecs.excel.parse.processor.IProcessor;
import io.github.sefeb.ecs.excel.parse.processor.ProcessorException;
import io.github.sefeb.ecs.excel.parse.validator.IValidator;
import io.github.sefeb.ecs.excel.parse.validator.ValidateResult;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class RowSerializer<T> {

    private static final Logger log = LoggerFactory.getLogger(RowSerializer.class);

    record Prop(
        String propertyName,
        Class<?> propertyType
    ){}

    private Map<String, Prop> propertyMap;

    private final Map<String, List<IInterceptor>> interceptors;

    private final Map<String, CellInfo> headerCache;

    private final Class<? extends T> targetClass;

    private final IExcelParserObserver[] observers;

    public RowSerializer(Class<? extends T> clazz, Map<String, CellInfo> headers, Map<String, List<IInterceptor>> interceptors, IExcelParserObserver[] observers){
        this.interceptors = Objects.requireNonNullElse(interceptors, new HashMap<>());
        this.targetClass = clazz;
        this.headerCache = headers;
        this.observers = observers;
    }

    /**
     * initialize object property mapping:
     * key=header
     * value=object property
     * @return
     */
    private Map<String, Prop> getPropertyMap(){
        if(null == this.propertyMap) {
            List<Field> mappingFields = FieldUtils.getFieldsListWithAnnotation(this.targetClass, ExcelColumn.class);
            Map<String, Prop> propertyMap = new HashMap<>();
            for (Field field : mappingFields) {
                propertyMap.put(
                    field.getAnnotation(ExcelColumn.class).value(),
                    new Prop(field.getName(), field.getType())
                );
            }
            this.propertyMap = propertyMap;
        }
        return this.propertyMap;
    }

    private T convertRowValue(RowInfo row, Class<? extends T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        T result = clazz.getConstructor().newInstance();
        Map<String, CellInfo> data = ExcelParserUtils.getRowHeaderCache(row);
        for(String header: this.headerCache.keySet()){
            String rowIndex = Integer.toString(row.getIndex());
            String colIndex = this.headerCache.get(header).getColIndex();
            CellInfo ci = data.containsKey(header) ? data.get(header) : new CellInfo(
                rowIndex,
                colIndex,
                colIndex + rowIndex,
                header,
                null
            );
            Object value = ci.getValue();
            if (this.interceptors.containsKey(header)) {
                value = executeInterceptors(row.getIndex(), ci, this.interceptors.get(header));
            }
            setBeanProperty(result, header, value);
        }
        return result;
    }

    private Object executeInterceptors(int rowIndex, CellInfo cell, List<IInterceptor> interceptors) throws InvocationTargetException, IllegalAccessException {
        Object v = cell.getValue();
        for (IInterceptor t : interceptors) {
            if (t instanceof IValidator<?>) {
                ValidateResult validateResult = ((IValidator) t).validate(cell, v);
                if (!validateResult.success()) {
                    Arrays.stream(this.observers).forEach(o -> o.validateError(validateResult));
                }
            } else if (t instanceof IProcessor<?,?>){
                try {
                    v = ((IProcessor) t).process(v);
                }catch(ProcessorException e){
                    throw new ProcessorException(cell, e.getMessage(), e.getCause());
                }
            }
        }
        return v;

    }

    private void setBeanProperty(Object bean, String header, Object value) throws InvocationTargetException, IllegalAccessException {
        Prop p = getPropertyMap().get(header);
        if (null != p && !Strings.isBlank(p.propertyName())) {
            BeanUtils.setProperty(bean, p.propertyName(), value);
        }
    }

    public T serialize(RowInfo row){
        try {
            return convertRowValue(row, this.targetClass);
        }catch(ProcessorException e){
            throw e;
        } catch (Exception e) {
            throw new ExcelParseException(e.getMessage(), e);
        }
    }

}
