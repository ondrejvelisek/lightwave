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

#ifdef __cplusplus
extern "C" {
#endif

/* snpolicy.c */

DWORD
VMCAPolicySNLoad(
    json_t              *pJsonPolicy,
    PVMCA_POLICY        pPolicy
    );

DWORD
VMCAPolicySNValidate(
    PVMCA_POLICY                    pPolicy,
    PSTR                            pszPKCS10Request,
    PVMCA_REQ_CONTEXT               pReqContext,
    PBOOLEAN                        pbBypass,
    PBOOLEAN                        pbIsValid
    );

VOID
VMCAPolicySNFree(
    PVMCA_POLICY_RULES  pPolicyRules
    );

/* cnpolicy.c */

DWORD
VMCAPolicyCNValidate(
    PCSTR                           pszPKCS10Request,
    PVMCA_REQ_CONTEXT               pReqContext,
    PBOOLEAN                        pbIsValid
    );

/* sanpolicy.c */

DWORD
VMCAPolicySANValidate(
    PCSTR                           pszPKCS10Request,
    PVMCA_REQ_CONTEXT               pReqContext,
    PBOOLEAN                        pbIsValid
    );

#ifdef __cplusplus
}
#endif
