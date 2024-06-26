/*
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
package org.apache.pulsar.broker.service.plugin;

import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.bookkeeper.mledger.Entry;
import org.apache.pulsar.common.nar.NarClassLoader;

@Slf4j
@ToString
public class EntryFilterWithClassLoader implements EntryFilter {
    private final EntryFilter entryFilter;
    private final NarClassLoader classLoader;
    private final boolean classLoaderOwned;

    public EntryFilterWithClassLoader(EntryFilter entryFilter, NarClassLoader classLoader, boolean classLoaderOwned) {
        this.entryFilter = entryFilter;
        this.classLoader = classLoader;
        this.classLoaderOwned = classLoaderOwned;
    }

    @Override
    public FilterResult filterEntry(Entry entry, FilterContext context) {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            return entryFilter.filterEntry(entry, context);
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    @VisibleForTesting
    public EntryFilter getEntryFilter() {
        return entryFilter;
    }

    @Override
    public void close() {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            entryFilter.close();
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
        if (classLoaderOwned) {
            log.info("Closing classloader {} for EntryFilter {}", classLoader, entryFilter.getClass().getName());
            try {
                classLoader.close();
            } catch (IOException e) {
                log.error("close EntryFilterWithClassLoader failed", e);
            }
        }
    }
}
