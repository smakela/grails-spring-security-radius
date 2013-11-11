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
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.plugins.springsecurity.radius.GrailsRadiusAuthenticationProvider
import org.codehaus.groovy.grails.plugins.springsecurity.radius.GrailsRadiusAuthenticator
import org.codehaus.groovy.grails.plugins.springsecurity.radius.GrailsRadiusAuthenticatorImpl

/**
 * @author <a href="mailto:smakela@iki.fi">Sami Mäkelä</a>
 */
class SpringSecurityRadiusGrailsPlugin {
    def version = '1.1.1'
    def grailsVersion = '2.0 > *'
    def pluginExcludes = [
        'grails-app/views/**',
        'grails-app/controllers/**',
        'grails-app/domain/**',
        'grails-app/i18n/**',
        'src/docs/**',
        'web-app/**'
    ]

    def title = 'Spring Security RADIUS Plugin'
    def author = 'Sami Mäkelä'
    def authorEmail = 'smakela@iki.fi'
    def description = 'RADIUS support for the Spring Security plugin.'
    def documentation = 'http://smakela.github.com/grails-spring-security-radius/'

    def license = 'APACHE'
    def issueManagement = [ system: 'GitHub', url: 'https://github.com/smakela/grails-spring-security-radius/issues' ]
    def scm = [ url: 'https://github.com/smakela/grails-spring-security-radius/' ]

    def doWithSpring = {

        def conf = SpringSecurityUtils.securityConfig

        if (!conf || !conf.active) {
            return
        }

        SpringSecurityUtils.loadSecondaryConfig 'DefaultRadiusSecurityConfig'
        conf = SpringSecurityUtils.securityConfig

        if (!conf.radius.active) {
            return
        }

        println 'Configuring Spring Security RADIUS ...'

        List<GrailsRadiusAuthenticator> radiusServers = new ArrayList()

        conf.radius.servers.each { server ->
            println "Configuring server... ${server}"
            def authenticator = new GrailsRadiusAuthenticatorImpl(
                radiusAuthenticatorClassName: server.authenticatorClassName?:conf.radius.authenticatorClassName,
                sharedSecret: server.sharedSecret,
                radiusHost: server.host,
                authenticationPort: server.authentication?.port?:conf.radius.authentication.port,
                accountingPort: server.accounting?.port?:conf.radius.accounting.port,
                retries: server.retries?:conf.radius.retries,
                timeout: server.timeout?:conf.radius.timeout)
            authenticator.afterPropertiesSet()
            radiusServers.add(authenticator)
        }

        radiusAuthenticationProvider(GrailsRadiusAuthenticationProvider) {
            radiusAuthenticators = radiusServers
            userDetailsService = ref('userDetailsService')
            preAuthenticationChecks = ref('preAuthenticationChecks')
            postAuthenticationChecks = ref('postAuthenticationChecks')
            authorizeFromDb = conf.radius.authorization.useDatabase
        }

        SpringSecurityUtils.registerProvider 'radiusAuthenticationProvider'
    }
}
