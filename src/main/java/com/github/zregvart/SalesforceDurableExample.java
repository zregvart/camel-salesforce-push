/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.zregvart;

import java.util.ResourceBundle;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.salesforce.SalesforceComponent;
import org.apache.camel.component.salesforce.SalesforceLoginConfig;
import org.apache.camel.impl.DefaultCamelContext;

public class SalesforceDurableExample {

    public static void main(final String[] args) throws Exception {
        final ResourceBundle configuration = ResourceBundle.getBundle("login");

        final SalesforceComponent salesforce = new SalesforceComponent();
        final String loginUrl = configuration.getString("salesforce.login.url");
        final String clientId = configuration.getString("salesforce.client.id");
        final String clientSecret = configuration.getString("salesforce.client.secret");
        final String username = configuration.getString("salesforce.username");
        final String password = configuration.getString("salesforce.password");
        final SalesforceLoginConfig loginConfig = new SalesforceLoginConfig(loginUrl, clientId, clientSecret, username,
            password, false);
        salesforce.setLoginConfig(loginConfig);
        salesforce.setPackages("org.apache.camel.salesforce.dto");

        final DefaultCamelContext context = new DefaultCamelContext();
        context.addComponent("salesforce", salesforce);

        context.addRoutes(new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                from("salesforce:AccountTopic?"//
                    + "notifyForFields=ALL"//
                    + "&notifyForOperationCreate=true"//
                    + "&notifyForOperationDelete=true"//
                    + "&notifyForOperationUndelete=true"//
                    + "&notifyForOperationUpdate"//
                    + "&sObjectName=Account"//
                    + "&updateTopic=true"//
                    + "&sObjectQuery=SELECT Id, Name FROM Account")//
                        .log(LoggingLevel.ERROR, "Received notification for: ${body}");
            }
        });

        context.start();
    }
}
