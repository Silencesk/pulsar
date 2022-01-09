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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.pulsar.broker.PulsarServerException;
import org.apache.pulsar.broker.PulsarService;
import org.apache.pulsar.common.naming.ServiceUnitId;
import org.apache.pulsar.common.stats.Metrics;
import org.apache.pulsar.policies.data.loadbalancer.LocalBrokerData;

/**
 * New proposal for a load manager interface which attempts to use more intuitive method names and provide a starting
 * place for new load manager proposals.
 * proposal-提议/建议   intuitive-直觉的/易懂的
 */
public interface ModularLoadManager {

    /**
     * As any broker, disable the broker this manager is running on.
     *
     * @throws PulsarServerException
     *             If ZooKeeper failed to disable the broker.
     */
    void disableBroker() throws PulsarServerException;

    /**
     * As the leader broker, select bundles for the namespace service to unload so that they may be reassigned to new
     * brokers.
     * 执行负载的卸载
     */
    void doLoadShedding();

    /**
     * As the leader broker, attempt to automatically detect and split hot namespace bundles.
     * 检查namespaceBundle的切分；尝试自动探测与切分过热的namespace bundles
     */
    void checkNamespaceBundleSplit();

    /**
     * Initialize this load manager using the given pulsar service.
     */
    void initialize(PulsarService pulsar);

    /**
     * As the leader broker, find a suitable broker for the assignment of the given bundle.
     * 为分配选择broker
     *
     * @param serviceUnit
     *            ServiceUnitId for the bundle.
     * @return The name of the selected broker, as it appears on ZooKeeper.
     */
    Optional<String> selectBrokerForAssignment(ServiceUnitId serviceUnit);

    /**
     * As any broker, start the load manager.
     *
     * @throws PulsarServerException
     *             If an unexpected error prevented the load manager from being started.
     */
    void start() throws PulsarServerException;

    /**
     * As any broker, stop the load manager.
     *
     * @throws PulsarServerException
     *             If an unexpected error occurred when attempting to stop the load manager.
     */
    void stop() throws PulsarServerException;

    /**
     * As any broker, retrieve the namespace bundle stats and system resource usage to update data local to this broker.
     * 更新本地broker数据
     */
    LocalBrokerData updateLocalBrokerData();

    /**
     * As any broker, write the local broker data to ZooKeeper.
     * 写broker数据到zk
     */
    void writeBrokerDataOnZooKeeper();

    /**
     * As any broker, write the local broker data to ZooKeeper, forced or not.
     * 强制写broker数据到zk，if or not
     */
    default void writeBrokerDataOnZooKeeper(boolean force) {
        writeBrokerDataOnZooKeeper();
    }

    /**
     * As the leader broker, write bundle data aggregated from all brokers to ZooKeeper.
     * 写bundle数据到zk
     */
    void writeBundleDataOnZooKeeper();

    /**
     * Get available broker list in cluster.
     * 获取集群中可用的broker列表
     * @return
     */
    Set<String> getAvailableBrokers();

    /**
     * Fetch local-broker data from load-manager broker cache.
     * 后去broker的本地数据
     * @param broker load-balancer path
     * @return
     */
    LocalBrokerData getBrokerLocalData(String broker);

    /**
     * Fetch load balancing metrics.
     * 获取负载均衡的指标
     *
     * @return List of LoadBalancing Metrics
     */
    List<Metrics> getLoadBalancingMetrics();
}
