// configuration for plugin testing - will not be included in the plugin zip

log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'

    warn   'org.mortbay.log'
    debug  'net.jradius',
           'org.codehaus.groovy.grails.plugins.springsecurity.radius'
}

// Radius config
grails.plugins.springsecurity.radius.host='motp'
grails.plugins.springsecurity.radius.sharedSecret='1234567890'

// Added by the Spring Security Core plugin:
grails.plugins.springsecurity.userLookup.userDomainClassName = 'radius.test.User'
grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'radius.test.UserRole'
grails.plugins.springsecurity.authority.className = 'radius.test.Role'

grails.plugins.springsecurity.securityConfigType = "InterceptUrlMap"

grails.plugins.springsecurity.interceptUrlMap = [
    '/js/**':        ['IS_AUTHENTICATED_ANONYMOUSLY'],
    '/css/**':       ['IS_AUTHENTICATED_ANONYMOUSLY'],
    '/images/**':    ['IS_AUTHENTICATED_ANONYMOUSLY'],
    '/login/**':     ['IS_AUTHENTICATED_ANONYMOUSLY'],
    '/logout/**':    ['IS_AUTHENTICATED_ANONYMOUSLY'],
    '/dbconsole/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
    '/*':            ['IS_AUTHENTICATED_FULLY']
 ]

// Documdntation config
grails.doc.title = 'Spring Security RADIUS Plugin'
grails.doc.subtitle = 'Spring Security RADIUS Plugin'
grails.doc.authors = 'Sami Mäkelä'

