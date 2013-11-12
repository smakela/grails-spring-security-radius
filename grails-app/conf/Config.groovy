log4j = {
    error 'org.codehaus.groovy.grails',
          'org.springframework',
          'org.hibernate',
          'net.sf.ehcache.hibernate'
    debug 'net.jradius',
          'org.codehaus.groovy.grails.plugins.springsecurity.radius'
}

// Radius config
grails.plugin.springsecurity.radius.servers = [
    [host:'motp',
     sharedSecret:'1234567890'],
    [host:'10.0.0.95',
     sharedSecret:'1234567890']
]

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'radius.test.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'radius.test.UserRole'
grails.plugin.springsecurity.authority.className = 'radius.test.Role'

grails.plugin.springsecurity.securityConfigType = "InterceptUrlMap"

grails.plugin.springsecurity.interceptUrlMap = [
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

// Uncomment and edit the following lines to start using Grails encoding & escaping improvements

/* remove this line 
// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside null
                scriptlet = 'none' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}
remove this line */
