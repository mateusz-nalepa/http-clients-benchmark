/*
 * Copyright (c) 2016-2022 VMware Inc. or its affiliates, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package reactor.core.publisher;

import com.mateusznalepa.http.clients.api.Logeusz;
import io.micrometer.core.instrument.Metrics;
import reactor.core.CoreSubscriber;
import reactor.core.Fuseable;
import reactor.util.annotation.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.hc.client5.http.impl.nio.NalepaLogger.NALEPA_LOGXDD;



/**
 * COPY PASTED Version of MonoCollectList so I'm able to add custom metrics
 *
 * Buffers all values from the source Publisher and emits it as a single List.
 *
 * @param <T> the source value type
 */
final class MonoCollectList<T> extends MonoFromFluxOperator<T, List<T>> implements Fuseable {

	MonoCollectList(Flux<? extends T> source) {
		super(source);
	}

	@Override
	public CoreSubscriber<? super T> subscribeOrReturn(CoreSubscriber<? super List<T>> actual) {
		return new MonoCollectListSubscriber<>(actual);
	}

	@Override
	public Object scanUnsafe(Attr key) {
		if (key == Attr.RUN_STYLE) return Attr.RunStyle.SYNC;
		return super.scanUnsafe(key);
	}

	static final class MonoCollectListSubscriber<T> extends Operators.BaseFluxToMonoOperator<T, List<T>> {

//		static AtomicInteger atomicInteger = new AtomicInteger(1);

//		String Idk = "IDK# " + atomicInteger.getAndIncrement();

		List<T> list;

		Long startCollectList = System.nanoTime();

		Long startCollectListMillis;

		boolean done;

		MonoCollectListSubscriber(CoreSubscriber<? super List<T>> actual) {
			super(actual);
			//not this is not thread safe so concurrent discarding multiple + add might fail with ConcurrentModificationException
			this.list = new ArrayList<>();
//			NALEPA_LOG.error("");
			startCollectListMillis = System.currentTimeMillis();
//			counterek.getAndIncrement();
//			Metrics.gauge("iloscMonoCollectList", counterek, AtomicInteger::get);
//			NALEPA_LOGXDD.error("{} START-MONO. ObjectId: {}, Time: {}", Thread.currentThread(), Idk, startCollectListMillis);
		}

		@Override
		@Nullable
		public Object scanUnsafe(Attr key) {
			if (key == Attr.TERMINATED) return done;
			if (key == Attr.CANCELLED) return !done && list == null;

			return super.scanUnsafe(key);
		}

		@Override
		public void onNext(T t) {
			if (done) {
				Operators.onNextDropped(t, actual.currentContext());
				return;
			}

			final List<T> l;
			Long startCollectListxd = System.nanoTime();

			synchronized (this) {
				l = list;
				if (l != null) {
//					NALEPA_LOGXDD.error("{} MONO-ADD ObjectId: {}", Thread.currentThread(), Idk);
					Logeusz.INSTANCE.loguj("Adding new element :D ");
					l.add(t);
					Long endCollectList2 = System.nanoTime();
					Duration collectListDuration2 = Duration.ofNanos(endCollectList2 - startCollectListxd);
					Metrics.timer("monoOnNext").record(collectListDuration2);
					return;
				}
			}



			Operators.onDiscard(t, actual.currentContext());
		}

		@Override
		public void onError(Throwable t) {
			if (done) {
				Operators.onErrorDropped(t, actual.currentContext());
				return;
			}
			done = true;

			final List<T> l;
			synchronized (this) {
				l = list;
				list = null;
			}

			if (l == null) {
				return;
			}

			Operators.onDiscardMultiple(l, actual.currentContext());

			actual.onError(t);
		}

		@Override
		public void onComplete() {
			if (done) {
				return;
			}
			done = true;

			completePossiblyEmpty();
		}

		@Override
		public void cancel() {
			s.cancel();

			final List<T> l;
			synchronized (this) {
				l = list;
				list = null;
			}

			if (l != null) {
				Operators.onDiscardMultiple(l, actual.currentContext());
			}
		}

		@Override
		List<T> accumulatedValue() {
			final List<T> l;

			Long startCollectListXD = System.nanoTime();

			synchronized (this) {
				l = list;
				list = null;
			}
			Long endCollectList1 = System.nanoTime();
			Duration collectListDuration1 = Duration.ofNanos(endCollectList1 - startCollectListXD);
			Metrics.timer("monoAccumulatedValue").record(collectListDuration1);

			Long endCollectList = System.nanoTime();
			Duration collectListDuration = Duration.ofNanos(endCollectList - startCollectList);
			Metrics.timer("collectListDuration").record(collectListDuration);
//			Metrics.summary("collectListDurationSummary").record(collectListDuration.toMillis());
//			counterek.getAndDecrement();

//			NALEPA_LOGXDD.error("{} FINISH-MONO. Took: {}, ObjectId: {}, StartTime: {}, Time: {}",
//					Thread.currentThread(),
//					collectListDuration.toMillis(),
//					Idk,
//					startCollectListMillis,
//					System.currentTimeMillis() - startCollectListMillis
//			);
			return l;
		}
	}
}
