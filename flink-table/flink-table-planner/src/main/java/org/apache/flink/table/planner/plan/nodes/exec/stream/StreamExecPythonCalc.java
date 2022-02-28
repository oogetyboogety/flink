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

package org.apache.flink.table.planner.plan.nodes.exec.stream;

import org.apache.flink.table.data.RowData;
import org.apache.flink.table.planner.plan.nodes.exec.ExecNode;
import org.apache.flink.table.planner.plan.nodes.exec.ExecNodeContext;
import org.apache.flink.table.planner.plan.nodes.exec.InputProperty;
import org.apache.flink.table.planner.plan.nodes.exec.common.CommonExecPythonCalc;
import org.apache.flink.table.types.logical.RowType;

import org.apache.calcite.rex.RexNode;

import java.util.Collections;
import java.util.List;

/** Stream {@link ExecNode} for Python ScalarFunctions. */
public class StreamExecPythonCalc extends CommonExecPythonCalc implements StreamExecNode<RowData> {

    public StreamExecPythonCalc(
            List<RexNode> projection,
            InputProperty inputProperty,
            RowType outputType,
            String description) {
        this(
                ExecNodeContext.newNodeId(),
                ExecNodeContext.newContext(StreamExecPythonCalc.class),
                projection,
                Collections.singletonList(inputProperty),
                outputType,
                description);
    }

    public StreamExecPythonCalc(
            int id,
            ExecNodeContext context,
            List<RexNode> projection,
            List<InputProperty> inputProperties,
            RowType outputType,
            String description) {
        super(id, context, projection, inputProperties, outputType, description);
    }
}
