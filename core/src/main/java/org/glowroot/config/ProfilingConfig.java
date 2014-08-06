/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glowroot.config;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;

import org.glowroot.config.JsonViews.UiView;
import org.glowroot.markers.Immutable;
import org.glowroot.markers.UsedByJsonBinding;

/**
 * Immutable structure to hold the profiling config.
 * 
 * @author Trask Stalnaker
 * @since 0.5
 */
@Immutable
public class ProfilingConfig {

    public static final int USE_GENERAL_STORE_THRESHOLD = -1;

    // percentage of traces to apply profiling, between 0.0 and 100.0
    private final double tracePercentage;
    private final int intervalMillis;
    private final int maxSeconds;
    // store threshold of -1 means use general config store threshold for profiled traces,
    // the real threshold is the minimum of this and the general threshold
    private final int storeThresholdMillis;

    private final String version;

    static ProfilingConfig getDefault() {
        final double tracePercentage = 0;
        final int intervalMillis = 50;
        final int maxSeconds = 30;
        final int storeThresholdMillis = USE_GENERAL_STORE_THRESHOLD;
        return new ProfilingConfig(tracePercentage, intervalMillis, maxSeconds,
                storeThresholdMillis);
    }

    public static Overlay overlay(ProfilingConfig base) {
        return new Overlay(base);
    }

    @VisibleForTesting
    public ProfilingConfig(double tracePercentage, int intervalMillis, int maxSeconds,
            int storeThresholdMillis) {
        this.tracePercentage = tracePercentage;
        this.intervalMillis = intervalMillis;
        this.maxSeconds = maxSeconds;
        this.storeThresholdMillis = storeThresholdMillis;
        version = VersionHashes.sha1(tracePercentage, intervalMillis, maxSeconds,
                storeThresholdMillis);
    }

    public double getTracePercentage() {
        return tracePercentage;
    }

    public int getIntervalMillis() {
        return intervalMillis;
    }

    public int getMaxSeconds() {
        return maxSeconds;
    }

    public int getStoreThresholdMillis() {
        return storeThresholdMillis;
    }

    @JsonView(UiView.class)
    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("tracePercentage", tracePercentage)
                .add("intervalMillis", intervalMillis)
                .add("maxSeconds", maxSeconds)
                .add("storeThresholdMillis", storeThresholdMillis)
                .add("version", version)
                .toString();
    }

    // for overlaying values on top of another config using ObjectMapper.readerForUpdating()
    @UsedByJsonBinding
    public static class Overlay {

        private double tracePercentage;
        private int intervalMillis;
        private int maxSeconds;
        private int storeThresholdMillis;

        private Overlay(ProfilingConfig base) {
            tracePercentage = base.tracePercentage;
            intervalMillis = base.intervalMillis;
            maxSeconds = base.maxSeconds;
            storeThresholdMillis = base.storeThresholdMillis;
        }
        public void setTracePercentage(double tracePercentage) {
            this.tracePercentage = tracePercentage;
        }
        public void setIntervalMillis(int intervalMillis) {
            this.intervalMillis = intervalMillis;
        }
        public void setMaxSeconds(int maxSeconds) {
            this.maxSeconds = maxSeconds;
        }
        public void setStoreThresholdMillis(int storeThresholdMillis) {
            this.storeThresholdMillis = storeThresholdMillis;
        }
        public ProfilingConfig build() {
            return new ProfilingConfig(tracePercentage, intervalMillis, maxSeconds,
                    storeThresholdMillis);
        }
    }
}
