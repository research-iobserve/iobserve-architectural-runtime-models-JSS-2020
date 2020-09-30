/***************************************************************************
 * Copyright (C) 2015 iObserve Project (https://www.iobserve-devops.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/
package org.iobserve.monitoring.probe.j2ee;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.object.AfterOperationFailedObjectEvent;
import kieker.common.record.flow.trace.operation.object.AfterOperationObjectEvent;
import kieker.common.record.flow.trace.operation.object.BeforeOperationObjectEvent;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.core.registry.TraceRegistry;
import kieker.monitoring.timer.ITimeSource;

/**
 *
 * @author Reiner Jung
 *
 */
@Interceptor
public class TraceObjectInterceptor {

    /** Kieker trace registry. */
    private static final TraceRegistry TRACEREGISTRY = TraceRegistry.INSTANCE;

    /** Kieker monitoring controller. */
    private final IMonitoringController monitoringCtrl = MonitoringController.getInstance();
    /** Kieker time source. */
    private final ITimeSource timeSource = this.monitoringCtrl.getTimeSource();

    /**
     * Initialize trace interceptor.
     */
    public TraceObjectInterceptor() {
        // nothing to be done here
    }

    /**
     * Around advice configuration.
     *
     * @param context
     *            the invocation context of a bean call.
     * @return the return value of the next method in the chain
     * @throws Throwable
     */
    @AroundInvoke
    public Object interceptMethodCall(final InvocationContext context) throws Throwable { // NOCS
                                                                                          // (IllegalThrowsCheck)
        if (this.monitoringCtrl.isMonitoringEnabled()) {
            final String signature = context.getMethod().toString();

            if (this.monitoringCtrl.isProbeActivated(signature)) {
                // common fields
                TraceMetadata trace = TraceObjectInterceptor.TRACEREGISTRY.getTrace();
                final boolean newTrace = trace == null;
                if (newTrace) {
                    trace = TraceObjectInterceptor.TRACEREGISTRY.registerTrace();
                    this.monitoringCtrl.newMonitoringRecord(trace);
                }

                final long traceId = trace.getTraceId();
                final String clazz = context.getTarget().getClass().getCanonicalName();
                final int objectId = System.identityHashCode(context);

                // measure before execution
                this.monitoringCtrl.newMonitoringRecord(new BeforeOperationObjectEvent(this.timeSource.getTime(),
                        traceId, trace.getNextOrderId(), signature, clazz, objectId));

                // execution of the called method
                try {
                    final Object retval = context.proceed();

                    // measure after successful execution
                    this.monitoringCtrl.newMonitoringRecord(new AfterOperationObjectEvent(this.timeSource.getTime(),
                            traceId, trace.getNextOrderId(), signature, clazz, objectId));
                    return retval;
                } catch (final Throwable th) { // NOPMD NOCS (catch throw might
                                               // ok here)
                    // measure after failed execution
                    this.monitoringCtrl
                            .newMonitoringRecord(new AfterOperationFailedObjectEvent(this.timeSource.getTime(), traceId,
                                    trace.getNextOrderId(), signature, clazz, th.toString(), objectId));
                    throw th;
                } finally {
                    if (newTrace) { // close the trace
                        TraceObjectInterceptor.TRACEREGISTRY.unregisterTrace();
                    }
                }
            } else {
                return context.proceed();
            }
        } else {
            return context.proceed();
        }
    }
}
