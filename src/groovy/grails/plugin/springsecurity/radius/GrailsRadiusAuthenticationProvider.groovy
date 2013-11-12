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
import grails.plugin.springsecurity.userdetails.GormUserDetailsService
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.core.userdetails.UserDetails

/**
 * @author <a href="mailto:smakela@iki.fi">Sami Mäkelä</a>
 */
class GrailsRadiusAuthenticationProvider extends
    AbstractUserDetailsAuthenticationProvider {

    private static final log = LogFactory.getLog(this)

    List<GrailsRadiusAuthenticator> radiusAuthenticators
    GormUserDetailsService userDetailsService
    boolean authorizeFromDb

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
        UsernamePasswordAuthenticationToken token) {
        //no-operation, radius server has already done the password checking etc.
    }

    @Override
    protected UserDetails retrieveUser(String userName,
        UsernamePasswordAuthenticationToken token) {
        def userDetails
        for (def authenticators = radiusAuthenticators.iterator(); authenticators.hasNext();) {
            try {
                userDetails = authenticators.next().authenticate(token)
                if (authorizeFromDb) {
                    userDetails = userDetailsService.loadUserByUsername(userName)
                }
                return userDetails
            } catch (AuthenticationServiceException ase) {
                if (authenticators.hasNext())
                    log.debug("Could not authenticate! Trying next server...")
                else
                    throw ase
            }
        }
        userDetails
    }
}
