/*
 * ====================================================================
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
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.hc.core5.reactor;

import java.time.Duration;

import static org.apache.hc.client5.http.impl.nio.NalepaLogger.NALEPA_LOG;

final class IOReactorWorker implements Runnable {

    private final AbstractSingleCoreIOReactor ioReactor;

    private volatile Throwable throwable;

    public IOReactorWorker(final AbstractSingleCoreIOReactor ioReactor) {
        super();
        this.ioReactor = ioReactor;
    }

    @Override
    public void run() {
        try {
            Long startIOReactorWorker = System.nanoTime();
            this.ioReactor.execute();

            Long endIOReactorWorker = System.nanoTime();
            Duration processIOReactorWorker = Duration.ofNanos(endIOReactorWorker - startIOReactorWorker);
//            NALEPA_LOG.error("{} processIOReactorWorker took: {} ",
//                    Thread.currentThread(),
//                    processIOReactorWorker
//            );
        } catch (final Error ex) {
            this.throwable = ex;
            throw ex;
        } catch (final Exception ex) {
            this.throwable = ex;
        }
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

}