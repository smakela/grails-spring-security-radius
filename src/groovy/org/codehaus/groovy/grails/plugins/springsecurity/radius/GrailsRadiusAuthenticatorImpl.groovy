/* Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.groovy.grails.plugins.springsecurity.radius

import net.jradius.client.RadiusClient
import net.jradius.client.auth.RadiusAuthenticator
import net.jradius.dictionary.Attr_UserName
import net.jradius.dictionary.Attr_UserPassword
import net.jradius.exception.RadiusException
import net.jradius.packet.AccessAccept
import net.jradius.packet.AccessRequest
import net.jradius.packet.RadiusPacket
import net.jradius.packet.attribute.AttributeFactory
import net.jradius.packet.attribute.AttributeList

import org.springframework.beans.factory.InitializingBean
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.GrantedAuthorityImpl
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.util.Assert

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * @author <a href="mailto:smakela@iki.fi">Sami Mäkelä</a>
 */
class GrailsRadiusAuthenticatorImpl implements GrailsRadiusAuthenticator, InitializingBean {

    static final List NO_ROLES = [
        new GrantedAuthorityImpl(SpringSecurityUtils.NO_ROLE)
    ]

    RadiusAuthenticator radiusAuthenticator
    String radiusHost
    String sharedSecret
    int authenticationPort
    int accountingPort
    int retries
    int timeout

    static {
        AttributeFactory.loadAttributeDictionary("net.jradius.dictionary.AttributeDictionaryImpl")
    }

    @Override
    public UserDetails authenticate(
    UsernamePasswordAuthenticationToken authentication) {

        String username = authentication.getPrincipal()
        String password = authentication.getCredentials()

        AttributeList radiusAttributes = new AttributeList()
        radiusAttributes.add(new Attr_UserName(username))
        radiusAttributes.add(new Attr_UserPassword(password))

        RadiusPacket reply

        try {
            RadiusClient radiusClient = new RadiusClient(InetAddress.getByName(radiusHost),
                sharedSecret, authenticationPort, accountingPort, timeout)
            RadiusPacket request = new AccessRequest(radiusClient, radiusAttributes)
            reply = radiusClient.authenticate(request, radiusAuthenticator, retries)
        } catch (RadiusException re) {
            throw new AuthenticationServiceException("Error connecting to radius server", re)
        } catch (UnknownHostException uhe) {
            throw new AuthenticationServiceException("Unknown radius host", uhe)
        } catch (IOException ioe) {
            throw new AuthenticationServiceException("Error connecting to radius server", ioe)
        }

        if (!reply) {
            throw new AuthenticationServiceException("Timed out while connecting to radius server")
        }

        if (!(reply instanceof AccessAccept)) {
            throw new BadCredentialsException("Bad credentials")
        }

        new User(username, password, true, true, true, true, NO_ROLES);
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(radiusAuthenticator, "RadiusAuthenticator must be specified")
        Assert.notNull(sharedSecret, "Shared secret must be specified")
        Assert.notNull(radiusHost, "Hostname must be specified")
        Assert.isTrue(authenticationPort > 0, "Authentication port number must be greater than 0")
        Assert.isTrue(accountingPort > 0, "Accounting port number must be greater than 0")
        Assert.isTrue(timeout > 0, "Timeout must be greater than 0")
        Assert.isTrue(retries >= 0, "Retries must be greater than 0 or equal to 0")
    }
}
