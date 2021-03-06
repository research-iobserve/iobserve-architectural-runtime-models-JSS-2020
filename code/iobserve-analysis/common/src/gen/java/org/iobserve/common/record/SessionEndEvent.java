/***************************************************************************
 * Copyright 2017 iObserve Project
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
package org.iobserve.common.record;

import java.nio.BufferOverflowException;

import kieker.common.record.flow.AbstractEvent;
import kieker.common.record.io.IValueDeserializer;
import kieker.common.record.io.IValueSerializer;
import kieker.common.util.registry.IRegistry;

import org.iobserve.common.record.ISessionEvent;

/**
 * @author Generic Kieker
 * API compatibility: Kieker 1.13.0
 * 
 * @since 1.13
 */
public class SessionEndEvent extends AbstractEvent implements ISessionEvent {
	private static final long serialVersionUID = -2981998362744145083L;

	/** Descriptive definition of the serialization size of the record. */
	public static final int SIZE = TYPE_SIZE_LONG // IEventRecord.timestamp
			 + TYPE_SIZE_STRING // ISessionEvent.sessionId
	;
	
	public static final Class<?>[] TYPES = {
		long.class, // IEventRecord.timestamp
		String.class, // ISessionEvent.sessionId
	};
	
	
	/** default constants. */
	public static final String SESSION_ID = "";
	
	/** property name array. */
	private static final String[] PROPERTY_NAMES = {
		"timestamp",
		"sessionId",
	};
	
	/** property declarations. */
	private final String sessionId;
	
	/**
	 * Creates a new instance of this class using the given parameters.
	 * 
	 * @param timestamp
	 *            timestamp
	 * @param sessionId
	 *            sessionId
	 */
	public SessionEndEvent(final long timestamp, final String sessionId) {
		super(timestamp);
		this.sessionId = sessionId == null?"":sessionId;
	}

	/**
	 * This constructor converts the given array into a record.
	 * It is recommended to use the array which is the result of a call to {@link #toArray()}.
	 * 
	 * @param values
	 *            The values for the record.
	 *
	 * @deprecated since 1.13. Use {@link #SessionEndEvent(IValueDeserializer)} instead.
	 */
	@Deprecated
	public SessionEndEvent(final Object[] values) { // NOPMD (direct store of values)
		super(values, TYPES);
		this.sessionId = (String) values[1];
	}

	/**
	 * This constructor uses the given array to initialize the fields of this record.
	 * 
	 * @param values
	 *            The values for the record.
	 * @param valueTypes
	 *            The types of the elements in the first array.
	 *
	 * @deprecated since 1.13. Use {@link #SessionEndEvent(IValueDeserializer)} instead.
	 */
	@Deprecated
	protected SessionEndEvent(final Object[] values, final Class<?>[] valueTypes) { // NOPMD (values stored directly)
		super(values, valueTypes);
		this.sessionId = (String) values[1];
	}

	
	/**
	 * @param deserializer
	 *            The deserializer to use
	 */
	public SessionEndEvent(final IValueDeserializer deserializer) {
		super(deserializer);
		this.sessionId = deserializer.getString();
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @deprecated since 1.13. Use {@link #serialize(IValueSerializer)} with an array serializer instead.
	 */
	@Override
	@Deprecated
	public Object[] toArray() {
		return new Object[] {
			this.getTimestamp(),
			this.getSessionId()
		};
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerStrings(final IRegistry<String> stringRegistry) {	// NOPMD (generated code)
		stringRegistry.get(this.getSessionId());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void serialize(final IValueSerializer serializer) throws BufferOverflowException {
		//super.serialize(serializer);
		serializer.putLong(this.getTimestamp());
		serializer.putString(this.getSessionId());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?>[] getValueTypes() {
		return TYPES; // NOPMD
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getValueNames() {
		return PROPERTY_NAMES; // NOPMD
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSize() {
		return SIZE;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated This record uses the {@link kieker.common.record.IMonitoringRecord.Factory} mechanism. Hence, this method is not implemented.
	 */
	@Override
	@Deprecated
	public void initFromArray(final Object[] values) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj.getClass() != this.getClass()) return false;
		
		final SessionEndEvent castedRecord = (SessionEndEvent) obj;
		if (this.getLoggingTimestamp() != castedRecord.getLoggingTimestamp()) return false;
		if (this.getTimestamp() != castedRecord.getTimestamp()) return false;
		if (!this.getSessionId().equals(castedRecord.getSessionId())) return false;
		return true;
	}
	
	public final String getSessionId() {
		return this.sessionId;
	}
	
}
