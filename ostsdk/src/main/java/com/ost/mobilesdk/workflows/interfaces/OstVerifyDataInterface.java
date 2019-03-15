/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.mobilesdk.workflows.interfaces;

/**
 * Sub Interface of
 *
 * @see OstBaseInterface
 * It declares dataVerified api of Workflows.
 */
public interface OstVerifyDataInterface extends OstBaseInterface {
    void dataVerified();
}