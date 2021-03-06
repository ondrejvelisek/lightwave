/*
 * Copyright © 2012-2018 VMware, Inc.  All Rights Reserved.
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

package com.vmware.identity.wstrust.test.integration.acquiretokenbycertificate;

import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.vmware.identity.rest.idm.data.TenantConfigurationDTO;
import com.vmware.identity.rest.idm.data.TokenPolicyDTO;
import com.vmware.identity.wstrust.client.TokenSpec;
import com.vmware.identity.wstrust.test.util.Assert;
import com.vmware.identity.wstrust.test.util.ITokenService;
import com.vmware.identity.wstrust.test.util.KeyStoreHelper;
import com.vmware.identity.wstrust.test.util.MethodEnum;
import com.vmware.identity.wstrust.test.util.TestConfig;
import com.vmware.identity.wstrust.test.util.TokenServiceFactory;
import com.vmware.identity.wstrust.test.util.TokenUtil;
import com.vmware.identity.wstrust.test.util.WSTrustTestBase;
import com.vmware.vim.sso.PrincipalId;
import com.vmware.vim.sso.client.Advice;
import com.vmware.vim.sso.client.ConfirmationType;
import com.vmware.vim.sso.client.SamlToken;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Acquire token by certificate for a solution user
 */

@RunWith(Parameterized.class)
public class GoodUserIT extends WSTrustTestBase {
  protected static final Logger logger = LoggerFactory.getLogger(GoodUserIT.class);

  private static String _solutionUserName;
  private static String _solutionCertificateStore;
  private static String _solutionCertificateAlias;
  private static char[] _solutionCertificateStorePassword;
  private static String _userGroup = "group";

  @Rule
  public TestName name = new TestName();

  @Parameterized.Parameters
  public static Collection<TestParam> data() {
    ArrayList<TestParam> testParams = new ArrayList<TestParam>();
    // SYNC
    testParams.add(new TestParam(MethodEnum.SYNC,
                                 TestConfig.SOLUTIONNAME,
                                 true,
                                 ConfirmationType.HOLDER_OF_KEY,
                                 RestrictionType.RESTRICTION_TYPE_NONE,
                                 AdviceType.ADVICE_TYPE_NONE));
    testParams.add(new TestParam(MethodEnum.SYNC,
                                 TestConfig.SOLUTIONNAME,
                                 true,
                                 ConfirmationType.HOLDER_OF_KEY,
                                 RestrictionType.RESTRICTION_TYPE_NONE,
                                 AdviceType.ADVICE_TYPE_VALID));
    testParams.add(new TestParam(MethodEnum.SYNC,
                                 TestConfig.SOLUTIONNAME,
                                 true,
                                 ConfirmationType.HOLDER_OF_KEY,
                                 RestrictionType.RESTRICTION_TYPE_VALID,
                                 AdviceType.ADVICE_TYPE_NONE));
    testParams.add(new TestParam(MethodEnum.SYNC,
                                 TestConfig.SOLUTIONNAME,
                                 true,
                                 ConfirmationType.HOLDER_OF_KEY,
                                 RestrictionType.RESTRICTION_TYPE_VALID,
                                 AdviceType.ADVICE_TYPE_VALID));
    testParams.add(new TestParam(MethodEnum.SYNC,
                                 TestConfig.SOLUTIONNAME,
                                 false,
                                 ConfirmationType.HOLDER_OF_KEY,
                                 RestrictionType.RESTRICTION_TYPE_NONE,
                                 AdviceType.ADVICE_TYPE_NONE));
    testParams.add(new TestParam(MethodEnum.SYNC,
                                 TestConfig.SOLUTIONNAME,
                                 false,
                                 ConfirmationType.HOLDER_OF_KEY,
                                 RestrictionType.RESTRICTION_TYPE_NONE,
                                 AdviceType.ADVICE_TYPE_VALID));
    testParams.add(new TestParam(MethodEnum.SYNC,
                                 TestConfig.SOLUTIONNAME,
                                 false,
                                 ConfirmationType.HOLDER_OF_KEY,
                                 RestrictionType.RESTRICTION_TYPE_VALID,
                                 AdviceType.ADVICE_TYPE_NONE));
    testParams.add(new TestParam(MethodEnum.SYNC,
                                 TestConfig.SOLUTIONNAME,
                                 false,
                                 ConfirmationType.HOLDER_OF_KEY,
                                 RestrictionType.RESTRICTION_TYPE_VALID,
                                 AdviceType.ADVICE_TYPE_VALID));
    // ASYNC
    testParams.add(new TestParam(MethodEnum.ASYNC,
                                 TestConfig.SOLUTIONNAME,
                                 true,
                                 ConfirmationType.HOLDER_OF_KEY,
                                 RestrictionType.RESTRICTION_TYPE_NONE,
                                 AdviceType.ADVICE_TYPE_NONE));
    testParams.add(new TestParam(MethodEnum.ASYNC,
                                 TestConfig.SOLUTIONNAME,
                                 true,
                                 ConfirmationType.HOLDER_OF_KEY,
                                 RestrictionType.RESTRICTION_TYPE_NONE,
                                 AdviceType.ADVICE_TYPE_VALID));
    testParams.add(new TestParam(MethodEnum.ASYNC,
                                 TestConfig.SOLUTIONNAME,
                                 true,
                                 ConfirmationType.HOLDER_OF_KEY,
                                 RestrictionType.RESTRICTION_TYPE_VALID,
                                 AdviceType.ADVICE_TYPE_NONE));
    testParams.add(new TestParam(MethodEnum.ASYNC,
                                 TestConfig.SOLUTIONNAME,
                                 true,
                                 ConfirmationType.HOLDER_OF_KEY,
                                 RestrictionType.RESTRICTION_TYPE_VALID,
                                 AdviceType.ADVICE_TYPE_VALID));
    testParams.add(new TestParam(MethodEnum.ASYNC,
                                 TestConfig.SOLUTIONNAME,
                                 false,
                                 ConfirmationType.HOLDER_OF_KEY,
                                 RestrictionType.RESTRICTION_TYPE_NONE,
                                 AdviceType.ADVICE_TYPE_NONE));
    testParams.add(new TestParam(MethodEnum.ASYNC,
                                 TestConfig.SOLUTIONNAME,
                                 false,
                                 ConfirmationType.HOLDER_OF_KEY,
                                 RestrictionType.RESTRICTION_TYPE_NONE,
                                 AdviceType.ADVICE_TYPE_VALID));
    testParams.add(new TestParam(MethodEnum.ASYNC,
                                 TestConfig.SOLUTIONNAME,
                                 false,
                                 ConfirmationType.HOLDER_OF_KEY,
                                 RestrictionType.RESTRICTION_TYPE_VALID,
                                 AdviceType.ADVICE_TYPE_NONE));
    testParams.add(new TestParam(MethodEnum.ASYNC,
                                 TestConfig.SOLUTIONNAME,
                                 false,
                                 ConfirmationType.HOLDER_OF_KEY,
                                 RestrictionType.RESTRICTION_TYPE_VALID,
                                 AdviceType.ADVICE_TYPE_VALID));
    // ASYNC_NULLHANDLER
    testParams.add(new TestParam(MethodEnum.ASYNC_NULLHANDLER,
                                 TestConfig.SOLUTIONNAME,
                                 true,
                                 ConfirmationType.HOLDER_OF_KEY,
                                 RestrictionType.RESTRICTION_TYPE_NONE,
                                 AdviceType.ADVICE_TYPE_NONE));
    testParams.add(new TestParam(MethodEnum.ASYNC_NULLHANDLER,
                                 TestConfig.SOLUTIONNAME,
                                 true,
                                 ConfirmationType.HOLDER_OF_KEY,
                                 RestrictionType.RESTRICTION_TYPE_NONE,
                                 AdviceType.ADVICE_TYPE_VALID));
    testParams.add(new TestParam(MethodEnum.ASYNC_NULLHANDLER,
                                 TestConfig.SOLUTIONNAME,
                                 true,
                                 ConfirmationType.HOLDER_OF_KEY,
                                 RestrictionType.RESTRICTION_TYPE_VALID,
                                 AdviceType.ADVICE_TYPE_NONE));
    testParams.add(new TestParam(MethodEnum.ASYNC_NULLHANDLER,
                                 TestConfig.SOLUTIONNAME,
                                 true,
                                 ConfirmationType.HOLDER_OF_KEY,
                                 RestrictionType.RESTRICTION_TYPE_VALID,
                                 AdviceType.ADVICE_TYPE_VALID));
    testParams.add(new TestParam(MethodEnum.ASYNC_NULLHANDLER,
                                 TestConfig.SOLUTIONNAME,
                                 false,
                                 ConfirmationType.HOLDER_OF_KEY,
                                 RestrictionType.RESTRICTION_TYPE_NONE,
                                 AdviceType.ADVICE_TYPE_NONE));
    testParams.add(new TestParam(MethodEnum.ASYNC_NULLHANDLER,
                                 TestConfig.SOLUTIONNAME,
                                 false,
                                 ConfirmationType.HOLDER_OF_KEY,
                                 RestrictionType.RESTRICTION_TYPE_NONE,
                                 AdviceType.ADVICE_TYPE_VALID));
    testParams.add(new TestParam(MethodEnum.ASYNC_NULLHANDLER,
                                 TestConfig.SOLUTIONNAME,
                                 false,
                                 ConfirmationType.HOLDER_OF_KEY,
                                 RestrictionType.RESTRICTION_TYPE_VALID,
                                 AdviceType.ADVICE_TYPE_NONE));
    testParams.add(new TestParam(MethodEnum.ASYNC_NULLHANDLER,
                                 TestConfig.SOLUTIONNAME,
                                 false,
                                 ConfirmationType.HOLDER_OF_KEY,
                                 RestrictionType.RESTRICTION_TYPE_VALID,
                                 AdviceType.ADVICE_TYPE_VALID));
    return testParams;
  }

  @Parameterized.Parameter // first data value (0) is default
  public TestParam param;

  @BeforeClass
  public static void testSetUp() throws Exception {

    WSTrustTestBase.setup();

    String solutionIdKey = TestConfig.SOLUTIONNAME;
    _solutionUserName = properties.getUserName(solutionIdKey);
    _solutionCertificateStore = properties.getUserCertificatePath(solutionIdKey);
    _solutionCertificateAlias = properties.getUserCertificateAlias(solutionIdKey);
    _solutionCertificateStorePassword = properties.getKeystorePassword(_solutionCertificateAlias);

    logger.debug("Default maxhok lifetime=" +
                     idmClientForSystemTenant.tenant()
                                             .getConfig(systemTenant)
                                             .getTokenPolicy()
                                             .getMaxHOKTokenLifeTimeMillis());
      TokenPolicyDTO tokenPolicyDTO = TokenPolicyDTO
                                          .builder()
                                          .withMaxHOKTokenLifeTimeMillis(300000000L)
                                          .withMaxBearerTokenLifeTimeMillis(300000000L)
                                          .build();
    TenantConfigurationDTO tenantConfigDTO = TenantConfigurationDTO
                                                .builder()
                                                .withTokenPolicy(tokenPolicyDTO)
                                                .build();
    idmClientForSystemTenant.tenant().updateConfig(systemTenant, tenantConfigDTO);

    logger.debug("maxhok lifetime=" +
                     idmClientForSystemTenant.tenant()
                                             .getConfig(systemTenant)
                                             .getTokenPolicy()
                                             .getMaxHOKTokenLifeTimeMillis());

    pmUtil.deleteSolutionUserIfExist(systemTenant, _solutionUserName);
    pmUtil.deleteGroupIfExist(systemTenant, _userGroup, systemTenant);

    logger.debug("Deleted local solution user and group");

    Certificate certificate = new KeyStoreHelper(_solutionCertificateStore).getCertificate(_solutionCertificateAlias);

    pmUtil.CreateSolutionUser(_solutionUserName, systemTenant, false, certificate);
    pmUtil.CreateGroup(systemTenant, _userGroup);

    logger.debug("Created local solution user and group");
  }

  @Test
  public void test() throws Exception {
    ITokenService tokenService = TokenServiceFactory
        .getHokTokenServiceInstance(
            stsURL,
            sslTrustedRootsKeystore,
            stsSigningCertificates,
            param.GetMethod(),
            _solutionCertificateStore,
            _solutionCertificateAlias,
            _solutionCertificateStorePassword
        );

    Set<String> audienceRestrictions = null;
    if (param.GetRestrictionType() == RestrictionType.RESTRICTION_TYPE_VALID) {
      audienceRestrictions = properties.getAudienceRestricitions("valid.restriction");
    }

    List<Advice> advices = null;
    if (param.GetAdviceType() == AdviceType.ADVICE_TYPE_VALID) {
      advices = properties.getAdviceList("valid.advice");
    }

    TokenSpec spec =  createTokenSpec(
                          param.IsRenewable(),
                          advices,
                          audienceRestrictions,
                          param.GetConfirmationType()
                      );
    Certificate certificate = new KeyStoreHelper(_solutionCertificateStore).getCertificate(_solutionCertificateAlias);
    SamlToken token = tokenService.acquireTokenByCertificate(certificate, spec);
    logger.info("Token validity in seconds =" + token.getExpirationTime());
    Assert.assertNotNull(token, "Null token returned");
    Assert.assertTrue(
        tokenService.validateToken(token),
        "Validate Token Failed with validateToken api"
    );
  }

  @Ignore
  @Test
  // @TestDescription("Retrieve a SAML token after adding the user to a group")
  public void testAddUserToGroup() throws Exception {
    ITokenService tokenService = TokenServiceFactory
        .getHokTokenServiceInstance(
            stsURL,
            sslTrustedRootsKeystore,
            stsSigningCertificates,
            param.GetMethod(),
            _solutionCertificateStore,
            _solutionCertificateAlias,
            _solutionCertificateStorePassword
        );
    // Add user to group
    pmUtil.AddSolutionUserToGroup(
        new PrincipalId(_solutionUserName, systemTenant),
        _userGroup
    );
    Set<String> audienceRestrictions = null;
    if (param.GetRestrictionType() == RestrictionType.RESTRICTION_TYPE_VALID) {
      audienceRestrictions = properties.getAudienceRestricitions("valid.restriction");
    }

    List<Advice> advices = null;
    if (param.GetAdviceType() == AdviceType.ADVICE_TYPE_VALID) {
      advices = properties.getAdviceList("valid.advice");
    }

    TokenSpec spec =  createTokenSpec(
                          param.IsRenewable(),
                          advices,
                          audienceRestrictions,
                          param.GetConfirmationType()
                      );
    Certificate certificate = new KeyStoreHelper(_solutionCertificateStore).getCertificate(_solutionCertificateAlias);
    SamlToken token = tokenService.acquireTokenByCertificate(certificate, spec);
    Assert.assertNotNull(token, "Null token returned");
    Assert.assertTrue(
        tokenService.validateToken(token),
        "Validate Token Failed with validateToken api"
    );
    TokenUtil.validateSAMLToken(spec, token);
    /*
     * TODO add code to verify that the user is part of the "SolutionUsers"
     * as well as the new group that the user is added to.
     */
    Assert.assertTrue(
        _userGroup.equals(token.getGroupList().get(1).getName()),
        "Group Name did not match the token"
    );
  }

  private TokenSpec createTokenSpec(
      boolean isRenewable, List<Advice> advices, Set<String> audienceRestrictions,
      ConfirmationType confirmation) {
    TokenSpec.Builder specBuilder = new TokenSpec.Builder(600);
    if (advices != null && !advices.isEmpty()) {
      specBuilder.advice(advices);
    }
    if (audienceRestrictions != null && !audienceRestrictions.isEmpty()) {
      specBuilder.audienceRestriction(audienceRestrictions);
    }
    specBuilder.renewable(isRenewable);
    if (confirmation.equals(ConfirmationType.BEARER)) {
      specBuilder.confirmation(TokenSpec.Confirmation.NONE);
    }
    TokenSpec spec = specBuilder.createTokenSpec();
    return spec;
  }

  @AfterClass
  public static void testCleanup() throws Exception {
    if (pmUtil != null) {
      pmUtil.deleteSolutionUserIfExist(systemTenant, _solutionUserName);
      pmUtil.deleteGroupIfExist(systemTenant, _userGroup, systemTenant);
    }
    WSTrustTestBase.cleanup();
  }

  private enum RestrictionType {
    RESTRICTION_TYPE_NONE,
    RESTRICTION_TYPE_VALID
  }

  private enum AdviceType {
    ADVICE_TYPE_NONE,
    ADVICE_TYPE_VALID
  }

  private static class TestParam {
    private MethodEnum method;
    private String userId;
    private boolean isRenewable;
    private ConfirmationType confirmationType;
    private RestrictionType restrictionType;
    private AdviceType adviceType;

    public TestParam(MethodEnum methodEnum, String userId, boolean isRenewable, ConfirmationType confirmationType,
                     RestrictionType restrictionType, AdviceType adviceType) {
      method = methodEnum;
      this.userId = userId;
      this.isRenewable = isRenewable;
      this.confirmationType = confirmationType;
      this.restrictionType = restrictionType;
      this.adviceType = adviceType;
    }

    public MethodEnum GetMethod() {
      return method;
    }

    public String GetUserId() { return userId; }

    public boolean IsRenewable() { return isRenewable; }

    public RestrictionType GetRestrictionType() { return restrictionType; }

    public AdviceType GetAdviceType() { return adviceType; }

    public ConfirmationType GetConfirmationType() { return confirmationType; }
  }
}
