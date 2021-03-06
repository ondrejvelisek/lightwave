﻿/*
 * Copyright © 2012-2016 VMware, Inc.  All Rights Reserved.
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

using System;
using VmIdentity.UI.Common;

namespace VMDNS.Nodes
{
    public class VMDNSRootScopeNode:ScopeNodeBase
    {
        public VMDNSServerNode ServerNode { get; protected set; }

        public VMDNSRootScopeNode(VMDNSServerNode node)
        {
            this.ServerNode = node;
            this.DisplayName = ServerNode.ServerDTO.Server;
        }

        public void AddChildren()
        {
            this.Children.Clear();
            var zonesNode = new VMDNSZonesNode(this.ServerNode);
            this.Children.Add(zonesNode);
        }
    }
}

