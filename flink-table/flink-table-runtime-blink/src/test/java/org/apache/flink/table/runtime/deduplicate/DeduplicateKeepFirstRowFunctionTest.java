/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.table.runtime.deduplicate;

import org.apache.flink.api.common.time.Time;
import org.apache.flink.streaming.api.operators.KeyedProcessOperator;
import org.apache.flink.streaming.util.KeyedOneInputStreamOperatorTestHarness;
import org.apache.flink.streaming.util.OneInputStreamOperatorTestHarness;
import org.apache.flink.table.dataformat.BaseRow;
import org.apache.flink.table.runtime.util.BaseRowHarnessAssertor;
import org.apache.flink.table.runtime.util.BinaryRowKeySelector;
import org.apache.flink.table.runtime.util.GenericRowRecordSortComparator;
import org.apache.flink.table.type.InternalTypes;
import org.apache.flink.table.typeutils.BaseRowTypeInfo;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.apache.flink.table.runtime.util.StreamRecordUtils.record;

/**
 * Tests for {@link DeduplicateKeepFirstRowFunction}.
 */
public class DeduplicateKeepFirstRowFunctionTest {

	private Time minTime = Time.milliseconds(10);
	private Time maxTime = Time.milliseconds(20);

	private BaseRowTypeInfo inputRowType = new BaseRowTypeInfo(InternalTypes.STRING, InternalTypes.LONG,
			InternalTypes.INT);

	private int rowKeyIdx = 1;
	private BinaryRowKeySelector rowKeySelector = new BinaryRowKeySelector(new int[] { rowKeyIdx },
			inputRowType.getInternalTypes());

	private BaseRowHarnessAssertor assertor = new BaseRowHarnessAssertor(
			inputRowType.getFieldTypes(),
			new GenericRowRecordSortComparator(rowKeyIdx, inputRowType.getInternalTypes()[rowKeyIdx]));

	private OneInputStreamOperatorTestHarness<BaseRow, BaseRow> createTestHarness(
			DeduplicateKeepFirstRowFunction func)
			throws Exception {
		KeyedProcessOperator<BaseRow, BaseRow, BaseRow> operator = new KeyedProcessOperator<>(func);
		return new KeyedOneInputStreamOperatorTestHarness<>(operator, rowKeySelector, rowKeySelector.getProducedType());
	}

	@Test
	public void test() throws Exception {
		DeduplicateKeepFirstRowFunction func = new DeduplicateKeepFirstRowFunction(minTime.toMilliseconds(),
				maxTime.toMilliseconds());
		OneInputStreamOperatorTestHarness<BaseRow, BaseRow> testHarness = createTestHarness(func);
		testHarness.open();
		testHarness.processElement(record("book", 1L, 12));
		testHarness.processElement(record("book", 2L, 11));
		testHarness.processElement(record("book", 1L, 13));
		testHarness.close();

		// Keep FirstRow in deduplicate will not send retraction
		List<Object> expectedOutputOutput = new ArrayList<>();
		expectedOutputOutput.add(record("book", 1L, 12));
		expectedOutputOutput.add(record("book", 2L, 11));
		assertor.assertOutputEqualsSorted("output wrong.", expectedOutputOutput, testHarness.getOutput());
	}

}
