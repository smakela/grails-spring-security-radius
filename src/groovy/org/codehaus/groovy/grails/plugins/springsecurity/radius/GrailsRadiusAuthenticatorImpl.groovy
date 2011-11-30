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
import net.jradius.dictionary.Attr_UserName
import net.jradius.dictionary.Attr_UserPassword
import net.jradius.exception.RadiusException
import net.jradius.packet.AccessAccept
import net.jradius.packet.AccessRequest
import net.jradius.packet.RadiusPacket
import net.jradius.packet.attribute.AttributeFactory
import net.jradius.packet.attribute.AttributeList

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.GrantedAuthorityImpl
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.util.Assert

/**
 * @author <a href="mailto:smakela@iki.fi">Sami Mäkelä</a>
 */
class GrailsRadiusAuthenticatorImpl implements GrailsRadiusAuthenticator, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(getClass())
    private static final List NO_ROLES = [
        new GrantedAuthorityImpl(SpringSecurityUtils.NO_ROLE)
    ]

    static {
        AttributeFactory.loadAttributeDictionary("net.jradius.dictionary.AttributeDictionaryImpl")
    }

    String radiusAuthenticatorClassName
    String radiusHost
    String sharedSecret
    int authenticationPort
    int accountingPort
    int retries
    int timeout

    @Override
    UserDetails authenticate(UsernamePasswordAuthenticationToken authentication) {

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
            reply = radiusClient.authenticate(request, Class.forName(radiusAuthenticatorClassNme).newInstance(), retries)
        } catch (RadiusException re) {
            throw new AuthenticationServiceException("Error connecting to radius server", re)
        } catch (UnknownHostException uhe) {
            throw new AuthenticationServiceException("Unknown radius host", uhe)
        } catch (IOException ioe) {
            throw new AuthenticationServiceException("Error connecting to radius server", ioe)
        } catch (ClassNotFoundException cnfe) {
            throw new AuthenticationServiceException("Cannot find radius authenticator: ${radiusAuthenticatorClassNme}", cnfe)
        }

        if (!reply) {
            log.debug("Timed out while connecting to radius server")
            throw new AuthenticationServiceException("Timed out while connecting to radius server")
        }

        if (!(reply instanceof AccessAccept)) {
            log.debug("Bad credentials")
            throw new BadCredentialsException("Bad credentials")
        }

        //TODO: Radius attributes handling for roles etc.

        new User(username, password, true, true, true, true, NO_ROLES);
    }

    @Override
    void afterPropertiesSet() {
        Assert.hasLength(radiusAuthenticatorClassNme, "RadiusAuthenticator classname must be specified")
        log.debug("radiusAuthenticatorClassNme: ${radiusAuthenticatorClassNme}")
        Assert.hasLength(sharedSecret, "Shared secret must be specified")
        log.debug("sharedSecret: ${sharedSecret}")
        Assert.hasLength(radiusHost, "Hostname must be specified")
        log.debug("radiusHost: ${radiusHost}")
        Assert.isTrue(authenticationPort > 0, "Authentication port number must be greater than 0")
        log.debug("authenticationPort: ${authenticationPort}")
        Assert.isTrue(accountingPort > 0, "Accounting port number must be greater than 0")
        log.debug("accountingPort: ${accountingPort}")
        Assert.isTrue(timeout > 0, "Timeout must be greater than 0")
        log.debug("timeout: ${timeout}")
        Assert.isTrue(retries >= 0, "Retries must be greater than 0 or equal to 0")
        log.debug("retries: ${retries}")
    }
}
