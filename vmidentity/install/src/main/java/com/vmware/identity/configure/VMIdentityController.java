/* **********************************************************************
 * Copyright 2015 VMware, Inc.  All rights reserved.
 * *********************************************************************/

package com.vmware.identity.configure;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vmware.af.PasswordCredential;
import com.vmware.af.interop.VmAfClientAdapter;
import com.vmware.identity.installer.ReleaseUtil;
import com.vmware.identity.interop.ldap.DirectoryStoreProtocol;
import com.vmware.identity.interop.ldap.ILdapConnectionEx;
import com.vmware.identity.interop.ldap.ILdapConnectionExWithGetConnectionString;
import com.vmware.identity.interop.ldap.LdapConnectionFactory;
import com.vmware.identity.interop.ldap.LdapConstants;
import com.vmware.identity.interop.ldap.LdapOption;
import com.vmware.identity.interop.ldap.LdapScope;
import com.vmware.identity.interop.ldap.LdapSetting;
import com.vmware.identity.interop.registry.IRegistryAdapter;
import com.vmware.identity.interop.registry.IRegistryKey;
import com.vmware.identity.interop.registry.RegKeyAccess;
import com.vmware.identity.interop.registry.RegistryAdapterFactory;
import com.vmware.identity.interop.vmafd.VmAfdFactory;
import com.vmware.identity.interop.vmafd.VmAfdStatus;

public class VMIdentityController {

    private IPlatformInstallObserver observer = null;
    private static String CONFIG_DIRECTORY_ROOT_KEY;
    private static String CONFIG_DIRECTORY_PARAMETERS_KEY;
    private static final String CONFIG_DIRECTORY_LDAP_PORT_VALUE =
                                    "LdapPort";
    private static Integer port;
    private static final long DEFAULT_LDAP_NETWORK_TIMEOUT = 30;

    public boolean setupInstanceStandalone(
            VmIdentityParams standaloneParams)
            throws DomainControllerNativeException {

        Validate.validateNotNull(standaloneParams.getUsername(), "Username");
        Validate.validateNotNull(standaloneParams.getPassword(), "Password");
        Validate.validateNotEmpty(standaloneParams.getDomainName(), "Domain");

        setupInstance(standaloneParams);

        return true;
    }

    public boolean upgradeStandaloneInstance(
            VmIdentityParams standaloneParams) throws DomainControllerNativeException {

        upgradeInstance(standaloneParams);

        return true;
    }

    public boolean migrateStandaloneInstance(
            VmIdentityParams standaloneParams) throws DomainControllerNativeException {

        migrateInstance(standaloneParams);
        return true;
    }

    private void migrateInstance(VmIdentityParams params) throws DomainControllerNativeException{
        // check services vmafd, vmca and vmdir if they are confgiured.
        try {
            // check authentication services
            checkVMAFDService();

            // check directory services
            checkVMDIRService();

            // check certificate services
            checkVMCAService(params.getHostname());

        } catch (Exception ex) {
            System.err.println("Error: Cannot proceed. Failed to check services.\n");
            ex.printStackTrace(System.err);
            System.exit(1);
        }

        List<IPlatformComponentInstaller> components = getComponents(params);
        List<PlatformInstallComponent> componentsInfo = new ArrayList<>();

        if (observer != null) {
            for (IPlatformComponentInstaller comp : components) {
                componentsInfo.add(comp.getComponentInfo());
            }
            observer.beginMigration(componentsInfo);
        }

        boolean status = true;
        try {
            for (IPlatformComponentInstaller comp : components) {
                try {
                    if (observer != null)
                        observer.beginComponentMigration(comp.getComponentInfo()
                                .getId());

                    comp.upgrade();

                } catch (Exception e) {
                    status = false;
                    throw new DomainControllerNativeException(-1, e);
                } finally {
                    if (observer != null)
                        observer.endComponentMigration(comp.getComponentInfo()
                                .getId(), status);
                }
            }
        } finally {
            if (observer != null)
                observer.endUpgrade(status);
        }
    }
    private void upgradeInstance (VmIdentityParams params) throws DomainControllerNativeException
    {
        List<IPlatformComponentInstaller> components = getComponents(params);
        List<PlatformInstallComponent> componentsInfo = new ArrayList<>();

        if (observer != null) {
            for (IPlatformComponentInstaller comp : components) {
                componentsInfo.add(comp.getComponentInfo());
            }
            observer.beginUpgrade(componentsInfo);
        }

        boolean status = true;
        try {
            for (IPlatformComponentInstaller comp : components) {
                try {
                    if (observer != null)
                        observer.beginComponentUpgrade(comp.getComponentInfo()
                                .getId());

                    comp.upgrade();

                } catch (Exception e) {
                    status = false;
                    throw new DomainControllerNativeException(-1, e);
                } finally {
                    if (observer != null)
                        observer.endComponentUpgrade(comp.getComponentInfo()
                                .getId(), status);
                }
            }
        } finally {
            if (observer != null)
                observer.endUpgrade(status);
        }
    }
    private void setupInstance(VmIdentityParams params)
            throws DomainControllerNativeException {
        if (params.getHostname() == null || params.getHostname().isEmpty()) {
            try {
                params.setHostname(InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException e) {
                throw new DomainControllerNativeException(
                        DeployUtilsErrors.ERROR_INVALID_NETNAME.getErrorCode(),
                        e);
            }
        }

        checkPrerequisites(params);

        // check services vmafd, vmca and vmdir if they are confgiured.
        try {
            // check authentication services
            checkVMAFDService();

            // check directory services
            checkVMDIRService();

            // check certificate services
            checkVMCAService(params.getHostname());

        } catch (Exception ex) {
            System.err.println("Error: Cannot proceed. Failed to check services.\n");
            ex.printStackTrace(System.err);
            System.exit(1);
        }

        List<IPlatformComponentInstaller> components = getComponents(params);
        List<PlatformInstallComponent> componentsInfo = new ArrayList<>();

        if (observer != null) {
            for (IPlatformComponentInstaller comp : components) {
                componentsInfo.add(comp.getComponentInfo());
            }
            observer.beginInstall(componentsInfo);
        }

        boolean status = true;
        try {
            for (IPlatformComponentInstaller comp : components) {
                try {
                    if (observer != null)
                        observer.beginComponentInstall(comp.getComponentInfo()
                                .getId());

                    comp.install();

                } catch (DomainControllerNativeException e) {
                    status = false;
                    throw e;
                } catch (Exception e) {
                    status = false;
                    e.printStackTrace();
                    System.out.println(e.getMessage());

                    throw new DomainControllerNativeException(-1, e);
                } finally {
                    if (observer != null)
                        observer.endComponentInstall(comp.getComponentInfo()
                                .getId(), status);
                }
            }
        } finally {
            if (observer != null)
                observer.endInstall(status);
        }
    }

    public void setPlatformInstallObserver(IPlatformInstallObserver observer) {
        this.observer = observer;
    }

    private List<IPlatformComponentInstaller> getComponents(
            VmIdentityParams standaloneParams) {
        List<IPlatformComponentInstaller> components = new ArrayList<IPlatformComponentInstaller>();
        try {
            if(ReleaseUtil.isLightwave()) {
                components.add(new IdentityManagerInstaller(false, standaloneParams));
                components.add(new SecureTokenServerInstaller(standaloneParams));
                components.add(new LightwaveUIInstaller(standaloneParams));
            } else {
                components.add(new IdentityManagerInstaller(false, standaloneParams));
                // components.add(new LookupServiceInstaller(false));
                components.add(new SecureTokenServerInstaller(standaloneParams));
                }
        } catch (IOException e) {
            System.err.println("Failed to fetch components for SecureTokenservice");
            System.exit(1);
        }
        return components;
    }

    private void checkPrerequisites(VmIdentityParams params) {
        Validate.validateNotEmpty(params.getHostname(), "Hostname");

        Set<String> illegalHostanames = new HashSet<String>();
        illegalHostanames.add("localhost.localdomain");
        illegalHostanames.add("localhost");
        illegalHostanames.add("localhost.localdom");
        if (illegalHostanames.contains(params.getHostname().toLowerCase())) {
            throw new IllegalArgumentException(String.format(
                    "Invalid host name - %s", params.getHostname()));
        }
    }

    private void checkVMCAService(String hostname) throws Exception {

        System.out.println("\n-----Checking Certificate service-----");
        int exitCode = -1;
        String command = InstallerUtils.getInstallerHelper()
                .getVmcaSvcChkCommand(hostname);

        try {
            Process p = Runtime.getRuntime().exec(command);
            exitCode = p.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new ServiceCheckException(
                    "Failed to check certificate service. Cannot configure IDM or STS.",
                    e);
        }

        if (exitCode != 0) {
            throw new ServiceCheckException(
                    String.format(
                            "Failed to check certificate service. Cannot configure IDM or STS.",
                            exitCode), null);
        }
        System.out.println("Certificate Service checked successfully.");

    }

    private void checkVMDIRService() throws Exception {

        System.out.println("\n-----Checking Directory service-----");
        try{
            ILdapConnectionEx connection = null;
            try
            {
                connection = getConnection();
                connection.search(
                    "",
                    LdapScope.SCOPE_BASE,
                    "(objectclass=*)",
                    new String [] {},
                    false);
            }
            finally
            {
                if(connection != null)
                   connection.close();
            }
        } catch(Exception ex){
            throw new ServiceCheckException("Failed to check directory service. Cannot configure IDM or STS.", ex);
        }
        System.out.println("Directory Service checked successfully.");

    }

    private void checkVMAFDService() throws Exception {

        System.out.println("\n\n-----Checking Authentication service-----");
        int status = VmAfdFactory.getInstance().GetStatus();
        if (
           ( status != VmAfdStatus.VMAFD_STATUS_INITIALIZING.getCode() ) &&
           ( status != VmAfdStatus.VMAFD_STATUS_RUNNING.getCode() ) )
        {
           throw new ServiceCheckException("Failed to check authentication service. Cannot configure IDM or STS.");
        }

        System.out.println("Authentication Service checked successfully.");
    }

    private ILdapConnectionEx getConnection() {
        if (port == null || port == 0) {
            IRegistryAdapter regAdapter = RegistryAdapterFactory.getInstance().getRegistryAdapter();
            IRegistryKey rootKey = regAdapter.openRootKey((int) RegKeyAccess.KEY_READ);

            CONFIG_DIRECTORY_ROOT_KEY = InstallerUtils.getInstallerHelper().getConfigDirectoryRootKey();
            CONFIG_DIRECTORY_PARAMETERS_KEY = CONFIG_DIRECTORY_ROOT_KEY + "\\Parameters";

            try {
                port = regAdapter.getIntValue(rootKey, CONFIG_DIRECTORY_PARAMETERS_KEY,
                        CONFIG_DIRECTORY_LDAP_PORT_VALUE, true);
                // fall back to lotus default port
                if (port == null || port == 0) {
                    port = LdapConstants.LDAP_PORT_LOTUS;
                }
            } finally {
                rootKey.close();
            }
        }

        URI uri = DirectoryStoreProtocol.LDAP.getUri("localhost", port);
        List<LdapSetting> settings = new ArrayList<LdapSetting>();
        settings.add(new LdapSetting(LdapOption.LDAP_OPT_PROTOCOL_VERSION, LdapConstants.LDAP_VERSION3));
        settings.add(new LdapSetting(LdapOption.LDAP_OPT_REFERRALS, Boolean.FALSE));
        settings.add(new LdapSetting(LdapOption.LDAP_OPT_NETWORK_TIMEOUT, DEFAULT_LDAP_NETWORK_TIMEOUT));
        settings.add(new LdapSetting(LdapOption.LDAP_OPT_X_SASL_NOCANON, LdapConstants.LDAP_OPT_ON));
        List<LdapSetting> connOptions = Collections.unmodifiableList(settings);
        ILdapConnectionEx connection = (ILdapConnectionEx) LdapConnectionFactory.getInstance().getLdapConnection(uri,
                connOptions, true);
        PasswordCredential creds = VmAfClientAdapter.getMachineAccountCredentials();
        String domain = VmAfClientAdapter.getDomainName("localhost");
        String upn = String.format("%s@%s", creds.getUserName(), domain);
        ((ILdapConnectionExWithGetConnectionString) connection).bindSaslSrpConnection(upn, creds.getPassword());
        return connection;
    }
}
