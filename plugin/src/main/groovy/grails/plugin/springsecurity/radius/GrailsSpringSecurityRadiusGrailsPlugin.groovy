package grails.plugin.springsecurity.radius

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugins.*

class GrailsSpringSecurityRadiusGrailsPlugin extends Plugin {

    def grailsVersion = "3.3.5 > *"

    def pluginExcludes = [
    ]

    def loadAfter = ['springSecurityCore']

    def title = 'Spring Security RADIUS Plugin'
    def author = 'Sami Mäkelä'
    def authorEmail = 'smakela@iki.fi'
    def description = 'RADIUS support for the Spring Security plugin.'
    def documentation = 'http://smakela.github.com/grails-spring-security-radius/'

    def license = 'APACHE'
    def issueManagement = [ system: 'GitHub', url: 'https://github.com/smakela/grails-spring-security-radius/issues' ]
    def scm = [ url: 'https://github.com/smakela/grails-spring-security-radius/' ]
    
    Closure doWithSpring() { {->
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
                        authenticationProtocol: server.authenticationProtocol?:conf.radius.authenticationProtocol,
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

            println '... finished configuring Spring Security RADIUS'
        }
    }

    void doWithDynamicMethods() {
        // TODO Implement registering dynamic methods to classes (optional)
    }

    void doWithApplicationContext() {
        // TODO Implement post initialization spring config (optional)
    }

    void onChange(Map<String, Object> event) {
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    void onConfigChange(Map<String, Object> event) {
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    void onShutdown(Map<String, Object> event) {
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
