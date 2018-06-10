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
package grails.plugin.springsecurity.radius


import org.apache.commons.logging.LogFactory
import grails.plugin.springsecurity.SpringSecurityUtils
import org.springframework.beans.factory.InitializingBean
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.util.Assert
import org.tinyradius.packet.AccessRequest
import org.tinyradius.packet.RadiusPacket
import org.tinyradius.util.RadiusClient
import org.tinyradius.util.RadiusException

/**
 * @author <a href="mailto:smakela@iki.fi">Sami Mäkelä</a>
 */
class GrailsRadiusAuthenticatorImpl implements GrailsRadiusAuthenticator, InitializingBean {

    private static final log = LogFactory.getLog(this)
    private static final List NO_ROLES = [
        new SimpleGrantedAuthority(SpringSecurityUtils.NO_ROLE)
    ]

    String authenticationProtocol
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

        RadiusPacket reply

        try {
            AccessRequest accessRequest = new AccessRequest(username, password)
            accessRequest.setAuthProtocol(authenticationProtocol)
            RadiusClient radiusClient = new RadiusClient(radiusHost, sharedSecret)
            radiusClient.setAuthPort(authenticationPort)
            radiusClient.setAcctPort(accountingPort)
            radiusClient.setSocketTimeout(timeout)
            radiusClient.setRetryCount(retries)
            reply = radiusClient.authenticate(accessRequest)
        } catch (RadiusException re) {
            throw new AuthenticationServiceException("Error connecting to radius server", re)
        } catch (UnknownHostException uhe) {
            throw new AuthenticationServiceException("Unknown radius host", uhe)
        } catch (IOException ioe) {
            throw new AuthenticationServiceException("Error connecting to radius server", ioe)
        }

        if (reply.getPacketType() != RadiusPacket.ACCESS_ACCEPT) {
            log.debug("Bad credentials")
            throw new BadCredentialsException("Bad credentials")
        }

        /**
         * TODO: Authorization from RADIUS
         *
         * Is there a standard way?
         * Only vendor specific role attributes exist?
         * Could configurable name of role attribute
         * be solution?
         * How do we separate multiple roles?
         *
         * For now let's implement authorization from database.
         */
        new User(username, password, true, true, true, true, NO_ROLES);
    }

    @Override
    void afterPropertiesSet() {
        Assert.hasLength(authenticationProtocol, "RadiusAuthenticator authentication protocol must be specified")
        log.debug("authenticationProtocol: ${authenticationProtocol}")
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
