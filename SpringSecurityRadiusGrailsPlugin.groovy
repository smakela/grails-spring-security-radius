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
import org.codehaus.groovy.grails.plugins.springsecurity.radius.GrailsRadiusAuthenticatorImpl
import org.codehaus.groovy.grails.plugins.springsecurity.radius.GrailsRadiusAuthenticationProvider

/**
 * @author <a href="mailto:smakela@iki.fi">Sami Mäkelä</a>
 */
class SpringSecurityRadiusGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.7 > *"
    // the other plugins this plugin depends on
    def dependsOn = ['springSecurityCore': '1.2.4 > *']
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    def title = "Spring Security Radius Plugin" // Headline display name of the plugin
    def author = "Sami Mäkelä"
    def authorEmail = "smakela@iki.fi"
    def description = 'Integrates Radius Authentication to Grails'

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/spring-security-radius"

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Location of the plugin's issue tracker.
    def issueManagement = [ system: "GitHub", url: "https://github.com/smakela/grails-spring-security-radius/issues" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/smakela/grails-spring-security-radius/" ]

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

        def authenticator = classForName(conf.radius.authenticatorClassName).newInstance()

        grailsRadiusAuthenticator(GrailsRadiusAuthenticatorImpl) {
            radiusAuthenticator = authenticator
            sharedSecret = conf.radius.sharedSecret
            radiusHost = conf.radius.host
            authenticationPort = conf.radius.authentication.port
            accountingPort = conf.radius.accounting.port
            retries = conf.radius.retries
            timeout = conf.radius.timeout
        }

        grailsRadiusAuthenticationProvider(GrailsRadiusAuthenticationProvider) {
            grailsRadiusAuthenticator = ref('grailsRadiusAuthenticator')
            userDetailsService = ref('userDetailsService')
            preAuthenticationChecks = ref('preAuthenticationChecks')
            postAuthenticationChecks = ref('postAuthenticationChecks')
        }

        SpringSecurityUtils.registerProvider 'grailsRadiusAuthenticationProvider'
    }

    private Class<?> classForName(String name) {
        Class.forName(name, true, Thread.currentThread().contextClassLoader)
    }
}
