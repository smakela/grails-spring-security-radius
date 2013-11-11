log4j = {
    error 'org.codehaus.groovy.grails',
          'org.springframework',
          'org.hibernate',
          'net.sf.ehcache.hibernate'
    debug 'net.jradius',
          'org.codehaus.groovy.grails.plugins.springsecurity.radius'
}

// Radius config
grails.plugins.springsecurity.radius.servers = [
    [host:'motp',
     sharedSecret:'1234567890'],
    [host:'10.0.0.95',
     sharedSecret:'1234567890']
]

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
