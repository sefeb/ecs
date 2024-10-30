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

package io.github.sefeb.ecs.excel.parse.processor;

import io.github.sefeb.ecs.excel.parse.AbstractConfigurableInterceptor;
import lombok.Builder;
import lombok.Getter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Convert cell string value to Boolean.
 * In config, a symbol can not belong to both true and false symbol sets.
 * Return null if cell value not exists in both true and false symbol sets.
 */
public class PBool extends AbstractConfigurableInterceptor<PBool.PBoolConfig> implements IProcessor<String, Boolean> {

	/**
	 * Config for PBool
	 * <p>
	 * Options:
	 * <ol>
	 *   <li>symbolsForTrue: contains symbols which should be determine as true</li>
	 *   <li>symbolsForFalse: contains symbols which should be determine as false</li>
	 * </ol>
	 * if cell value not exists in both two sets, then return null
	 */
	@Builder
	@Getter
	public static class PBoolConfig{
		/**
		 * symbols of value Boolean.TRUE
		 */

		private Set<String> symbolsForTrue;
		/**
		 * symbols of value Boolean.FALSE
		 */
		private Set<String> symbolsForFalse;
	}

	private final Set<String> trueSet = new HashSet<>();
	private final Set<String> falseSet = new HashSet<>();

	public PBool(PBoolConfig config) {
		super(config);
		trueSet.addAll(Objects.requireNonNullElse(config.getSymbolsForTrue(), new HashSet<>()));
		falseSet.addAll(Objects.requireNonNullElse(config.getSymbolsForFalse(), new HashSet<>()));
		if(trueSet.retainAll(falseSet))
			throw new IllegalArgumentException("a symbol can not belong to both true and false symbol set");
	}

	@Override
    public Boolean process(String text) {
		if(!trueSet.isEmpty() && trueSet.contains(text))
			return Boolean.TRUE;
		else if(!falseSet.isEmpty() && falseSet.contains(text))
			return Boolean.FALSE;
		return null;
    }
}
