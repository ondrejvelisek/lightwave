/*
 *  Copyright (c) 2012-2015 VMware, Inc.  All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License.  You may obtain a copy
 *  of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, without
 *  warranties or conditions of any kind, EITHER EXPRESS OR IMPLIED.  See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 */

package com.vmware.identity.openidconnect.common;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * @author Yehia Zayour
 */
public final class CorrelationID extends Identifier {
    public CorrelationID() {
    }

    public CorrelationID(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object other) {
        return
                other instanceof CorrelationID &&
                ((CorrelationID) other).getValue().equals(this.getValue());
    }

    public static CorrelationID get(Map<String, String> parameters) {
        Validate.notNull(parameters, "parameters");
        String correlationIdString = parameters.get("correlation_id");
        return StringUtils.isEmpty(correlationIdString) ? new CorrelationID() : new CorrelationID(correlationIdString);
    }
}