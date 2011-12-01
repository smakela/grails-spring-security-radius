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

import org.codehaus.groovy.grails.plugins.springsecurity.GormUserDetailsService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.core.userdetails.UserDetails

/**
 * @author <a href="mailto:smakela@iki.fi">Sami Mäkelä</a>
 */
class GrailsRadiusAuthenticationProvider extends
    AbstractUserDetailsAuthenticationProvider {

    GrailsRadiusAuthenticator grailsRadiusAuthenticator
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
        def userDetails = grailsRadiusAuthenticator.authenticate(token)
        if (authorizeFromDb) {
            userDetails = userDetailsService.loadUserByUsername(userName)
        }
        userDetails
    }
}
