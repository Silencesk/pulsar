/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.pulsar.broker.loadbalance;

import com.google.common.collect.Multimap;
import org.apache.pulsar.broker.ServiceConfiguration;

/**
 * Load management component which determines the criteria for unloading bundles.
 * 负载切分的策略：也就是达到切分条件（策略），如何切分（策略）
 */
public interface LoadSheddingStrategy {

    /**
     * Recommend that all of the returned bundles be unloaded.
     * 查找需要卸载的bundles
     *
     * @param loadData
     *            The load data to used to make the unloading decision.
     * @param conf
     *            The service configuration.
     * @return A map from all selected bundles to the brokers on which they reside.
     */
    Multimap<String, String> findBundlesForUnloading(LoadData loadData, ServiceConfiguration conf);
}
