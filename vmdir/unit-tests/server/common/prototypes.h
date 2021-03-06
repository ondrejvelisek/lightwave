/*
 * Copyright © 2018 VMware, Inc.  All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the “License”); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an “AS IS” BASIS, without
 * warranties or conditions of any kind, EITHER EXPRESS OR IMPLIED.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
#ifndef METADATA_H_
#define METADATA_H_

//////////////////
// metadata.c
//////////////////

//setup and teardown functions
int
VmDirSetupMetaData(
    VOID    **state
    );

int
VmDirMetaDataFree(
    VOID    **state
    );

int
VmDirSetupMetaDataAttribute(
    VOID    **state
    );

int
VmDirFreeMetaDataAttribute(
    VOID    **state
    );

//unit test functions - VmDirMetaDataDeserialize
VOID
VmDirMetaDataDeserialize_ValidInput(
    VOID    **state
    );

VOID
VmDirMetaDataDeserialize_InvalidInput(
    VOID    **state
    );

//unit test functions - VmDirMetaDataSerialize
VOID
VmDirMetaDataSerialize_ValidInput(
    VOID    **state
    );

VOID
VmDirMetaDataSerialize_InvalidInput(
    VOID    **state
    );

//unit test functions - VmDirMetaDataCopyContent
VOID
VmDirMetaDataCopyContent_ValidInput(
    VOID    **state
    );


VOID
VmDirMetaDataCopyContent_InvalidInput(
    VOID    **state
    );

//unit test functions - VmDirMetaDataCreate
VOID
VmDirMetaDataCreate_ValidInput(
    VOID    **state
    );


VOID
VmDirMetaDataCreate_InvalidInput(
    VOID    **state
    );

//unit test functions - VmDirAttributeMetaDataToHashMap
VOID
VmDirAttributeMetaDataToHashMap_ValidInput(
    VOID    **state
    );


VOID
VmDirAttributeMetaDataToHashMap_InvalidInput(
    VOID    **state
    );

//////////////////
// valuemetadata.c
//////////////////
VOID
VmDirValueMetaDataDeserialize_ValidInput(
    VOID    **state
    );

VOID
VmDirValueMetaDataDeserialize_InvalidInput(
    VOID    **state
    );

int
VmDirSetupValueMetaData(
    VOID    **state
    );

int
VmDirValueMetaDataFree(
    VOID    **state
    );

VOID
VmDirValueMetaDataSerialize_ValidInput(
    VOID    **state
    );

VOID
VmDirValueMetaDataSerialize_InvalidInput(
    VOID    **state
    );
#endif
