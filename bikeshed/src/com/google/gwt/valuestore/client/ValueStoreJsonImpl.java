/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.valuestore.client;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.valuestore.shared.ValueStore;
import com.google.gwt.valuestore.shared.impl.RecordJsoImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link ValueStore} implementation based on {@link ValuesImpl}.
 */
public class ValueStoreJsonImpl implements ValueStore {
  // package protected fields for use by DeltaValueStoreJsonImpl

  final HandlerManager eventBus;

  final Map<RecordKey, RecordJsoImpl> records = new HashMap<RecordKey, RecordJsoImpl>();

  public ValueStoreJsonImpl(HandlerManager eventBus) {
    this.eventBus = eventBus;
  }

  public void addValidation() {
    throw new UnsupportedOperationException("Auto-generated method stub");
  }

  public void setRecords(JsArray<RecordJsoImpl> newRecords) {

    for (int i = 0, l = newRecords.length(); i < l; i++) {
      RecordJsoImpl newRecord = newRecords.get(i);
      RecordKey recordKey = new RecordKey(newRecord);

      RecordJsoImpl oldRecord = records.get(recordKey);
      if (oldRecord == null) {
        records.put(recordKey, newRecord);
      } else {
        boolean changed = oldRecord.merge(newRecord);
        newRecord = oldRecord.cast();
        newRecords.set(i, newRecord);
        if (changed) {
          eventBus.fireEvent(newRecord.getSchema().createChangeEvent(newRecord));
        }
      }
    }
  }

  /**
   * @return
   */
  public DeltaValueStoreJsonImpl spawnDeltaView() {
    return new DeltaValueStoreJsonImpl(this);
  }
}